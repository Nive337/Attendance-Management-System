package com.attendance.management.repository;

import com.attendance.management.entity.SmsNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SmsNotificationRepository extends JpaRepository<SmsNotification, Long> {

    // The idempotency check from Phase 11: one warning per student per month.
    Optional<SmsNotification> findByStudentEnrollmentIdAndCourseOfferingIdAndNotifYearAndNotifMonth(
        Long studentEnrollmentId, Long courseOfferingId, Integer notifYear, Integer notifMonth);

    List<SmsNotification> findByCourseOfferingId(Long courseOfferingId);

    List<SmsNotification> findByCourseOfferingIdAndStatusIn(Long courseOfferingId, List<SmsNotification.Status> statuses);

    List<SmsNotification> findByStatusIn(List<SmsNotification.Status> statuses);
}