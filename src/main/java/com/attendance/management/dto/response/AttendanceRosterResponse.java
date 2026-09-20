package com.attendance.management.dto.response;

import java.time.LocalDate;
import java.util.List;

public class AttendanceRosterResponse {

    private Long courseOfferingId;
    private String subjectName;
    private String subjectCode;
    private String lecturerName;
    private String degreeName;
    private Integer semester;
    private String section;
    private LocalDate attendanceDate;
    private Integer sessionNumber;
    private boolean alreadySubmitted;
    private int totalStudents;
    private List<RosterStudent> students;

    public AttendanceRosterResponse(Long courseOfferingId, String subjectName, String subjectCode,
                                     String lecturerName, String degreeName, Integer semester, String section,
                                     LocalDate attendanceDate, Integer sessionNumber, boolean alreadySubmitted,
                                     List<RosterStudent> students) {
        this.courseOfferingId = courseOfferingId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.lecturerName = lecturerName;
        this.degreeName = degreeName;
        this.semester = semester;
        this.section = section;
        this.attendanceDate = attendanceDate;
        this.sessionNumber = sessionNumber;
        this.alreadySubmitted = alreadySubmitted;
        this.students = students;
        this.totalStudents = students.size();
    }

    public Long getCourseOfferingId() {
        return courseOfferingId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getLecturerName() {
        return lecturerName;
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

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public boolean isAlreadySubmitted() {
        return alreadySubmitted;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public List<RosterStudent> getStudents() {
        return students;
    }

    public static class RosterStudent {

        private Long enrollmentId;
        private String rollNumber;
        private String name;
        private String status;
        private String note;

        public RosterStudent(Long enrollmentId, String rollNumber, String name, String status, String note) {
            this.enrollmentId = enrollmentId;
            this.rollNumber = rollNumber;
            this.name = name;
            this.status = status;
            this.note = note;
        }

        public Long getEnrollmentId() {
            return enrollmentId;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }

        public String getNote() {
            return note;
        }
    }
}