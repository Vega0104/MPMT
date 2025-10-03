-- ============================================
-- PMT Database Schema
-- PostgreSQL 12+
-- ============================================

-- Suppression des tables existantes
DROP TABLE IF EXISTS notification CASCADE;
DROP TABLE IF EXISTS task_history CASCADE;
DROP TABLE IF EXISTS task_assignment CASCADE;
DROP TABLE IF EXISTS task CASCADE;
DROP TABLE IF EXISTS project_member CASCADE;
DROP TABLE IF EXISTS project CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;

-- Suppression des types ENUM existants
DROP TYPE IF EXISTS role_type CASCADE;
DROP TYPE IF EXISTS priority_type CASCADE;
DROP TYPE IF EXISTS status_type CASCADE;

-- ============================================
-- Création des types ENUM
-- ============================================

CREATE TYPE role_type AS ENUM ('ADMIN', 'MEMBER', 'OBSERVER');
CREATE TYPE priority_type AS ENUM ('LOW', 'MEDIUM', 'HIGH');
CREATE TYPE status_type AS ENUM ('TODO', 'IN_PROGRESS', 'DONE');

-- ============================================
-- Création des tables
-- ============================================

CREATE TABLE "user" (
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(100) NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(200) NOT NULL,
                         description TEXT,
                         start_date DATE NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_member (
                                id SERIAL PRIMARY KEY,
                                user_id INTEGER NOT NULL,
                                project_id INTEGER NOT NULL,
                                role role_type NOT NULL,
                                joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_project_member_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
                                CONSTRAINT fk_project_member_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
                                CONSTRAINT unique_user_project UNIQUE (user_id, project_id)
);

CREATE TABLE task (
                      id SERIAL PRIMARY KEY,
                      project_id INTEGER NOT NULL,
                      name VARCHAR(200) NOT NULL,
                      description TEXT,
                      due_date DATE,
                      end_date DATE,
                      priority priority_type DEFAULT 'MEDIUM',
                      status status_type DEFAULT 'TODO',
                      created_by INTEGER NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
                      CONSTRAINT fk_task_creator FOREIGN KEY (created_by) REFERENCES "user"(id)
);

CREATE TABLE task_assignment (
                                 id SERIAL PRIMARY KEY,
                                 task_id INTEGER NOT NULL,
                                 project_member_id INTEGER NOT NULL,
                                 assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_assignment_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_assignment_member FOREIGN KEY (project_member_id) REFERENCES project_member(id) ON DELETE CASCADE,
                                 CONSTRAINT unique_task_member UNIQUE (task_id, project_member_id)
);

CREATE TABLE task_history (
                              id SERIAL PRIMARY KEY,
                              task_id INTEGER NOT NULL,
                              changed_by INTEGER NOT NULL,
                              change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              change_description TEXT NOT NULL,
                              CONSTRAINT fk_history_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE,
                              CONSTRAINT fk_history_user FOREIGN KEY (changed_by) REFERENCES "user"(id)
);

CREATE TABLE notification (
                              id SERIAL PRIMARY KEY,
                              user_id INTEGER NOT NULL,
                              task_id INTEGER,
                              content TEXT NOT NULL,
                              read BOOLEAN DEFAULT FALSE,
                              sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
                              CONSTRAINT fk_notification_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE SET NULL
);

-- ============================================
-- Index pour performances
-- ============================================

CREATE INDEX idx_project_member_user ON project_member(user_id);
CREATE INDEX idx_project_member_project ON project_member(project_id);
CREATE INDEX idx_task_project ON task(project_id);
CREATE INDEX idx_task_status ON task(status);
CREATE INDEX idx_task_assignment_task ON task_assignment(task_id);
CREATE INDEX idx_task_assignment_member ON task_assignment(project_member_id);
CREATE INDEX idx_task_history_task ON task_history(task_id);
CREATE INDEX idx_notification_user ON notification(user_id);
CREATE INDEX idx_notification_read ON notification(read);

-- ============================================
-- Données de test
-- ============================================

-- Users
INSERT INTO "user" (username, email, password) VALUES
                                                   ('Alice Martin', 'alice@pmt.com', 'password123'),
                                                   ('Bob Durant', 'bob@pmt.com', 'password123'),
                                                   ('Charlie Dubois', 'charlie@pmt.com', 'password123'),
                                                   ('Diana Prince', 'diana@pmt.com', 'password123'),
                                                   ('Eve Laurent', 'eve@pmt.com', 'password123');

-- Projects
INSERT INTO project (name, description, start_date) VALUES
                                                        ('Project Alpha', 'Development of the new mobile application', '2025-01-15'),
                                                        ('Project Beta', 'Website redesign and optimization', '2025-02-01');

-- Project Members (Project Alpha)
INSERT INTO project_member (user_id, project_id, role) VALUES
                                                           (1, 1, 'ADMIN'),      -- Alice is admin of Project Alpha
                                                           (2, 1, 'MEMBER'),     -- Bob is member
                                                           (3, 1, 'MEMBER'),     -- Charlie is member
                                                           (4, 1, 'OBSERVER');   -- Diana is observer

-- Project Members (Project Beta)
INSERT INTO project_member (user_id, project_id, role) VALUES
                                                           (2, 2, 'ADMIN'),      -- Bob is admin of Project Beta
                                                           (1, 2, 'MEMBER'),     -- Alice is member
                                                           (5, 2, 'OBSERVER');   -- Eve is observer

-- Tasks for Project Alpha
INSERT INTO task (project_id, name, description, due_date, priority, status, created_by) VALUES
                                                                                             (1, 'Setup development environment', 'Configure Docker, databases, and CI/CD pipeline', '2025-02-05', 'HIGH', 'DONE', 1),
                                                                                             (1, 'Design database schema', 'Create ER diagram and SQL scripts', '2025-02-10', 'HIGH', 'DONE', 1),
                                                                                             (1, 'Implement user authentication', 'Login, registration, and JWT token management', '2025-02-20', 'HIGH', 'IN_PROGRESS', 2),
                                                                                             (1, 'Create project management module', 'CRUD operations for projects', '2025-02-25', 'MEDIUM', 'IN_PROGRESS', 2),
                                                                                             (1, 'Develop task tracking feature', 'Task creation, assignment, and status updates', '2025-03-01', 'HIGH', 'TODO', 1),
                                                                                             (1, 'Write unit tests', 'Achieve 60% code coverage', '2025-03-10', 'MEDIUM', 'TODO', 3);

-- Tasks for Project Beta
INSERT INTO task (project_id, name, description, due_date, priority, status, created_by) VALUES
                                                                                             (2, 'Analyze current website', 'Identify pain points and improvement areas', '2025-02-12', 'HIGH', 'DONE', 2),
                                                                                             (2, 'Create wireframes', 'Design new layouts for all pages', '2025-02-18', 'HIGH', 'IN_PROGRESS', 2),
                                                                                             (2, 'Develop responsive header', 'Mobile-first navigation component', '2025-02-25', 'MEDIUM', 'TODO', 1),
                                                                                             (2, 'Optimize images and assets', 'Reduce page load time', '2025-03-05', 'LOW', 'TODO', 5);

-- Task Assignments
INSERT INTO task_assignment (task_id, project_member_id) VALUES
-- Project Alpha tasks
(1, 1), -- Alice assigned to task 1
(2, 1), -- Alice assigned to task 2
(3, 2), -- Bob assigned to task 3
(4, 2), -- Bob assigned to task 4
(5, 1), -- Alice assigned to task 5
(5, 2), -- Bob also assigned to task 5 (collaborative)
(6, 3), -- Charlie assigned to task 6
-- Project Beta tasks
(7, 5), -- Bob assigned to task 7
(8, 5), -- Bob assigned to task 8
(9, 6), -- Alice assigned to task 9
(10, 7); -- Eve assigned to task 10

-- Task History
INSERT INTO task_history (task_id, changed_by, change_description) VALUES
                                                                       (1, 1, 'Task created'),
                                                                       (1, 2, 'Status changed from TODO to IN_PROGRESS'),
                                                                       (1, 2, 'Status changed from IN_PROGRESS to DONE'),
                                                                       (3, 2, 'Task created'),
                                                                       (3, 2, 'Status changed to IN_PROGRESS'),
                                                                       (4, 2, 'Task created'),
                                                                       (4, 1, 'Priority changed from LOW to MEDIUM');

-- Notifications
INSERT INTO notification (user_id, task_id, content, read) VALUES
                                                               (1, 5, 'You have been assigned to task: Develop task tracking feature', FALSE),
                                                               (2, 3, 'You have been assigned to task: Implement user authentication', TRUE),
                                                               (2, 4, 'You have been assigned to task: Create project management module', TRUE),
                                                               (2, 5, 'You have been assigned to task: Develop task tracking feature', FALSE),
                                                               (3, 6, 'You have been assigned to task: Write unit tests', FALSE),
                                                               (5, 10, 'You have been assigned to task: Optimize images and assets', FALSE);

-- ============================================
-- Vérifications
-- ============================================

-- Affichage du résumé
SELECT 'Users' AS table_name, COUNT(*) AS count FROM "user"
UNION ALL
SELECT 'Projects', COUNT(*) FROM project
UNION ALL
SELECT 'Project Members', COUNT(*) FROM project_member
UNION ALL
SELECT 'Tasks', COUNT(*) FROM task
UNION ALL
SELECT 'Task Assignments', COUNT(*) FROM task_assignment
UNION ALL
SELECT 'Task History', COUNT(*) FROM task_history
UNION ALL
SELECT 'Notifications', COUNT(*) FROM notification;