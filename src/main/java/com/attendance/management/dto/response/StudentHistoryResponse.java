package com.attendance.management.dto.response;

import java.util.List;

public class StudentHistoryResponse {

    private Long studentId;
    private String name;
    private String parentName;
    private String parentPhone;
    private List<EnrollmentHistoryEntry> enrollments;

    public StudentHistoryResponse() {
    }

    public StudentHistoryResponse(Long studentId, String name, String parentName, String parentPhone,
                                   List<EnrollmentHistoryEntry> enrollments) {
        this.studentId = studentId;
        this.name = name;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.enrollments = enrollments;
    }

    public Long getStudentId() {
        return studentId;
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

    public List<EnrollmentHistoryEntry> getEnrollments() {
        return enrollments;
    }

    public static class EnrollmentHistoryEntry {

        private Long enrollmentId;
        private String academicYearLabel;
        private String degreeName;
        private Integer semester;
        private String section;
        private String rollNumber;
        private String status;
        private long totalClasses;
        private long presentCount;
        private long absentCount;
        private long lateCount;
        private double attendancePercentage;

        public EnrollmentHistoryEntry(Long enrollmentId, String academicYearLabel, String degreeName,
                                       Integer semester, String section, String rollNumber, String status,
                                       long totalClasses, long presentCount, long absentCount, long lateCount,
                                       double attendancePercentage) {
            this.enrollmentId = enrollmentId;
            this.academicYearLabel = academicYearLabel;
            this.degreeName = degreeName;
            this.semester = semester;
            this.section = section;
            this.rollNumber = rollNumber;
            this.status = status;
            this.totalClasses = totalClasses;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.lateCount = lateCount;
            this.attendancePercentage = attendancePercentage;
        }

        public Long getEnrollmentId() {
            return enrollmentId;
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

        public String getRollNumber() {
            return rollNumber;
        }

        public String getStatus() {
            return status;
        }

        public long getTotalClasses() {
            return totalClasses;
        }

        public long getPresentCount() {
            return presentCount;
        }

        public long getAbsentCount() {
            return absentCount;
        }

        public long getLateCount() {
            return lateCount;
        }

        public double getAttendancePercentage() {
            return attendancePercentage;
        }
    }
}