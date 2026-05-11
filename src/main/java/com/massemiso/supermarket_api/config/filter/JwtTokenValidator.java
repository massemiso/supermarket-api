package com.massemiso.supermarket_api.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.massemiso.supermarket_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtTokenValidator extends OncePerRequestFilter {

  private JwtUtil jwtUtil;

  public JwtTokenValidator(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Remove the "Bearer " prefix
    jwtToken = jwtToken.substring(7);
    DecodedJWT decodedJWT = jwtUtil.decodeToken(jwtToken);

    SecurityContext context = SecurityContextHolder.getContext();
    Authentication auth = new UsernamePasswordAuthenticationToken(
        jwtUtil.getUsernameFromToken(decodedJWT),
        null,
        jwtUtil.getAuthoritiesFromToken(decodedJWT)
    );
    context.setAuthentication(auth);
    filterChain.doFilter(request, response);
  }

}
