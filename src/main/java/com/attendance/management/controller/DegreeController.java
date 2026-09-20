// controller/DegreeController.java
package com.attendance.management.controller;

import com.attendance.management.dto.request.DegreeRequest;
import com.attendance.management.dto.request.StatusUpdateRequest;
import com.attendance.management.dto.response.DegreeResponse;
import com.attendance.management.service.DegreeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/degrees")
public class DegreeController {

    private final DegreeService degreeService;

    public DegreeController(DegreeService degreeService) {
        this.degreeService = degreeService;
    }

    @GetMapping
    public List<DegreeResponse> getAll() {
        return degreeService.findAll().stream()
                .map(DegreeResponse::from)
                .toList();
    }

    @PostMapping
    public DegreeResponse create(@Valid @RequestBody DegreeRequest request) {
        return DegreeResponse.from(degreeService.create(request.getName()));
    }

    @PatchMapping("/{id}/status")
    public DegreeResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return DegreeResponse.from(degreeService.updateStatus(id, request.getStatus()));
    }
}