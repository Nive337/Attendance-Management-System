package com.attendance.management.dto.response;

import java.time.LocalDate;
import java.util.List;

public class AttendanceReportResponse {

    private Long courseOfferingId;
    private String subjectName;
    private String subjectCode;
    private String lecturerName;
    private String degreeName;
    private Integer semester;
    private String section;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReportSummary summary;
    private List<StudentReportRow> students;

    public AttendanceReportResponse(Long courseOfferingId, String subjectName, String subjectCode,
                                     String lecturerName, String degreeName, Integer semester, String section,
                                     LocalDate startDate, LocalDate endDate, ReportSummary summary,
                                     List<StudentReportRow> students) {
        this.courseOfferingId = courseOfferingId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.lecturerName = lecturerName;
        this.degreeName = degreeName;
        this.semester = semester;
        this.section = section;
        this.startDate = startDate;
        this.endDate = endDate;
        this.summary = summary;
        this.students = students;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ReportSummary getSummary() {
        return summary;
    }

    public List<StudentReportRow> getStudents() {
        return students;
    }

    public static class StudentReportRow {

        private Long enrollmentId;
        private String rollNumber;
        private String name;
        private long totalClasses;
        private long presentCount;
        private long absentCount;
        private long lateCount;
        private double attendancePercentage;
        private String status; // GOOD | WARNING | NO_DATA

        public StudentReportRow(Long enrollmentId, String rollNumber, String name, long totalClasses,
                                 long presentCount, long absentCount, long lateCount,
                                 double attendancePercentage, String status) {
            this.enrollmentId = enrollmentId;
            this.rollNumber = rollNumber;
            this.name = name;
            this.totalClasses = totalClasses;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.lateCount = lateCount;
            this.attendancePercentage = attendancePercentage;
            this.status = status;
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

        public String getStatus() {
            return status;
        }
    }

    public static class ReportSummary {

        private int totalStudents;
        private int studentsAboveThreshold;
        private int studentsBelowThreshold;
        private int studentsWithNoData;
        private long totalSessionsConducted;
        private long totalAttendanceRecords;
        private double averageAttendancePercentage;

        public ReportSummary(int totalStudents, int studentsAboveThreshold, int studentsBelowThreshold,
                              int studentsWithNoData, long totalSessionsConducted, long totalAttendanceRecords,
                              double averageAttendancePercentage) {
            this.totalStudents = totalStudents;
            this.studentsAboveThreshold = studentsAboveThreshold;
            this.studentsBelowThreshold = studentsBelowThreshold;
            this.studentsWithNoData = studentsWithNoData;
            this.totalSessionsConducted = totalSessionsConducted;
            this.totalAttendanceRecords = totalAttendanceRecords;
            this.averageAttendancePercentage = averageAttendancePercentage;
        }

        public int getTotalStudents() {
            return totalStudents;
        }

        public int getStudentsAboveThreshold() {
            return studentsAboveThreshold;
        }

        public int getStudentsBelowThreshold() {
            return studentsBelowThreshold;
        }

        public int getStudentsWithNoData() {
            return studentsWithNoData;
        }

        public long getTotalSessionsConducted() {
            return totalSessionsConducted;
        }

        public long getTotalAttendanceRecords() {
            return totalAttendanceRecords;
        }

        public double getAverageAttendancePercentage() {
            return averageAttendancePercentage;
        }
    }
}