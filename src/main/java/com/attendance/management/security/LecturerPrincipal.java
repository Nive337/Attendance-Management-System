package com.attendance.management.security;

import com.attendance.management.entity.Lecturer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class LecturerPrincipal implements UserDetails {

    private final Lecturer lecturer;

    public LecturerPrincipal(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + lecturer.getRole().name()));
    }

    @Override
    public String getPassword() {
        return lecturer.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return lecturer.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return lecturer.getStatus() == Lecturer.Status.ACTIVE;
    }
}