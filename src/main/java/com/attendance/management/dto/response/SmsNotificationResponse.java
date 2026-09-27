// dto/response/SmsNotificationResponse.java
package com.attendance.management.dto.response;

import com.attendance.management.entity.SmsNotification;

public class SmsNotificationResponse {

    private Long id;
    private Long studentEnrollmentId;
    private String studentName;
    private String rollNumber;
    private Long courseOfferingId;
    private String subjectName;
    private String subjectCode;
    private Integer notifYear;
    private Integer notifMonth;
    private String parentPhone;
    private String message;
    private String status;

    public static SmsNotificationResponse from(SmsNotification notification) {
        SmsNotificationResponse response = new SmsNotificationResponse();
        response.id = notification.getId();
        response.studentEnrollmentId = notification.getStudentEnrollment().getId();
        response.studentName = notification.getStudentEnrollment().getStudent().getName();
        response.rollNumber = notification.getStudentEnrollment().getRollNumber();
        response.courseOfferingId = notification.getCourseOffering().getId();
        response.subjectName = notification.getCourseOffering().getSubject().getSubjectName();
        response.subjectCode = notification.getCourseOffering().getSubject().getSubjectCode();
        response.notifYear = notification.getNotifYear();
        response.notifMonth = notification.getNotifMonth();
        response.parentPhone = notification.getParentPhone();
        response.message = notification.getMessage();
        response.status = notification.getStatus().name();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentEnrollmentId() {
        return studentEnrollmentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRollNumber() {
        return rollNumber;
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

    public Integer getNotifYear() {
        return notifYear;
    }

    public Integer getNotifMonth() {
        return notifMonth;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}