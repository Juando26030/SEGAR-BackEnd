import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.css'
})
export class LoginFormComponent implements OnInit {
  loginForm: FormGroup;
  showPassword = false;
  isLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService  // ← Servicio de Keycloak
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
      userType: ['administrador', [Validators.required]],
      rememberMe: [false]
    });
  }

  ngOnInit(): void {
    // Verificar si ya está autenticado
    if (this.authService.isAuthenticated()) {
      this.redirectBasedOnRole();
    }
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  async onSubmit(): Promise<void> {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      try {
        const { username, password, userType } = this.loginForm.value;
        
        // 🔐 AUTENTICACIÓN REAL CON KEYCLOAK
        const success = await this.authService.loginWithCredentials(username, password);
        
        if (success) {
          // ✅ LOGIN EXITOSO
          console.log('✅ Autenticación exitosa');
          
          // Verificar que el usuario tiene el rol correcto
          const userInfo = this.authService.getUser();
          const hasRequiredRole = this.validateUserRole(userType, userInfo?.roles || []);
          
          if (hasRequiredRole) {
            console.log(`✅ Usuario autorizado como: ${userType}`);
            this.redirectBasedOnRole();
          } else {
            this.errorMessage = `No tienes permisos para acceder como ${userType}`;
            await this.authService.logout(); // Cerrar sesión si no tiene el rol
          }
        } else {
          // ❌ CREDENCIALES INVÁLIDAS
          this.errorMessage = 'Usuario o contraseña incorrectos';
        }
        
      } catch (error) {
        console.error('❌ Error en autenticación:', error);
        this.errorMessage = 'Error de conexión. Intenta nuevamente.';
      } finally {
        this.isLoading = false;
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  private validateUserRole(selectedUserType: string, userRoles: string[]): boolean {
    switch (selectedUserType) {
      case 'administrador':
        return userRoles.includes('ADMIN');
      case 'empleado':
        return userRoles.includes('EMPLEADO') || userRoles.includes('ADMIN'); // Admin puede ser empleado también
      default:
        return false;
    }
  }

  private redirectBasedOnRole(): void {
    const user = this.authService.getUser();
    
    if (user?.roles.includes('ADMIN')) {
      console.log('🔄 Redirigiendo a panel de administrador');
      this.router.navigate(['/main/panel']); // Panel completo para admin
    } else if (user?.roles.includes('EMPLEADO')) {
      console.log('🔄 Redirigiendo a panel de empleado');
      this.router.navigate(['/main/panel']); // Panel limitado para empleado
    } else {
      console.error('❌ Usuario sin roles válidos');
      this.errorMessage = 'Usuario sin permisos válidos';
      this.authService.logout();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      control?.markAsTouched();
    });
  }

  // 🚨 Método para debugging - eliminar en producción
  onTestLogin(testUser: 'admin' | 'empleado'): void {
    if (testUser === 'admin') {
      this.loginForm.patchValue({
        username: 'admin.segar',
        password: 'admin123',
        userType: 'administrador'
      });
    } else {
      this.loginForm.patchValue({
        username: 'empleado.segar',
        password: 'empleado123',
        userType: 'empleado'
      });
    }
  }
}