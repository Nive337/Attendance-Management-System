package com.attendance.management.service;

import com.attendance.management.dto.response.SmsNotificationResponse;
import com.attendance.management.entity.AttendanceRecord;
import com.attendance.management.entity.CourseOffering;
import com.attendance.management.entity.SemesterTerm;
import com.attendance.management.entity.SmsNotification;
import com.attendance.management.entity.StudentEnrollment;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.AttendanceAggregateRow;
import com.attendance.management.repository.AttendanceRecordRepository;
import com.attendance.management.repository.CourseOfferingRepository;
import com.attendance.management.repository.SemesterTermRepository;
import com.attendance.management.repository.SmsNotificationRepository;
import com.attendance.management.repository.StudentEnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonthlyAttendanceNotificationService {

    private static final DateTimeFormatter MESSAGE_DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy");

    private final CourseOfferingRepository courseOfferingRepository;
    private final SemesterTermRepository semesterTermRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final SmsNotificationRepository smsNotificationRepository;
    private final CourseOfferingAccessGuard accessGuard;

    public MonthlyAttendanceNotificationService(CourseOfferingRepository courseOfferingRepository,
                                                 SemesterTermRepository semesterTermRepository,
                                                 StudentEnrollmentRepository studentEnrollmentRepository,
                                                 AttendanceRecordRepository attendanceRecordRepository,
                                                 SmsNotificationRepository smsNotificationRepository,
                                                 CourseOfferingAccessGuard accessGuard) {
        this.courseOfferingRepository = courseOfferingRepository;
        this.semesterTermRepository = semesterTermRepository;
        this.studentEnrollmentRepository = studentEnrollmentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.smsNotificationRepository = smsNotificationRepository;
        this.accessGuard = accessGuard;
    }

    // HTTP-exposed: one lecturer, one of their own offerings, one month.
    @Transactional
    public List<SmsNotificationResponse> generateForOffering(Long courseOfferingId, int year, int month) {
        CourseOffering offering = courseOfferingRepository.findById(courseOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Course offering not found: " + courseOfferingId));
        accessGuard.requireOwnership(offering);

        return generateForSingleOffering(offering, year, month).stream()
                .map(SmsNotificationResponse::from)
                .toList();
    }

    // Not yet exposed via HTTP. Iterates every active offering regardless of
    // which lecturer teaches it, so it deliberately bypasses the per-request
    // ownership check above - meant to be called by a trusted internal trigger
    // (a scheduled job, or an admin-only endpoint), which Phase 11 proper will add.
    @Transactional
    public List<SmsNotificationResponse> generateForAllActiveOfferings(int year, int month) {
        List<SmsNotification> results = new ArrayList<>();
        for (CourseOffering offering : courseOfferingRepository.findByStatus(CourseOffering.Status.ACTIVE)) {
            results.addAll(generateForSingleOffering(offering, year, month));
        }
        return results.stream().map(SmsNotificationResponse::from).toList();
    }

    private List<SmsNotification> generateForSingleOffering(CourseOffering offering, int year, int month) {
        SemesterTerm term = semesterTermRepository
                .findByAcademicYearIdAndDegreeIdAndSemester(
                        offering.getAcademicYear().getId(), offering.getDegree().getId(), offering.getSemester())
                .orElse(null);
        if (term == null) {
            return List.of(); // semester dates not configured yet for this year/degree/semester - nothing to calculate
        }

        YearMonth targetMonth = YearMonth.of(year, month);
        LocalDate monthStart = targetMonth.atDay(1);
        LocalDate monthEnd = targetMonth.atEndOfMonth();

        if (term.getEndDate().isBefore(monthStart)) {
            return List.of(); // semester was already over before this month began
        }
        if (term.getStartDate().isAfter(monthEnd)) {
            return List.of(); // semester hasn't started yet as of this month
        }

        LocalDate windowStart = term.getStartDate();
        LocalDate windowEnd = monthEnd.isAfter(term.getEndDate()) ? term.getEndDate() : monthEnd;

        List<StudentEnrollment> roster = studentEnrollmentRepository
                .findByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndStatusOrderByRollNumberAsc(
                        offering.getAcademicYear().getId(), offering.getDegree().getId(),
                        offering.getSemester(), offering.getSection(), StudentEnrollment.Status.ACTIVE);
        if (roster.isEmpty()) {
            return List.of();
        }

        List<AttendanceAggregateRow> aggregates = attendanceRecordRepository.aggregateByCourseOffering(
                offering.getId(), windowStart, windowEnd,
                AttendanceRecord.Status.PRESENT, AttendanceRecord.Status.ABSENT, AttendanceRecord.Status.LATE);
        Map<Long, AttendanceAggregateRow> byEnrollmentId = new HashMap<>();
        aggregates.forEach(row -> byEnrollmentId.put(row.getEnrollmentId(), row));

        List<SmsNotification> results = new ArrayList<>();
        for (StudentEnrollment enrollment : roster) {
            AttendanceAggregateRow aggregate = byEnrollmentId.get(enrollment.getId());
            long total = aggregate != null ? aggregate.getTotalClasses() : 0L;
            long present = aggregate != null ? aggregate.getPresentCount() : 0L;

            AttendanceStatusCalculator.Result result = AttendanceStatusCalculator.calculate(total, present);
            if (!"WARNING".equals(result.status())) {
                continue; // GOOD -> nothing to send; NO_DATA -> never treated as a below-75% warning
            }

            SmsNotification notification = smsNotificationRepository
                    .findByStudentEnrollmentIdAndCourseOfferingIdAndNotifYearAndNotifMonth(
                            enrollment.getId(), offering.getId(), year, month)
                    .orElse(null);

            if (notification != null && notification.getStatus() == SmsNotification.Status.SENT) {
                continue; // already confirmed delivered for this student+subject+month - never re-touch it
            }

            boolean isNew = notification == null;
            if (isNew) {
                notification = new SmsNotification();
                notification.setStudentEnrollment(enrollment);
                notification.setCourseOffering(offering);
                notification.setNotifYear(year);
                notification.setNotifMonth(month);
                notification.setStatus(SmsNotification.Status.PENDING);
                notification.setAttemptCount(0);
            }

            // PENDING/FAILED rows get their numbers refreshed on every re-run, since
            // cumulative attendance only ever grows more complete as the month progresses.
            notification.setParentPhone(enrollment.getStudent().getParentPhone());
            notification.setMessage(buildMessage(enrollment, offering, windowStart, windowEnd, result.percentage()));

            results.add(smsNotificationRepository.save(notification));
        }

        return results;
    }

    private String buildMessage(StudentEnrollment enrollment, CourseOffering offering,
                                 LocalDate periodStart, LocalDate periodEnd, double percentage) {
        return String.format(
                "Dear Parent, %s's attendance in %s (%s) is %s%% for the period %s to %s. " +
                        "This is below the required 75%% attendance. Please ensure regular attendance.",
                enrollment.getStudent().getName(),
                offering.getSubject().getSubjectName(),
                offering.getSubject().getSubjectCode(),
                trimPercentage(percentage),
                periodStart.format(MESSAGE_DATE_FORMAT),
                periodEnd.format(MESSAGE_DATE_FORMAT));
    }

    private String trimPercentage(double percentage) {
        if (percentage == Math.floor(percentage)) {
            return String.valueOf((long) percentage); // 70.0 -> "70", not "70.0"
        }
        return String.valueOf(percentage); // 66.67 -> "66.67"
    }
}