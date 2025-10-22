package com.mpmt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.repository.ProjectRepository;
import com.mpmt.backend.repository.UserRepository;
import com.mpmt.backend.service.ProjectMemberService;
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
class ProjectMemberControllerTest {

    @Mock
    private ProjectMemberService projectMemberService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectMemberController projectMemberController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectMemberController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllProjectMembers_returnsList() throws Exception {
        // Arrange
        ProjectMember member = new ProjectMember();
        member.setId(1L);
        when(projectMemberService.getAllMembers()).thenReturn(List.of(member));

        // Act & Assert
        mockMvc.perform(get("/api/project-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getProjectMemberById_ok() throws Exception {
        // Arrange
        ProjectMember member = new ProjectMember();
        member.setId(1L);
        when(projectMemberService.getById(1L)).thenReturn(Optional.of(member));

        // Act & Assert
        mockMvc.perform(get("/api/project-members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getProjectMemberById_notFound() throws Exception {
        // Arrange
        when(projectMemberService.getById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/project-members/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProjectMember_ok() throws Exception {
        // Arrange
        ProjectMemberController.CreateMemberRequest request = new ProjectMemberController.CreateMemberRequest();
        request.setProjectId(1L);
        request.setUserId(1L);
        request.setRole("MEMBER");

        Project project = new Project();
        project.setId(1L);
        User user = new User();
        user.setId(1L);

        ProjectMember member = new ProjectMember();
        member.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectMemberService.getByUserIdAndProjectId(1L, 1L)).thenReturn(Optional.empty());
        when(projectMemberService.createProjectMember(any(ProjectMember.class))).thenReturn(member);

        // Act & Assert
        mockMvc.perform(post("/api/project-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createProjectMember_conflict() throws Exception {
        // Arrange
        ProjectMemberController.CreateMemberRequest request = new ProjectMemberController.CreateMemberRequest();
        request.setProjectId(1L);
        request.setUserId(1L);
        request.setRole("MEMBER");

        when(projectMemberService.getByUserIdAndProjectId(1L, 1L)).thenReturn(Optional.of(new ProjectMember()));

        // Act & Assert
        mockMvc.perform(post("/api/project-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProjectMember_ok() throws Exception {
        // Arrange
        when(projectMemberService.getById(1L)).thenReturn(Optional.of(new ProjectMember()));

        // Act & Assert
        mockMvc.perform(delete("/api/project-members/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProjectMember_notFound() throws Exception {
        // Arrange
        when(projectMemberService.getById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/project-members/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRole_ok() throws Exception {
        // Arrange
        ProjectMemberController.RoleUpdateRequest request = new ProjectMemberController.RoleUpdateRequest();
        request.setRole("ADMIN");

        ProjectMember member = new ProjectMember();
        member.setId(1L);
        member.setRole(RoleType.ADMIN);

        when(projectMemberService.updateRole(1L, "ADMIN")).thenReturn(Optional.of(member));

        // Act & Assert
        mockMvc.perform(put("/api/project-members/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void updateRole_notFound() throws Exception {
        // Arrange
        ProjectMemberController.RoleUpdateRequest request = new ProjectMemberController.RoleUpdateRequest();
        request.setRole("ADMIN");

        when(projectMemberService.updateRole(99L, "ADMIN")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/project-members/99/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
