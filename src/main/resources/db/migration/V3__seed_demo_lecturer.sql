-- Local/dev-only seed so Phase 5 login can be tested immediately.
-- Login: priya.sharma@college.edu / ChangeMe123!
-- The hash below was generated with an actual bcrypt run (cost 10), not typed by hand.
-- Change or delete this row before any shared or production environment.
INSERT INTO lecturer (lecturer_code, name, email, password_hash, department, role, status)
VALUES (
    'LEC-001',
    'Priya Sharma',
    'priya.sharma@college.edu',
    '$2b$10$S1tuZbgn9dtwiGX0Td4mJutUNLK5TO1xshNaAAlHoXZu7xOPyuzIi',
    'Commerce',
    'LECTURER',
    'ACTIVE'
);