// java
package com.mpmt.backend.service;

import com.mpmt.backend.entity.PriorityType;
import com.mpmt.backend.entity.StatusType;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.entity.TaskHistory;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock TaskHistoryService taskHistoryService;
    @InjectMocks TaskService taskService;

    @AfterEach
    void clearCtx() { SecurityContextHolder.clearContext(); }

    private void setAuthUser(long id) {
        User u = new User(); u.setId(id);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(u, null, "ROLE_MEMBER")
        );
    }

    // --------- CRUD rapides (couverture facile)
    @Test
    void getAllTasks_ok() {
        when(taskRepository.findAll()).thenReturn(List.of(new Task()));
        assertEquals(1, taskService.getAllTasks().size());
    }

    @Test
    void getTaskById_ok_empty() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertTrue(taskService.getTaskById(1L).isEmpty());
    }

    @Test
    void getTasksByProjectId_ok() {
        when(taskRepository.findByProject_Id(10L)).thenReturn(List.of(new Task()));
        assertEquals(1, task_service_getTasksByProjectId().size());
    }

    // helper to avoid accidental refactoring name mismatch in IDEs
    private List<Task> task_service_getTasksByProjectId() {
        return taskService.getTasksByProjectId(10L);
    }

    @Test
    void getTasksByProjectIdAndStatus_ok() {
        when(taskRepository.findByProject_IdAndStatus(10L, StatusType.TODO)).thenReturn(List.of(new Task()));
        assertEquals(1, taskService.getTasksByProjectIdAndStatus(10L, StatusType.TODO).size());
    }

    @Test
    void createTask_ok() {
        Task t = new Task(); t.setName("N");
        when(taskRepository.save(t)).thenReturn(t);
        assertEquals("N", taskService.createTask(t).getName());
    }

    @Test
    void deleteTask_ok() {
        taskService.deleteTask(99L);
        verify(taskRepository).deleteById(99L);
    }

    // --------- updateTask
    @Test
    void updateTask_missingId_throwsIAE() {
        Task t = new Task(); // no id
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(t));
    }

    @Test
    void updateTask_notFound_throwsENF() {
        Task t = new Task(); t.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(t));
    }

    @Test
    void updateTask_changesAndHistory_created() {
        // existing
        Task existing = new Task();
        existing.setId(1L);
        existing.setName("Old");
        existing.setDescription("D0");
        existing.setPriority(PriorityType.values()[0]);
        existing.setStatus(StatusType.TODO);
        LocalDate due = LocalDate.now(); existing.setDueDate(due);
        existing.setEndDate(null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        // save returns the mutated "existing"
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // incoming changes: name, description, priority, status, dueDate -> null (clear)
        Task incoming = new Task();
        incoming.setId(1L);
        incoming.setName("New");
        incoming.setDescription("D1");
        incoming.setPriority(PriorityType.values()[1]);
        incoming.setStatus(StatusType.IN_PROGRESS);
        incoming.setDueDate(null); // explicit null accepted
        // endDate left null (no change)

        setAuthUser(7L);

        Task result = taskService.updateTask(incoming);
        assertEquals("New", result.getName());
        assertEquals("D1", result.getDescription());
        assertEquals(incoming.getPriority(), result.getPriority());
        assertEquals(StatusType.IN_PROGRESS, result.getStatus());
        assertNull(result.getDueDate());
        assertNull(result.getEndDate());

        ArgumentCaptor<TaskHistory> cap = ArgumentCaptor.forClass(TaskHistory.class);
        verify(taskHistoryService).createHistory(cap.capture());
        TaskHistory h = cap.getValue();
        assertEquals(1L, h.getTaskId());
        assertEquals(7L, h.getChangedBy());
        String desc = h.getChangeDescription();

        // Assertions tolérantes : on vérifie la présence des tokens essentiels plutôt que la mise en forme exacte
        assertTrue(desc.toLowerCase().contains("name") && desc.contains("Old") && desc.contains("New") && desc.contains("->"));
        assertTrue(desc.toLowerCase().contains("description") && desc.contains("D0") && desc.contains("D1"));
        assertTrue(desc.toLowerCase().contains("priority") && desc.contains(existing.getPriority().name()) && desc.contains(incoming.getPriority().name()));
        assertTrue(desc.toLowerCase().contains("status") && desc.contains("TODO") && desc.contains("IN_PROGRESS"));
        assertTrue(desc.toLowerCase().contains("duedate") || desc.toLowerCase().contains("due date") || desc.contains("dueDate"));
    }

    @Test
    void updateTask_noEffectiveChange_noHistory() {
        // existing with null dates so setting null again is "no change"
        Task existing = new Task();
        existing.setId(2L);
        existing.setName("Same");
        existing.setDescription("Desc");
        existing.setPriority(PriorityType.values()[0]);
        existing.setStatus(StatusType.TODO);
        existing.setDueDate(null);
        existing.setEndDate(null);

        when(taskRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task incoming = new Task(); incoming.setId(2L); // all other fields null

        Task res = taskService.updateTask(incoming);
        assertEquals("Same", res.getName());
        verify(taskHistoryService, never()).createHistory(any());
    }

    // --------- updateTaskStatus
    @Test
    void updateTaskStatus_notFound_throws() {
        when(taskRepository.findById(55L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> taskService.updateTaskStatus(55L, StatusType.DONE));
    }

    @Test
    void updateTaskStatus_same_noSave_noHistory() {
        Task existing = new Task(); existing.setId(3L); existing.setStatus(StatusType.TODO);
        when(taskRepository.findById(3L)).thenReturn(Optional.of(existing));

        Task out = taskService.updateTaskStatus(3L, StatusType.TODO);
        assertSame(existing, out);
        verify(taskRepository, never()).save(any());
        verify(taskHistoryService, never()).createHistory(any());
    }

    @Test
    void updateTaskStatus_change_saves_andHistory() {
        Task existing = new Task(); existing.setId(4L); existing.setStatus(StatusType.TODO);
        when(taskRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        setAuthUser(99L);

        Task res = taskService.updateTaskStatus(4L, StatusType.IN_PROGRESS);
        assertEquals(StatusType.IN_PROGRESS, res.getStatus());

        verify(taskRepository).save(existing);

        ArgumentCaptor<TaskHistory> cap = ArgumentCaptor.forClass(TaskHistory.class);
        verify(taskHistoryService).createHistory(cap.capture());
        assertEquals(4L, cap.getValue().getTaskId());
        assertEquals(99L, cap.getValue().getChangedBy());
        assertEquals("status: TODO -> IN_PROGRESS", cap.getValue().getChangeDescription());
    }
}
