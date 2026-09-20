// dto/response/SubjectResponse.java
package com.attendance.management.dto.response;

import com.attendance.management.entity.Subject;

public class SubjectResponse {

    private Long id;
    private String subjectCode;
    private String subjectName;
    private Long degreeId;
    private String status;

    public static SubjectResponse from(Subject subject) {
        SubjectResponse response = new SubjectResponse();
        response.id = subject.getId();
        response.subjectCode = subject.getSubjectCode();
        response.subjectName = subject.getSubjectName();
        response.degreeId = subject.getDegree().getId(); // safe: reading a lazy proxy's own id never hits the DB
        response.status = subject.getStatus().name();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Long getDegreeId() {
        return degreeId;
    }

    public String getStatus() {
        return status;
    }
}