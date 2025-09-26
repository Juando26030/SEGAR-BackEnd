import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    // ✅ Verificar si está autenticado SIN forzar redirección
    if (this.authService.isAuthenticated()) {
      console.log('✅ Usuario autenticado, permitiendo acceso');
      return true;
    } else {
      console.log('❌ Usuario NO autenticado, redirigiendo al login personalizado');
      // 🏠 Redirigir a TU componente de login (no a Keycloak)
      this.router.navigate(['/auth/login']); // ← Tu login personalizado
      return false;
    }
  }
}

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.authService.isAuthenticated() && this.authService.isAdmin()) {
      console.log('✅ Usuario admin autenticado, permitiendo acceso');
      return true;
    } else if (this.authService.isAuthenticated()) {
      console.log('❌ Usuario sin permisos de admin, redirigiendo');
      this.router.navigate(['/unauthorized']);
      return false;
    } else {
      console.log('❌ Usuario NO autenticado, redirigiendo al login');
      this.router.navigate(['/auth/login']); // ← Tu login personalizado
      return false;
    }
  }
}