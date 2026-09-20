package com.attendance.management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class AttendanceSubmitRequest {

    @NotNull(message = "Course offering is required")
    private Long courseOfferingId;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @Min(value = 1, message = "Session number must be at least 1")
    @Max(value = 20, message = "Session number must be at most 20")
    private Integer sessionNumber = 1;

    @NotEmpty(message = "At least one attendance record is required")
    @Valid
    private List<AttendanceStatusRequest> records;

    public AttendanceSubmitRequest() {
    }

    public Long getCourseOfferingId() {
        return courseOfferingId;
    }

    public void setCourseOfferingId(Long courseOfferingId) {
        this.courseOfferingId = courseOfferingId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public List<AttendanceStatusRequest> getRecords() {
        return records;
    }

    public void setRecords(List<AttendanceStatusRequest> records) {
        this.records = records;
    }
}