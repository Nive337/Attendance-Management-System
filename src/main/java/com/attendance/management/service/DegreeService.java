// service/DegreeService.java
package com.attendance.management.service;

import com.attendance.management.entity.Degree;
import com.attendance.management.exception.DuplicateResourceException;
import com.attendance.management.exception.ResourceNotFoundException;
import com.attendance.management.repository.DegreeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DegreeService {

    private final DegreeRepository degreeRepository;

    public DegreeService(DegreeRepository degreeRepository) {
        this.degreeRepository = degreeRepository;
    }

    public List<Degree> findAll() {
        return degreeRepository.findAll();
    }

    public Degree create(String name) {
        if (degreeRepository.existsByName(name)) {
            throw new DuplicateResourceException("Degree '" + name + "' already exists.");
        }
        Degree degree = new Degree();
        degree.setName(name);
        degree.setStatus(Degree.Status.ACTIVE);
        return degreeRepository.save(degree);
    }

    public Degree updateStatus(Long id, String statusValue) {
        Degree degree = degreeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found: " + id));
        Degree.Status status;
        try {
            status = Degree.Status.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Status must be one of: ACTIVE, INACTIVE");
        }
        degree.setStatus(status);
        return degreeRepository.save(degree);
    }
}