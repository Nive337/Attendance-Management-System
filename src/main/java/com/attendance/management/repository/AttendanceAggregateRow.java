package com.attendance.management.repository;

// Spring Data interface projection - getter names below must match the
// "AS alias" names in AttendanceRecordRepository.aggregateByCourseOffering().
public interface AttendanceAggregateRow {

    Long getEnrollmentId();

    Long getTotalClasses();

    Long getPresentCount();

    Long getAbsentCount();

    Long getLateCount();
}