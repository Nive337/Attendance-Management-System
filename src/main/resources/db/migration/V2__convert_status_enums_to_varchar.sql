-- MySQL's native ENUM type is reported to JDBC/Hibernate differently across
-- driver and dialect versions, which can fail Hibernate's schema validation
-- (ddl-auto=validate) even when the allowed values are identical. VARCHAR +
-- CHECK gives the same data-integrity guarantee without that risk.
-- Requires MySQL 8.0.16+ for CHECK to be enforced (earlier versions parse but ignore it).

ALTER TABLE academic_year
    MODIFY status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT chk_academic_year_status CHECK (status IN ('ACTIVE','ARCHIVED'));

ALTER TABLE degree
    MODIFY status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT chk_degree_status CHECK (status IN ('ACTIVE','INACTIVE'));

ALTER TABLE lecturer
    MODIFY status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT chk_lecturer_status CHECK (status IN ('ACTIVE','INACTIVE'));

ALTER TABLE subject
    MODIFY status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT chk_subject_status CHECK (status IN ('ACTIVE','INACTIVE'));

ALTER TABLE course_offering
    MODIFY status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT chk_course_offering_status CHECK (status IN ('ACTIVE','ARCHIVED'));

ALTER TABLE student_enrollment
    MODIFY status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT chk_enrollment_status CHECK (status IN ('ACTIVE','COMPLETED','WITHDRAWN'));

ALTER TABLE attendance_record
    MODIFY status VARCHAR(20) NOT NULL,
    ADD CONSTRAINT chk_attendance_status CHECK (status IN ('PRESENT','ABSENT','LATE'));

ALTER TABLE sms_notification
    MODIFY status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ADD CONSTRAINT chk_sms_status CHECK (status IN ('PENDING','SENT','FAILED'));