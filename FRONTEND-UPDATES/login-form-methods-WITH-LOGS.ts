  async onSubmit(): Promise<void> {
    if (this.loginForm.valid) {
      console.log('🚀 =================================');
      console.log('🚀 INICIANDO PROCESO DE LOGIN');
      console.log('🚀 =================================');
      
      this.isLoading = true;
      this.errorMessage = '';

      try {
        const { username, password, userType } = this.loginForm.value;
        console.log('📋 Datos del formulario:');
        console.log('📋 - Usuario:', username);
        console.log('📋 - Tipo de usuario:', userType);
        
        // 🔐 AUTENTICACIÓN REAL CON KEYCLOAK
        console.log('🔐 Llamando a AuthService.loginWithCredentials...');
        const success = await this.authService.loginWithCredentials(username, password);
        
        console.log('📊 Resultado del login:', success);
        
        if (success) {
          // ✅ LOGIN EXITOSO
          console.log('✅ =================================');
          console.log('✅ AUTENTICACIÓN EXITOSA');
          
          // Verificar que el usuario tiene el rol correcto
          const userInfo = this.authService.getUser();
          console.log('👤 UserInfo después del login:', userInfo);
          console.log('👤 Roles del usuario:', userInfo?.roles);
          
          const hasRequiredRole = this.validateUserRole(userType, userInfo?.roles || []);
          console.log('🔍 Validación de rol resultado:', hasRequiredRole);
          console.log('🔍 Tipo requerido:', userType);
          console.log('🔍 Roles disponibles:', userInfo?.roles);
          
          if (hasRequiredRole) {
            console.log('✅ USUARIO AUTORIZADO como:', userType);
            console.log('🔄 Iniciando redirección...');
            this.redirectBasedOnRole();
          } else {
            console.log('❌ USUARIO NO AUTORIZADO para el tipo:', userType);
            this.errorMessage = `No tienes permisos para acceder como ${userType}`;
            await this.authService.logout(); // Cerrar sesión si no tiene el rol
          }
          console.log('✅ =================================');
        } else {
          // ❌ CREDENCIALES INVÁLIDAS
          console.log('❌ =================================');
          console.log('❌ CREDENCIALES INVÁLIDAS');
          console.log('❌ =================================');
          this.errorMessage = 'Usuario o contraseña incorrectos';
        }
        
      } catch (error) {
        console.error('💥 =================================');
        console.error('💥 ERROR EN AUTENTICACIÓN:', error);
        console.error('💥 =================================');
        this.errorMessage = 'Error de conexión. Intenta nuevamente.';
      } finally {
        this.isLoading = false;
        console.log('🏁 Proceso de login finalizado');
        console.log('🚀 =================================');
      }
    } else {
      console.log('❌ Formulario inválido');
      this.markFormGroupTouched();
    }
  }

  private validateUserRole(selectedUserType: string, userRoles: string[]): boolean {
    console.log('🔍 =================================');
    console.log('🔍 VALIDANDO ROLES');
    console.log('🔍 Tipo seleccionado:', selectedUserType);
    console.log('🔍 Roles del usuario:', userRoles);
    
    let isValid = false;
    
    switch (selectedUserType) {
      case 'administrador':
        isValid = userRoles.some(role => 
          role.toUpperCase() === 'ADMIN' || 
          role === 'Admin' || 
          role === 'ADMIN'
        );
        console.log('🔍 Validación para ADMIN:', isValid);
        break;
      case 'empleado':
        isValid = userRoles.some(role => {
          const match = (
            role.toUpperCase() === 'EMPLEADO' || 
            role === 'Empleado' || 
            role === 'EMPLOYEE' ||
            role === 'EMPLEADO' ||
            role.toUpperCase() === 'ADMIN' || // Admin puede ser empleado también
            role === 'Admin'
          );
          console.log('🔍 Verificando rol "' + role + '":', match);
          return match;
        });
        console.log('🔍 Validación para EMPLEADO:', isValid);
        break;
      default:
        console.log('🔍 Tipo de usuario no reconocido:', selectedUserType);
        isValid = false;
    }
    
    console.log('🔍 RESULTADO FINAL:', isValid);
    console.log('🔍 =================================');
    return isValid;
  }

  private redirectBasedOnRole(): void {
    console.log('🔄 =================================');
    console.log('🔄 INICIANDO REDIRECCIÓN');
    
    const user = this.authService.getUser();
    console.log('👤 Usuario para redirección:', user);
    console.log('👤 Roles disponibles:', user?.roles);
    
    const isAdmin = user?.roles.some(role => 
      role.toUpperCase() === 'ADMIN' || 
      role === 'Admin'
    );
    
    const isEmpleado = user?.roles.some(role => 
      role.toUpperCase() === 'EMPLEADO' || 
      role === 'Empleado' || 
      role === 'EMPLOYEE'
    );
    
    console.log('🔍 Es Admin?', isAdmin);
    console.log('🔍 Es Empleado?', isEmpleado);
    
    if (isAdmin) {
      console.log('🔄 Redirigiendo a panel de administrador');
      console.log('🔄 Destino: /main/panel');
      console.log('🔄 Ejecutando this.router.navigate(["/main/panel"])');
      this.router.navigate(['/main/panel']);
      console.log('🔄 Comando de navegación enviado');
    } else if (isEmpleado) {
      console.log('🔄 Redirigiendo a panel de empleado');
      console.log('🔄 Destino: /main/panel');
      console.log('🔄 Ejecutando this.router.navigate(["/main/panel"])');
      this.router.navigate(['/main/panel']);
      console.log('🔄 Comando de navegación enviado');
    } else {
      console.error('❌ Usuario sin roles válidos');
      console.error('❌ Roles encontrados:', user?.roles);
      this.errorMessage = 'Usuario sin permisos válidos';
      this.authService.logout();
    }
    
    console.log('🔄 =================================');
  }