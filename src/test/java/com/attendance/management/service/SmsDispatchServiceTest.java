package com.attendance.management.service;

import com.attendance.management.dto.response.SmsNotificationResponse;
import com.attendance.management.entity.CourseOffering;
import com.attendance.management.entity.Lecturer;
import com.attendance.management.entity.SmsNotification;
import com.attendance.management.entity.StudentEnrollment;
import com.attendance.management.entity.Student;
import com.attendance.management.repository.CourseOfferingRepository;
import com.attendance.management.repository.SmsNotificationRepository;
import com.attendance.management.service.sms.SmsDispatchResult;
import com.attendance.management.service.sms.SmsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.attendance.management.entity.Subject;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsDispatchServiceTest {

    @Mock
    private SmsNotificationRepository smsNotificationRepository;
    @Mock
    private CourseOfferingRepository courseOfferingRepository;
    @Mock
    private CourseOfferingAccessGuard accessGuard;
    @Mock
    private SmsProvider smsProvider;

    private SmsDispatchService service;
    private CourseOffering offering;

    @BeforeEach
    void setUp() {
        service = new SmsDispatchService(
            smsNotificationRepository,
            courseOfferingRepository,
            accessGuard,
            smsProvider
        );

        offering = new CourseOffering();

        Lecturer lecturer = new Lecturer();
        offering.setLecturer(lecturer);

        Subject subject = new Subject();
        subject.setSubjectName("Data Structures");
        offering.setSubject(subject);
    
        when(courseOfferingRepository.findById(1L))
            .thenReturn(Optional.of(offering));
    }

    private SmsNotification pendingNotification() {
    SmsNotification notification = new SmsNotification();
    notification.setStatus(SmsNotification.Status.PENDING);
    notification.setAttemptCount(0);
    notification.setParentPhone("9876543210");
    notification.setMessage("Dear Parent, ... below 75% ...");

    CourseOffering owningOffering = new CourseOffering();

    Subject subject = new Subject();
    subject.setSubjectName("Data Structures");
    owningOffering.setSubject(subject);

    notification.setCourseOffering(owningOffering);

    StudentEnrollment enrollment = new StudentEnrollment();

    Student student = new Student();
    student.setName("Rahul Verma");

    enrollment.setStudent(student);
    enrollment.setRollNumber("01");

    notification.setStudentEnrollment(enrollment);

    return notification;
}

    @Test
    void successfulSend_transitionsToSent_andSetsTimestamp() {
        SmsNotification notification = pendingNotification();
        when(smsNotificationRepository.findByCourseOfferingIdAndStatusIn(eq(1L), any()))
                .thenReturn(List.of(notification));
        when(smsProvider.send(anyString(), anyString())).thenReturn(SmsDispatchResult.success("provider-ref-123"));

        when(smsNotificationRepository.save(any(SmsNotification.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

        List<SmsNotificationResponse> result = service.dispatchForOffering(1L);

        assertEquals(1, result.size());
        assertEquals("SENT", result.get(0).getStatus());
        assertNotNull(notification.getSentAt());
        assertEquals(1, notification.getAttemptCount());
    }

    @Test
void failedSend_transitionsToFailed_neverSent() {
    SmsNotification notification = pendingNotification();

    when(smsNotificationRepository.findByCourseOfferingIdAndStatusIn(eq(1L), any()))
            .thenReturn(List.of(notification));

    when(smsProvider.send(anyString(), anyString()))
            .thenReturn(SmsDispatchResult.failure("Provider returned status 500"));

    when(smsNotificationRepository.save(any(SmsNotification.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    List<SmsNotificationResponse> result = service.dispatchForOffering(1L);

    assertEquals("FAILED", result.get(0).getStatus());
    assertNull(notification.getSentAt());
    assertEquals(1, notification.getAttemptCount());
}

    @Test
void retryAfterFailure_incrementsAttemptCount() {
    SmsNotification notification = pendingNotification();
    notification.setStatus(SmsNotification.Status.FAILED);
    notification.setAttemptCount(1);

    when(smsNotificationRepository.findByCourseOfferingIdAndStatusIn(eq(1L), any()))
            .thenReturn(List.of(notification));

    when(smsProvider.send(anyString(), anyString()))
            .thenReturn(SmsDispatchResult.success(null));

    when(smsNotificationRepository.save(any(SmsNotification.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    service.dispatchForOffering(1L);

    assertEquals(2, notification.getAttemptCount());
    assertEquals(SmsNotification.Status.SENT, notification.getStatus());
}

    @Test
    void ownershipStillEnforced_beforeAnyProviderCall() {
        doThrow(new org.springframework.security.access.AccessDeniedException("not yours"))
                .when(accessGuard).requireOwnership(offering);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.dispatchForOffering(1L));

        verifyNoInteractions(smsProvider);
    }
}