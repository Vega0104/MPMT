package com.mpmt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.service.ProjectMemberService;
import com.mpmt.backend.service.ProjectService;
import com.mpmt.backend.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;
    @MockBean
    private ProjectMemberService projectMemberService;
    @MockBean
    private TaskService taskService;  // <-- Ajouté ici !

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Devrait créer et récupérer un projet via HTTP")
    void shouldCreateAndGetProject() throws Exception {
        // Fabrique un projet
        Project project = new Project();
        project.setId(1L);
        project.setName("Projet API");
        project.setDescription("Via API test");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 2, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        project.setStartDate(startDate);
        project.setCreatedAt(new Date());

        // Mock le service pour le POST
        Mockito.when(projectService.createProject(any(Project.class))).thenReturn(project);

        // Mock le service pour le GET
        Mockito.when(projectService.getProjectById(1L)).thenReturn(Optional.of(project));
        Mockito.when(projectService.getAllProjects()).thenReturn(Collections.singletonList(project));

        // POST
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Projet API"));

        // GET by id
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Via API test"));

        // GET all
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Projet API"));
    }

    @Test
    @DisplayName("GET /api/projects/{id}/members retourne les membres du projet")
    void shouldGetMembersForProject() throws Exception {
        Project project = new Project();
        project.setId(10L);
        project.setName("Projet API");

        User user = new User();
        user.setId(1L);
        user.setUsername("user_member");

        ProjectMember pm = new ProjectMember();
        pm.setId(100L);
        pm.setRole(RoleType.MEMBER);
        pm.setUser(user);
        pm.setProject(project);

        Mockito.when(projectMemberService.findMembersByProjectId(10L))
                .thenReturn(Collections.singletonList(pm));

        mockMvc.perform(get("/api/projects/10/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username").value("user_member"))
                .andExpect(jsonPath("$[0].role").value("MEMBER"));
    }

    @Test
    @DisplayName("GET /api/projects/{id}/tasks doit retourner les tâches du projet")
    void shouldGetTasksForProject() throws Exception {
        Task t1 = new Task(); t1.setId(1L); t1.setName("Tâche A");
        Task t2 = new Task(); t2.setId(2L); t2.setName("Tâche B");

        Mockito.when(taskService.getTasksByProjectId(42L)).thenReturn(Arrays.asList(t1, t2));

        mockMvc.perform(get("/api/projects/42/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Tâche A"))
                .andExpect(jsonPath("$[1].name").value("Tâche B"));
    }
}
