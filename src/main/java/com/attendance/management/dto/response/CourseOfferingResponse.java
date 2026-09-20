// dto/response/CourseOfferingResponse.java
package com.attendance.management.dto.response;

import com.attendance.management.entity.CourseOffering;

public class CourseOfferingResponse {

    private Long id;
    private Long academicYearId;
    private String academicYearLabel;
    private Long degreeId;
    private String degreeName;
    private Integer semester;
    private String section;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private Long lecturerId;
    private String lecturerName;
    private String status;

    // Called only from within an @Transactional service method, since it reads
    // full field values (not just ids) off lazy-loaded year/degree/subject/lecturer.
    public static CourseOfferingResponse from(CourseOffering offering) {
        CourseOfferingResponse response = new CourseOfferingResponse();
        response.id = offering.getId();
        response.academicYearId = offering.getAcademicYear().getId();
        response.academicYearLabel = offering.getAcademicYear().getYearLabel();
        response.degreeId = offering.getDegree().getId();
        response.degreeName = offering.getDegree().getName();
        response.semester = offering.getSemester();
        response.section = offering.getSection();
        response.subjectId = offering.getSubject().getId();
        response.subjectCode = offering.getSubject().getSubjectCode();
        response.subjectName = offering.getSubject().getSubjectName();
        response.lecturerId = offering.getLecturer().getId();
        response.lecturerName = offering.getLecturer().getName();
        response.status = offering.getStatus().name();
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

    public String getSection() {
        return section;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Long getLecturerId() {
        return lecturerId;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public String getStatus() {
        return status;
    }
}