import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskAssignmentService, TaskAssignment } from './task-assignment.service';
import { API_BASE_URL } from '../../api-url';

describe('TaskAssignmentService', () => {
  let service: TaskAssignmentService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/task-assignments`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskAssignmentService]
    });
    service = TestBed.inject(TaskAssignmentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should assign task', () => {
    const mockAssignment: TaskAssignment = {
      id: 1,
      taskId: 5,
      projectMemberId: 3
    };

    service.assignTask(5, 3).subscribe(assignment => {
      expect(assignment).toEqual(mockAssignment);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ taskId: 5, projectMemberId: 3 });
    req.flush(mockAssignment);
  });

  it('should get assignments by task id', () => {
    const mockAssignments: TaskAssignment[] = [
      { id: 1, taskId: 5, projectMemberId: 3 },
      { id: 2, taskId: 5, projectMemberId: 7 }
    ];

    service.getByTaskId(5).subscribe(assignments => {
      expect(assignments).toEqual(mockAssignments);
      expect(assignments.length).toBe(2);
    });

    const req = httpMock.expectOne(`${baseUrl}/by-task/5`);
    expect(req.request.method).toBe('GET');
    req.flush(mockAssignments);
  });

  it('should remove assignment', () => {
    service.removeAssignment(1).subscribe();

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
