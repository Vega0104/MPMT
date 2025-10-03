package com.mpmt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpmt.backend.entity.TaskAssignment;
import com.mpmt.backend.service.TaskAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskAssignmentController.class)
class TaskAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskAssignmentService service;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskAssignment assignment;

    @BeforeEach
    void setUp() {
        assignment = new TaskAssignment();
        assignment.setId(1L);
        assignment.setTaskId(10L);
        assignment.setProjectMemberId(20L);
    }

    @Test
    void shouldReturnTaskAssignmentById() throws Exception {
        given(service.getById(1L)).willReturn(Optional.of(assignment));

        mockMvc.perform(get("/api/task-assignments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldCreateTaskAssignment() throws Exception {
        given(service.create(Mockito.any(TaskAssignment.class))).willReturn(assignment);

        mockMvc.perform(post("/api/task-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
