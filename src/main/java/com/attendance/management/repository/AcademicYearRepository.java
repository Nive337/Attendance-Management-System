package com.attendance.management.repository;

import com.attendance.management.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {

    Optional<AcademicYear> findByYearLabel(String yearLabel);

    List<AcademicYear> findByStatus(AcademicYear.Status status);

    boolean existsByYearLabel(String yearLabel);
}