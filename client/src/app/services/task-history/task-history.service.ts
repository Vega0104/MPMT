// src/app/services/task-history.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../api-url';

export interface TaskHistory {
  id: number;
  taskId: number;
  changedBy: number;
  changeDate: string;
  changeDescription: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskHistoryService {
  private baseUrl = `${API_BASE_URL}/tasks`;

  constructor(private http: HttpClient) {}

  getHistoryByTaskId(taskId: number): Observable<TaskHistory[]> {
    return this.http.get<TaskHistory[]>(`${this.baseUrl}/${taskId}/history`);
  }
}
