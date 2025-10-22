-- ATTENTION : supprime toutes les données !
TRUNCATE TABLE
    public.notification,
  public.task_history,
  public.task_assignment,
  public.tasks,
  public.project_member,
  public.project,
  public."user"
RESTART IDENTITY CASCADE;


-- Commande pour executer ce script :
-- docker exec -i mpmt-postgres psql -U postgres -d mpmt < sql/reset.sql




