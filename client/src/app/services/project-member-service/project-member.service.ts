import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../api-url';

export interface ProjectMember {
  id: number;
  project: { id: number };
  user: { id: number; username: string };
  role: 'ADMIN' | 'MEMBER' | 'OBSERVER';
}

export interface AddMemberRequest {
  projectId: number;
  userId: number;
  role: string;
}


@Injectable({
  providedIn: 'root'
})
export class ProjectMemberService {
  private baseUrl = `${API_BASE_URL}/project-members`;

  constructor(private http: HttpClient) {}

  getAllMembers(): Observable<ProjectMember[]> {
    return this.http.get<ProjectMember[]>(this.baseUrl);
  }

  addMember(request: AddMemberRequest): Observable<ProjectMember> {
    return this.http.post<ProjectMember>(this.baseUrl, request);
  }

  removeMember(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updateRole(id: number, role: string): Observable<ProjectMember> {
    return this.http.put<ProjectMember>(`${this.baseUrl}/${id}/role`, { role });
  }
}
