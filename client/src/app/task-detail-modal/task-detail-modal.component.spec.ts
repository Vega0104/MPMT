import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskDetailModalComponent } from './task-detail-modal.component';
import { TaskService, Task } from '../services/task-service/task.service';
import { TaskAssignmentService } from '../services/tast-assignment-service/task-assignment.service';
import { ProjectMemberService, ProjectMember } from '../services/project-member-service/project-member.service';
import { TaskHistoryService } from '../services/task-history/task-history.service';
import { of, throwError } from 'rxjs';

describe('TaskDetailModalComponent', () => {
  let component: TaskDetailModalComponent;
  let fixture: ComponentFixture<TaskDetailModalComponent>;
  let mockTaskService: jest.Mocked<Partial<TaskService>>;
  let mockTaskAssignmentService: jest.Mocked<Partial<TaskAssignmentService>>;
  let mockProjectMemberService: jest.Mocked<Partial<ProjectMemberService>>;
  let mockTaskHistoryService: jest.Mocked<Partial<TaskHistoryService>>;

  const mockTask: Task = {
    id: 1,
    name: 'Test Task',
    description: 'Test Description',
    status: 'TODO',
    priority: 'MEDIUM',
    dueDate: '2025-12-31',
    projectId: 1,
    createdBy: 1,
  };

  const mockMembers: ProjectMember[] = [
    { id: 1, user: { id: 1, username: 'user1', email: 'user1@test.com' }, project: { id: 1, name: 'Project' }, role: 'ADMIN' },
    { id: 2, user: { id: 2, username: 'user2', email: 'user2@test.com' }, project: { id: 1, name: 'Project' }, role: 'MEMBER' }
  ];

  const mockAssignments = [{ id: 1, taskId: 1, projectMemberId: 1 }];
  const mockHistory = [{ id: 1, taskId: 1, field: 'status', oldValue: 'TODO', newValue: 'IN_PROGRESS', changedAt: '2025-01-01' }];

  beforeEach(async () => {
    mockTaskService = {
      updateTask: jest.fn().mockReturnValue(of(mockTask))
    };

    mockTaskAssignmentService = {
      getByTaskId: jest.fn().mockReturnValue(of(mockAssignments)),
      assignTask: jest.fn().mockReturnValue(of({})),
      removeAssignment: jest.fn().mockReturnValue(of({}))
    };

    mockProjectMemberService = {
      getMembersByProjectId: jest.fn().mockReturnValue(of(mockMembers))
    };

    mockTaskHistoryService = {
      getHistoryByTaskId: jest.fn().mockReturnValue(of(mockHistory))
    };

    await TestBed.configureTestingModule({
      imports: [TaskDetailModalComponent]
    })
      .overrideComponent(TaskDetailModalComponent, {
        set: {
          providers: [
            { provide: TaskService, useValue: mockTaskService },
            { provide: TaskAssignmentService, useValue: mockTaskAssignmentService },
            { provide: ProjectMemberService, useValue: mockProjectMemberService },
            { provide: TaskHistoryService, useValue: mockTaskHistoryService }
          ]
        }
      })
      .compileComponents();

    fixture = TestBed.createComponent(TaskDetailModalComponent);
    component = fixture.componentInstance;
    component.task = { ...mockTask };
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load task data on init', () => {
    fixture.detectChanges();

    expect(component.form.name).toBe('Test Task');
    expect(component.form.status).toBe('TODO');
    expect(mockTaskAssignmentService.getByTaskId).toHaveBeenCalledWith(1);
    expect(mockProjectMemberService.getMembersByProjectId).toHaveBeenCalledWith(1);
    expect(mockTaskHistoryService.getHistoryByTaskId).toHaveBeenCalledWith(1);
  });

  it('should populate assigned members', () => {
    fixture.detectChanges();

    expect(component.assignedMembers.length).toBe(1);
    expect(component.assignedUsernames).toBe('user1');
  });

  it('should toggle member selection', () => {
    component.toggleMember(2, true);
    expect(component.selectedMemberIds.has(2)).toBe(true);

    component.toggleMember(2, false);
    expect(component.selectedMemberIds.has(2)).toBe(false);
  });

  it('should emit close event', () => {
    const closeSpy = jest.spyOn(component.close, 'emit');
    component.onClose();
    expect(closeSpy).toHaveBeenCalled();
  });

  it('should show error if name is empty', async () => {
    component.form.name = '';
    await component.onSave();
    expect(component.error).toBe('Title is required');
  });

  it('should update task successfully', async () => {
    fixture.detectChanges();
    const statusUpdatedSpy = jest.spyOn(component.statusUpdated, 'emit');
    const closeSpy = jest.spyOn(component.close, 'emit');

    component.form.name = 'Updated Task';
    component.form.status = 'DONE';
    await component.onSave();

    expect(mockTaskService.updateTask).toHaveBeenCalledWith(1, expect.objectContaining({
      name: 'Updated Task',
      status: 'DONE'
    }));
    expect(statusUpdatedSpy).toHaveBeenCalled();
    expect(closeSpy).toHaveBeenCalled();
  });

  it('should handle update error', async () => {
    mockTaskService.updateTask = jest.fn().mockReturnValue(
      throwError(() => ({ error: { message: 'Update failed' } }))
    );

    component.form.name = 'Test';
    await component.onSave();

    expect(component.error).toBe('Update failed');
    expect(component.isSubmitting).toBe(false);
  });

  it('should add new assignment', async () => {
    fixture.detectChanges();
    component.selectedMemberIds.add(2);

    await component.onSave();

    expect(mockTaskAssignmentService.assignTask).toHaveBeenCalledWith(1, 2);
  });

  it('should remove assignment', async () => {
    fixture.detectChanges();
    component.selectedMemberIds.clear();

    await component.onSave();

    expect(mockTaskAssignmentService.removeAssignment).toHaveBeenCalledWith(1);
  });
});
