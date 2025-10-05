import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('auth_token');
  console.log('=== Interceptor - Token:', token ? 'Present' : 'Missing');
  console.log('=== Interceptor - URL:', req.url);

  if (token) {
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    console.log('=== Interceptor - Headers:', cloned.headers.get('Authorization'));
    return next(cloned);
  }

  return next(req);
};
