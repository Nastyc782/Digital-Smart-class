package com.digitalsmartclass.security;

import com.digitalsmartclass.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
  private final SecretKey key;
  private final long ttlMs;

  public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-minutes}") long minutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.ttlMs = minutes * 60_000;
  }
  public String generate(User u) {
    return Jwts.builder().subject(u.getEmail()).claim("role", u.getRole().name())
        .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + ttlMs)).signWith(key).compact();
  }
  public String extractEmail(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
  }
}
