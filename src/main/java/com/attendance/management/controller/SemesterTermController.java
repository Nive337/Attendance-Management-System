package com.attendance.management.controller;

import com.attendance.management.dto.request.SemesterTermRequest;
import com.attendance.management.dto.response.SemesterTermResponse;
import com.attendance.management.service.SemesterTermService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/semester-terms")
public class SemesterTermController {

    private final SemesterTermService semesterTermService;

    public SemesterTermController(SemesterTermService semesterTermService) {
        this.semesterTermService = semesterTermService;
    }

    @PostMapping
    public SemesterTermResponse save(@Valid @RequestBody SemesterTermRequest request) {
        return SemesterTermResponse.from(semesterTermService.save(request));
    }
}