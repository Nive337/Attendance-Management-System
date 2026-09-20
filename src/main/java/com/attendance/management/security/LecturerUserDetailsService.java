package com.attendance.management.security;

import com.attendance.management.entity.Lecturer;
import com.attendance.management.repository.LecturerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LecturerUserDetailsService implements UserDetailsService {

    private final LecturerRepository lecturerRepository;

    public LecturerUserDetailsService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Lecturer lecturer = lecturerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No lecturer found with email: " + email));
        return new LecturerPrincipal(lecturer);
    }
}