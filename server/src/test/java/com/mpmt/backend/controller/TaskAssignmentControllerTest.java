package com.mpmt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpmt.backend.entity.TaskAssignment;
import com.mpmt.backend.service.TaskAssignmentService;
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
class TaskAssignmentControllerTest {

    @Mock
    private TaskAssignmentService service;

    @InjectMocks
    private TaskAssignmentController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAll_returnsList() throws Exception {
        // Arrange
        TaskAssignment assignment = new TaskAssignment();
        assignment.setId(1L);
        when(service.getAll()).thenReturn(List.of(assignment));

        // Act & Assert
        mockMvc.perform(get("/api/task-assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getById_ok() throws Exception {
        // Arrange
        TaskAssignment assignment = new TaskAssignment();
        assignment.setId(1L);
        when(service.getById(1L)).thenReturn(Optional.of(assignment));

        // Act & Assert
        mockMvc.perform(get("/api/task-assignments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_notFound() throws Exception {
        // Arrange
        when(service.getById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/task-assignments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByTaskId_returnsList() throws Exception {
        // Arrange
        TaskAssignment assignment = new TaskAssignment();
        assignment.setId(1L);
        when(service.getByTaskId(1L)).thenReturn(List.of(assignment));

        // Act & Assert
        mockMvc.perform(get("/api/task-assignments/by-task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getByProjectMemberId_returnsList() throws Exception {
        // Arrange
        TaskAssignment assignment = new TaskAssignment();
        assignment.setId(1L);
        when(service.getByProjectMemberId(1L)).thenReturn(List.of(assignment));

        // Act & Assert
        mockMvc.perform(get("/api/task-assignments/by-project-member/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getByTaskAndMember_ok() throws Exception {
        // Arrange
        TaskAssignment assignment = new TaskAssignment();
        assignment.setId(1L);
        when(service.getByTaskIdAndProjectMemberId(1L, 1L)).thenReturn(assignment);

        // Act & Assert
        mockMvc.perform(get("/api/task-assignments/by-task-and-member")
                        .param("taskId", "1")
                        .param("projectMemberId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByTaskAndMember_notFound() throws Exception {
        // Arrange
        when(service.getByTaskIdAndProjectMemberId(1L, 99L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/task-assignments/by-task-and-member")
                        .param("taskId", "1")
                        .param("projectMemberId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ok() throws Exception {
        // Arrange
        TaskAssignmentController.CreateAssignmentRequest request = new TaskAssignmentController.CreateAssignmentRequest();
        request.setTaskId(1L);
        request.setProjectMemberId(1L);

        TaskAssignment assignment = new TaskAssignment();
        assignment.setId(1L);

        when(service.getByTaskIdAndProjectMemberId(1L, 1L)).thenReturn(null);
        when(service.create(any(TaskAssignment.class))).thenReturn(assignment);

        // Act & Assert
        mockMvc.perform(post("/api/task-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_conflict() throws Exception {
        // Arrange
        TaskAssignmentController.CreateAssignmentRequest request = new TaskAssignmentController.CreateAssignmentRequest();
        request.setTaskId(1L);
        request.setProjectMemberId(1L);

        TaskAssignment existing = new TaskAssignment();
        existing.setId(1L);

        when(service.getByTaskIdAndProjectMemberId(1L, 1L)).thenReturn(existing);

        // Act & Assert
        mockMvc.perform(post("/api/task-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already assigned to this task"));
    }

    @Test
    void delete_ok() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/task-assignments/1"))
                .andExpect(status().isNoContent());
    }
}
