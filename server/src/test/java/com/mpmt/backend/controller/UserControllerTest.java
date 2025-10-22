package com.mpmt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.Notification;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.service.UserService;
import com.mpmt.backend.service.ProjectMemberService;
import com.mpmt.backend.service.NotificationService;
import com.mpmt.backend.service.TaskAssignmentService;
import com.mpmt.backend.service.TaskService;
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
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ProjectMemberService projectMemberService;

    @Mock
    private TaskAssignmentService taskAssignmentService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllUsers_returnsList() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getAllUsers()).thenReturn(List.of(user));

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void getUserById_ok() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserById_notFound() throws Exception {
        // Arrange
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByUsername_ok() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(get("/api/users/by-username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserByUsername_notFound() throws Exception {
        // Arrange
        when(userService.getUserByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/by-username/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_ok() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("newuser");
        user.setEmail("new@example.com");

        when(userService.emailExists("new@example.com")).thenReturn(false);
        when(userService.createUser(any(User.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void createUser_emailExists() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("existing@example.com");

        when(userService.emailExists("existing@example.com")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_ok() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(new User()));

        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        // Arrange
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProjectsForUser_ok() throws Exception {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        when(projectMemberService.findProjectsByUserId(1L)).thenReturn(List.of(project));

        // Act & Assert
        mockMvc.perform(get("/api/users/1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getNotificationsForUser_ok() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setId(1L);
        when(notificationService.getByUserId(1L)).thenReturn(List.of(notification));

        // Act & Assert
        mockMvc.perform(get("/api/users/1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAssignedTasksForUser_ok() throws Exception {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        when(taskAssignmentService.findTasksAssignedToUser(1L)).thenReturn(List.of(task));

        // Act & Assert
        mockMvc.perform(get("/api/users/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
