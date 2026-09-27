package com.attendance.management.repository;

import com.attendance.management.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByAttendanceSessionId(Long attendanceSessionId);

    // Feeds the attendance report calculations (Phase 10).
    List<AttendanceRecord> findByStudentEnrollmentId(Long studentEnrollmentId);

    // Enum values are bound as query parameters rather than written as JPQL literals -
// avoids any Hibernate-version quirks around resolving nested enum type names inline.
@Query("""
        SELECT r.studentEnrollment.id AS enrollmentId,
               COUNT(r) AS totalClasses,
               SUM(CASE WHEN r.status = :present THEN 1L ELSE 0L END) AS presentCount,
               SUM(CASE WHEN r.status = :absent THEN 1L ELSE 0L END) AS absentCount,
               SUM(CASE WHEN r.status = :late THEN 1L ELSE 0L END) AS lateCount
        FROM AttendanceRecord r
        WHERE r.attendanceSession.courseOffering.id = :courseOfferingId
          AND (:startDate IS NULL OR r.attendanceSession.attendanceDate >= :startDate)
          AND (:endDate IS NULL OR r.attendanceSession.attendanceDate <= :endDate)
        GROUP BY r.studentEnrollment.id
        """)
List<AttendanceAggregateRow> aggregateByCourseOffering(
        @Param("courseOfferingId") Long courseOfferingId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("present") AttendanceRecord.Status present,
        @Param("absent") AttendanceRecord.Status absent,
        @Param("late") AttendanceRecord.Status late);
}