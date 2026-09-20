// service/CourseOfferingService.java
package com.attendance.management.service;

import com.attendance.management.dto.request.CourseOfferingRequest;
import com.attendance.management.dto.response.CourseOfferingResponse;
import com.attendance.management.entity.AcademicYear;
import com.attendance.management.entity.CourseOffering;
import com.attendance.management.entity.Degree;
import com.attendance.management.entity.Lecturer;
import com.attendance.management.entity.Subject;
import com.attendance.management.exception.DuplicateResourceException;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.AcademicYearRepository;
import com.attendance.management.repository.CourseOfferingRepository;
import com.attendance.management.repository.DegreeRepository;
import com.attendance.management.repository.LecturerRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseOfferingService {

    private final CourseOfferingRepository courseOfferingRepository;
    private final AcademicYearRepository academicYearRepository;
    private final DegreeRepository degreeRepository;
    private final LecturerRepository lecturerRepository;
    private final SubjectService subjectService;

    public CourseOfferingService(CourseOfferingRepository courseOfferingRepository,
                                  AcademicYearRepository academicYearRepository,
                                  DegreeRepository degreeRepository,
                                  LecturerRepository lecturerRepository,
                                  SubjectService subjectService) {
        this.courseOfferingRepository = courseOfferingRepository;
        this.academicYearRepository = academicYearRepository;
        this.degreeRepository = degreeRepository;
        this.lecturerRepository = lecturerRepository;
        this.subjectService = subjectService;
    }

    @Transactional(readOnly = true)
    public List<CourseOfferingResponse> search(Long academicYearId, Long degreeId, Integer semester,
                                                String section, Long lecturerId, CourseOffering.Status status,
                                                String search) {
        Specification<CourseOffering> spec = CourseOfferingSpecifications.withFilters(
                academicYearId, degreeId, semester, section, lecturerId, status, search);

        return courseOfferingRepository.findAll(spec).stream()
                .map(CourseOfferingResponse::from)
                .toList();
    }

    @Transactional
    public CourseOfferingResponse create(CourseOfferingRequest request) {
        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found: " + request.getAcademicYearId()));
        Degree degree = degreeRepository.findById(request.getDegreeId())
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found: " + request.getDegreeId()));
        Lecturer lecturer = lecturerRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found: " + request.getLecturerId()));

        Subject subject = subjectService.findOrCreate(degree, request.getSubjectCode(), request.getSubjectName());

        boolean duplicate = courseOfferingRepository.existsByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndSubjectId(
                academicYear.getId(), degree.getId(), request.getSemester(), request.getSection(), subject.getId());
        if (duplicate) {
            throw new DuplicateResourceException(
                    "A course offering already exists for this year, degree, semester, section, and subject.");
        }

        CourseOffering offering = new CourseOffering();
        offering.setAcademicYear(academicYear);
        offering.setDegree(degree);
        offering.setSemester(request.getSemester());
        offering.setSection(request.getSection().toUpperCase());
        offering.setSubject(subject);
        offering.setLecturer(lecturer);
        offering.setStatus(CourseOffering.Status.ACTIVE);

        return CourseOfferingResponse.from(courseOfferingRepository.save(offering));
    }

    @Transactional
    public CourseOfferingResponse update(Long id, CourseOfferingRequest request) {
        CourseOffering offering = courseOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course offering not found: " + id));

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found: " + request.getAcademicYearId()));
        Degree degree = degreeRepository.findById(request.getDegreeId())
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found: " + request.getDegreeId()));
        Lecturer lecturer = lecturerRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found: " + request.getLecturerId()));
        Subject subject = subjectService.findOrCreate(degree, request.getSubjectCode(), request.getSubjectName());

        boolean duplicate = courseOfferingRepository
                .existsByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndSubjectIdAndIdNot(
                        academicYear.getId(), degree.getId(), request.getSemester(), request.getSection(),
                        subject.getId(), id);
        if (duplicate) {
            throw new DuplicateResourceException(
                    "Another course offering already exists for this year, degree, semester, section, and subject.");
        }

        offering.setAcademicYear(academicYear);
        offering.setDegree(degree);
        offering.setSemester(request.getSemester());
        offering.setSection(request.getSection().toUpperCase());
        offering.setSubject(subject);
        offering.setLecturer(lecturer);

        return CourseOfferingResponse.from(courseOfferingRepository.save(offering));
    }

    @Transactional
    public CourseOfferingResponse updateStatus(Long id, String statusValue) {
        CourseOffering offering = courseOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course offering not found: " + id));

        CourseOffering.Status status;
        try {
            status = CourseOffering.Status.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Status must be one of: ACTIVE, ARCHIVED");
        }

        offering.setStatus(status);
        return CourseOfferingResponse.from(courseOfferingRepository.save(offering));
    }
}