package com.attendance.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AttendanceStatusRequest {

    @NotNull(message = "Enrollment id is required")
    private Long enrollmentId;

    @NotBlank(message = "Status is required")
    private String status;

    @Size(max = 255, message = "Note must be 255 characters or fewer")
    private String note;

    public AttendanceStatusRequest() {
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}