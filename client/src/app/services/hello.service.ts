import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

declare global {
  interface Window { __env?: { API_URL?: string } }
}

@Injectable({ providedIn: 'root' })
export class HelloService {
  private http = inject(HttpClient);
  // private baseUrl = (window.__env?.API_URL) ?? 'http://localhost:8081';
  private baseUrl =
    (window as any).__env?.API_URL   // en prod Docker: on mettra "/api"
    ?? '/api';

  getHello() {
    // adapte la route si besoin, mais on va créer /hello côté back
    return this.http.get(`${this.baseUrl}/hello`, { responseType: 'text' });
  }
}
