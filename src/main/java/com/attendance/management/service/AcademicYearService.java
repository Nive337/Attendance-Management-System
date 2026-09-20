// service/AcademicYearService.java
package com.attendance.management.service;

import com.attendance.management.entity.AcademicYear;
import com.attendance.management.exception.DuplicateResourceException;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.AcademicYearRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    public AcademicYearService(AcademicYearRepository academicYearRepository) {
        this.academicYearRepository = academicYearRepository;
    }

    public List<AcademicYear> findAll() {
        return academicYearRepository.findAll();
    }

    public AcademicYear create(String yearLabel) {
        if (academicYearRepository.existsByYearLabel(yearLabel)) {
            throw new DuplicateResourceException("Academic year '" + yearLabel + "' already exists.");
        }
        AcademicYear academicYear = new AcademicYear();
        academicYear.setYearLabel(yearLabel);
        academicYear.setStatus(AcademicYear.Status.ACTIVE);
        return academicYearRepository.save(academicYear);
    }

    public AcademicYear updateStatus(Long id, String statusValue) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found: " + id));
        AcademicYear.Status status;
        try {
            status = AcademicYear.Status.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Status must be one of: ACTIVE, ARCHIVED");
        }
        academicYear.setStatus(status);
        return academicYearRepository.save(academicYear);
    }
}
