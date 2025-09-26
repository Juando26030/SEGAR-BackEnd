  private validateUserRole(selectedUserType: string, userRoles: string[]): boolean {
    console.log('🔍 Validando rol:', selectedUserType, 'Roles del usuario:', userRoles);
    
    switch (selectedUserType) {
      case 'administrador':
        return userRoles.some(role => 
          role.toUpperCase() === 'ADMIN' || 
          role === 'Admin' || 
          role === 'ADMIN'
        );
      case 'empleado':
        return userRoles.some(role => 
          role.toUpperCase() === 'EMPLEADO' || 
          role === 'Empleado' || 
          role === 'EMPLOYEE' ||
          role === 'EMPLEADO' ||
          role.toUpperCase() === 'ADMIN' || // Admin puede ser empleado también
          role === 'Admin'
        );
      default:
        return false;
    }
  }

  private redirectBasedOnRole(): void {
    const user = this.authService.getUser();
    console.log('🔍 Usuario para redirección:', user);
    
    if (user?.roles.some(role => 
        role.toUpperCase() === 'ADMIN' || 
        role === 'Admin'
      )) {
      console.log('🔄 Redirigiendo a panel de administrador');
      this.router.navigate(['/main/panel']); // Panel completo para admin
    } else if (user?.roles.some(role => 
        role.toUpperCase() === 'EMPLEADO' || 
        role === 'Empleado' || 
        role === 'EMPLOYEE'
      )) {
      console.log('🔄 Redirigiendo a panel de empleado');
      this.router.navigate(['/main/panel']); // Panel limitado para empleado
    } else {
      console.error('❌ Usuario sin roles válidos. Roles encontrados:', user?.roles);
      this.errorMessage = 'Usuario sin permisos válidos';
      this.authService.logout();
    }
  }