// client/src/app/api-url.ts
declare global {
  interface Window { __env?: { API_URL?: string } }
}

// Dev local: http://localhost:8081
// Docker/Nginx: env.js met API_URL="/api"
export const API_BASE_URL = (window.__env?.API_URL) ?? 'http://localhost:8081/api';
