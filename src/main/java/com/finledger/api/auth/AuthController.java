package com.finledger.api.auth;

import com.finledger.api.auth.AuthDtos.*;
import com.finledger.api.common.ApiException;
import com.finledger.api.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository users;

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) { return authService.register(req); }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) { return authService.login(req); }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal Long userId) {
        var u = users.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserDto(u.getId(), u.getEmail(), u.getName());
    }

    @GetMapping("/health")
    public String health() { return "ok"; }
}