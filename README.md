# Attendance Management System

A web-based attendance management system developed using Java, Spring Boot, MySQL, HTML, CSS, and JavaScript. The application provides a centralized way to manage student information, record attendance, and view attendance reports.

## Overview

Managing attendance manually can become time-consuming, especially when dealing with multiple students and maintaining records over time. This project was developed to provide a simple digital solution for maintaining attendance records.

The application provides separate sections for student management, attendance recording, and attendance reports. The backend is built with Spring Boot and uses Spring Data JPA to communicate with a MySQL database.

## Features

### Student Management

- Add and manage student information
- Store student records in the database
- View existing student information

### Attendance Management

- Record attendance for students
- Store attendance records in MySQL
- Retrieve attendance information when required

### Attendance Reports

- View recorded attendance
- Generate and display attendance information
- Retrieve attendance data from the database

### Dashboard

- Provides a central navigation point for the application
- Provides access to student, attendance, and report sections

## Technologies Used

| Technology | Purpose |
|------------|---------|
| Java | Backend development |
| Spring Boot | Application framework |
| Spring Data JPA | Database interaction |
| MySQL | Data storage |
| HTML | Frontend structure |
| CSS | Frontend styling |
| JavaScript | Client-side functionality |
| Maven | Dependency management and build |

## Project Structure

```text
attendance-management/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/attendance/management/
│   │   │       ├── controller/
│   │   │       │   ├── AttendanceController.java
│   │   │       │   ├── DashboardController.java
│   │   │       │   ├── HomeController.java
│   │   │       │   ├── ReportController.java
│   │   │       │   └── StudentController.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── Attendance.java
│   │   │       │   └── Student.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── AttendanceRepository.java
│   │   │       │   └── StudentRepository.java
│   │   │       │
│   │   │       └── AttendanceManagementApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   ├── attendance.html
│   │       │   ├── students.html
│   │       │   └── reports.html
│   │       │
│   │       └── application.properties
│   │
│   └── test/
│
├── .gitignore
├── .gitattributes
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md