import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AssignTaskModalComponent } from '../assign-task-modal/assign-task-modal.component';
import { TaskAssignmentService } from '../services/tast-assignment-service/task-assignment.service';
import { ProjectMemberService, ProjectMember } from '../services/project-member-service/project-member.service';
import { of, throwError } from 'rxjs';

describe('AssignTaskModalComponent', () => {
  let component: AssignTaskModalComponent;
  let fixture: ComponentFixture<AssignTaskModalComponent>;
  let mockTaskAssignmentService: jest.Mocked<Partial<TaskAssignmentService>>;
  let mockProjectMemberService: jest.Mocked<Partial<ProjectMemberService>>;

  const mockMembers: ProjectMember[] = [
    { id: 1, user: { id: 1, username: 'user1', email: 'user1@test.com' }, project: { id: 1, name: 'Test Project' }, role: 'ADMIN'},
    { id: 2, user: { id: 2, username: 'user2', email: 'user2@test.com' }, project: { id: 1, name: 'Test Project' }, role: 'MEMBER'},
  ];

  const mockAssignments = [
    { id: 1, taskId: 1, projectMemberId: 1 }
  ];

  beforeEach(async () => {
    mockTaskAssignmentService = {
      getByTaskId: jest.fn().mockReturnValue(of(mockAssignments)),
      assignTask: jest.fn().mockReturnValue(of({}))
    };

    mockProjectMemberService = {
      getMembersByProjectId: jest.fn().mockReturnValue(of(mockMembers))
    };

    await TestBed.configureTestingModule({
      imports: [AssignTaskModalComponent]
    })
      .overrideComponent(AssignTaskModalComponent, {
        set: {
          providers: [
            { provide: TaskAssignmentService, useValue: mockTaskAssignmentService },
            { provide: ProjectMemberService, useValue: mockProjectMemberService },
          ]
        }
      })
      .compileComponents();

    fixture = TestBed.createComponent(AssignTaskModalComponent);
    component = fixture.componentInstance;
    component.taskId = 1;
    component.projectId = 1;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load members and assignments on init', () => {
    fixture.detectChanges();

    expect(mockProjectMemberService.getMembersByProjectId).toHaveBeenCalledWith(1);
    expect(mockTaskAssignmentService.getByTaskId).toHaveBeenCalledWith(1);
    expect(component.members).toEqual(mockMembers);
    expect(component.assignments).toEqual(mockAssignments);
  });

  it('should reload when inputs change', () => {
    fixture.detectChanges();
    jest.clearAllMocks();

    component.taskId = 2;
    component.ngOnChanges({ taskId: { currentValue: 2, previousValue: 1, firstChange: false, isFirstChange: () => false } });

    expect(mockTaskAssignmentService.getByTaskId).toHaveBeenCalledWith(2);
  });

  it('should not load if taskId or projectId missing', () => {
    component.taskId = 0;
    component.projectId = 0;

    fixture.detectChanges();

    expect(mockProjectMemberService.getMembersByProjectId).not.toHaveBeenCalled();
    expect(mockTaskAssignmentService.getByTaskId).not.toHaveBeenCalled();
  });

  it('should show error when no members found', () => {
    mockProjectMemberService.getMembersByProjectId = jest.fn().mockReturnValue(of([]));

    fixture.detectChanges();

    expect(component.error).toBe('No members found for this project.');
  });

  it('should handle error loading members', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    const errorResponse = { error: { message: 'Server error' } };
    mockProjectMemberService.getMembersByProjectId = jest.fn().mockReturnValue(
      throwError(() => errorResponse)
    );

    fixture.detectChanges();

    expect(component.error).toBe('Server error');
    expect(consoleErrorSpy).toHaveBeenCalledWith('Error loading members', errorResponse);
    consoleErrorSpy.mockRestore();
  });

  it('should handle error loading assignments without blocking', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    mockTaskAssignmentService.getByTaskId = jest.fn().mockReturnValue(
      throwError(() => new Error('Load error'))
    );

    fixture.detectChanges();

    expect(consoleErrorSpy).toHaveBeenCalledWith('Error loading assignments', expect.any(Error));
    expect(component.members).toEqual(mockMembers);
    consoleErrorSpy.mockRestore();
  });

  it('should check if member is assigned', () => {
    component.assignments = mockAssignments;

    expect(component.isAssigned(1)).toBe(true);
    expect(component.isAssigned(2)).toBe(false);
  });

  it('should emit close event', () => {
    const closeSpy = jest.spyOn(component.close, 'emit');

    component.onClose();

    expect(closeSpy).toHaveBeenCalled();
  });

  it('should show error if no member selected', () => {
    component.selectedMemberId = null;

    component.onAssign();

    expect(component.error).toBe('Please select a member');
    expect(mockTaskAssignmentService.assignTask).not.toHaveBeenCalled();
  });

  it('should assign task successfully', () => {
    fixture.detectChanges();
    const assignedSpy = jest.spyOn(component.assigned, 'emit');
    component.selectedMemberId = 2;

    component.onAssign();

    expect(mockTaskAssignmentService.assignTask).toHaveBeenCalledWith(1, 2);
    expect(assignedSpy).toHaveBeenCalled();
    expect(component.selectedMemberId).toBeNull();
    expect(component.isSubmitting).toBe(false);
    expect(component.error).toBe('');
  });

  it('should handle assignment error', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    const errorResponse = { error: { message: 'Already assigned' } };
    mockTaskAssignmentService.assignTask = jest.fn().mockReturnValue(
      throwError(() => errorResponse)
    );
    component.selectedMemberId = 2;

    component.onAssign();

    expect(component.error).toBe('Already assigned');
    expect(component.isSubmitting).toBe(false);
    expect(consoleErrorSpy).toHaveBeenCalled();
    consoleErrorSpy.mockRestore();
  });
});
