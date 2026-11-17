package com.finledger.api.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expiryHours;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiry-hours}") long expiryHours) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiryHours = expiryHours;
    }

    public String issue(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer("finledger")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofHours(expiryHours))))
                .signWith(key)
                .compact();
    }

    /** Returns the userId, or throws JwtException if invalid/expired. */
    public Long verify(String token) {
        String sub = Jwts.parser().verifyWith(key).requireIssuer("finledger").build()
                .parseSignedClaims(token).getPayload().getSubject();
        return Long.parseLong(sub);
    }
}