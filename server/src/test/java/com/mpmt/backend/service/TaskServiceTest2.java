package com.mpmt.backend.service;

import com.mpmt.backend.entity.*;
import com.mpmt.backend.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest2 {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskHistoryService taskHistoryService;

    @InjectMocks
    private TaskService service;

    private Task task;
    private Project project;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        task = new Task();
        task.setId(10L);
        task.setName("Test Task");
        task.setDescription("Description");
        task.setPriority(PriorityType.HIGH);
        task.setStatus(StatusType.TODO);
        task.setCreatedBy(1L);
        task.setProject(project);
        task.setDueDate(LocalDate.now().plusDays(7));
    }

    @Test
    void shouldGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));
        List<Task> result = service.getAllTasks();
        assertThat(result).hasSize(1);
        verify(taskRepository).findAll();
    }

    @Test
    void shouldGetTaskById() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        Optional<Task> result = service.getTaskById(10L);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Task");
    }

    @Test
    void shouldGetTasksByProjectId() {
        when(taskRepository.findByProject_Id(1L)).thenReturn(Arrays.asList(task));
        List<Task> result = service.getTasksByProjectId(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldGetTasksByProjectIdAndStatus() {
        when(taskRepository.findByProject_IdAndStatus(1L, StatusType.TODO))
                .thenReturn(Arrays.asList(task));
        List<Task> result = service.getTasksByProjectIdAndStatus(1L, StatusType.TODO);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(StatusType.TODO);
    }

    @Test
    void shouldCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        Task created = service.createTask(task);
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Task");
        verify(taskRepository).save(task);
    }

    @Test
    void shouldDeleteTask() {
        doNothing().when(taskRepository).deleteById(10L);
        service.deleteTask(10L);
        verify(taskRepository).deleteById(10L);
    }

    @Test
    void shouldUpdateTask() {
        // Mock Security Context
        User mockUser = new User();
        mockUser.setId(2L);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockUser);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Task existante
        Task existing = new Task();
        existing.setId(10L);
        existing.setName("Old Name");
        existing.setStatus(StatusType.TODO);
        existing.setPriority(PriorityType.LOW);
        existing.setCreatedBy(1L);
        existing.setProject(project);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(existing);
        when(taskHistoryService.createHistory(any(TaskHistory.class))).thenReturn(new TaskHistory());

        // Modification
        Task updates = new Task();
        updates.setId(10L);
        updates.setName("New Name");
        updates.setStatus(StatusType.IN_PROGRESS);

        Task updated = service.updateTask(updates);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getStatus()).isEqualTo(StatusType.IN_PROGRESS);
        verify(taskRepository).save(any(Task.class));
        verify(taskHistoryService).createHistory(any(TaskHistory.class));
    }

    @Test
    void shouldUpdateTaskStatus() {
        User mockUser = new User();
        mockUser.setId(2L);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockUser);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskHistoryService.createHistory(any(TaskHistory.class))).thenReturn(new TaskHistory());

        Task updated = service.updateTaskStatus(10L, StatusType.DONE);

        assertThat(updated.getStatus()).isEqualTo(StatusType.DONE);
        verify(taskHistoryService).createHistory(any(TaskHistory.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForUpdate() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        Task updates = new Task();
        updates.setId(999L);

        assertThatThrownBy(() -> service.updateTask(updates))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForStatusUpdate() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTaskStatus(999L, StatusType.DONE))
                .isInstanceOf(EntityNotFoundException.class);
    }
}