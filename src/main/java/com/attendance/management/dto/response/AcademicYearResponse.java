// dto/response/AcademicYearResponse.java
package com.attendance.management.dto.response;

import com.attendance.management.entity.AcademicYear;

public class AcademicYearResponse {

    private Long id;
    private String yearLabel;
    private String status;

    public static AcademicYearResponse from(AcademicYear year) {
        AcademicYearResponse response = new AcademicYearResponse();
        response.id = year.getId();
        response.yearLabel = year.getYearLabel();
        response.status = year.getStatus().name();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getYearLabel() {
        return yearLabel;
    }

    public String getStatus() {
        return status;
    }
}