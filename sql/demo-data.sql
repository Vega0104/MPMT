-- ===== Utilisateurs (mettre ici tes vrais hash BCrypt) =====
INSERT INTO public."user"(email, username, password, role)
VALUES
    ('admin@demo.local', 'admin',  '$2a$10$tNtmnYD.reqCzPNWtLWBheGQHzsqXZVRh0JpFQSrbLaxn85Ffjj8O',  'ADMIN'),
    ('alice@demo.local', 'alice',  '$2a$10$H4QX1uOJ.kTbRQTH4XZ48OGejhAUuIjiJ2B4SzEKLvxG6lVMosPeG',  'MEMBER'),
    ('bob@demo.local',   'bob',    '$2a$10$DNGVg324fnPVMikVNU0CY.n1Pnuw0uiB95Nu0CWHAcs.ZYqFBGDWu',    'OBSERVER')
    ON CONFLICT (email) DO NOTHING;

-- ===== Projet démo =====
INSERT INTO public.project(name, description, start_date, created_at)
VALUES ('Cardio Assist', 'Plateforme de suivi & planification', CURRENT_DATE - 10, NOW())
    ON CONFLICT (name) DO NOTHING;

-- ===== Membres du projet (UNIQUE(project_id,user_id) protège les doublons) =====
INSERT INTO public.project_member(project_id, user_id, role, joined_at)
SELECT p.id, u.id, 'ADMIN', NOW()
FROM public.project p
         JOIN public."user" u ON u.email='admin@demo.local'
WHERE p.name='Cardio Assist'
    ON CONFLICT DO NOTHING;

INSERT INTO public.project_member(project_id, user_id, role, joined_at)
SELECT p.id, u.id, 'MEMBER', NOW()
FROM public.project p
         JOIN public."user" u ON u.email='alice@demo.local'
WHERE p.name='Cardio Assist'
    ON CONFLICT DO NOTHING;

INSERT INTO public.project_member(project_id, user_id, role, joined_at)
SELECT p.id, u.id, 'OBSERVER', NOW()
FROM public.project p
         JOIN public."user" u ON u.email='bob@demo.local'
WHERE p.name='Cardio Assist'
    ON CONFLICT DO NOTHING;

-- ===== Tâches =====
INSERT INTO public.tasks(title, name, description, priority, status, project_id, created_by)
SELECT 'Importer les données DICOM', 'Importer DICOM', 'ETL initial des examens', 'HIGH', 'TODO', p.id, ua.id
FROM public.project p
         JOIN public."user" ua ON ua.email='admin@demo.local'
WHERE p.name='Cardio Assist'
  AND NOT EXISTS (
    SELECT 1 FROM public.tasks t WHERE t.project_id=p.id AND t.title='Importer les données DICOM'
);

INSERT INTO public.tasks(title, name, description, priority, status, project_id, created_by)
SELECT 'UI liste de tâches', 'UI tâches', 'Vue Kanban + filtres', 'MEDIUM', 'IN_PROGRESS', p.id, ua.id
FROM public.project p
         JOIN public."user" ua ON ua.email='admin@demo.local'
WHERE p.name='Cardio Assist'
  AND NOT EXISTS (
    SELECT 1 FROM public.tasks t WHERE t.project_id=p.id AND t.title='UI liste de tâches'
);

INSERT INTO public.tasks(title, name, description, priority, status, project_id, created_by)
SELECT 'Notifications e-mail', 'Notif e-mail', 'Envoi lors d’une assignation', 'LOW', 'TODO', p.id, ua.id
FROM public.project p
         JOIN public."user" ua ON ua.email='admin@demo.local'
WHERE p.name='Cardio Assist'
  AND NOT EXISTS (
    SELECT 1 FROM public.tasks t WHERE t.project_id=p.id AND t.title='Notifications e-mail'
);

-- ===== Assignations (UNIQUE(task_id, project_member_id)) =====
-- Alice -> DICOM
INSERT INTO public.task_assignment(task_id, project_member_id)
SELECT t.id, pm.id
FROM public.tasks t
         JOIN public.project p ON p.id = t.project_id AND p.name='Cardio Assist'
         JOIN public."user" u ON u.email='alice@demo.local'
         JOIN public.project_member pm ON pm.project_id = p.id AND pm.user_id = u.id
WHERE t.title='Importer les données DICOM'
    ON CONFLICT DO NOTHING;

-- Admin -> UI
INSERT INTO public.task_assignment(task_id, project_member_id)
SELECT t.id, pm.id
FROM public.tasks t
         JOIN public.project p ON p.id = t.project_id AND p.name='Cardio Assist'
         JOIN public."user" u ON u.email='admin@demo.local'
         JOIN public.project_member pm ON pm.project_id = p.id AND pm.user_id = u.id
WHERE t.title='UI liste de tâches'
    ON CONFLICT DO NOTHING;

-- Bob -> Notifications
INSERT INTO public.task_assignment(task_id, project_member_id)
SELECT t.id, pm.id
FROM public.tasks t
         JOIN public.project p ON p.id = t.project_id AND p.name='Cardio Assist'
         JOIN public."user" u ON u.email='bob@demo.local'
         JOIN public.project_member pm ON pm.project_id = p.id AND pm.user_id = u.id
WHERE t.title='Notifications e-mail'
    ON CONFLICT DO NOTHING;

-- ===== Historique (optionnel) =====
INSERT INTO public.task_history(change_date, change_description, changed_by, task_id)
SELECT NOW(), 'Tâche créée', ua.id, t.id
FROM public."user" ua
         JOIN public.tasks t ON 1=1
         JOIN public.project p ON p.id = t.project_id
WHERE ua.email='admin@demo.local'
  AND p.name='Cardio Assist'
  AND t.title IN ('Importer les données DICOM','UI liste de tâches','Notifications e-mail')
    ON CONFLICT DO NOTHING;

-- ===== Notifications (optionnel) =====
INSERT INTO public.notification(content, read, sent_at, task_id, user_id)
SELECT 'Assignation de tâche', false, NOW(), ta.task_id, pm.user_id
FROM public.task_assignment ta
         JOIN public.project_member pm ON pm.id = ta.project_member_id
    ON CONFLICT DO NOTHING;




-- Commande pour executer ce script :
-- docker exec -i mpmt-postgres psql -U postgres -d mpmt < sql/demo-data.sql
