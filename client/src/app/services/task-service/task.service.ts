import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../api-url';

export interface Task {
  id: number;
  name: string;
  description: string;
  dueDate: string;
  endDate?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  createdBy: number;
  projectId: number;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private TasksUrl = `${API_BASE_URL}/tasks`;

  constructor(private http: HttpClient) {}

  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.TasksUrl);
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.TasksUrl}/${id}`);
  }

  getTasksByStatus(projectId: number, status: string): Observable<Task[]> {
    const params = new HttpParams()
      .set('projectId', projectId.toString())
      .set('status', status);
    return this.http.get<Task[]>(`${this.TasksUrl}/by-status`, { params });
  }

  createTask(task: Partial<Task>): Observable<Task> {
    return this.http.post<Task>(this.TasksUrl, task);
  }

  updateTask(id: number, task: Partial<Task>): Observable<Task> {
    return this.http.put<Task>(`${this.TasksUrl}/${id}`, task);
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.TasksUrl}/${id}`);
  }
}
