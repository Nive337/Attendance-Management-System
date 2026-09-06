package com.attendance.management.controller;

import com.attendance.management.entity.Attendance;
import com.attendance.management.entity.Student;
import com.attendance.management.repository.AttendanceRepository;
import com.attendance.management.repository.StudentRepository;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public AttendanceController(
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository) {

        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    // Get all attendance records
    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    // Mark or update attendance
    @PostMapping
    public Attendance markAttendance(
            @RequestParam Long studentId,
            @RequestParam LocalDate date,
            @RequestParam String status) {

        Student student = studentRepository
                .findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        Attendance attendance =
                attendanceRepository
                        .findByStudent_IdAndAttendanceDate(studentId, date)
                        .orElse(new Attendance());

        attendance.setStudent(student);
        attendance.setAttendanceDate(date);
        attendance.setStatus(status);

        return attendanceRepository.save(attendance);
    }
}