import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskHistoryService, TaskHistory } from './task-history.service';
import { API_BASE_URL } from '../../api-url';

describe('TaskHistoryService', () => {
  let service: TaskHistoryService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/tasks`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskHistoryService]
    });
    service = TestBed.inject(TaskHistoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get history by task id', () => {
    const mockHistory: TaskHistory[] = [
      { id: 1, taskId: 1, changedBy: 1, changeDate: '2025-01-01', changeDescription: 'Status changed to IN_PROGRESS' },
      { id: 2, taskId: 1, changedBy: 2, changeDate: '2025-01-02', changeDescription: 'Priority changed to HIGH' }
    ];

    service.getHistoryByTaskId(1).subscribe(history => {
      expect(history).toEqual(mockHistory);
      expect(history.length).toBe(2);
    });

    const req = httpMock.expectOne(`${baseUrl}/1/history`);
    expect(req.request.method).toBe('GET');
    req.flush(mockHistory);
  });

  it('should handle empty history', () => {
    service.getHistoryByTaskId(999).subscribe(history => {
      expect(history).toEqual([]);
    });

    const req = httpMock.expectOne(`${baseUrl}/999/history`);
    req.flush([]);
  });
});
