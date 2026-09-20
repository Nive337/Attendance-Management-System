// service/SubjectService.java
package com.attendance.management.service;

import com.attendance.management.entity.Degree;
import com.attendance.management.entity.Subject;
import com.attendance.management.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> findByDegree(Long degreeId) {
        return subjectRepository.findByDegreeIdAndStatus(degreeId, Subject.Status.ACTIVE);
    }

    // Subjects have no dedicated "Add Subject" screen: a subject is found or
    // created the moment a lecturer saves a course offering with its code/name.
    @Transactional
    public Subject findOrCreate(Degree degree, String subjectCode, String subjectName) {
        return subjectRepository.findByDegreeIdAndSubjectCode(degree.getId(), subjectCode)
                .orElseGet(() -> {
                    Subject subject = new Subject();
                    subject.setDegree(degree);
                    subject.setSubjectCode(subjectCode);
                    subject.setSubjectName(subjectName);
                    subject.setStatus(Subject.Status.ACTIVE);
                    return subjectRepository.save(subject);
                });
    }
}