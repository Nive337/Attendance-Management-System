package com.attendance.management.repository;

import com.attendance.management.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByDegreeIdAndStatus(Long degreeId, Subject.Status status);

    Optional<Subject> findByDegreeIdAndSubjectCode(Long degreeId, String subjectCode);
}