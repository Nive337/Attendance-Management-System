-- Phase 3: adds only the new tables. The existing `students` and `attendance`
-- tables (created earlier by Hibernate ddl-auto=update) are left untouched
-- and keep serving the current controllers unchanged.

CREATE TABLE academic_year (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year_label VARCHAR(20) NOT NULL,
    status ENUM('ACTIVE','ARCHIVED') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uq_academic_year_label UNIQUE (year_label)
);

CREATE TABLE degree (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uq_degree_name UNIQUE (name)
);

CREATE TABLE lecturer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lecturer_code VARCHAR(20) NOT NULL,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    department VARCHAR(80),
    role VARCHAR(20) NOT NULL DEFAULT 'LECTURER',
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_lecturer_code UNIQUE (lecturer_code),
    CONSTRAINT uq_lecturer_email UNIQUE (email)
);

CREATE TABLE subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(30) NOT NULL,
    subject_name VARCHAR(150) NOT NULL,
    degree_id BIGINT NOT NULL,
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uq_subject_degree_code UNIQUE (degree_id, subject_code),
    CONSTRAINT fk_subject_degree FOREIGN KEY (degree_id) REFERENCES degree(id)
);

CREATE TABLE course_offering (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    academic_year_id BIGINT NOT NULL,
    degree_id BIGINT NOT NULL,
    semester INT NOT NULL,
    section VARCHAR(5) NOT NULL,
    subject_id BIGINT NOT NULL,
    lecturer_id BIGINT NOT NULL,
    status ENUM('ACTIVE','ARCHIVED') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uq_course_offering UNIQUE (academic_year_id, degree_id, semester, section, subject_id),
    CONSTRAINT fk_offering_year FOREIGN KEY (academic_year_id) REFERENCES academic_year(id),
    CONSTRAINT fk_offering_degree FOREIGN KEY (degree_id) REFERENCES degree(id),
    CONSTRAINT fk_offering_subject FOREIGN KEY (subject_id) REFERENCES subject(id),
    CONSTRAINT fk_offering_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturer(id)
);

CREATE TABLE student_enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    degree_id BIGINT NOT NULL,
    semester INT NOT NULL,
    section VARCHAR(5) NOT NULL,
    roll_number VARCHAR(20) NOT NULL,
    status ENUM('ACTIVE','COMPLETED','WITHDRAWN') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uq_enrollment_roll UNIQUE (academic_year_id, degree_id, semester, section, roll_number),
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_enrollment_year FOREIGN KEY (academic_year_id) REFERENCES academic_year(id),
    CONSTRAINT fk_enrollment_degree FOREIGN KEY (degree_id) REFERENCES degree(id)
);

CREATE TABLE attendance_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_offering_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    session_number INT NOT NULL DEFAULT 1,
    created_by_lecturer_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_attendance_session UNIQUE (course_offering_id, attendance_date, session_number),
    CONSTRAINT fk_session_offering FOREIGN KEY (course_offering_id) REFERENCES course_offering(id),
    CONSTRAINT fk_session_lecturer FOREIGN KEY (created_by_lecturer_id) REFERENCES lecturer(id)
);

CREATE TABLE attendance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attendance_session_id BIGINT NOT NULL,
    student_enrollment_id BIGINT NOT NULL,
    status ENUM('PRESENT','ABSENT','LATE') NOT NULL,
    note VARCHAR(255),
    marked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_attendance_record UNIQUE (attendance_session_id, student_enrollment_id),
    CONSTRAINT fk_record_session FOREIGN KEY (attendance_session_id) REFERENCES attendance_session(id),
    CONSTRAINT fk_record_enrollment FOREIGN KEY (student_enrollment_id) REFERENCES student_enrollment(id)
);

CREATE TABLE sms_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_enrollment_id BIGINT NOT NULL,
    notif_year INT NOT NULL,
    notif_month INT NOT NULL,
    parent_phone VARCHAR(20) NOT NULL,
    message VARCHAR(320) NOT NULL,
    status ENUM('PENDING','SENT','FAILED') NOT NULL DEFAULT 'PENDING',
    attempt_count INT NOT NULL DEFAULT 0,
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_sms_month UNIQUE (student_enrollment_id, notif_year, notif_month),
    CONSTRAINT fk_sms_enrollment FOREIGN KEY (student_enrollment_id) REFERENCES student_enrollment(id)
);