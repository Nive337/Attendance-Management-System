// dto/response/DegreeResponse.java
package com.attendance.management.dto.response;

import com.attendance.management.entity.Degree;

public class DegreeResponse {

    private Long id;
    private String name;
    private String status;

    public static DegreeResponse from(Degree degree) {
        DegreeResponse response = new DegreeResponse();
        response.id = degree.getId();
        response.name = degree.getName();
        response.status = degree.getStatus().name();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}