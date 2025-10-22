import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateTaskModalComponent } from './create-task-modal.component';
import { TaskService } from '../services/task-service/task.service';
import { AuthService } from '../services/auth-service/auth-service.service';
import { of, throwError } from 'rxjs';

describe('CreateTaskModalComponent', () => {
  let component: CreateTaskModalComponent;
  let fixture: ComponentFixture<CreateTaskModalComponent>;
  let mockTaskService: jest.Mocked<Partial<TaskService>>;
  let mockAuthService: jest.Mocked<Partial<AuthService>>;

  beforeEach(async () => {
    mockTaskService = {
      createTask: jest.fn().mockReturnValue(of({ id: 1, name: 'Test Task' }))
    };

    mockAuthService = {
      getCurrentUserId: jest.fn().mockReturnValue(1)
    };

    await TestBed.configureTestingModule({
      imports: [CreateTaskModalComponent]
    })
      .overrideComponent(CreateTaskModalComponent, {
        set: {
          providers: [
            { provide: TaskService, useValue: mockTaskService },
            { provide: AuthService, useValue: mockAuthService }
          ]
        }
      })
      .compileComponents();

    fixture = TestBed.createComponent(CreateTaskModalComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default priority as MEDIUM', () => {
    expect(component.priority).toBe('MEDIUM');
  });

  it('should emit close event', () => {
    const closeSpy = jest.spyOn(component.close, 'emit');

    component.onClose();

    expect(closeSpy).toHaveBeenCalled();
  });

  it('should show error if task name is empty', () => {
    component.taskName = '';

    component.onSubmit();

    expect(component.error).toBe('Task name is required');
    expect(mockTaskService.createTask).not.toHaveBeenCalled();
  });

  it('should show error if task name is only spaces', () => {
    component.taskName = '   ';

    component.onSubmit();

    expect(component.error).toBe('Task name is required');
    expect(mockTaskService.createTask).not.toHaveBeenCalled();
  });

  it('should show error if user not authenticated', () => {
    mockAuthService.getCurrentUserId = jest.fn().mockReturnValue(null);
    component.taskName = 'Test Task';

    component.onSubmit();

    expect(component.error).toBe('User not authenticated');
    expect(mockTaskService.createTask).not.toHaveBeenCalled();
  });

  it('should create task successfully', () => {
    const taskCreatedSpy = jest.spyOn(component.taskCreated, 'emit');
    const closeSpy = jest.spyOn(component.close, 'emit');

    component.taskName = 'Test Task';
    component.taskDescription = 'Test Description';
    component.priority = 'HIGH';
    component.dueDate = '2025-12-31';

    component.onSubmit();

    expect(mockTaskService.createTask).toHaveBeenCalledWith({
      name: 'Test Task',
      description: 'Test Description',
      priority: 'HIGH',
      status: 'TODO',
      dueDate: '2025-12-31',
      projectId: 1,
      createdBy: 1
    });
    expect(taskCreatedSpy).toHaveBeenCalled();
    expect(closeSpy).toHaveBeenCalled();
  });

  it('should create task with LOW priority', () => {
    component.taskName = 'Test Task';
    component.priority = 'LOW';

    component.onSubmit();

    // @ts-ignore
    expect(mockTaskService.createTask).toHaveBeenCalledWith(
      expect.objectContaining({ priority: 'LOW' })
    );
  });

  it('should create task with MEDIUM priority', () => {
    component.taskName = 'Test Task';
    component.priority = 'MEDIUM';

    component.onSubmit();

    // @ts-ignore
    expect(mockTaskService.createTask).toHaveBeenCalledWith(
      expect.objectContaining({ priority: 'MEDIUM' })
    );
  });

  it('should handle creation error', () => {
    const errorResponse = { error: { message: 'Task limit reached' } };
    mockTaskService.createTask = jest.fn().mockReturnValue(
      throwError(() => errorResponse)
    );

    component.taskName = 'Test Task';

    component.onSubmit();

    expect(component.error).toBe('Task limit reached');
    expect(component.isSubmitting).toBe(false);
  });

  it('should handle creation error without message', () => {
    mockTaskService.createTask = jest.fn().mockReturnValue(
      throwError(() => new Error('Network error'))
    );

    component.taskName = 'Test Task';

    component.onSubmit();

    expect(component.error).toBe('Failed to create task');
    expect(component.isSubmitting).toBe(false);
  });

  it('should set status to TODO by default', () => {
    component.taskName = 'Test Task';

    component.onSubmit();

    // @ts-ignore
    expect(mockTaskService.createTask).toHaveBeenCalledWith(
      expect.objectContaining({ status: 'TODO' })
    );
  });
});
