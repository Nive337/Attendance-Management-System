package com.attendance.management.repository;

import com.attendance.management.entity.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {

    // Powers the Students page cascading filter (Phase 7).
    List<StudentEnrollment> findByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndStatusOrderByRollNumberAsc(
        Long academicYearId, Long degreeId, Integer semester, String section, StudentEnrollment.Status status);

    List<StudentEnrollment> findByStudentIdOrderByAcademicYearIdAscSemesterAsc(Long studentId);

    boolean existsByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndRollNumber(
            Long academicYearId, Long degreeId, Integer semester, String section, String rollNumber);

    boolean existsByAcademicYearIdAndDegreeIdAndSemesterAndSectionAndRollNumberAndIdNot(
        Long academicYearId, Long degreeId, Integer semester, String section, String rollNumber, Long excludeId);
}