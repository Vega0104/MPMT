import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../api-url';

export interface Project {
  id: number;
  name: string;
  description: string;
  startDate: string;
  createdAt: string;
}

export interface ProjectStats {
  totalTasks: number;
  todoCount: number;
  inProgressCount: number;
  doneCount: number;
  progress: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private projectsUrl = `${API_BASE_URL}/projects`;

  constructor(private http: HttpClient) {}

  getAllProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.projectsUrl);
  }

  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.projectsUrl}/${id}`);
  }

  getProjectStats(id: number): Observable<ProjectStats> {
    return this.http.get<ProjectStats>(`${this.projectsUrl}/${id}/stats`);
  }

  createProject(project: Partial<Project>): Observable<Project> {
    return this.http.post<Project>(this.projectsUrl, project);
  }

  updateProject(id: number, project: Partial<Project>): Observable<Project> {
    return this.http.put<Project>(`${this.projectsUrl}/${id}`, project);
  }

  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.projectsUrl}/${id}`);
  }
}
