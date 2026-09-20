package com.attendance.management.controller;

import com.attendance.management.dto.request.BulkEnrollmentRequest;
import com.attendance.management.dto.request.StatusUpdateRequest;
import com.attendance.management.dto.request.StudentRowRequest;
import com.attendance.management.dto.response.StudentEnrollmentResponse;
import com.attendance.management.service.StudentEnrollmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.attendance.management.dto.request.PromotionRequest;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final StudentEnrollmentService studentEnrollmentService;

    public EnrollmentController(StudentEnrollmentService studentEnrollmentService) {
        this.studentEnrollmentService = studentEnrollmentService;
    }

    @GetMapping
    public List<StudentEnrollmentResponse> getRoster(@RequestParam Long courseOfferingId) {
        return studentEnrollmentService.getRoster(courseOfferingId);
    }

    @PostMapping("/bulk")
    public List<StudentEnrollmentResponse> bulkCreate(@Valid @RequestBody BulkEnrollmentRequest request) {
        return studentEnrollmentService.bulkCreate(request);
    }

    @PutMapping("/{enrollmentId}")
    public StudentEnrollmentResponse update(@PathVariable Long enrollmentId,
                                             @Valid @RequestBody StudentRowRequest request) {
        return studentEnrollmentService.update(enrollmentId, request);
    }

    @PatchMapping("/{enrollmentId}/status")
    public StudentEnrollmentResponse updateStatus(@PathVariable Long enrollmentId,
                                                   @Valid @RequestBody StatusUpdateRequest request) {
        return studentEnrollmentService.updateStatus(enrollmentId, request.getStatus());
    }
    @PostMapping("/promote")
    public List<StudentEnrollmentResponse> promote(@Valid @RequestBody PromotionRequest request) {
        return studentEnrollmentService.promote(request);
    }
}