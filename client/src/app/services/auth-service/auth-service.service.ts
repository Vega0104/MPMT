// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import {API_BASE_URL} from "../../api-url";

export interface SignupData {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  userId: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = `${API_BASE_URL}/auth`;

  constructor(private http: HttpClient) {}

  // Inscription
  signup(data: SignupData): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/signup`, data);
  }

  // Connexion
  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, { email, password }).pipe(
      tap(response => {
        localStorage.setItem('auth_token', response.token);
        localStorage.setItem('user_id', response.userId.toString());  // ← AJOUT
      })
    );
  }

  // Token (localStorage)
  saveToken(token: string): void {
    localStorage.setItem('auth_token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  logout(): void {
    localStorage.removeItem('auth_token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUserId(): number | null {
    const token = localStorage.getItem('auth_token');
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // Le JWT contient l'email, pas l'ID. Il faut stocker l'ID au login
      return Number(localStorage.getItem('user_id'));
    } catch {
      return null;
    }
  }
}




