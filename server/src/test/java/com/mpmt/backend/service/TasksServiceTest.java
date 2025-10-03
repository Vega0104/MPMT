package com.mpmt.backend.service;

import com.mpmt.backend.entity.Task;
import com.mpmt.backend.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    public TaskServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAndGetTask() {
        Task task = new Task();
        task.setName("Mock Tâche");
        task.setPriority("HIGH");
        task.setStatus("TODO");
        task.setCreatedBy(1L);
        task.setProjectId(1L);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task created = taskService.createTask(task);
        assertThat(created.getName()).isEqualTo("Mock Tâche");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        Optional<Task> found = taskService.getTaskById(1L);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Mock Tâche");
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task();
        task1.setName("Tâche 1");
        Task task2 = new Task();
        task2.setName("Tâche 2");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getAllTasks();
        assertThat(tasks).hasSize(2);
    }
}
