package com.attendance.management.service;

import com.attendance.management.dto.request.BulkEnrollmentRequest;
import com.attendance.management.dto.request.StudentRowRequest;
import com.attendance.management.dto.response.StudentEnrollmentResponse;
import com.attendance.management.entity.AcademicYear;
import com.attendance.management.entity.CourseOffering;
import com.attendance.management.entity.Degree;
import com.attendance.management.entity.Student;
import com.attendance.management.entity.StudentEnrollment;
import com.attendance.management.exception.DuplicateResourceException;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.AcademicYearRepository;
import com.attendance.management.repository.CourseOfferingRepository;
import com.attendance.management.repository.DegreeRepository;
import com.attendance.management.repository.StudentEnrollmentRepository;
import com.attendance.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.attendance.management.dto.request.PromotionRequest;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StudentEnrollmentService {

    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final StudentRepository studentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final DegreeRepository degreeRepository;
    private final CourseOfferingRepository courseOfferingRepository;

    public StudentEnrollmentService(StudentEnrollmentRepository studentEnrollmentRepository,
                                     StudentRepository studentRepository,
                                     AcademicYearRepository academicYearRepository,
                                     DegreeRepository degreeRepository,
                                     CourseOfferingRepository courseOfferingRepository) {
        this.studentEnrollmentRepository = studentEnrollmentRepository;
        this.studentRepository = studentRepository;
        this.academicYearRepository = academicYearRepository;
        this.degreeRepository = degreeRepository;
        this.courseOfferingRepository = courseOfferingRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentEnrollmentResponse> getRoster(Long courseOfferingId) {
        CourseOffering offering = courseOfferingRepository.findById(courseOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Course offering not found: " + courseOfferingId));

        List<StudentEnrollment> enrollments = studentEnrollmentRepository
                .findByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndStatusOrderByRollNumberAsc(
                        offering.getAcademicYear().getId(), offering.getDegree().getId(),
                        offering.getSemester(), offering.getSection(), StudentEnrollment.Status.ACTIVE);

        return enrollments.stream().map(StudentEnrollmentResponse::from).toList();
    }

    @Transactional
    public List<StudentEnrollmentResponse> bulkCreate(BulkEnrollmentRequest request) {
        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic year not found: " + request.getAcademicYearId()));
        if (academicYear.getStatus() != AcademicYear.Status.ACTIVE) {
            throw new IllegalArgumentException("Cannot enroll students into an archived academic year.");
        }

        Degree degree = degreeRepository.findById(request.getDegreeId())
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found: " + request.getDegreeId()));
        if (degree.getStatus() != Degree.Status.ACTIVE) {
            throw new IllegalArgumentException("Cannot enroll students into an inactive degree.");
        }

        String section = request.getSection().trim().toUpperCase();

        // Reject duplicate roll numbers within the submitted batch itself.
        Set<String> rollNumbersInBatch = new HashSet<>();
        List<String> inBatchDuplicates = new ArrayList<>();
        for (StudentRowRequest row : request.getStudents()) {
            String rollNumber = row.getRollNumber().trim();
            if (!rollNumbersInBatch.add(rollNumber)) {
                inBatchDuplicates.add(rollNumber);
            }
        }
        if (!inBatchDuplicates.isEmpty()) {
            throw new DuplicateResourceException(
                    "Duplicate roll numbers within the submitted list: " + String.join(", ", inBatchDuplicates));
        }

        // Reject roll numbers already used in this exact year/degree/semester/section.
        List<String> existingConflicts = new ArrayList<>();
        for (String rollNumber : rollNumbersInBatch) {
            boolean exists = studentEnrollmentRepository
                    .existsByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndRollNumber(
                            academicYear.getId(), degree.getId(), request.getSemester(), section, rollNumber);
            if (exists) {
                existingConflicts.add(rollNumber);
            }
        }
        if (!existingConflicts.isEmpty()) {
            throw new DuplicateResourceException(
                    "Roll numbers already used in this class: " + String.join(", ", existingConflicts));
        }

        // All clear - create a new Student + StudentEnrollment for every row.
        // This path is for NEW intake only; re-enrolling a continuing student
        // into their next semester is Phase 8's job, and reuses their existing Student row.
        List<StudentEnrollmentResponse> results = new ArrayList<>();
        for (StudentRowRequest row : request.getStudents()) {
            Student student = new Student();
            student.setName(row.getName().trim());
            student.setParentName(row.getParentName().trim());
            student.setParentPhone(row.getParentPhone().trim());
            student = studentRepository.save(student);

            StudentEnrollment enrollment = new StudentEnrollment();
            enrollment.setStudent(student);
            enrollment.setAcademicYear(academicYear);
            enrollment.setDegree(degree);
            enrollment.setSemester(request.getSemester());
            enrollment.setSection(section);
            enrollment.setRollNumber(row.getRollNumber().trim());
            enrollment.setStatus(StudentEnrollment.Status.ACTIVE);
            enrollment = studentEnrollmentRepository.save(enrollment);

            results.add(StudentEnrollmentResponse.from(enrollment));
        }

        return results;
    }

    @Transactional
    public StudentEnrollmentResponse update(Long enrollmentId, StudentRowRequest request) {
        StudentEnrollment enrollment = studentEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        String rollNumber = request.getRollNumber().trim();
        boolean duplicate = studentEnrollmentRepository
                .existsByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndRollNumberAndIdNot(
                        enrollment.getAcademicYear().getId(), enrollment.getDegree().getId(),
                        enrollment.getSemester(), enrollment.getSection(), rollNumber, enrollmentId);
        if (duplicate) {
            throw new DuplicateResourceException("Roll number '" + rollNumber + "' is already used in this class.");
        }

        Student student = enrollment.getStudent();
        student.setName(request.getName().trim());
        student.setParentName(request.getParentName().trim());
        student.setParentPhone(request.getParentPhone().trim());
        studentRepository.save(student);

        enrollment.setRollNumber(rollNumber);
        return StudentEnrollmentResponse.from(studentEnrollmentRepository.save(enrollment));
    }

    @Transactional
    public StudentEnrollmentResponse updateStatus(Long enrollmentId, String statusValue) {
        StudentEnrollment enrollment = studentEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        StudentEnrollment.Status status;
        try {
            status = StudentEnrollment.Status.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Status must be one of: ACTIVE, COMPLETED, WITHDRAWN");
        }

        enrollment.setStatus(status);
        return StudentEnrollmentResponse.from(studentEnrollmentRepository.save(enrollment));
    }
    @Transactional
    public List<StudentEnrollmentResponse> promote(PromotionRequest request) {

        AcademicYear targetYear = academicYearRepository.findById(request.getTargetAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic year not found: " + request.getTargetAcademicYearId()));
        if (targetYear.getStatus() != AcademicYear.Status.ACTIVE) {
            throw new IllegalArgumentException("Cannot promote students into an archived academic year.");
        }

        String targetSection = request.getTargetSection().trim().toUpperCase();

        List<StudentEnrollment> sourceEnrollments =
                studentEnrollmentRepository.findAllById(request.getEnrollmentIds());
        if (sourceEnrollments.size() != request.getEnrollmentIds().size()) {
            throw new ResourceNotFoundException("One or more enrollment ids were not found.");
        }

    // All selected enrollments must be ACTIVE and share one degree - a mixed-degree
    // selection in a single promotion call is almost certainly a mistake, not intent.
        Long degreeId = sourceEnrollments.get(0).getDegree().getId();
        for (StudentEnrollment enrollment : sourceEnrollments) {
            if (enrollment.getStatus() != StudentEnrollment.Status.ACTIVE) {
                throw new IllegalArgumentException(
                        "Enrollment " + enrollment.getId() + " is not ACTIVE and cannot be promoted.");
            }
            if (!enrollment.getDegree().getId().equals(degreeId)) {
                throw new IllegalArgumentException("All selected enrollments must belong to the same degree.");
            }
        }
        Degree degree = sourceEnrollments.get(0).getDegree();

        Map<Long, String> overrides = new HashMap<>();
        if (request.getRollNumberOverrides() != null) {
            for (PromotionRequest.RollNumberOverride override : request.getRollNumberOverrides()) {
                overrides.put(override.getEnrollmentId(), override.getRollNumber().trim());
            }
        }

    // Resolve each student's target roll number (override, or carry the old one forward)
    // and check for conflicts before writing anything.
        Map<Long, String> resolvedRollNumbers = new HashMap<>();
        Set<String> rollNumbersInBatch = new HashSet<>();
        List<String> inBatchDuplicates = new ArrayList<>();
        for (StudentEnrollment enrollment : sourceEnrollments) {
            String rollNumber = overrides.getOrDefault(enrollment.getId(), enrollment.getRollNumber());
            resolvedRollNumbers.put(enrollment.getId(), rollNumber);
            if (!rollNumbersInBatch.add(rollNumber)) {
                inBatchDuplicates.add(rollNumber);
            }
        }
        if (!inBatchDuplicates.isEmpty()) {
            throw new DuplicateResourceException(
                    "Duplicate target roll numbers in this promotion batch: " + String.join(", ", inBatchDuplicates));
        }

        List<String> existingConflicts = new ArrayList<>();
        for (String rollNumber : rollNumbersInBatch) {
            boolean exists = studentEnrollmentRepository
                    .existsByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndRollNumber(
                            targetYear.getId(), degree.getId(), request.getTargetSemester(), targetSection, rollNumber);
            if (exists) {
                existingConflicts.add(rollNumber);
            }
        }
        if (!existingConflicts.isEmpty()) {
            throw new DuplicateResourceException(
                    "Roll numbers already used in the target class: " + String.join(", ", existingConflicts));
        }

    // Validated - close out the old enrollments and open the new ones.
        List<StudentEnrollmentResponse> results = new ArrayList<>();
        for (StudentEnrollment oldEnrollment : sourceEnrollments) {
            oldEnrollment.setStatus(StudentEnrollment.Status.COMPLETED);
            studentEnrollmentRepository.save(oldEnrollment);

            StudentEnrollment newEnrollment = new StudentEnrollment();
            newEnrollment.setStudent(oldEnrollment.getStudent());
            newEnrollment.setAcademicYear(targetYear);
            newEnrollment.setDegree(degree);
            newEnrollment.setSemester(request.getTargetSemester());
            newEnrollment.setSection(targetSection);
            newEnrollment.setRollNumber(resolvedRollNumbers.get(oldEnrollment.getId()));
            newEnrollment.setStatus(StudentEnrollment.Status.ACTIVE);
            newEnrollment = studentEnrollmentRepository.save(newEnrollment);

            results.add(StudentEnrollmentResponse.from(newEnrollment));
        }

        return results;
    }
}