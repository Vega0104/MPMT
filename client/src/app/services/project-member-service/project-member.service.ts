import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { API_BASE_URL } from '../../api-url';

export interface ProjectRef {
  id: number;
  name?: string;
}

export interface UserRef {
  id: number;
  username: string;
  email?: string;
}

export interface ProjectMember {
  id: number;
  role: 'ADMIN' | 'MEMBER' | 'OBSERVER';
  user: UserRef;
  project?: ProjectRef;
}

export interface AddMemberRequest {
  projectId: number;
  userId: number;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class ProjectMemberService {
  private baseUrl = `${API_BASE_URL}/project-members`;

  constructor(private http: HttpClient) {}

  getAllMembers(): Observable<ProjectMember[]> {
    return this.http.get<ProjectMember[]>(this.baseUrl);
  }

  getMembersByProjectId(projectId: number): Observable<ProjectMember[]> {
    return this.http
      .get<ProjectMember[]>(`${API_BASE_URL}/projects/${projectId}/members`)
      .pipe(
        map((list: any[]) =>
          (Array.isArray(list) ? list : []).map((m: any) => ({
            id: Number(m?.id),
            role: String(m?.role ?? 'MEMBER') as 'ADMIN' | 'MEMBER' | 'OBSERVER',
            user: {
              id: Number(m?.user?.id),
              username: String(m?.user?.username ?? ''),
              email: m?.user?.email ? String(m.user.email) : undefined,
            },
            project: m?.project
              ? { id: Number(m.project.id), name: m.project.name }
              : undefined,
          })).filter(pm => pm.id && pm.user && pm.user.id) // évite les undefined.id
        )
      );
  }

  addMember(request: AddMemberRequest): Observable<ProjectMember> {
    const body = { ...request, role: request.role.toUpperCase() };
    return this.http.post<ProjectMember>(this.baseUrl, body);
  }

  removeMember(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updateRole(id: number, role: string): Observable<ProjectMember> {
    return this.http.put<ProjectMember>(`${this.baseUrl}/${id}/role`, {
      role: role.toUpperCase(),
    });
  }
}
