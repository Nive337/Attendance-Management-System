package com.attendance.management.dto.response;

public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private long expiresInMs;
    private Long lecturerId;
    private String lecturerCode;
    private String name;
    private String email;
    private String department;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(String token, long expiresInMs, Long lecturerId, String lecturerCode,
                          String name, String email, String department, String role) {
        this.token = token;
        this.expiresInMs = expiresInMs;
        this.lecturerId = lecturerId;
        this.lecturerCode = lecturerCode;
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }

    public Long getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Long lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getLecturerCode() {
        return lecturerCode;
    }

    public void setLecturerCode(String lecturerCode) {
        this.lecturerCode = lecturerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}