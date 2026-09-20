package com.attendance.management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BulkEnrollmentRequest {

    @NotNull(message = "Academic year is required")
    private Long academicYearId;

    @NotNull(message = "Degree is required")
    private Long degreeId;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 12, message = "Semester must be at most 12")
    private Integer semester;

    @NotBlank(message = "Section is required")
    private String section;

    @NotEmpty(message = "At least one student row is required")
    @Valid
    private List<StudentRowRequest> students;

    public BulkEnrollmentRequest() {
    }

    public Long getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(Long academicYearId) {
        this.academicYearId = academicYearId;
    }

    public Long getDegreeId() {
        return degreeId;
    }

    public void setDegreeId(Long degreeId) {
        this.degreeId = degreeId;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<StudentRowRequest> getStudents() {
        return students;
    }

    public void setStudents(List<StudentRowRequest> students) {
        this.students = students;
    }
}