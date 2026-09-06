package com.attendance.management.repository;

import com.attendance.management.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudent_Id(Long studentId);

    Optional<Attendance> findByStudent_IdAndAttendanceDate(
            Long studentId,
            LocalDate attendanceDate
    );
}