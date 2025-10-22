package com.mpmt.backend.service;

import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.entity.TaskAssignment;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.mail.MailService;
import com.mpmt.backend.repository.ProjectMemberRepository;
import com.mpmt.backend.repository.TaskAssignmentRepository;
import com.mpmt.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskAssignmentService {

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final MailService mailService;

    @Autowired
    public TaskAssignmentService(TaskAssignmentRepository taskAssignmentRepository,
                                 ProjectMemberRepository projectMemberRepository,
                                 TaskRepository taskRepository,
                                 MailService mailService) {
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskRepository = taskRepository;
        this.mailService = mailService;
    }

    public List<TaskAssignment> getAll() {
        return taskAssignmentRepository.findAll();
    }

    public Optional<TaskAssignment> getById(Long id) {
        return taskAssignmentRepository.findById(id);
    }

    public List<TaskAssignment> getByTaskId(Long taskId) {
        return taskAssignmentRepository.findByTaskId(taskId);
    }

    public List<TaskAssignment> getByProjectMemberId(Long projectMemberId) {
        return taskAssignmentRepository.findByProjectMemberId(projectMemberId);
    }

    public TaskAssignment getByTaskIdAndProjectMemberId(Long taskId, Long projectMemberId) {
        return taskAssignmentRepository.findByTaskIdAndProjectMemberId(taskId, projectMemberId);
    }

    /** Crée l’assignation + envoie un e-mail de notification (best-effort). */
    public TaskAssignment create(TaskAssignment assignment) {
        // 1) Anti-doublon : même taskId + projectMemberId => refuse
        TaskAssignment existing = taskAssignmentRepository
                .findByTaskIdAndProjectMemberId(assignment.getTaskId(), assignment.getProjectMemberId());
        if (existing != null) {
            throw new IllegalStateException("Already assigned");
        }

        // 2) Persist
        TaskAssignment saved = taskAssignmentRepository.save(assignment);

        // 3) Charger entités utiles pour l’e-mail
        Task task = taskRepository.findById(saved.getTaskId()).orElse(null);
        ProjectMember pm = projectMemberRepository.findById(saved.getProjectMemberId()).orElse(null);
        User assignee = (pm != null) ? pm.getUser() : null;

        // 4) Récupérer l'assigner depuis le SecurityContext (si principal = User)
        User assigner = null;
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User u) {
            assigner = u;
        }

        // 5) Envoi e-mail best-effort : on ne casse pas la requête si ça échoue
        try {
            if (task != null && assignee != null) {
                mailService.sendTaskAssignedEmail(task, assignee, assigner);
            }
        } catch (Exception e) {
            System.out.println("[TaskAssignmentService] Notification e-mail non envoyée: " + e.getMessage());
        }

        return saved;
    }

    public void delete(Long id) {
        taskAssignmentRepository.deleteById(id);
    }

    public List<Task> findTasksAssignedToUser(Long userId) {
        List<ProjectMember> memberships = projectMemberRepository.findByUser_Id(userId);
        List<Long> memberIds = memberships.stream()
                .map(ProjectMember::getId)
                .collect(Collectors.toList());

        List<TaskAssignment> assignments = taskAssignmentRepository.findByProjectMemberIdIn(memberIds);
        List<Long> taskIds = assignments.stream()
                .map(TaskAssignment::getTaskId)
                .collect(Collectors.toList());

        return taskRepository.findAllById(taskIds);
    }
}
