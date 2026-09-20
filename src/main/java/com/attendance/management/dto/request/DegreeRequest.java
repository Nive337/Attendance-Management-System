// dto/request/DegreeRequest.java
package com.attendance.management.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DegreeRequest {

    @NotBlank(message = "Degree name is required")
    private String name;

    public DegreeRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}