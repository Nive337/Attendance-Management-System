// controller/CourseController.java
package com.attendance.management.controller;

import com.attendance.management.dto.request.CourseOfferingRequest;
import com.attendance.management.dto.request.StatusUpdateRequest;
import com.attendance.management.dto.response.CourseOfferingResponse;
import com.attendance.management.entity.CourseOffering;
import com.attendance.management.service.CourseOfferingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseOfferingService courseOfferingService;

    public CourseController(CourseOfferingService courseOfferingService) {
        this.courseOfferingService = courseOfferingService;
    }

    @GetMapping
    public List<CourseOfferingResponse> search(
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) Long degreeId,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) Long lecturerId,
            @RequestParam(required = false) CourseOffering.Status status,
            @RequestParam(required = false) String search) {

        return courseOfferingService.search(academicYearId, degreeId, semester, section, lecturerId, status, search);
    }

    @PostMapping
    public CourseOfferingResponse create(@Valid @RequestBody CourseOfferingRequest request) {
        return courseOfferingService.create(request);
    }

    @PutMapping("/{id}")
    public CourseOfferingResponse update(@PathVariable Long id, @Valid @RequestBody CourseOfferingRequest request) {
        return courseOfferingService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public CourseOfferingResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return courseOfferingService.updateStatus(id, request.getStatus());
    }
}