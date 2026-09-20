package com.attendance.management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PromotionRequest {

    @NotEmpty(message = "At least one enrollment id is required")
    private List<Long> enrollmentIds;

    @NotNull(message = "Target academic year is required")
    private Long targetAcademicYearId;

    @NotNull(message = "Target semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 12, message = "Semester must be at most 12")
    private Integer targetSemester;

    @NotBlank(message = "Target section is required")
    private String targetSection;

    // Optional: override the carried-over roll number for specific students
    // (e.g. a section reshuffle). Anyone not listed here keeps their old roll number.
    @Valid
    private List<RollNumberOverride> rollNumberOverrides;

    public PromotionRequest() {
    }

    public List<Long> getEnrollmentIds() {
        return enrollmentIds;
    }

    public void setEnrollmentIds(List<Long> enrollmentIds) {
        this.enrollmentIds = enrollmentIds;
    }

    public Long getTargetAcademicYearId() {
        return targetAcademicYearId;
    }

    public void setTargetAcademicYearId(Long targetAcademicYearId) {
        this.targetAcademicYearId = targetAcademicYearId;
    }

    public Integer getTargetSemester() {
        return targetSemester;
    }

    public void setTargetSemester(Integer targetSemester) {
        this.targetSemester = targetSemester;
    }

    public String getTargetSection() {
        return targetSection;
    }

    public void setTargetSection(String targetSection) {
        this.targetSection = targetSection;
    }

    public List<RollNumberOverride> getRollNumberOverrides() {
        return rollNumberOverrides;
    }

    public void setRollNumberOverrides(List<RollNumberOverride> rollNumberOverrides) {
        this.rollNumberOverrides = rollNumberOverrides;
    }

    public static class RollNumberOverride {

        @NotNull(message = "Enrollment id is required for a roll number override")
        private Long enrollmentId;

        @NotBlank(message = "Roll number is required for a roll number override")
        private String rollNumber;

        public RollNumberOverride() {
        }

        public Long getEnrollmentId() {
            return enrollmentId;
        }

        public void setEnrollmentId(Long enrollmentId) {
            this.enrollmentId = enrollmentId;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }
    }
}