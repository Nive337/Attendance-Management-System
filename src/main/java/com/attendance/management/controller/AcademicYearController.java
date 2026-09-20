// controller/AcademicYearController.java
package com.attendance.management.controller;

import com.attendance.management.dto.request.AcademicYearRequest;
import com.attendance.management.dto.request.StatusUpdateRequest;
import com.attendance.management.dto.response.AcademicYearResponse;
import com.attendance.management.service.AcademicYearService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-years")
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    public AcademicYearController(AcademicYearService academicYearService) {
        this.academicYearService = academicYearService;
    }

    @GetMapping
    public List<AcademicYearResponse> getAll() {
        return academicYearService.findAll().stream()
                .map(AcademicYearResponse::from)
                .toList();
    }

    @PostMapping
    public AcademicYearResponse create(@Valid @RequestBody AcademicYearRequest request) {
        return AcademicYearResponse.from(academicYearService.create(request.getYearLabel()));
    }

    @PatchMapping("/{id}/status")
    public AcademicYearResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return AcademicYearResponse.from(academicYearService.updateStatus(id, request.getStatus()));
    }
}