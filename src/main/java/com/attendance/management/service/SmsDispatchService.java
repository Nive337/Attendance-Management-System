package com.attendance.management.service;

import com.attendance.management.dto.response.SmsNotificationResponse;
import com.attendance.management.entity.CourseOffering;
import com.attendance.management.entity.SmsNotification;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.CourseOfferingRepository;
import com.attendance.management.repository.SmsNotificationRepository;
import com.attendance.management.service.sms.SmsDispatchResult;
import com.attendance.management.service.sms.SmsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SmsDispatchService {

    private static final Logger log = LoggerFactory.getLogger(SmsDispatchService.class);

    private final SmsNotificationRepository smsNotificationRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseOfferingAccessGuard accessGuard;
    private final SmsProvider smsProvider;

    public SmsDispatchService(SmsNotificationRepository smsNotificationRepository,
                               CourseOfferingRepository courseOfferingRepository,
                               CourseOfferingAccessGuard accessGuard,
                               SmsProvider smsProvider) {
        this.smsNotificationRepository = smsNotificationRepository;
        this.courseOfferingRepository = courseOfferingRepository;
        this.accessGuard = accessGuard;
        this.smsProvider = smsProvider;
    }

    @Transactional(readOnly = true)
    public List<SmsNotificationResponse> listForOffering(Long courseOfferingId) {
        CourseOffering offering = courseOfferingRepository.findById(courseOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Course offering not found: " + courseOfferingId));
        accessGuard.requireOwnership(offering);

        return smsNotificationRepository.findByCourseOfferingId(courseOfferingId).stream()
                .map(SmsNotificationResponse::from)
                .toList();
    }

    // HTTP-exposed: one lecturer, one of their own offerings.
    @Transactional
    public List<SmsNotificationResponse> dispatchForOffering(Long courseOfferingId) {
        CourseOffering offering = courseOfferingRepository.findById(courseOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Course offering not found: " + courseOfferingId));
        accessGuard.requireOwnership(offering);

        List<SmsNotification> pending = smsNotificationRepository.findByCourseOfferingIdAndStatusIn(
                courseOfferingId, List.of(SmsNotification.Status.PENDING, SmsNotification.Status.FAILED));

        return pending.stream().map(this::dispatchOne).map(SmsNotificationResponse::from).toList();
    }

    // Not yet exposed via HTTP - crosses ownership boundaries deliberately, meant
    // for a trusted internal trigger (scheduled job / admin endpoint), same as
    // MonthlyAttendanceNotificationService.generateForAllActiveOfferings().
    @Transactional
    public List<SmsNotificationResponse> dispatchAllPending() {
        List<SmsNotification> pending = smsNotificationRepository.findByStatusIn(
                List.of(SmsNotification.Status.PENDING, SmsNotification.Status.FAILED));

        return pending.stream().map(this::dispatchOne).map(SmsNotificationResponse::from).toList();
    }

    private SmsNotification dispatchOne(SmsNotification notification) {
        SmsDispatchResult result = smsProvider.send(notification.getParentPhone(), notification.getMessage());
        notification.setAttemptCount(notification.getAttemptCount() + 1);

        if (result.success()) {
            notification.setStatus(SmsNotification.Status.SENT);
            notification.setSentAt(LocalDateTime.now());
        } else {
            notification.setStatus(SmsNotification.Status.FAILED);
            log.warn("SMS dispatch failed for notification {} (attempt {}): {}",
                    notification.getId(), notification.getAttemptCount(), result.failureReason());
        }

        return smsNotificationRepository.save(notification);
    }
}