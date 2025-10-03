package com.mpmt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.Notification;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.service.UserService;
import com.mpmt.backend.service.ProjectMemberService;
import com.mpmt.backend.service.TaskAssignmentService;
import com.mpmt.backend.service.NotificationService;
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
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private ProjectMemberService projectMemberService;
    @MockBean
    private TaskAssignmentService taskAssignmentService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/users doit retourner la liste des utilisateurs")
    void getAllUsers() throws Exception {
        User user1 = new User(); user1.setId(1L); user1.setUsername("user1");
        User user2 = new User(); user2.setId(2L); user2.setUsername("user2");
        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @DisplayName("GET /api/users/{id} doit retourner l'utilisateur demandé")
    void getUserById_found() throws Exception {
        User user = new User(); user.setId(42L); user.setUsername("user42");
        Mockito.when(userService.getUserById(42L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user42"));
    }

    @Test
    @DisplayName("GET /api/users/{id} doit retourner 404 si non trouvé")
    void getUserById_notFound() throws Exception {
        Mockito.when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/users doit créer un utilisateur")
    void createUser_ok() throws Exception {
        User user = new User();
        user.setUsername("nouveau");
        user.setEmail("nouveau@example.com");
        user.setPassword("mdp");
        User saved = new User();
        saved.setId(1L);
        saved.setUsername("nouveau");
        saved.setEmail("nouveau@example.com");
        saved.setPassword("mdp");

        Mockito.when(userService.emailExists("nouveau@example.com")).thenReturn(false);
        Mockito.when(userService.createUser(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("nouveau"))
                .andExpect(jsonPath("$.email").value("nouveau@example.com"));
    }

    @Test
    @DisplayName("POST /api/users doit renvoyer 400 si email existe déjà")
    void createUser_emailExists() throws Exception {
        User user = new User();
        user.setUsername("duplicate");
        user.setEmail("duplicate@example.com");
        user.setPassword("mdp");

        Mockito.when(userService.emailExists("duplicate@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} doit supprimer un utilisateur")
    void deleteUser_ok() throws Exception {
        User user = new User(); user.setId(55L);
        Mockito.when(userService.getUserById(55L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/users/55"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} doit retourner 404 si non trouvé")
    void deleteUser_notFound() throws Exception {
        Mockito.when(userService.getUserById(77L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/77"))
                .andExpect(status().isNotFound());
    }

    // ===============================
    // ===== TESTS ENDPOINTS AVANCÉS ==
    // ===============================

    @Test
    @DisplayName("GET /api/users/{id}/projects doit retourner les projets du user")
    void getProjectsForUser() throws Exception {
        Project project = new Project();
        project.setId(10L); project.setName("ProjectX");
        Mockito.when(projectMemberService.findProjectsByUserId(1L))
                .thenReturn(Collections.singletonList(project));

        mockMvc.perform(get("/api/users/1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].name").value("ProjectX"));
    }

    @Test
    @DisplayName("GET /api/users/{id}/notifications doit retourner les notifications du user")
    void getNotificationsForUser() throws Exception {
        Notification notif = new Notification();
        notif.setId(100L); notif.setUserId(1L); notif.setContent("Notif content");
        Mockito.when(notificationService.getByUserId(1L))
                .thenReturn(Collections.singletonList(notif));

        mockMvc.perform(get("/api/users/1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L))
                .andExpect(jsonPath("$[0].content").value("Notif content"));
    }

    @Test
    @DisplayName("GET /api/users/{id}/tasks doit retourner les tâches assignées au user")
    void getAssignedTasksForUser() throws Exception {
        Task task = new Task();
        task.setId(5L); task.setName("Ma tâche");
        Mockito.when(taskAssignmentService.findTasksAssignedToUser(1L))
                .thenReturn(Collections.singletonList(task));

        mockMvc.perform(get("/api/users/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5L))
                .andExpect(jsonPath("$[0].name").value("Ma tâche"));
    }
}
