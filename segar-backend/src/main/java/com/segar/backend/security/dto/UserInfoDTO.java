package com.segar.backend.security.dto;

import java.util.List;

public record UserInfoDTO(
    String username,
    String email,
    String firstName,
    String lastName,
    List<String> roles,
    boolean enabled
) {}