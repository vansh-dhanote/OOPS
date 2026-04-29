USE faculty_student_portal;

ALTER TABLE users
ADD COLUMN subject VARCHAR(100) NULL,
ADD COLUMN schedule VARCHAR(150) NULL;

ALTER TABLE queries
ADD COLUMN faculty_id INT NULL,
ADD COLUMN file_path VARCHAR(255) NULL,
ADD COLUMN priority VARCHAR(20) NULL,
ADD COLUMN submitted_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE queries
ADD CONSTRAINT fk_queries_faculty
FOREIGN KEY (faculty_id) REFERENCES users(id);

UPDATE users
SET subject = 'OOP', schedule = 'Mon 10-12, Wed 2-4'
WHERE id = 201;

UPDATE users
SET subject = 'DBMS', schedule = 'Tue 11-1, Thu 1-3'
WHERE id = 202;
