package com.attendance.management.controller;

import com.attendance.management.dto.response.SmsNotificationResponse;
import com.attendance.management.service.MonthlyAttendanceNotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.attendance.management.service.SmsDispatchService;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// Resolves to POST /api/reports/attendance/notifications - distinct from
// GET /api/reports/attendance on AttendanceReportController (Phase 10), no collision.
@RestController
@RequestMapping("/api/reports/attendance/notifications")
public class AttendanceNotificationController {

    private final MonthlyAttendanceNotificationService notificationService;
    private final SmsDispatchService smsDispatchService;

    public AttendanceNotificationController(MonthlyAttendanceNotificationService notificationService,
                                             SmsDispatchService smsDispatchService) {
        this.notificationService = notificationService;
        this.smsDispatchService = smsDispatchService;
    }

    @PostMapping
    public List<SmsNotificationResponse> generate(
            @RequestParam Long courseOfferingId,
            @RequestParam int year,
            @RequestParam int month) {
        return notificationService.generateForOffering(courseOfferingId, year, month);
    }
    @GetMapping
    public List<SmsNotificationResponse> list(@RequestParam Long courseOfferingId) {
        return smsDispatchService.listForOffering(courseOfferingId);
    }

    @PostMapping("/dispatch")
    public List<SmsNotificationResponse> dispatch(@RequestParam Long courseOfferingId) {
        return smsDispatchService.dispatchForOffering(courseOfferingId);
    }
}