package com.attendance.management.service;

// Extracted from AttendanceReportService in Phase 11 so the exact same
// percentage/threshold rule is used everywhere it's needed - reports and
// the monthly notification engine both call this instead of each computing it.
public final class AttendanceStatusCalculator {

    private static final double ATTENDANCE_THRESHOLD = 75.0;

    private AttendanceStatusCalculator() {
    }

    public static Result calculate(long totalClasses, long presentCount) {
        if (totalClasses == 0) {
            return new Result(0.0, "NO_DATA");
        }
        double percentage = Math.round((presentCount * 10000.0) / totalClasses) / 100.0;
        String status = percentage < ATTENDANCE_THRESHOLD ? "WARNING" : "GOOD";
        return new Result(percentage, status);
    }

    public record Result(double percentage, String status) {
    }
}