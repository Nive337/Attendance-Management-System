# 📊 Attendance Management System

A web-based Attendance Management System developed using **Java, Spring Boot, MySQL, HTML, CSS, and JavaScript**. The system provides a simple interface for managing students, recording attendance, and generating attendance reports.

---

## 📌 Overview

The Attendance Management System is designed to simplify the process of maintaining student attendance digitally.

Instead of managing attendance manually, the application allows users to:

- 👨‍🎓 Manage student records
- 📝 Record student attendance
- 📅 View attendance information
- 📊 Generate attendance reports
- 🏠 Navigate through a centralized dashboard

The backend is developed using **Spring Boot**, while **MySQL** is used for persistent data storage.

---

## ✨ Features

### 👨‍🎓 Student Management
- Add student records
- View student information
- Manage student data

### 📝 Attendance Management
- Record attendance
- Associate attendance records with students
- View attendance information

### 📊 Attendance Reports
- View attendance reports
- Retrieve attendance information from the database
- Display attendance data through a web interface

### 🖥️ Web Interface
- Dashboard
- Student management page
- Attendance page
- Reports page

---

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| **Java** | Backend development |
| **Spring Boot** | Backend framework and REST controllers |
| **MySQL** | Database |
| **HTML** | Web page structure |
| **CSS** | Styling |
| **JavaScript** | Client-side functionality |
| **Maven** | Dependency management and build tool |

---

## 🏗️ Project Architecture

The project follows a layered Spring Boot architecture:

```text
Attendance Management System
│
├── Controller Layer
│   ├── AttendanceController
│   ├── DashboardController
│   ├── HomeController
│   ├── ReportController
│   └── StudentController
│
├── Entity Layer
│   ├── Attendance
│   └── Student
│
├── Repository Layer
│   ├── AttendanceRepository
│   └── StudentRepository
│
└── Frontend
    ├── index.html
    ├── attendance.html
    ├── students.html
    └── reports.html