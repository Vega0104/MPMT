package com.mpmt.backend.controller;

import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.security.JwtAuthFilter;
import com.mpmt.backend.security.ProjectSecurity;
import com.mpmt.backend.service.ProjectMemberService;
import com.mpmt.backend.service.ProjectService;
import com.mpmt.backend.service.TaskService;
import com.mpmt.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProjectController.class,
        excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("deprecation")
class ProjectControllerTest {
    @Autowired MockMvc mvc;

    @MockBean ProjectService projectService;
    @MockBean ProjectMemberService projectMemberService;
    @MockBean TaskService taskService;
    @MockBean UserService userService;
    @MockBean
    private ProjectSecurity projectSecurity;


    // ✅ si malgré tout Spring tente d’instancier le filtre:
    @MockBean(com.mpmt.backend.security.JwtService.class) private com.mpmt.backend.security.JwtService jwtService;
    @MockBean(JwtAuthFilter.class) private JwtAuthFilter jwtAuthFilter;


    private void setAuthPrincipal(Object principal) {
        var auth = new org.springframework.security.authentication.TestingAuthenticationToken(principal, null, "ROLE_MEMBER");
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getProjectById_ok() throws Exception {
        Project p = new Project();
        p.setId(1L);
        p.setName("Alpha");
        p.setDescription("Desc");
        p.setStartDate(new Date());
        p.setCreatedAt(new Date());

        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p));

        mvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alpha"));
    }

    @Test
    void getProjectById_notFound() throws Exception {
        when(projectService.getProjectById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/projects/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProjectByName_ok() throws Exception {
        Project p = new Project(); p.setId(2L); p.setName("Alpha");
        when(projectService.getProjectByName("Alpha")).thenReturn(Optional.of(p));

        mvc.perform(get("/api/projects/by-name/Alpha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getProjectByName_notFound() throws Exception {
        when(projectService.getProjectByName("Ghost")).thenReturn(Optional.empty());
        mvc.perform(get("/api/projects/by-name/Ghost")).andExpect(status().isNotFound());
    }

    @Test
    void getAllProjects_returnsSummaries() throws Exception {
        Project p = new Project(); p.setId(1L); p.setName("A"); p.setDescription("D");
        p.setStartDate(new java.util.Date()); p.setCreatedAt(new java.util.Date());
        when(projectService.getAllProjects()).thenReturn(java.util.List.of(p));

        mvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[0].description").value("D"));
    }
    @Test
    void getProjectStats_empty() throws Exception {
        when(taskService.getTasksByProjectId(10L)).thenReturn(java.util.List.of());
        mvc.perform(get("/api/projects/10/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").value(0))
                .andExpect(jsonPath("$.progress").value(0));
    }

    @Test
    void getTasksForProject_ok() throws Exception {
        when(taskService.getTasksByProjectId(5L)).thenReturn(java.util.List.of(new Task()));
        mvc.perform(get("/api/projects/5/tasks")).andExpect(status().isOk());
    }

    @Test
    void getMembersForProject_ok() throws Exception {
        when(projectMemberService.findMembersByProjectId(6L)).thenReturn(java.util.List.of(new ProjectMember()));
        mvc.perform(get("/api/projects/6/members")).andExpect(status().isOk());
    }

    @Test
    void updateProject_notFound() throws Exception {
        when(projectService.getProjectById(99L)).thenReturn(Optional.empty());
        mvc.perform(put("/api/projects/99")
                        .contentType("application/json")
                        .content("{\"name\":\"N\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProject_ok() throws Exception {
        Project existing=new Project(); existing.setId(7L);
        when(projectService.getProjectById(7L)).thenReturn(Optional.of(existing));
        Project saved=new Project(); saved.setId(7L); saved.setName("N");
        when(projectService.updateProject(org.mockito.ArgumentMatchers.any())).thenReturn(saved);

        mvc.perform(put("/api/projects/7")
                        .contentType("application/json")
                        .content("{\"name\":\"N\",\"description\":\"D\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("N"));
    }


    @Test
    void createTaskForProject_notFound() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(Optional.empty());
        mvc.perform(post("/api/projects/1/tasks")
                        .contentType("application/json")
                        .content("{\"title\":\"T\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTaskForProject_ok() throws Exception {
        when(projectService.getProjectById(2L)).thenReturn(Optional.of(new Project()));
        Task created=new Task(); created.setId(123L);
        when(taskService.createTask(org.mockito.ArgumentMatchers.any())).thenReturn(created);

        mvc.perform(post("/api/projects/2/tasks")
                        .contentType("application/json")
                        .content("{\"title\":\"T\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123));
    }

    @Test
    void createProject_ok_addsCreatorAsAdmin() throws Exception {
        // principal = User JPA
        com.mpmt.backend.entity.User u = new com.mpmt.backend.entity.User();
        u.setId(99L);
        setAuthPrincipal(u);

        // le projet créé par le service
        Project created = new Project(); created.setId(42L);
        when(projectService.createProject(any(Project.class))).thenReturn(created);

        mvc.perform(post("/api/projects")
                        .contentType("application/json")
                        .content("{\"name\":\"Alpha\",\"description\":\"D\",\"startDate\":\"2025-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42));

        // vérifie l’ajout du membre ADMIN
        var captor = org.mockito.ArgumentCaptor.forClass(ProjectMember.class);
        verify(projectMemberService).createMember(captor.capture());
        ProjectMember pm = captor.getValue();
        assertEquals(42L, pm.getProjectId());
        assertEquals(99L, pm.getUserId());
        assertEquals(RoleType.ADMIN, pm.getRole());
    }

    @Test
    void deleteProject_forbidden() throws Exception {
        when(((com.mpmt.backend.security.ProjectSecurity) projectSecurity)
                .canDeleteProject(eq(7L), any())).thenReturn(false);
        // si cast dur te gêne, fais: when(org.mockito.Mockito.mockingDetails(projectSecurity).isMock()).thenReturn(true);

        mvc.perform(delete("/api/projects/7"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProject_notFound() throws Exception {
        when(((com.mpmt.backend.security.ProjectSecurity) projectSecurity)
                .canDeleteProject(eq(8L), any())).thenReturn(true);
        when(projectService.getProjectById(8L)).thenReturn(Optional.empty());

        mvc.perform(delete("/api/projects/8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProject_ok() throws Exception {
        when(((com.mpmt.backend.security.ProjectSecurity) projectSecurity)
                .canDeleteProject(eq(9L), any())).thenReturn(true);
        when(projectService.getProjectById(9L)).thenReturn(Optional.of(new Project()));

        mvc.perform(delete("/api/projects/9"))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject(9L);
    }





}
