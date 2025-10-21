package com.mpmt.backend.service;

import com.mpmt.backend.entity.StatusType;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.entity.TaskHistory;
import com.mpmt.backend.entity.User; // pour extraire l'id utilisateur courant
import com.mpmt.backend.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskHistoryService taskHistoryService;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       TaskHistoryService taskHistoryService) {
        this.taskRepository = taskRepository;
        this.taskHistoryService = taskHistoryService;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProject_Id(projectId);
    }

    public List<Task> getTasksByProjectIdAndStatus(Long projectId, StatusType status) {
        return taskRepository.findByProject_IdAndStatus(projectId, status);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    /**
     * Met à jour UNE tâche sans toucher à createdBy ni à project.
     * Historise les champs effectivement modifiés (une entrée d'historique par PUT).
     */
    @Transactional
    public Task updateTask(Task task) {
        if (task.getId() == null) {
            throw new IllegalArgumentException("Task id must be provided for update");
        }

        Task existing = taskRepository.findById(task.getId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + task.getId()));

        // --- Snapshot OLD values ---
        String oldName     = existing.getName();
        String oldDesc     = existing.getDescription();
        var    oldPriority = existing.getPriority();
        var    oldStatus   = existing.getStatus();
        var    oldDueDate  = existing.getDueDate();
        var    oldEndDate  = existing.getEndDate();

        // --- Apply NEW values (null explicite accepté pour dates) ---
        if (task.getName() != null) {
            existing.setName(task.getName());
        }
        if (task.getDescription() != null) {
            existing.setDescription(task.getDescription());
        }
        if (task.getPriority() != null) {
            existing.setPriority(task.getPriority());
        }
        if (task.getStatus() != null) {
            existing.setStatus(task.getStatus());
        }
        if (task.getDueDate() != null || isExplicitNull(task.getDueDate())) {
            existing.setDueDate(task.getDueDate());
        }
        if (task.getEndDate() != null || isExplicitNull(task.getEndDate())) {
            existing.setEndDate(task.getEndDate());
        }

        // ⚠️ ne pas toucher à createdBy / project
        Task saved = taskRepository.save(existing);

        // --- Compute diff lisible ---
        StringBuilder summary = new StringBuilder();
        appendChange(summary, "name",       oldName,     saved.getName());
        appendChange(summary, "description",oldDesc,     saved.getDescription());
        appendChange(summary, "priority",   oldPriority, saved.getPriority());
        appendChange(summary, "status",     oldStatus,   saved.getStatus());
        appendChange(summary, "dueDate",    oldDueDate,  saved.getDueDate());
        appendChange(summary, "endDate",    oldEndDate,  saved.getEndDate());

        // Créer l'historique uniquement s'il y a un vrai changement
        if (summary.length() > 0) {
            TaskHistory h = new TaskHistory();
            h.setTaskId(saved.getId());
            h.setChangedBy(currentUserId()); // peut être null si non dispo
            h.setChangeDescription(summary.toString());
            taskHistoryService.createHistory(h);
        }

        return saved;
    }

    /**
     * PATCH /tasks/{id}/status
     * Historise UNIQUEMENT le changement de statut (ex: "status: TODO -> IN_PROGRESS").
     */
    @Transactional
    public Task updateTaskStatus(Long id, StatusType newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));

        if (task.getStatus() == newStatus) {
            return task; // pas de changement -> pas d'historique
        }

        StatusType old = task.getStatus();
        task.setStatus(newStatus);
        Task saved = taskRepository.save(task);

        TaskHistory h = new TaskHistory();
        h.setTaskId(saved.getId());
        h.setChangedBy(currentUserId());
        h.setChangeDescription("status: " + old + " -> " + newStatus);
        taskHistoryService.createHistory(h);

        return saved;
    }

    // ----------------- Helpers -----------------

    /** J'accepte le null "volontaire" (utile pour effacer dueDate/endDate). */
    private boolean isExplicitNull(Object value) {
        return value == null;
    }

    /** Concatène 'field: old -> new' si différent, avec virgule si besoin. */
    private void appendChange(StringBuilder sb, String field, Object oldVal, Object newVal) {
        if (equalsSafe(oldVal, newVal)) return;
        if (sb.length() > 0) sb.append(", ");
        sb.append(field).append(": ")
                .append(formatVal(oldVal))
                .append(" -> ")
                .append(formatVal(newVal));
    }

    /** Égalité null-safe. */
    private boolean equalsSafe(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    /** Mise en forme simple : quotes pour String, sinon toString, null explicite. */
    private String formatVal(Object v) {
        if (v == null) return "null";
        if (v instanceof String s) return "\"" + s + "\"";
        return String.valueOf(v);
    }

    /** Récupère l'id utilisateur depuis le SecurityContext (si principal est un User). */
    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof User u) {
            return u.getId();
        }
        return null;
    }
}
