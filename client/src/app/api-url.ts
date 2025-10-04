// client/src/app/api-url.ts
declare global {
  interface Window { __env?: { API_URL?: string } }
}
// Dev: '/api' (proxy Angular) ; Docker/Nginx: env.js met API_URL="/api"
export const API_BASE_URL = (window.__env?.API_URL) ?? '/api';
