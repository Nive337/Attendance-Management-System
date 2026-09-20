package com.attendance.management.repository;

import com.attendance.management.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    // Duplicate-attendance guard used before creating a new session (Phase 9),
    // backed by the DB unique constraint as the final safety net.
    Optional<AttendanceSession> findByCourseOfferingIdAndAttendanceDateAndSessionNumber(
            Long courseOfferingId, LocalDate attendanceDate, Integer sessionNumber);
}