package com.attendance.management.repository;

import com.attendance.management.entity.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CourseOfferingRepository
        extends JpaRepository<CourseOffering, Long>, JpaSpecificationExecutor<CourseOffering> {

    List<CourseOffering> findByLecturerIdAndStatus(Long lecturerId, CourseOffering.Status status);

    List<CourseOffering> findByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndStatus(
            Long academicYearId, Long degreeId, Integer semester, String section, CourseOffering.Status status);

    boolean existsByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndSubjectId(
            Long academicYearId, Long degreeId, Integer semester, String section, Long subjectId);

    boolean existsByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndSubjectIdAndIdNot(
            Long academicYearId, Long degreeId, Integer semester, String section, Long subjectId, Long excludeId);

    List<CourseOffering> findByStatus(CourseOffering.Status status);
}