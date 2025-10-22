ALTER TABLE public.project_member
    ADD CONSTRAINT uq_project_member UNIQUE (project_id, user_id);

ALTER TABLE public.task_assignment
    ADD CONSTRAINT uq_task_assignment UNIQUE (task_id, project_member_id);

ALTER TABLE public.tasks
    ADD CONSTRAINT fk_tasks_created_by
        FOREIGN KEY (created_by) REFERENCES public."user"(id) ON DELETE RESTRICT;

ALTER TABLE public.task_assignment
    ADD CONSTRAINT fk_task_assignment_task
        FOREIGN KEY (task_id) REFERENCES public.tasks(id) ON DELETE CASCADE;

ALTER TABLE public.task_assignment
    ADD CONSTRAINT fk_task_assignment_pm
        FOREIGN KEY (project_member_id) REFERENCES public.project_member(id) ON DELETE CASCADE;

ALTER TABLE public.task_history
    ADD CONSTRAINT fk_task_history_task
        FOREIGN KEY (task_id) REFERENCES public.tasks(id) ON DELETE CASCADE;

ALTER TABLE public.task_history
    ADD CONSTRAINT fk_task_history_changed_by
        FOREIGN KEY (changed_by) REFERENCES public."user"(id) ON DELETE SET NULL;

ALTER TABLE public.notification
    ADD CONSTRAINT fk_notification_task
        FOREIGN KEY (task_id) REFERENCES public.tasks(id) ON DELETE CASCADE;

ALTER TABLE public.notification
    ADD CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES public."user"(id) ON DELETE CASCADE;

ALTER TABLE public.notification
    ALTER COLUMN read SET DEFAULT false;

ALTER TABLE public.task_history
    ALTER COLUMN change_date SET DEFAULT now();

ALTER TABLE public.notification
    ALTER COLUMN sent_at SET DEFAULT now();

CREATE INDEX IF NOT EXISTS idx_tasks_project_id            ON public.tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_created_by            ON public.tasks(created_by);
CREATE INDEX IF NOT EXISTS idx_pm_project_id               ON public.project_member(project_id);
CREATE INDEX IF NOT EXISTS idx_pm_user_id                  ON public.project_member(user_id);
CREATE INDEX IF NOT EXISTS idx_ta_task_id                  ON public.task_assignment(task_id);
CREATE INDEX IF NOT EXISTS idx_ta_pm_id                    ON public.task_assignment(project_member_id);
CREATE INDEX IF NOT EXISTS idx_th_task_id                  ON public.task_history(task_id);
CREATE INDEX IF NOT EXISTS idx_th_changed_by               ON public.task_history(changed_by);
CREATE INDEX IF NOT EXISTS idx_notif_task_id               ON public.notification(task_id);
CREATE INDEX IF NOT EXISTS idx_notif_user_id               ON public.notification(user_id);
