package com.attendance.management.service;

import com.attendance.management.dto.request.SemesterTermRequest;
import com.attendance.management.entity.AcademicYear;
import com.attendance.management.entity.Degree;
import com.attendance.management.entity.SemesterTerm;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.AcademicYearRepository;
import com.attendance.management.repository.DegreeRepository;
import com.attendance.management.repository.SemesterTermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SemesterTermService {

    private final SemesterTermRepository semesterTermRepository;
    private final AcademicYearRepository academicYearRepository;
    private final DegreeRepository degreeRepository;

    public SemesterTermService(SemesterTermRepository semesterTermRepository,
                                AcademicYearRepository academicYearRepository,
                                DegreeRepository degreeRepository) {
        this.semesterTermRepository = semesterTermRepository;
        this.academicYearRepository = academicYearRepository;
        this.degreeRepository = degreeRepository;
    }

    // Upsert: saving dates for a year/degree/semester that's already configured
    // corrects it in place instead of erroring or creating a duplicate row.
    @Transactional
    public SemesterTerm save(SemesterTermRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic year not found: " + request.getAcademicYearId()));
        Degree degree = degreeRepository.findById(request.getDegreeId())
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found: " + request.getDegreeId()));

        SemesterTerm term = semesterTermRepository
                .findByAcademicYearIdAndDegreeIdAndSemester(academicYear.getId(), degree.getId(), request.getSemester())
                .orElseGet(SemesterTerm::new);

        term.setAcademicYear(academicYear);
        term.setDegree(degree);
        term.setSemester(request.getSemester());
        term.setStartDate(request.getStartDate());
        term.setEndDate(request.getEndDate());

        return semesterTermRepository.save(term);
    }
}