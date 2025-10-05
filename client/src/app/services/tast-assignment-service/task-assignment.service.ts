import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../api-url';

export interface TaskAssignment {
  id: number;
  taskId: number;
  projectMemberId: number;
}

@Injectable({
  providedIn: 'root'
})
export class TaskAssignmentService {
  private baseUrl = `${API_BASE_URL}/task-assignments`;

  constructor(private http: HttpClient) {}

  assignTask(taskId: number, projectMemberId: number): Observable<TaskAssignment> {
    return this.http.post<TaskAssignment>(this.baseUrl, { taskId, projectMemberId });
  }

  getByTaskId(taskId: number): Observable<TaskAssignment[]> {
    return this.http.get<TaskAssignment[]>(`${this.baseUrl}/by-task/${taskId}`);
  }

  removeAssignment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
