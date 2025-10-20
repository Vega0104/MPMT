import { HttpInterceptorFn } from '@angular/common/http';

const AUTH_WHITELIST = [
  '/auth/login',
  '/auth/signup',
  '/api/auth/login',
  '/api/auth/signup'
];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('auth_token');
  // SUPPRIMER AVANT VERSION PROD
  console.log('=== Interceptor - Token:', token ? 'Present' : 'Missing');
  console.log('=== Interceptor - URL:', req.url);

  const isWhitelisted = AUTH_WHITELIST.some(path => req.url.includes(path));

  if (token && !isWhitelisted) {
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    console.log('=== Interceptor - Headers:', cloned.headers.get('Authorization'));
    return next(cloned);
  }

  return next(req);
};
