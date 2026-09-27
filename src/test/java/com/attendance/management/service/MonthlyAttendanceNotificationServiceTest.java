package com.attendance.management.service;

import com.attendance.management.dto.response.SmsNotificationResponse;
import com.attendance.management.entity.*;
import com.attendance.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // not every test exercises every shared stub
class MonthlyAttendanceNotificationServiceTest {

    @Mock
    private CourseOfferingRepository courseOfferingRepository;
    @Mock
    private SemesterTermRepository semesterTermRepository;
    @Mock
    private StudentEnrollmentRepository studentEnrollmentRepository;
    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;
    @Mock
    private SmsNotificationRepository smsNotificationRepository;
    @Mock
    private CourseOfferingAccessGuard accessGuard;

    private MonthlyAttendanceNotificationService service;

    private CourseOffering offering;
    private StudentEnrollment rahulEnrollment;

    @BeforeEach
    void setUp() {
        service = new MonthlyAttendanceNotificationService(
                courseOfferingRepository, semesterTermRepository, studentEnrollmentRepository,
                attendanceRecordRepository, smsNotificationRepository, accessGuard);

        AcademicYear year = new AcademicYear();
        year.setYearLabel("2026");

        Degree degree = new Degree();
        degree.setName("BCom");

        Subject subject = new Subject();
        subject.setSubjectCode("BCOM-DBMS");
        subject.setSubjectName("Database Management Systems");
        subject.setDegree(degree);

        Lecturer lecturer = new Lecturer();
        lecturer.setName("Priya Sharma");

        offering = new CourseOffering();
        offering.setAcademicYear(year);
        offering.setDegree(degree);
        offering.setSemester(3);
        offering.setSection("A");
        offering.setSubject(subject);
        offering.setLecturer(lecturer);
        offering.setStatus(CourseOffering.Status.ACTIVE);

        SemesterTerm term = new SemesterTerm();
        term.setAcademicYear(year);
        term.setDegree(degree);
        term.setSemester(3);
        term.setStartDate(LocalDate.of(2026, 7, 12));
        term.setEndDate(LocalDate.of(2026, 12, 15));

        Student rahul = new Student();
        rahul.setName("Rahul Verma");
        rahul.setParentPhone("9876543210");

        rahulEnrollment = new StudentEnrollment();
        rahulEnrollment.setStudent(rahul);
        rahulEnrollment.setAcademicYear(year);
        rahulEnrollment.setDegree(degree);
        rahulEnrollment.setSemester(3);
        rahulEnrollment.setSection("A");
        rahulEnrollment.setRollNumber("01");
        rahulEnrollment.setStatus(StudentEnrollment.Status.ACTIVE);

        when(courseOfferingRepository.findById(1L)).thenReturn(Optional.of(offering));
        when(semesterTermRepository.findByAcademicYearIdAndDegreeIdAndSemester(any(), any(), eq(3)))
                .thenReturn(Optional.of(term));
        when(studentEnrollmentRepository
                .findByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndStatusOrderByRollNumberAsc(
                        any(), any(), eq(3), eq("A"), eq(StudentEnrollment.Status.ACTIVE)))
                .thenReturn(List.of(rahulEnrollment));
    }

    @Test
    void julyExample_eightyPercent_generatesNoNotification() {
        stubAggregate(5, 4);

        List<SmsNotificationResponse> result = service.generateForOffering(1L, 2026, 7);

        assertTrue(result.isEmpty());
        verify(smsNotificationRepository, never()).save(any());
        verifyAggregateCalledWithWindow(LocalDate.of(2026, 7, 12), LocalDate.of(2026, 7, 31));
    }

    @Test
    void augustExample_seventyPercent_generatesNotification_cumulativeFromSemesterStart() {
        stubAggregate(10, 7);
        when(smsNotificationRepository.findByStudentEnrollmentIdAndCourseOfferingIdAndNotifYearAndNotifMonth(
                any(), any(), eq(2026), eq(8))).thenReturn(Optional.empty());
        when(smsNotificationRepository.save(any(SmsNotification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<SmsNotificationResponse> result = service.generateForOffering(1L, 2026, 8);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getMessage().contains("70%"));
        assertTrue(result.get(0).getMessage().contains("12 July 2026 to 31 August 2026"));
        // Cumulative window starts at the semester start date, NOT 1 August.
        verifyAggregateCalledWithWindow(LocalDate.of(2026, 7, 12), LocalDate.of(2026, 8, 31));
    }

    @Test
    void septemberExample_updatesExistingPendingNotification_doesNotDuplicate() {
        stubAggregate(15, 10);

        SmsNotification existingPending = new SmsNotification();
        existingPending.setStatus(SmsNotification.Status.PENDING);
        existingPending.setStudentEnrollment(rahulEnrollment);
        existingPending.setCourseOffering(offering);
        when(smsNotificationRepository.findByStudentEnrollmentIdAndCourseOfferingIdAndNotifYearAndNotifMonth(
                any(), any(), eq(2026), eq(9))).thenReturn(Optional.of(existingPending));
        when(smsNotificationRepository.save(any(SmsNotification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<SmsNotificationResponse> result = service.generateForOffering(1L, 2026, 9);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getMessage().contains("66.67%"));
        verify(smsNotificationRepository, times(1)).save(existingPending);
        verifyAggregateCalledWithWindow(LocalDate.of(2026, 7, 12), LocalDate.of(2026, 9, 30));
    }

    @Test
    void alreadySentNotification_isNeverOverwritten() {
        stubAggregate(15, 10);

        SmsNotification alreadySent = new SmsNotification();
        alreadySent.setStatus(SmsNotification.Status.SENT);
        when(smsNotificationRepository.findByStudentEnrollmentIdAndCourseOfferingIdAndNotifYearAndNotifMonth(
                any(), any(), eq(2026), eq(9))).thenReturn(Optional.of(alreadySent));

        List<SmsNotificationResponse> result = service.generateForOffering(1L, 2026, 9);

        assertTrue(result.isEmpty());
        verify(smsNotificationRepository, never()).save(any());
    }

    @Test
    void noAttendanceRecordsYet_isNoData_doesNotGenerateWarning() {
        when(attendanceRecordRepository.aggregateByCourseOffering(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        List<SmsNotificationResponse> result = service.generateForOffering(1L, 2026, 7);

        assertTrue(result.isEmpty());
        verify(smsNotificationRepository, never()).save(any());
    }

    @Test
    void monthAfterSemesterEnd_generatesNothing() {
        List<SmsNotificationResponse> result = service.generateForOffering(1L, 2027, 3); // semester ended 2026-12-15

        assertTrue(result.isEmpty());
        verifyNoInteractions(attendanceRecordRepository);
    }

    @Test
    void lecturerNotAssignedToOffering_isRejected() {
        doThrow(new AccessDeniedException("You are not assigned to this course offering."))
                .when(accessGuard).requireOwnership(offering);

        assertThrows(AccessDeniedException.class, () -> service.generateForOffering(1L, 2026, 7));
    }

    private void stubAggregate(long total, long present) {
        AttendanceAggregateRow row = mock(AttendanceAggregateRow.class);
        when(row.getEnrollmentId()).thenReturn(rahulEnrollment.getId());
        when(row.getTotalClasses()).thenReturn(total);
        when(row.getPresentCount()).thenReturn(present);
        when(row.getAbsentCount()).thenReturn(total - present);
        when(row.getLateCount()).thenReturn(0L);
        when(attendanceRecordRepository.aggregateByCourseOffering(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(row));
    }

    private void verifyAggregateCalledWithWindow(LocalDate expectedStart, LocalDate expectedEnd) {
        verify(attendanceRecordRepository).aggregateByCourseOffering(
                any(), eq(expectedStart), eq(expectedEnd), any(), any(), any());
    }
}