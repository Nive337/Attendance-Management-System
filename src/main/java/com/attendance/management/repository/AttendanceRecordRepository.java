package com.attendance.management.repository;

import com.attendance.management.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByAttendanceSessionId(Long attendanceSessionId);

    // Feeds the attendance report calculations (Phase 10).
    List<AttendanceRecord> findByStudentEnrollmentId(Long studentEnrollmentId);
}