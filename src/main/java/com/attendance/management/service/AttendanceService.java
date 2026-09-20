package com.attendance.management.service;

import com.attendance.management.dto.request.AttendanceStatusRequest;
import com.attendance.management.dto.request.AttendanceSubmitRequest;
import com.attendance.management.dto.response.AttendanceRosterResponse;
import com.attendance.management.dto.response.AttendanceSubmitResponse;
import com.attendance.management.entity.AttendanceRecord;
import com.attendance.management.entity.AttendanceSession;
import com.attendance.management.entity.CourseOffering;
import com.attendance.management.entity.Lecturer;
import com.attendance.management.entity.StudentEnrollment;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.AttendanceRecordRepository;
import com.attendance.management.repository.AttendanceSessionRepository;
import com.attendance.management.repository.CourseOfferingRepository;
import com.attendance.management.repository.StudentEnrollmentRepository;
import com.attendance.management.security.LecturerPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AttendanceService {

    private final CourseOfferingRepository courseOfferingRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceService(CourseOfferingRepository courseOfferingRepository,
                              StudentEnrollmentRepository studentEnrollmentRepository,
                              AttendanceSessionRepository attendanceSessionRepository,
                              AttendanceRecordRepository attendanceRecordRepository) {
        this.courseOfferingRepository = courseOfferingRepository;
        this.studentEnrollmentRepository = studentEnrollmentRepository;
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Transactional(readOnly = true)
    public AttendanceRosterResponse getRoster(Long courseOfferingId, LocalDate date, Integer sessionNumber) {
        CourseOffering offering = courseOfferingRepository.findById(courseOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Course offering not found: " + courseOfferingId));
        requireOwnership(offering);

        List<StudentEnrollment> enrollments = studentEnrollmentRepository
                .findByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndStatusOrderByRollNumberAsc(
                        offering.getAcademicYear().getId(), offering.getDegree().getId(),
                        offering.getSemester(), offering.getSection(), StudentEnrollment.Status.ACTIVE);

        var existingSession = attendanceSessionRepository
                .findByCourseOfferingIdAndAttendanceDateAndSessionNumber(courseOfferingId, date, sessionNumber);

        Map<Long, AttendanceRecord> existingByEnrollmentId = new HashMap<>();
        existingSession.ifPresent(session ->
                attendanceRecordRepository.findByAttendanceSessionId(session.getId())
                        .forEach(record -> existingByEnrollmentId.put(record.getStudentEnrollment().getId(), record)));

        // Every student defaults to PRESENT unless a saved record from an
        // earlier submission for this exact date+session says otherwise.
        List<AttendanceRosterResponse.RosterStudent> students = enrollments.stream()
                .map(enrollment -> {
                    AttendanceRecord existing = existingByEnrollmentId.get(enrollment.getId());
                    String status = existing != null ? existing.getStatus().name() : AttendanceRecord.Status.PRESENT.name();
                    String note = existing != null ? existing.getNote() : null;
                    return new AttendanceRosterResponse.RosterStudent(
                            enrollment.getId(), enrollment.getRollNumber(), enrollment.getStudent().getName(),
                            status, note);
                })
                .toList();

        return new AttendanceRosterResponse(
                offering.getId(), offering.getSubject().getSubjectName(), offering.getSubject().getSubjectCode(),
                offering.getLecturer().getName(), offering.getDegree().getName(), offering.getSemester(),
                offering.getSection(), date, sessionNumber, existingSession.isPresent(), students);
    }

    @Transactional
    public AttendanceSubmitResponse submit(AttendanceSubmitRequest request) {
        CourseOffering offering = courseOfferingRepository.findById(request.getCourseOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course offering not found: " + request.getCourseOfferingId()));
        requireOwnership(offering);

        List<StudentEnrollment> activeRoster = studentEnrollmentRepository
                .findByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndStatusOrderByRollNumberAsc(
                        offering.getAcademicYear().getId(), offering.getDegree().getId(),
                        offering.getSemester(), offering.getSection(), StudentEnrollment.Status.ACTIVE);
        Map<Long, StudentEnrollment> activeById = new HashMap<>();
        activeRoster.forEach(e -> activeById.put(e.getId(), e));

        Set<Long> seenInRequest = new HashSet<>();
        for (AttendanceStatusRequest row : request.getRecords()) {
            if (!activeById.containsKey(row.getEnrollmentId())) {
                throw new IllegalArgumentException(
                        "Enrollment " + row.getEnrollmentId() + " is not an active student in this class.");
            }
            if (!seenInRequest.add(row.getEnrollmentId())) {
                throw new IllegalArgumentException(
                        "Enrollment " + row.getEnrollmentId() + " appears more than once in this submission.");
            }
        }

        // Same date+session -> reuse the existing session (upsert), so a lecturer
        // correcting today's attendance updates it rather than creating a duplicate.
        AttendanceSession session = attendanceSessionRepository
                .findByCourseOfferingIdAndAttendanceDateAndSessionNumber(
                        offering.getId(), request.getAttendanceDate(), request.getSessionNumber())
                .orElseGet(() -> {
                    AttendanceSession newSession = new AttendanceSession();
                    newSession.setCourseOffering(offering);
                    newSession.setAttendanceDate(request.getAttendanceDate());
                    newSession.setSessionNumber(request.getSessionNumber());
                    newSession.setCreatedByLecturer(currentLecturer());
                    return attendanceSessionRepository.save(newSession);
                });

        Map<Long, AttendanceRecord> existingByEnrollmentId = new HashMap<>();
        attendanceRecordRepository.findByAttendanceSessionId(session.getId())
                .forEach(record -> existingByEnrollmentId.put(record.getStudentEnrollment().getId(), record));

        for (AttendanceStatusRequest row : request.getRecords()) {
            AttendanceRecord.Status status;
            try {
                status = AttendanceRecord.Status.valueOf(row.getStatus().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Status must be one of: PRESENT, ABSENT, LATE");
            }

            AttendanceRecord record = existingByEnrollmentId.get(row.getEnrollmentId());
            if (record == null) {
                record = new AttendanceRecord();
                record.setAttendanceSession(session);
                record.setStudentEnrollment(activeById.get(row.getEnrollmentId()));
            }
            record.setStatus(status);
            record.setNote(row.getNote());
            attendanceRecordRepository.save(record);
        }

        List<AttendanceRecord> allRecords = attendanceRecordRepository.findByAttendanceSessionId(session.getId());
        return buildSummary(offering, session, allRecords);
    }

    private AttendanceSubmitResponse buildSummary(CourseOffering offering, AttendanceSession session,
                                                    List<AttendanceRecord> records) {
        long total = records.size();
        long present = records.stream().filter(r -> r.getStatus() == AttendanceRecord.Status.PRESENT).count();
        long absent = records.stream().filter(r -> r.getStatus() == AttendanceRecord.Status.ABSENT).count();
        long late = records.stream().filter(r -> r.getStatus() == AttendanceRecord.Status.LATE).count();
        double percentage = total == 0 ? 0.0 : Math.round((present * 10000.0) / total) / 100.0;

        List<AttendanceSubmitResponse.RecordSummary> summaries = records.stream()
                .map(r -> new AttendanceSubmitResponse.RecordSummary(
                        r.getStudentEnrollment().getId(), r.getStudentEnrollment().getRollNumber(),
                        r.getStudentEnrollment().getStudent().getName(), r.getStatus().name(), r.getNote()))
                .toList();

        return new AttendanceSubmitResponse(
                session.getId(), offering.getId(), session.getAttendanceDate(), session.getSessionNumber(),
                total, present, absent, late, percentage, summaries);
    }

    private void requireOwnership(CourseOffering offering) {
        Lecturer current = currentLecturer();
        boolean isOwner = current.getId().equals(offering.getLecturer().getId());
        boolean isAdmin = current.getRole() == Lecturer.Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not assigned to this course offering.");
        }
    }

    private Lecturer currentLecturer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LecturerPrincipal principal = (LecturerPrincipal) authentication.getPrincipal();
        return principal.getLecturer();
    }
}