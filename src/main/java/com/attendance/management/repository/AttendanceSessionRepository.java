package com.attendance.management.repository;

import com.attendance.management.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    // Duplicate-attendance guard used before creating a new session (Phase 9),
    // backed by the DB unique constraint as the final safety net.
    Optional<AttendanceSession> findByCourseOfferingIdAndAttendanceDateAndSessionNumber(
            Long courseOfferingId, LocalDate attendanceDate, Integer sessionNumber);

    @Query("""
        SELECT COUNT(s) FROM AttendanceSession s
        WHERE s.courseOffering.id = :courseOfferingId
          AND (:startDate IS NULL OR s.attendanceDate >= :startDate)
          AND (:endDate IS NULL OR s.attendanceDate <= :endDate)
        """)
long countSessions(@Param("courseOfferingId") Long courseOfferingId,
                    @Param("startDate") LocalDate startDate,
                    @Param("endDate") LocalDate endDate);
}