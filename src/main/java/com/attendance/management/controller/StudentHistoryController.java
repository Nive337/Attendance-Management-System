package com.attendance.management.controller;

import com.attendance.management.dto.response.StudentHistoryResponse;
import com.attendance.management.service.StudentHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentHistoryController {

    private final StudentHistoryService studentHistoryService;

    public StudentHistoryController(StudentHistoryService studentHistoryService) {
        this.studentHistoryService = studentHistoryService;
    }

    @GetMapping("/{studentId}/history")
    public StudentHistoryResponse getHistory(@PathVariable Long studentId) {
        return studentHistoryService.getHistory(studentId);
    }
}