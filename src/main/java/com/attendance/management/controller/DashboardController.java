package com.attendance.management.controller;

import com.attendance.management.entity.Attendance;
import com.attendance.management.repository.AttendanceRepository;
import com.attendance.management.repository.StudentRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    public DashboardController(StudentRepository studentRepository,
                               AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping
    public Map<String, Object> getDashboardData() {

        Map<String, Object> dashboard = new HashMap<>();

        // Total number of students
        long totalStudents = studentRepository.count();

        // Today's date
        LocalDate today = LocalDate.now();

        // Get all attendance records
        List<Attendance> attendanceList = attendanceRepository.findAll();

        // Get today's attendance
        List<Attendance> todayAttendance = attendanceList.stream()
                .filter(a -> today.equals(a.getAttendanceDate()))
                .toList();

        int totalToday = todayAttendance.size();

        int presentToday = (int) todayAttendance.stream()
                .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()))
                .count();

        double todayPercentage = 0;

        if (totalToday > 0) {
            todayPercentage = (presentToday * 100.0) / totalToday;
        }

        // Calculate students below 75%
        int below75 = 0;

        for (var student : studentRepository.findAll()) {

            List<Attendance> studentAttendance = attendanceList.stream()
                    .filter(a -> a.getStudent().getId().equals(student.getId()))
                    .toList();

            if (!studentAttendance.isEmpty()) {

                long present = studentAttendance.stream()
                        .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()))
                        .count();

                double percentage =
                        (present * 100.0) / studentAttendance.size();

                if (percentage < 75) {
                    below75++;
                }
            }
        }

        dashboard.put("totalStudents", totalStudents);
        dashboard.put("todayAttendance",
                Math.round(todayPercentage * 100.0) / 100.0);
        dashboard.put("below75", below75);

        return dashboard;
    }
}