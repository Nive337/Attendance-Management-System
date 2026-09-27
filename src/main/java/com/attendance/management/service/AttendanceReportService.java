package com.attendance.management.service;

import com.attendance.management.dto.response.AttendanceReportResponse;
import com.attendance.management.entity.AttendanceRecord;
import com.attendance.management.entity.CourseOffering;
import com.attendance.management.entity.StudentEnrollment;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.AttendanceAggregateRow;
import com.attendance.management.repository.AttendanceRecordRepository;
import com.attendance.management.repository.AttendanceSessionRepository;
import com.attendance.management.repository.CourseOfferingRepository;
import com.attendance.management.repository.StudentEnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceReportService {

    

    private final CourseOfferingRepository courseOfferingRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final CourseOfferingAccessGuard accessGuard;

    public AttendanceReportService(CourseOfferingRepository courseOfferingRepository,
                                    StudentEnrollmentRepository studentEnrollmentRepository,
                                    AttendanceRecordRepository attendanceRecordRepository,
                                    AttendanceSessionRepository attendanceSessionRepository,
                                    CourseOfferingAccessGuard accessGuard) {
        this.courseOfferingRepository = courseOfferingRepository;
        this.studentEnrollmentRepository = studentEnrollmentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.accessGuard = accessGuard;
    }

    @Transactional(readOnly = true)
    public AttendanceReportResponse generate(Long courseOfferingId, Integer year, Integer month,
                                              LocalDate startDate, LocalDate endDate) {

        CourseOffering offering = courseOfferingRepository.findById(courseOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Course offering not found: " + courseOfferingId));
        accessGuard.requireOwnership(offering);

        LocalDate rangeStart = startDate;
        LocalDate rangeEnd = endDate;

        if (year != null || month != null) {
            if (year == null || month == null) {
                throw new IllegalArgumentException("Both year and month are required together.");
            }
            if (startDate != null || endDate != null) {
                throw new IllegalArgumentException("Use either year+month or startDate+endDate, not both.");
            }
            YearMonth ym = YearMonth.of(year, month);
            rangeStart = ym.atDay(1);
            rangeEnd = ym.atEndOfMonth();
        } else if ((startDate == null) != (endDate == null)) {
            throw new IllegalArgumentException("Both startDate and endDate are required together.");
        }

        List<StudentEnrollment> roster = studentEnrollmentRepository
                .findByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndStatusOrderByRollNumberAsc(
                        offering.getAcademicYear().getId(), offering.getDegree().getId(),
                        offering.getSemester(), offering.getSection(), StudentEnrollment.Status.ACTIVE);

        List<AttendanceAggregateRow> aggregates = attendanceRecordRepository.aggregateByCourseOffering(
                courseOfferingId, rangeStart, rangeEnd,
                AttendanceRecord.Status.PRESENT, AttendanceRecord.Status.ABSENT, AttendanceRecord.Status.LATE);

        Map<Long, AttendanceAggregateRow> byEnrollmentId = new HashMap<>();
        aggregates.forEach(row -> byEnrollmentId.put(row.getEnrollmentId(), row));

        long totalSessions = attendanceSessionRepository.countSessions(courseOfferingId, rangeStart, rangeEnd);

        List<AttendanceReportResponse.StudentReportRow> studentRows = roster.stream()
                .map(enrollment -> toReportRow(enrollment, byEnrollmentId.get(enrollment.getId())))
                .toList();

        AttendanceReportResponse.ReportSummary summary = buildSummary(studentRows, totalSessions);

        return new AttendanceReportResponse(
                offering.getId(), offering.getSubject().getSubjectName(), offering.getSubject().getSubjectCode(),
                offering.getLecturer().getName(), offering.getDegree().getName(), offering.getSemester(),
                offering.getSection(), rangeStart, rangeEnd, summary, studentRows);
    }

    private AttendanceReportResponse.StudentReportRow toReportRow(StudentEnrollment enrollment,
                                                                AttendanceAggregateRow aggregate) {
        long total = aggregate != null ? aggregate.getTotalClasses() : 0L;
        long present = aggregate != null ? aggregate.getPresentCount() : 0L;
        long absent = aggregate != null ? aggregate.getAbsentCount() : 0L;
        long late = aggregate != null ? aggregate.getLateCount() : 0L;
        
        AttendanceStatusCalculator.Result result = AttendanceStatusCalculator.calculate(total, present);

        return new AttendanceReportResponse.StudentReportRow(
            enrollment.getId(), enrollment.getRollNumber(), enrollment.getStudent().getName(),
            total, present, absent, late, result.percentage(), result.status());
    }

    private AttendanceReportResponse.ReportSummary buildSummary(
            List<AttendanceReportResponse.StudentReportRow> rows, long totalSessions) {

        int totalStudents = rows.size();
        int aboveThreshold = 0;
        int belowThreshold = 0;
        int noData = 0;
        long totalRecords = 0;
        double percentageSum = 0.0;
        int studentsWithData = 0;

        for (AttendanceReportResponse.StudentReportRow row : rows) {
            totalRecords += row.getTotalClasses();
            switch (row.getStatus()) {
                case "GOOD" -> aboveThreshold++;
                case "WARNING" -> belowThreshold++;
                default -> noData++;
            }
            if (row.getTotalClasses() > 0) {
                percentageSum += row.getAttendancePercentage();
                studentsWithData++;
            }
        }

        // Averaged only over students who have at least one recorded class -
        // otherwise a fresh class with no sessions yet would misleadingly show 0% average.
        double averagePercentage = studentsWithData == 0
                ? 0.0
                : Math.round((percentageSum / studentsWithData) * 100.0) / 100.0;

        return new AttendanceReportResponse.ReportSummary(
                totalStudents, aboveThreshold, belowThreshold, noData,
                totalSessions, totalRecords, averagePercentage);
    }
}