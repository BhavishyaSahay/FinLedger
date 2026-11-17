package com.finledger.api.auth;

import com.finledger.api.auth.AuthDtos.*;
import com.finledger.api.common.ApiException;
import com.finledger.api.user.User;
import com.finledger.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ApiException(HttpStatus.CONFLICT, "Email already registered");

        User u = new User();
        u.setEmail(email);
        u.setName(req.name().trim());
        u.setPasswordHash(encoder.encode(req.password()));
        users.save(u);
        return toResponse(u);
    }

    public AuthResponse login(LoginRequest req) {
        User u = users.findByEmail(req.email().trim().toLowerCase())
                .filter(x -> encoder.matches(req.password(), x.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return toResponse(u);
    }

    private AuthResponse toResponse(User u) {
        return new AuthResponse(jwt.issue(u.getId()), new UserDto(u.getId(), u.getEmail(), u.getName()));
    }
}