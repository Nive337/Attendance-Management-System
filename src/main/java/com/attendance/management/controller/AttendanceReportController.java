package com.attendance.management.controller;

import com.attendance.management.dto.response.AttendanceReportResponse;
import com.attendance.management.service.AttendanceReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class AttendanceReportController {

    private final AttendanceReportService attendanceReportService;

    public AttendanceReportController(AttendanceReportService attendanceReportService) {
        this.attendanceReportService = attendanceReportService;
    }

    // Resolves to GET /api/reports/attendance - distinct from the legacy
    // GET /api/reports (year/month) on the existing ReportController, so no mapping collision.
    @GetMapping("/attendance")
    public AttendanceReportResponse getReport(
            @RequestParam Long courseOfferingId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return attendanceReportService.generate(courseOfferingId, year, month, startDate, endDate);
    }
}