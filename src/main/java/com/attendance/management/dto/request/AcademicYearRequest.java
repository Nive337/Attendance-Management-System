// dto/request/AcademicYearRequest.java
package com.attendance.management.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AcademicYearRequest {

    @NotBlank(message = "Year label is required")
    private String yearLabel;

    public AcademicYearRequest() {
    }

    public String getYearLabel() {
        return yearLabel;
    }

    public void setYearLabel(String yearLabel) {
        this.yearLabel = yearLabel;
    }
}