import { TestBed } from '@angular/core/testing';

import { TaskHistoryService } from './task-history.service';

describe('TaskHistoryService', () => {
  let service: TaskHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaskHistoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
