package com.attendance.management.controller;

import com.attendance.management.entity.Attendance;
import com.attendance.management.entity.Student;
import com.attendance.management.repository.AttendanceRepository;
import com.attendance.management.repository.StudentRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    public ReportController(
            StudentRepository studentRepository,
            AttendanceRepository attendanceRepository) {

        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getReports(
            @RequestParam int year,
            @RequestParam int month) {

        List<Student> students = studentRepository.findAll();

        List<Map<String, Object>> reports = new ArrayList<>();

        for (Student student : students) {

            // Get all attendance records for this student
            List<Attendance> attendanceList =
                    attendanceRepository.findByStudentId(student.getId());

            // Keep only records for selected year and month
            attendanceList = attendanceList.stream()
                    .filter(a -> a.getAttendanceDate() != null)
                    .filter(a -> a.getAttendanceDate().getYear() == year)
                    .filter(a -> a.getAttendanceDate().getMonthValue() == month)
                    .toList();

            // Total classes
            int totalClasses = attendanceList.size();

            // Present count
            int presentCount = (int) attendanceList.stream()
                    .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()))
                    .count();

            // Absent count
            int absentCount = (int) attendanceList.stream()
                    .filter(a -> "ABSENT".equalsIgnoreCase(a.getStatus()))
                    .count();

            // Attendance percentage
            double percentage = 0;

            if (totalClasses > 0) {
                percentage =
                        (presentCount * 100.0) / totalClasses;
            }

            // Create report
            Map<String, Object> report = new HashMap<>();

            report.put("id", student.getId());
            report.put("name", student.getName());
            report.put("rollNumber", student.getRollNumber());
            report.put("semester", student.getSemester());
            report.put("section", student.getSection());

            report.put("totalClasses", totalClasses);
            report.put("present", presentCount);
            report.put("absent", absentCount);

            report.put(
                    "percentage",
                    Math.round(percentage * 100.0) / 100.0
            );

            // Add below 75% information
            report.put("below75", percentage < 75);

            reports.add(report);
        }

        return reports;
    }
}