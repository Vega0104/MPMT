package com.mpmt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpmt.backend.entity.*;
import com.mpmt.backend.service.ProjectMemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectMemberController.class)
class ProjectMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectMemberService projectMemberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Devrait créer et récupérer un ProjectMember via API")
    void shouldCreateAndGetProjectMember() throws Exception {
        // Prépare un ProjectMember simulé
        User user = new User();
        user.setId(1L);
        user.setUsername("junituser");
        user.setEmail("junit@api.com");
        user.setPassword("pw");

        Project project = new Project();
        project.setId(2L);
        project.setName("ProjetJUnit");

        ProjectMember pm = new ProjectMember();
        pm.setId(11L);
        pm.setRole(RoleType.MEMBER);
        pm.setUser(user);
        pm.setProject(project);

        Mockito.when(projectMemberService.createProjectMember(any(ProjectMember.class))).thenReturn(pm);
        Mockito.when(projectMemberService.getById(11L)).thenReturn(Optional.of(pm));
        Mockito.when(projectMemberService.getAllMembers()).thenReturn(Collections.singletonList(pm));

        // POST
        mockMvc.perform(post("/api/project-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MEMBER"));

        // GET by id
        mockMvc.perform(get("/api/project-members/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("junituser"));

        // GET all
        mockMvc.perform(get("/api/project-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].project.name").value("ProjetJUnit"));
    }

}
