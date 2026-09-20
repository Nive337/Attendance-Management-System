// service/CourseOfferingSpecifications.java
package com.attendance.management.service;

import com.attendance.management.entity.CourseOffering;
import com.attendance.management.entity.Lecturer;
import com.attendance.management.entity.Subject;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class CourseOfferingSpecifications {

    private CourseOfferingSpecifications() {
    }

    public static Specification<CourseOffering> withFilters(Long academicYearId, Long degreeId, Integer semester,
                                                              String section, Long lecturerId,
                                                              CourseOffering.Status status, String search) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (academicYearId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("academicYear").get("id"), academicYearId));
            }
            if (degreeId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("degree").get("id"), degreeId));
            }
            if (semester != null) {
                predicate = cb.and(predicate, cb.equal(root.get("semester"), semester));
            }
            if (section != null && !section.isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.upper(root.get("section")), section.toUpperCase()));
            }
            if (lecturerId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("lecturer").get("id"), lecturerId));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (search != null && !search.isBlank()) {
                Join<CourseOffering, Subject> subjectJoin = root.join("subject");
                Join<CourseOffering, Lecturer> lecturerJoin = root.join("lecturer");
                String pattern = "%" + search.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(subjectJoin.get("subjectName")), pattern),
                        cb.like(cb.lower(subjectJoin.get("subjectCode")), pattern),
                        cb.like(cb.lower(lecturerJoin.get("name")), pattern)
                ));
            }

            return predicate;
        };
    }
}