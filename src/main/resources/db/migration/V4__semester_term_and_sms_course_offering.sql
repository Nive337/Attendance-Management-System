CREATE TABLE semester_term (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    academic_year_id BIGINT NOT NULL,
    degree_id BIGINT NOT NULL,
    semester INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    CONSTRAINT uq_semester_term
        UNIQUE (academic_year_id, degree_id, semester),

    CONSTRAINT fk_semester_term_year
        FOREIGN KEY (academic_year_id)
        REFERENCES academic_year(id),

    CONSTRAINT fk_semester_term_degree
        FOREIGN KEY (degree_id)
        REFERENCES degree(id),

    CONSTRAINT chk_semester_term_dates
        CHECK (end_date >= start_date)
);

ALTER TABLE sms_notification
    ADD COLUMN course_offering_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_sms_course_offering
        FOREIGN KEY (course_offering_id)
        REFERENCES course_offering(id);

ALTER TABLE sms_notification
    ADD INDEX idx_sms_enrollment (student_enrollment_id);

ALTER TABLE sms_notification
    DROP INDEX uq_sms_month;

ALTER TABLE sms_notification
    ADD CONSTRAINT uq_sms_month UNIQUE (
        student_enrollment_id,
        course_offering_id,
        notif_year,
        notif_month
    );