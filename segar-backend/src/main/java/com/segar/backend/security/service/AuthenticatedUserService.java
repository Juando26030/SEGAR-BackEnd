package com.segar.backend.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para obtener información del usuario autenticado desde el contexto de seguridad
 */
@Service
@Slf4j
public class AuthenticatedUserService {

    /**
     * Obtiene el username del usuario autenticado
     * @return username o "SYSTEM" si no hay usuario autenticado
     */
    public String getCurrentUsername() {
        return getJwt()
            .map(jwt -> jwt.getClaimAsString("preferred_username"))
            .orElse("SYSTEM");
    }

    /**
     * Obtiene el email del usuario autenticado
     * @return email o null si no está disponible
     */
    public String getCurrentUserEmail() {
        return getJwt()
            .map(jwt -> jwt.getClaimAsString("email"))
            .orElse(null);
    }

    /**
     * Obtiene el nombre completo del usuario autenticado
     * @return nombre completo o username si no está disponible
     */
    public String getCurrentUserFullName() {
        return getJwt()
            .map(jwt -> {
                String firstName = jwt.getClaimAsString("given_name");
                String lastName = jwt.getClaimAsString("family_name");
                if (firstName != null && lastName != null) {
                    return firstName + " " + lastName;
                } else if (firstName != null) {
                    return firstName;
                }
                return jwt.getClaimAsString("preferred_username");
            })
            .orElse("SYSTEM");
    }

    /**
     * Obtiene el ID de empresa del usuario autenticado
     * Este método asume que el empresaId está en los claims del JWT
     * Si no existe, devuelve 1L por defecto
     *
     * @return empresaId del usuario o 1L por defecto
     */
    public Long getCurrentUserEmpresaId() {
        return getJwt()
            .map(jwt -> {
                // Intentar obtener empresaId del JWT (debe configurarse en Keycloak)
                Object empresaIdClaim = jwt.getClaim("empresa_id");
                if (empresaIdClaim != null) {
                    if (empresaIdClaim instanceof Number) {
                        return ((Number) empresaIdClaim).longValue();
                    } else if (empresaIdClaim instanceof String) {
                        try {
                            return Long.parseLong((String) empresaIdClaim);
                        } catch (NumberFormatException e) {
                            log.warn("No se pudo parsear empresa_id del JWT: {}", empresaIdClaim);
                        }
                    }
                }
                // Si no existe el claim, usar valor por defecto
                log.debug("No se encontró empresa_id en JWT, usando valor por defecto: 1L");
                return 1L;
            })
            .orElse(1L);
    }

    /**
     * Obtiene el sub (subject/user ID) del usuario autenticado
     * @return user ID de Keycloak o null si no está disponible
     */
    public String getCurrentUserId() {
        return getJwt()
            .map(jwt -> jwt.getClaimAsString("sub"))
            .orElse(null);
    }

    /**
     * Verifica si hay un usuario autenticado
     * @return true si hay un usuario autenticado, false en caso contrario
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && authentication.isAuthenticated()
            && authentication.getPrincipal() instanceof Jwt;
    }

    /**
     * Obtiene el JWT del contexto de seguridad
     * @return Optional con el JWT si existe
     */
    private Optional<Jwt> getJwt() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                return Optional.of((Jwt) authentication.getPrincipal());
            }
        } catch (Exception e) {
            log.debug("No se pudo obtener JWT del contexto de seguridad: {}", e.getMessage());
        }
        return Optional.empty();
    }
}

