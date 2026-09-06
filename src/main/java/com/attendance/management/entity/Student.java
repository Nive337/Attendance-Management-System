package com.attendance.management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String rollNumber;

    private Integer semester;

    private String section;

    private String parentName;

    private String parentPhone;

    @Column(name = "joining_year")
    private Integer joiningYear;


    // Required by JPA
    public Student() {
    }


    // Constructor
    public Student(String name, String rollNumber, Integer semester,
                   String section, String parentName,
                   String parentPhone, Integer joiningYear) {

        this.name = name;
        this.rollNumber = rollNumber;
        this.semester = semester;
        this.section = section;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.joiningYear = joiningYear;
    }


    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }


    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }


    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }


    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }


    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }


    public Integer getJoiningYear() {
        return joiningYear;
    }

    public void setJoiningYear(Integer joiningYear) {
        this.joiningYear = joiningYear;
    }
}