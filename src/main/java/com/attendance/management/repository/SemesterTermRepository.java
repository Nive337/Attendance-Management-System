package com.attendance.management.repository;

import com.attendance.management.entity.SemesterTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SemesterTermRepository extends JpaRepository<SemesterTerm, Long> {

    Optional<SemesterTerm> findByAcademicYearIdAndDegreeIdAndSemester(
            Long academicYearId, Long degreeId, Integer semester);
}