// Actualización para: src/app/auth/services/auth.service.ts

import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private keycloak: Keycloak | undefined;
  private isInitialized = false;

  constructor() {}

  async initKeycloak(): Promise<boolean> {
    try {
      // Configuración de Keycloak
      this.keycloak = new Keycloak({
        url: 'http://localhost:8080',
        realm: 'segar',
        clientId: 'segar-frontend'
      });

      // Configuración de inicialización
      const authenticated = await this.keycloak.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        checkLoginIframe: false,
        pkceMethod: 'S256'
      });

      this.isInitialized = true;
      
      // Configurar renovación automática del token
      if (authenticated) {
        this.setupTokenRefresh();
      }

      console.log('🔐 Keycloak initialized successfully', {
        authenticated,
        realm: this.keycloak.realm,
        clientId: this.keycloak.clientId
      });

      // Hacer Keycloak disponible globalmente para debugging
      (window as any).keycloakInstance = this.keycloak;

      return true;
    } catch (error) {
      console.error('❌ Error initializing Keycloak:', error);
      return false;
    }
  }

  private setupTokenRefresh(): void {
    if (!this.keycloak) return;

    // Renovar token cada 5 minutos
    setInterval(() => {
      this.keycloak?.updateToken(70)
        .then(refreshed => {
          if (refreshed) {
            console.log('🔄 Token refreshed');
          }
        })
        .catch(error => {
          console.error('❌ Token refresh failed:', error);
          this.login();
        });
    }, 60000); // Cada 1 minuto
  }

  isAuthenticated(): boolean {
    return this.keycloak?.authenticated ?? false;
  }

  login(): void {
    if (!this.isInitialized || !this.keycloak) {
      console.error('Keycloak not initialized');
      return;
    }
    this.keycloak.login();
  }

  logout(): void {
    if (!this.isInitialized || !this.keycloak) {
      console.error('Keycloak not initialized');
      return;
    }
    this.keycloak.logout();
  }

  getToken(): string | undefined {
    return this.keycloak?.token;
  }

  getUsername(): string {
    return this.keycloak?.tokenParsed?.['preferred_username'] || '';
  }

  getEmail(): string {
    return this.keycloak?.tokenParsed?.['email'] || '';
  }

  getName(): string {
    const tokenParsed = this.keycloak?.tokenParsed;
    return tokenParsed?.['name'] || `${tokenParsed?.['given_name']} ${tokenParsed?.['family_name']}` || '';
  }

  getRoles(): string[] {
    const resourceAccess = this.keycloak?.tokenParsed?.['resource_access'];
    return resourceAccess?.['segar-backend']?.['roles'] || [];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  isAdmin(): boolean {
    return this.hasRole('admin');
  }

  isEmpleado(): boolean {
    return this.hasRole('empleado');
  }

  getUserInfo() {
    if (!this.isAuthenticated()) {
      return null;
    }

    return {
      username: this.getUsername(),
      email: this.getEmail(),
      name: this.getName(),
      roles: this.getRoles(),
      isAdmin: this.isAdmin(),
      isEmpleado: this.isEmpleado(),
      authenticated: this.isAuthenticated()
    };
  }
}