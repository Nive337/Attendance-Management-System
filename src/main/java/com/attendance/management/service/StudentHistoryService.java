package com.attendance.management.service;

import com.attendance.management.dto.response.StudentHistoryResponse;
import com.attendance.management.entity.AttendanceRecord;
import com.attendance.management.entity.Student;
import com.attendance.management.entity.StudentEnrollment;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.AttendanceRecordRepository;
import com.attendance.management.repository.StudentEnrollmentRepository;
import com.attendance.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentHistoryService {

    private final StudentRepository studentRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public StudentHistoryService(StudentRepository studentRepository,
                                  StudentEnrollmentRepository studentEnrollmentRepository,
                                  AttendanceRecordRepository attendanceRecordRepository) {
        this.studentRepository = studentRepository;
        this.studentEnrollmentRepository = studentEnrollmentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Transactional(readOnly = true)
    public StudentHistoryResponse getHistory(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        List<StudentEnrollment> enrollments = studentEnrollmentRepository
                .findByStudentIdOrderByAcademicYearIdAscSemesterAsc(studentId);

        List<StudentHistoryResponse.EnrollmentHistoryEntry> entries =
                enrollments.stream().map(this::toHistoryEntry).toList();

        return new StudentHistoryResponse(
                student.getId(), student.getName(), student.getParentName(), student.getParentPhone(), entries);
    }

    // Simple Java-side counting, matching the existing DashboardController's style.
    // Phase 10 formalizes this into a proper aggregate query for full reports;
    // this is fine at the scale of one student's history.
    private StudentHistoryResponse.EnrollmentHistoryEntry toHistoryEntry(StudentEnrollment enrollment) {
        List<AttendanceRecord> records = attendanceRecordRepository.findByStudentEnrollmentId(enrollment.getId());

        long total = records.size();
        long present = records.stream().filter(r -> r.getStatus() == AttendanceRecord.Status.PRESENT).count();
        long absent = records.stream().filter(r -> r.getStatus() == AttendanceRecord.Status.ABSENT).count();
        long late = records.stream().filter(r -> r.getStatus() == AttendanceRecord.Status.LATE).count();

        double percentage = total == 0 ? 0.0 : Math.round((present * 10000.0) / total) / 100.0;

        return new StudentHistoryResponse.EnrollmentHistoryEntry(
                enrollment.getId(),
                enrollment.getAcademicYear().getYearLabel(),
                enrollment.getDegree().getName(),
                enrollment.getSemester(),
                enrollment.getSection(),
                enrollment.getRollNumber(),
                enrollment.getStatus().name(),
                total, present, absent, late, percentage
        );
    }
}