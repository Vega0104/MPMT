package com.mpmt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpmt.backend.DTO.StatusUpdateRequest;
import com.mpmt.backend.entity.StatusType;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllTasks_returnsList() throws Exception {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        when(taskService.getAllTasks()).thenReturn(List.of(task));

        // Act & Assert
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Task"));
    }

    @Test
    void getTaskById_ok() throws Exception {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Task"));
    }

    @Test
    void getTaskById_notFound() throws Exception {
        // Arrange
        when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasksByStatus_ok() throws Exception {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setStatus(StatusType.TODO);
        when(taskService.getTasksByProjectIdAndStatus(1L, StatusType.TODO)).thenReturn(List.of(task));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/by-status")
                        .param("projectId", "1")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    void createTask_ok() throws Exception {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("New Task");
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Task"));
    }

    @Test
    void deleteTask_ok() throws Exception {
        // Arrange
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(new Task()));

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_notFound() throws Exception {
        // Arrange
        when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_ok() throws Exception {
        // Arrange
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setName("Old Name");

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setName("New Name");

        when(taskService.getTaskById(1L)).thenReturn(Optional.of(existingTask));
        when(taskService.updateTask(any(Task.class))).thenReturn(updatedTask);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void updateTask_notFound() throws Exception {
        // Arrange
        when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Name\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_ok() throws Exception {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setStatus(StatusType.IN_PROGRESS);

        StatusUpdateRequest request = new StatusUpdateRequest(StatusType.IN_PROGRESS);

        when(taskService.updateTaskStatus(1L, StatusType.IN_PROGRESS)).thenReturn(task);

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateStatus_notFound() throws Exception {
        // Arrange
        StatusUpdateRequest request = new StatusUpdateRequest(StatusType.IN_PROGRESS);
        when(taskService.updateTaskStatus(99L, StatusType.IN_PROGRESS)).thenThrow(new EntityNotFoundException());

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
