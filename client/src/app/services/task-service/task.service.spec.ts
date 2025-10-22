import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService, Task } from './task.service';
import { API_BASE_URL } from '../../api-url';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/tasks`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService]
    });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all tasks', () => {
    const mockTasks: Task[] = [
      { id: 1, name: 'Task 1', description: 'Desc', dueDate: '2025-12-31', priority: 'HIGH', status: 'TODO', createdBy: 1, projectId: 1 }
    ];

    service.getAllTasks().subscribe(tasks => {
      expect(tasks).toEqual(mockTasks);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockTasks);
  });

  it('should get task by id', () => {
    const mockTask: Task = {
      id: 1,
      name: 'Task 1',
      description: 'Description',
      dueDate: '2025-12-31',
      priority: 'MEDIUM',
      status: 'TODO',
      createdBy: 1,
      projectId: 1
    };

    service.getTaskById(1).subscribe(task => {
      expect(task).toEqual(mockTask);
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockTask);
  });

  it('should get tasks by status', () => {
    const mockTasks: Task[] = [
      { id: 1, name: 'Task 1', description: 'Desc', dueDate: '2025-12-31', priority: 'HIGH', status: 'TODO', createdBy: 1, projectId: 1 }
    ];

    service.getTasksByStatus(1, 'TODO').subscribe(tasks => {
      expect(tasks).toEqual(mockTasks);
    });

    const req = httpMock.expectOne(req => req.url === `${baseUrl}/by-status`);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('projectId')).toBe('1');
    expect(req.request.params.get('status')).toBe('TODO');
    req.flush(mockTasks);
  });

  it('should create task', () => {
    const newTask: Partial<Task> = {
      name: 'New Task',
      description: 'Description',
      dueDate: '2025-12-31',
      priority: 'HIGH',
      status: 'TODO',
      createdBy: 1,
      projectId: 1
    };
    const mockResponse: Task = { id: 1, ...newTask } as Task;

    service.createTask(newTask).subscribe(task => {
      expect(task).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newTask);
    req.flush(mockResponse);
  });

  it('should update task', () => {
    const updates: Partial<Task> = { name: 'Updated Task' };
    const mockResponse: Task = {
      id: 1,
      name: 'Updated Task',
      description: 'Desc',
      dueDate: '2025-12-31',
      priority: 'MEDIUM',
      status: 'TODO',
      createdBy: 1,
      projectId: 1
    };

    service.updateTask(1, updates).subscribe(task => {
      expect(task.name).toBe('Updated Task');
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updates);
    req.flush(mockResponse);
  });

  it('should delete task', () => {
    service.deleteTask(1).subscribe();

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should update task status', () => {
    const mockResponse: Task = {
      id: 1,
      name: 'Task',
      description: 'Desc',
      dueDate: '2025-12-31',
      priority: 'MEDIUM',
      status: 'DONE',
      createdBy: 1,
      projectId: 1
    };

    service.updateTaskStatus(1, 'DONE').subscribe(task => {
      expect(task.status).toBe('DONE');
    });

    const req = httpMock.expectOne(`${baseUrl}/1/status`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ status: 'DONE' });
    req.flush(mockResponse);
  });
});
