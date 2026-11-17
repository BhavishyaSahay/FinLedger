package com.finledger.api.auth;

import jakarta.validation.constraints.*;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(max = 80) String name,
            @NotBlank @Size(min = 8, max = 72) String password) {}

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}

    public record UserDto(Long id, String email, String name) {}

    public record AuthResponse(String token, UserDto user) {}
}