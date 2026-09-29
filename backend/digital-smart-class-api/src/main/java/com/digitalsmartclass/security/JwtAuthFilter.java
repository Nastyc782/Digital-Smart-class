package com.digitalsmartclass.security;

import com.digitalsmartclass.entity.UserStatus;
import com.digitalsmartclass.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserRepository users;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer ")) {
      try {
        String email = jwt.extractEmail(h.substring(7));
        users.findByEmail(email)
            .filter(u -> u.getStatus() != UserStatus.DISABLED && u.getStatus() != UserStatus.REJECTED)
            .ifPresent(u -> SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(u, null, List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole())))));
      } catch (JwtException | IllegalArgumentException ignored) {
        // invalid or expired token: request continues unauthenticated and is rejected by the rules below
      }
    }
    chain.doFilter(req, res);
  }
}
