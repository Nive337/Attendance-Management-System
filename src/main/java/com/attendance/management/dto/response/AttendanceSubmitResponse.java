package com.attendance.management.dto.response;

import java.time.LocalDate;
import java.util.List;

public class AttendanceSubmitResponse {

    private Long sessionId;
    private Long courseOfferingId;
    private LocalDate attendanceDate;
    private Integer sessionNumber;
    private long totalStudents;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private double attendancePercentage;
    private List<RecordSummary> records;

    public AttendanceSubmitResponse(Long sessionId, Long courseOfferingId, LocalDate attendanceDate,
                                     Integer sessionNumber, long totalStudents, long presentCount,
                                     long absentCount, long lateCount, double attendancePercentage,
                                     List<RecordSummary> records) {
        this.sessionId = sessionId;
        this.courseOfferingId = courseOfferingId;
        this.attendanceDate = attendanceDate;
        this.sessionNumber = sessionNumber;
        this.totalStudents = totalStudents;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.lateCount = lateCount;
        this.attendancePercentage = attendancePercentage;
        this.records = records;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getCourseOfferingId() {
        return courseOfferingId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public long getTotalStudents() {
        return totalStudents;
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

    public List<RecordSummary> getRecords() {
        return records;
    }

    public static class RecordSummary {

        private Long enrollmentId;
        private String rollNumber;
        private String name;
        private String status;
        private String note;

        public RecordSummary(Long enrollmentId, String rollNumber, String name, String status, String note) {
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