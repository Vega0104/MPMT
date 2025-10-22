import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectDetailComponent } from './project-detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../services/project-service/project.service';
import { TaskService, Task } from '../services/task-service/task.service';
import { ProjectMemberService } from '../services/project-member-service/project-member.service';
import { of, throwError } from 'rxjs';

describe('ProjectDetailComponent', () => {
  let component: ProjectDetailComponent;
  let fixture: ComponentFixture<ProjectDetailComponent>;
  let mockProjectService: any;
  let mockTaskService: any;
  let mockProjectMemberService: any;
  let mockRouter: any;

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1'),
      },
    },
  };

  const mockProject = {
    id: 1,
    name: 'Test Project',
    description: 'Test Description',
  };

  const mockTasks: Task[] = [
    {
      id: 1,
      name: 'Task 1',
      projectId: 1,
      status: 'TODO',
      description: 'Test description 1',
      dueDate: '2025-12-31',
      priority: 'HIGH',
      createdBy: 1
    },
    {
      id: 2,
      name: 'Task 2',
      projectId: 1,
      status: 'IN_PROGRESS',
      description: 'Test description 2',
      dueDate: '2025-12-31',
      priority: 'MEDIUM',
      createdBy: 1
    },
    {
      id: 3,
      name: 'Task 3',
      projectId: 2,
      status: 'TODO',
      description: 'Test description 3',
      dueDate: '2025-12-31',
      priority: 'LOW',
      createdBy: 2
    },
  ];

  const mockMembers = [
    { id: 1, userId: 1, projectId: 1, role: 'admin' },
    { id: 2, userId: 2, projectId: 1, role: 'member' },
  ];

  beforeEach(async () => {
    mockProjectService = {
      getProjectById: jest.fn().mockReturnValue(of(mockProject)),
      deleteProject: jest.fn().mockReturnValue(of(null)),
    };

    mockTaskService = {
      getAllTasks: jest.fn().mockReturnValue(of(mockTasks)),
    };

    mockProjectMemberService = {
      getMembersByProjectId: jest.fn().mockReturnValue(of(mockMembers)),
    };

    mockRouter = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ProjectDetailComponent],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter },
        { provide: ProjectService, useValue: mockProjectService },
        { provide: TaskService, useValue: mockTaskService },
        { provide: ProjectMemberService, useValue: mockProjectMemberService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load project on init', () => {
    fixture.detectChanges();

    expect(mockProjectService.getProjectById).toHaveBeenCalledWith(1);
    // @ts-ignore
    expect(component.project).toEqual(mockProject);
  });

  it('should load tasks for the project', () => {
    fixture.detectChanges();

    expect(mockTaskService.getAllTasks).toHaveBeenCalled();
    expect(component.tasks.length).toBe(2);
    expect(component.tasks.every(t => t.projectId === 1)).toBe(true);
  });

  it('should load members for the project', () => {
    fixture.detectChanges();

    expect(mockProjectMemberService.getMembersByProjectId).toHaveBeenCalledWith(1);
    // @ts-ignore
    expect(component.members).toEqual(mockMembers);
  });

  it('should handle empty tasks array', () => {
    mockTaskService.getAllTasks.mockReturnValue(of([]));
    fixture.detectChanges();

    expect(component.tasks).toEqual([]);
  });

  it('should handle tasks loading error', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    mockTaskService.getAllTasks.mockReturnValue(throwError(() => new Error('Test error')));

    fixture.detectChanges();

    // @ts-ignore
    expect(consoleErrorSpy).toHaveBeenCalledWith('Error loading tasks', expect.any(Error));
    consoleErrorSpy.mockRestore();
  });

  it('should open and close create task modal', () => {
    component.openCreateTaskModal();
    expect(component.showCreateTaskModal).toBe(true);

    component.closeCreateTaskModal();
    expect(component.showCreateTaskModal).toBe(false);
  });

  it('should reload tasks after task creation', () => {
    fixture.detectChanges();
    mockTaskService.getAllTasks.mockClear();

    component.onTaskCreated();

    expect(mockTaskService.getAllTasks).toHaveBeenCalled();
  });

  it('should open and close add member modal', () => {
    component.openAddMemberModal();
    expect(component.showAddMemberModal).toBe(true);

    component.closeAddMemberModal();
    expect(component.showAddMemberModal).toBe(false);
  });

  it('should reload members after member added', () => {
    fixture.detectChanges();
    mockProjectMemberService.getMembersByProjectId.mockClear();

    component.onMemberAdded();

    expect(component.showAddMemberModal).toBe(false);
    expect(mockProjectMemberService.getMembersByProjectId).toHaveBeenCalledWith(1);
  });

  it('should delete project and navigate to dashboard', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    mockProjectService.deleteProject.mockReturnValue(of(null));
    fixture.detectChanges();

    component.deleteProject();

    expect(mockProjectService.deleteProject).toHaveBeenCalledWith(1);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/dashboard']);
    confirmSpy.mockRestore();
  });

  it('should not delete project if user cancels', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(false);

    component.deleteProject();

    expect(mockProjectService.deleteProject).not.toHaveBeenCalled();
    confirmSpy.mockRestore();
  });

  it('should handle project deletion error', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    mockProjectService.deleteProject.mockReturnValue(throwError(() => new Error('Delete error')));

    component.deleteProject();

    // @ts-ignore
    expect(consoleErrorSpy).toHaveBeenCalledWith('Error deleting project', expect.any(Error));
    confirmSpy.mockRestore();
    consoleErrorSpy.mockRestore();
  });

  it('should open assign task modal with task id', () => {
    component.openAssignTaskModal(5);

    expect(component.selectedTaskId).toBe(5);
    expect(component.showAssignTaskModal).toBe(true);
  });

  it('should close assign task modal', () => {
    component.showAssignTaskModal = true;
    component.closeAssignTaskModal();

    expect(component.showAssignTaskModal).toBe(false);
  });

  it('should reload tasks after task assigned', () => {
    fixture.detectChanges();
    mockTaskService.getAllTasks.mockClear();

    component.onTaskAssigned();

    expect(mockTaskService.getAllTasks).toHaveBeenCalled();
  });

  it('should open task detail modal with task', () => {
    const task = mockTasks[0];
    component.openTaskDetailModal(task);

    expect(component.selectedTask).toBe(task);
    expect(component.showTaskDetailModal).toBe(true);
  });

  it('should close task detail modal', () => {
    component.showTaskDetailModal = true;
    component.closeTaskDetailModal();

    expect(component.showTaskDetailModal).toBe(false);
  });

  it('should have correct projectId from route', () => {
    fixture.detectChanges();

    expect(component.projectId).toBe(1);
  });
});
