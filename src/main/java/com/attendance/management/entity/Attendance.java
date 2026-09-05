package com.attendance.management.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "attendance",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"student_id", "attendance_date"}
        )
    }
)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate attendanceDate;

    private String status;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    public Attendance() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}