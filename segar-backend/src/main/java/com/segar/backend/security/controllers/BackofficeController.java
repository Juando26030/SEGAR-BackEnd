package com.segar.backend.security.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para el backoffice - Landing page y panel de administración
 */
@Controller
@RequestMapping("/backoffice")
public class BackofficeController {

    /**
     * Landing page principal del backoffice
     * Muestra botón de login normal y link para administradores
     */
    @GetMapping({"/", ""})
    public String landingPage(Model model) {
        model.addAttribute("segarFrontendUrl", "http://localhost:4200");
        return "backoffice/landing";
    }

    /**
     * Página de login para administradores con Keycloak
     */
    @GetMapping("/admin-login")
    public String adminLogin(Model model) {
        model.addAttribute("keycloakUrl", "http://localhost:8080");
        model.addAttribute("realm", "segar");
        model.addAttribute("clientId", "segar-backend");
        return "backoffice/admin-login";
    }

    /**
     * Panel de administración para SUPER_ADMIN
     * Por ahora solo estructura básica
     */
    @GetMapping("/admin-panel")
    public String adminPanel(Model model) {
        // TODO: Implementar funcionalidades de administración
        model.addAttribute("message", "Panel de Super Administración - En desarrollo");
        return "backoffice/admin-panel";
    }
}

