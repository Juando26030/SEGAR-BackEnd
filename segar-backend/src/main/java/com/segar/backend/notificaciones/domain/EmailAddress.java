package com.segar.backend.notificaciones.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.util.regex.Pattern;

/**
 * Value Object que representa una dirección de correo electrónico
 */
@Value
public class EmailAddress {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    @NotBlank(message = "La dirección de correo no puede estar vacía")
    @Email(message = "Formato de correo electrónico inválido")
    String address;

    String name;

    public EmailAddress(String address) {
        this(address, null);
    }

    public EmailAddress(String address, String name) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección de correo no puede estar vacía");
        }

        String trimmedAddress = address.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(trimmedAddress).matches()) {
            throw new IllegalArgumentException("Formato de correo electrónico inválido: " + address);
        }

        this.address = trimmedAddress;
        this.name = name != null ? name.trim() : null;
    }

    public String getFormattedAddress() {
        if (name != null && !name.isEmpty()) {
            return String.format("%s <%s>", name, address);
        }
        return address;
    }

    public String getDomain() {
        int atIndex = address.indexOf('@');
        return atIndex > 0 ? address.substring(atIndex + 1) : "";
    }

    public String getLocalPart() {
        int atIndex = address.indexOf('@');
        return atIndex > 0 ? address.substring(0, atIndex) : address;
    }

    @Override
    public String toString() {
        return getFormattedAddress();
    }
}
