package com.attendance.management.controller;

import com.attendance.management.dto.request.LoginRequest;
import com.attendance.management.dto.response.LoginResponse;
import com.attendance.management.entity.Lecturer;
import com.attendance.management.security.JwtUtil;
import com.attendance.management.security.LecturerPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        LecturerPrincipal principal = (LecturerPrincipal) authentication.getPrincipal();
        Lecturer lecturer = principal.getLecturer();

        String token = jwtUtil.generateToken(lecturer);

        return new LoginResponse(
                token,
                jwtUtil.getExpirationMs(),
                lecturer.getId(),
                lecturer.getLecturerCode(),
                lecturer.getName(),
                lecturer.getEmail(),
                lecturer.getDepartment(),
                lecturer.getRole().name()
        );
    }
}