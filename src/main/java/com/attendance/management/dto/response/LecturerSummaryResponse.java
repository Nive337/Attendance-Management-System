// dto/response/LecturerSummaryResponse.java
package com.attendance.management.dto.response;

import com.attendance.management.entity.Lecturer;

public class LecturerSummaryResponse {

    private Long id;
    private String lecturerCode;
    private String name;
    private String department;

    public static LecturerSummaryResponse from(Lecturer lecturer) {
        LecturerSummaryResponse response = new LecturerSummaryResponse();
        response.id = lecturer.getId();
        response.lecturerCode = lecturer.getLecturerCode();
        response.name = lecturer.getName();
        response.department = lecturer.getDepartment();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getLecturerCode() {
        return lecturerCode;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }
}