package com.segar.backend.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtConfig {

    // URL interna del contenedor para obtener las claves públicas (comunicación Docker)
    @Value("${app.security.jwks-uri:http://segar-keycloak:8080/realms/segar/protocol/openid-connect/certs}")
    private String jwksUri;

    // Issuer esperado en los tokens (el que genera Keycloak cuando se accede desde el navegador)
    @Value("${app.security.issuer-uri:http://localhost:8080/realms/segar}")
    private String issuerUri;

    @Bean
    public JwtDecoder jwtDecoder() {
        // Usar el JWKS URI del contenedor para comunicación interna Docker
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();

        // Validar el issuer del token (debe ser localhost porque el frontend lo genera así)
        JwtIssuerValidator issuerValidator = new JwtIssuerValidator(issuerUri);

        // Añadir tolerancia de 60 segundos para evitar problemas de sincronización de reloj
        JwtTimestampValidator timestampValidator = new JwtTimestampValidator(Duration.ofSeconds(60));

        // Combinar ambos validadores
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
            issuerValidator,
            timestampValidator
        );

        decoder.setJwtValidator(validator);
        return decoder;
    }
}

