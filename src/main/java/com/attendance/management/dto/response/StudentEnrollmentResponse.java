package com.attendance.management.dto.response;

import com.attendance.management.entity.StudentEnrollment;

public class StudentEnrollmentResponse {

    private Long enrollmentId;
    private Long studentId;
    private String rollNumber;
    private String name;
    private String parentName;
    private String parentPhone;
    private String academicYearLabel;
    private String degreeName;
    private Integer semester;
    private String section;
    private String status;

    // Called only from within an @Transactional service method - reads full
    // field values off lazy-loaded student/academicYear/degree associations.
    public static StudentEnrollmentResponse from(StudentEnrollment enrollment) {
        StudentEnrollmentResponse response = new StudentEnrollmentResponse();
        response.enrollmentId = enrollment.getId();
        response.studentId = enrollment.getStudent().getId();
        response.rollNumber = enrollment.getRollNumber();
        response.name = enrollment.getStudent().getName();
        response.parentName = enrollment.getStudent().getParentName();
        response.parentPhone = enrollment.getStudent().getParentPhone();
        response.academicYearLabel = enrollment.getAcademicYear().getYearLabel();
        response.degreeName = enrollment.getDegree().getName();
        response.semester = enrollment.getSemester();
        response.section = enrollment.getSection();
        response.status = enrollment.getStatus().name();
        return response;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getName() {
        return name;
    }

    public String getParentName() {
        return parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public String getAcademicYearLabel() {
        return academicYearLabel;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public Integer getSemester() {
        return semester;
    }

    public String getSection() {
        return section;
    }

    public String getStatus() {
        return status;
    }
}