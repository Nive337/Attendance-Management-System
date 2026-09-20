package com.attendance.management.repository;

import com.attendance.management.entity.Degree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DegreeRepository extends JpaRepository<Degree, Long> {

    Optional<Degree> findByName(String name);

    List<Degree> findByStatus(Degree.Status status);

    boolean existsByName(String name);
}