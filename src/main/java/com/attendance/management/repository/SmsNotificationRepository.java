package com.attendance.management.repository;

import com.attendance.management.entity.SmsNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsNotificationRepository extends JpaRepository<SmsNotification, Long> {

    // The idempotency check from Phase 11: one warning per student per month.
    Optional<SmsNotification> findByStudentEnrollmentIdAndNotifYearAndNotifMonth(
            Long studentEnrollmentId, Integer notifYear, Integer notifMonth);
}