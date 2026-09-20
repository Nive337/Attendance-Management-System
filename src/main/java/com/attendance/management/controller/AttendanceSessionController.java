package com.attendance.management.controller;

import com.attendance.management.dto.request.AttendanceSubmitRequest;
import com.attendance.management.dto.response.AttendanceRosterResponse;
import com.attendance.management.dto.response.AttendanceSubmitResponse;
import com.attendance.management.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceSessionController {

    private final AttendanceService attendanceService;

    public AttendanceSessionController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/roster")
    public AttendanceRosterResponse getRoster(
            @RequestParam Long courseOfferingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") Integer session) {
        return attendanceService.getRoster(courseOfferingId, date, session);
    }

    @PostMapping("/submit")
    public AttendanceSubmitResponse submit(@Valid @RequestBody AttendanceSubmitRequest request) {
        return attendanceService.submit(request);
    }
}