package com.zou.configrations;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);
    private final SecretKey key;
    public JwtProvider(
            @org.springframework.beans.factory.annotation.Value("${app.jwt.secret:}") String secret,
            @org.springframework.beans.factory.annotation.Value("${app.jwt.require-configured-secret:false}") boolean requireConfiguredSecret) {
        if (secret == null || secret.isBlank()) {
            if (requireConfiguredSecret) {
                throw new IllegalStateException("JWT_SECRET must be configured when JWT_REQUIRE_CONFIGURED_SECRET=true");
            }
            key = Jwts.SIG.HS256.key().build();
            log.warn("JWT_SECRET is not configured. Generated an ephemeral development key; all sessions will expire when the backend restarts.");
            return;
        }
        byte[] secretBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET must contain at least 32 UTF-8 bytes");
        }
        key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(Authentication authentication){
        Collection<? extends GrantedAuthority> authorities = authentication
                .getAuthorities();

        String roles = populateAuthorities(authorities);
        return Jwts.builder().issuedAt(new Date())
                .expiration(new Date(new Date().getTime()+86400000))
                .claim("email", authentication.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();
    }

    public String getEmailFromJwtToken(String jwt){
        if (jwt == null || !jwt.startsWith("Bearer ")) throw new IllegalArgumentException("Invalid authorization header");
        jwt = jwt.substring(7);
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(jwt).getPayload();

        String email = String.valueOf(claims.get("email"));
        return email;
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();

        for(GrantedAuthority authority: authorities){
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}
