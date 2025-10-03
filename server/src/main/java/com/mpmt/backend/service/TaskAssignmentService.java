package com.mpmt.backend.service;

import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.entity.TaskAssignment;
import com.mpmt.backend.repository.ProjectMemberRepository;
import com.mpmt.backend.repository.TaskAssignmentRepository;
import com.mpmt.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskAssignmentService {

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskAssignmentService(
            TaskAssignmentRepository taskAssignmentRepository,
            ProjectMemberRepository projectMemberRepository,
            TaskRepository taskRepository
    ) {
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskRepository = taskRepository;
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

    public TaskAssignment create(TaskAssignment assignment) {
        return taskAssignmentRepository.save(assignment);
    }

    public void delete(Long id) {
        taskAssignmentRepository.deleteById(id);
    }

    public List<Task> findTasksAssignedToUser(Long userId) {
        List<ProjectMember> memberships = projectMemberRepository.findByUserId(userId);
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
