package com.segar.backend.gestionUsuarios.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class KeycloakUserService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakUserService(Keycloak keycloak,
                               @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public String createUser(String username, String email, String password,
                             String firstName, String lastName) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        Response response = getUsersResource().create(user);

        if (response.getStatus() == 201) {
            String userId = response.getLocation().getPath()
                    .replaceAll(".*/([^/]+)$", "$1");

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            getUsersResource().get(userId).resetPassword(credential);
            return userId;
        }

        throw new RuntimeException("Error al crear usuario: " +
                response.getStatusInfo());
    }

    public void updateUser(String userId, String email, String firstName, String lastName, Boolean enabled) {
        UserRepresentation user = getUsersResource().get(userId).toRepresentation();

        if (email != null) {
            user.setEmail(email);
        }
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (enabled != null) {
            user.setEnabled(enabled);
        }

        getUsersResource().get(userId).update(user);
    }

    public void updatePassword(String userId, String newPassword, boolean temporary) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(temporary);

        getUsersResource().get(userId).resetPassword(credential);
    }

    public void enableUser(String userId, boolean enabled) {
        UserRepresentation user = getUsersResource().get(userId).toRepresentation();
        user.setEnabled(enabled);
        getUsersResource().get(userId).update(user);
    }

    public List<UserRepresentation> getAllUsers() {
        return getUsersResource().list();
    }

    public UserRepresentation getUserById(String userId) {
        try {
            return getUsersResource().get(userId).toRepresentation();
        } catch (ForbiddenException e) {
            logger.warn("⚠️ Error 403 al obtener usuario de Keycloak con ID: {}. Permisos insuficientes.", userId);
            throw e;
        } catch (NotFoundException e) {
            logger.warn("⚠️ Usuario no encontrado en Keycloak con ID: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("❌ Error inesperado al obtener usuario de Keycloak: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<UserRepresentation> getUserByIdSafe(String userId) {
        try {
            UserRepresentation user = getUsersResource().get(userId).toRepresentation();
            return Optional.of(user);
        } catch (ForbiddenException e) {
            logger.warn("⚠️ Error 403 al obtener usuario de Keycloak con ID: {}. Retornando empty.", userId);
            return Optional.empty();
        } catch (NotFoundException e) {
            logger.warn("⚠️ Usuario no encontrado en Keycloak con ID: {}", userId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("❌ Error inesperado al obtener usuario de Keycloak: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<UserRepresentation> getUserByUsername(String username) {
        try {
            List<UserRepresentation> users = getUsersResource().search(username, true);

            if (users.isEmpty()) {
                logger.info("Usuario no encontrado en Keycloak: {}", username);
                return Optional.empty();
            }

            // Buscar coincidencia exacta
            Optional<UserRepresentation> exactMatch = users.stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst();

            if (exactMatch.isPresent()) {
                return exactMatch;
            }

            // Si no hay coincidencia exacta, retornar el primero
            return Optional.of(users.get(0));

        } catch (ForbiddenException e) {
            logger.warn("⚠️ Error 403 al buscar usuario en Keycloak: {}. Permisos insuficientes.", username);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("❌ Error inesperado al buscar usuario en Keycloak: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteUser(String userId) {
        getUsersResource().delete(userId);
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }
}
