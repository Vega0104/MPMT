package com.mpmt.backend.DTO;

import com.mpmt.backend.entity.PriorityType;
import com.mpmt.backend.entity.StatusType;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UpdateTaskRequestTest {
    @Test
    void testGettersAndSetters() {
        UpdateTaskRequest request = new UpdateTaskRequest();

        request.setName("Task name");
        assertEquals("Task name", request.getName());

        request.setDescription("Task description");
        assertEquals("Task description", request.getDescription());

        request.setStatus(StatusType.IN_PROGRESS);
        assertEquals(StatusType.IN_PROGRESS, request.getStatus());

        request.setPriority(PriorityType.HIGH);
        assertEquals(PriorityType.HIGH, request.getPriority());

        request.setDueDate("2025-12-31");
        assertEquals("2025-12-31", request.getDueDate());

        request.setEndDate("2025-12-30");
        assertEquals("2025-12-30", request.getEndDate());
    }
}

class ProjectSummaryTest {
    @Test
    void testConstructorAndGetters() {
        Date startDate = new Date();
        Date createdAt = new Date();

        ProjectSummary summary = new ProjectSummary(
                1L,
                "Project Name",
                "Project Description",
                startDate,
                createdAt
        );

        assertEquals(1L, summary.getId());
        assertEquals("Project Name", summary.getName());
        assertEquals("Project Description", summary.getDescription());
        assertEquals(startDate, summary.getStartDate());
        assertEquals(createdAt, summary.getCreatedAt());
    }
}

class CreateProjectRequestTest {
    @Test
    void testGettersAndSetters() {
        CreateProjectRequest request = new CreateProjectRequest();

        request.setName("New Project");
        assertEquals("New Project", request.getName());

        request.setDescription("Project description");
        assertEquals("Project description", request.getDescription());

        Date startDate = new Date();
        request.setStartDate(startDate);
        assertEquals(startDate, request.getStartDate());
    }
}

class StatusUpdateRequestTest {
    @Test
    void testRecord() {
        StatusUpdateRequest request = new StatusUpdateRequest(StatusType.DONE);
        assertEquals(StatusType.DONE, request.status());
    }

    @Test
    void testRecordWithAllStatuses() {
        assertEquals(StatusType.TODO, new StatusUpdateRequest(StatusType.TODO).status());
        assertEquals(StatusType.IN_PROGRESS, new StatusUpdateRequest(StatusType.IN_PROGRESS).status());
        assertEquals(StatusType.DONE, new StatusUpdateRequest(StatusType.DONE).status());
    }
}

class ProjectStatsTest {
    @Test
    void testGettersAndSetters() {
        ProjectStats stats = new ProjectStats();

        stats.setTotalTasks(100);
        assertEquals(100, stats.getTotalTasks());

        stats.setTodoCount(30L);
        assertEquals(30L, stats.getTodoCount());

        stats.setInProgressCount(40L);
        assertEquals(40L, stats.getInProgressCount());

        stats.setDoneCount(30L);
        assertEquals(30L, stats.getDoneCount());

        stats.setProgress(60);
        assertEquals(60, stats.getProgress());
    }
}

class SignupRequestTest {
    @Test
    void testDefaultConstructor() {
        SignupRequest request = new SignupRequest();
        assertNotNull(request);
    }

    @Test
    void testParameterizedConstructor() {
        SignupRequest request = new SignupRequest("john", "john@example.com", "password123");

        assertEquals("john", request.getUsername());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testGettersAndSetters() {
        SignupRequest request = new SignupRequest();

        request.setUsername("alice");
        assertEquals("alice", request.getUsername());

        request.setEmail("alice@example.com");
        assertEquals("alice@example.com", request.getEmail());

        request.setPassword("securepass");
        assertEquals("securepass", request.getPassword());
    }
}

class LoginRequestTest {
    @Test
    void testDefaultConstructor() {
        LoginRequest request = new LoginRequest();
        assertNotNull(request);
    }

    @Test
    void testParameterizedConstructor() {
        LoginRequest request = new LoginRequest("user@example.com", "mypassword");

        assertEquals("user@example.com", request.getEmail());
        assertEquals("mypassword", request.getPassword());
    }

    @Test
    void testGettersAndSetters() {
        LoginRequest request = new LoginRequest();

        request.setEmail("test@example.com");
        assertEquals("test@example.com", request.getEmail());

        request.setPassword("testpass123");
        assertEquals("testpass123", request.getPassword());
    }
}