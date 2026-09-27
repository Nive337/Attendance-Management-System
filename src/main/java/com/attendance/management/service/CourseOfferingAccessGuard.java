package com.attendance.management.service;

import com.attendance.management.entity.CourseOffering;
import com.attendance.management.entity.Lecturer;
import com.attendance.management.security.LecturerPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Extracted from AttendanceService in Phase 10 so this exact check - used by
// attendance roster/submit and now attendance reports - lives in one place.
@Component
public class CourseOfferingAccessGuard {

    public void requireOwnership(CourseOffering offering) {
        Lecturer current = currentLecturer();
        boolean isOwner = current.getId().equals(offering.getLecturer().getId());
        boolean isAdmin = current.getRole() == Lecturer.Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not assigned to this course offering.");
        }
    }

    public Lecturer currentLecturer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LecturerPrincipal principal = (LecturerPrincipal) authentication.getPrincipal();
        return principal.getLecturer();
    }
}