// dto/response/SemesterTermResponse.java
package com.attendance.management.dto.response;

import com.attendance.management.entity.SemesterTerm;

import java.time.LocalDate;

public class SemesterTermResponse {

    private Long id;
    private Long academicYearId;
    private String academicYearLabel;
    private Long degreeId;
    private String degreeName;
    private Integer semester;
    private LocalDate startDate;
    private LocalDate endDate;

    public static SemesterTermResponse from(SemesterTerm term) {
        SemesterTermResponse response = new SemesterTermResponse();
        response.id = term.getId();
        response.academicYearId = term.getAcademicYear().getId();
        response.academicYearLabel = term.getAcademicYear().getYearLabel();
        response.degreeId = term.getDegree().getId();
        response.degreeName = term.getDegree().getName();
        response.semester = term.getSemester();
        response.startDate = term.getStartDate();
        response.endDate = term.getEndDate();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getAcademicYearId() {
        return academicYearId;
    }

    public String getAcademicYearLabel() {
        return academicYearLabel;
    }

    public Long getDegreeId() {
        return degreeId;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public Integer getSemester() {
        return semester;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}