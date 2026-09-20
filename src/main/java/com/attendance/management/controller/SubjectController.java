// controller/SubjectController.java
package com.attendance.management.controller;

import com.attendance.management.dto.response.SubjectResponse;
import com.attendance.management.service.SubjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public List<SubjectResponse> getByDegree(@RequestParam Long degreeId) {
        return subjectService.findByDegree(degreeId).stream()
                .map(SubjectResponse::from)
                .toList();
    }
}