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

  // Inicialización manual de Keycloak (sin auto-login)
  async initKeycloakSilent(): Promise<void> {
    try {
      if (!this.keycloak) {
        console.log('🔧 Inicializando Keycloak en modo silencioso...');
        this.keycloak = new Keycloak({
          url: 'http://localhost:8080',
          realm: 'segar',
          clientId: 'segar-frontend'
        });

        // Inicialización silenciosa - NO redirige automáticamente
        await this.keycloak.init({
          onLoad: 'check-sso',
          checkLoginIframe: false,
          pkceMethod: 'S256'
        });

        console.log('✅ Keycloak inicializado en modo silencioso');
      }
    } catch (error) {
      console.error('❌ Error inicializando Keycloak silencioso:', error);
    }
  }

  async initKeycloak(): Promise<boolean> {
    try {
      this.keycloak = new Keycloak({
        url: 'http://localhost:8080',
        realm: 'segar',
        clientId: 'segar-frontend'
      });

      const authenticated = await this.keycloak.init({
        onLoad: 'check-sso',  // No fuerza login automático
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
      console.log('✅ User profile loaded:', userInfo);
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
    const authenticated = !!this.keycloak?.authenticated;
    console.log('🔍 AuthService.isAuthenticated():', authenticated);
    return authenticated;
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

  // Método para login con credenciales (Resource Owner Password Flow)
  async loginWithCredentials(username: string, password: string): Promise<boolean> {
    try {
      console.log('🔐 Iniciando login con credenciales para:', username);
      
      // Asegurar que Keycloak esté inicializado silenciosamente
      await this.initKeycloakSilent();
      
      const response = await fetch('http://localhost:8080/realms/segar/protocol/openid-connect/token', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          grant_type: 'password',
          client_id: 'segar-frontend',
          username: username,
          password: password,
          scope: 'openid profile email'
        })
      });

      if (response.ok) {
        const tokenData = await response.json();
        console.log('✅ Token obtenido exitosamente');
        
        // Configurar Keycloak con el token obtenido
        if (!this.keycloak) {
          console.warn('⚠️ Keycloak no inicializado, creando instancia');
          this.keycloak = new Keycloak({
            url: 'http://localhost:8080',
            realm: 'segar',
            clientId: 'segar-frontend'
          });
        }

        // Simular que Keycloak está autenticado
        (this.keycloak as any).authenticated = true;
        (this.keycloak as any).token = tokenData.access_token;
        (this.keycloak as any).refreshToken = tokenData.refresh_token;
        (this.keycloak as any).tokenParsed = this.parseJwt(tokenData.access_token);

        // Cargar perfil del usuario
        await this.loadUserProfile();

        console.log('✅ Login with credentials successful');
        console.log('🔍 isAuthenticated después del login:', this.isAuthenticated());
        return true;
      } else {
        console.error('❌ Login failed:', response.status, response.statusText);
        return false;
      }
    } catch (error) {
      console.error('❌ Login error:', error);
      return false;
    }
  }

  // Método auxiliar para parsear JWT
  private parseJwt(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Error parsing JWT:', error);
      return {};
    }
  }

  async logout(): Promise<void> {
    if (this.keycloak) {
      console.log('🚪 Cerrando sesión...');
      this.userSubject.next(null);
      await this.keycloak.logout({
        redirectUri: window.location.origin
      });
    }
  }

  // Método para debugging
  logTokenInfo(): void {
    if (this.keycloak?.tokenParsed) {
      console.log('📋 Token info:', {
        username: this.keycloak.tokenParsed['preferred_username'],
        roles: this.keycloak.tokenParsed.resource_access?.['segar-backend']?.roles,
        exp: new Date(this.keycloak.tokenParsed.exp! * 1000),
        token: this.keycloak.token?.substring(0, 50) + '...'
      });
    } else {
      console.log('❌ No hay token disponible');
    }
  }

  // Método para debugging completo
  debugAuthState(): void {
    console.log('🔍 DEBUG AUTH STATE:');
    console.log('- Keycloak instance:', !!this.keycloak);
    console.log('- Authenticated:', this.isAuthenticated());
    console.log('- Has token:', !!this.keycloak?.token);
    console.log('- User info:', this.getUser());
    this.logTokenInfo();
  }
}