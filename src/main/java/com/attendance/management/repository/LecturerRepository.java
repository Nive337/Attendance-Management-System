package com.attendance.management.repository;

import com.attendance.management.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    // Used by Phase 5 login: lecturers authenticate with email + password.
    Optional<Lecturer> findByEmail(String email);

    Optional<Lecturer> findByLecturerCode(String lecturerCode);

    List<Lecturer> findByStatus(Lecturer.Status status);
}