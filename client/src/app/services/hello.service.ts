import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_BASE_URL } from '../api-url';

@Injectable({ providedIn: 'root' })
export class HelloService {
  private http = inject(HttpClient);

  getHello() {
    // passe par le proxy en dev: /api/hello  -> backend /hello
    return this.http.get(`${API_BASE_URL}/hello`, { responseType: 'text' });
  }
}
