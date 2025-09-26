import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { BehaviorSubject, Observable } from 'rxjs';

export interface UserInfo {
  username: string;
  email: string;
  roles: string[];
  fullName: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private keycloak: Keycloak | undefined;
  private userSubject = new BehaviorSubject<UserInfo | null>(null);
  public user$ = this.userSubject.asObservable();

  constructor() {}

  async initKeycloak(): Promise<boolean> {
    try {
      this.keycloak = new Keycloak({
        url: 'http://localhost:8080',
        realm: 'segar',
        clientId: 'segar-frontend'
      });

      const authenticated = await this.keycloak.init({
        onLoad: 'login-required',
        checkLoginIframe: false,
        pkceMethod: 'S256'
      });

      if (authenticated && this.keycloak.token) {
        this.loadUserProfile();
      }

      // Setup token refresh
      this.keycloak.onTokenExpired = () => {
        this.refreshToken();
      };

      console.log('Keycloak initialized successfully', { authenticated });
      return authenticated;
    } catch (error) {
      console.error('Failed to initialize Keycloak', error);
      return false;
    }
  }

  private async loadUserProfile() {
    try {
      if (!this.keycloak) return;

      const profile = await this.keycloak.loadUserProfile();
      const tokenParsed = this.keycloak.tokenParsed as any;
      
      // Extraer roles de resource_access.segar-backend.roles
      const roles = tokenParsed?.resource_access?.['segar-backend']?.roles || [];
      
      const userInfo: UserInfo = {
        username: profile.username || '',
        email: profile.email || '',
        fullName: `${profile.firstName || ''} ${profile.lastName || ''}`.trim(),
        roles: roles
      };

      this.userSubject.next(userInfo);
      console.log('User profile loaded:', userInfo);
    } catch (error) {
      console.error('Failed to load user profile', error);
    }
  }

  async refreshToken(): Promise<boolean> {
    try {
      if (!this.keycloak) return false;
      
      const refreshed = await this.keycloak.updateToken(30);
      if (refreshed) {
        console.log('Token refreshed');
      }
      return refreshed;
    } catch (error) {
      console.error('Failed to refresh token', error);
      this.logout();
      return false;
    }
  }

  getToken(): string | undefined {
    return this.keycloak?.token;
  }

  isAuthenticated(): boolean {
    return !!this.keycloak?.authenticated;
  }

  hasRole(role: string): boolean {
    const user = this.userSubject.value;
    return user?.roles.includes(role) || false;
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isEmpleado(): boolean {
    return this.hasRole('EMPLEADO');
  }

  getUser(): UserInfo | null {
    return this.userSubject.value;
  }

  async logout(): Promise<void> {
    if (this.keycloak) {
      this.userSubject.next(null);
      await this.keycloak.logout({
        redirectUri: window.location.origin
      });
    }
  }

  // Método para debugging
  logTokenInfo(): void {
    if (this.keycloak?.tokenParsed) {
      console.log('Token info:', {
        username: this.keycloak.tokenParsed['preferred_username'],
        roles: this.keycloak.tokenParsed.resource_access?.['segar-backend']?.roles,
        exp: new Date(this.keycloak.tokenParsed.exp! * 1000),
        token: this.keycloak.token?.substring(0, 50) + '...'
      });
    }
  }
}