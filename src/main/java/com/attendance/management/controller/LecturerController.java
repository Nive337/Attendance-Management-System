// controller/LecturerController.java
package com.attendance.management.controller;

import com.attendance.management.dto.response.LecturerSummaryResponse;
import com.attendance.management.entity.Lecturer;
import com.attendance.management.repository.LecturerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    private final LecturerRepository lecturerRepository;

    public LecturerController(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    @GetMapping
    public List<LecturerSummaryResponse> getActive() {
        return lecturerRepository.findByStatus(Lecturer.Status.ACTIVE).stream()
                .map(LecturerSummaryResponse::from)
                .toList();
    }
}