// Actualización para: src/app/auth/interceptors/auth.interceptor.ts

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  
  // Solo agregar token a peticiones a la API del backend
  if (req.url.includes('localhost:8090') || req.url.includes('/api/')) {
    const token = authService.getToken();
    
    if (token && authService.isAuthenticated()) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      
      console.log('🔐 Adding auth token to request:', req.url);
      return next(authReq);
    }
  }
  
  return next(req);
};