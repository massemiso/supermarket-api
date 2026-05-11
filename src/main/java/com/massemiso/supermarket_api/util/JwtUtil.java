package com.massemiso.supermarket_api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${security.jwt.key.private}") private String privateKey;
  @Value("${security.jwt.user.generator}") private String userGenerator;
  @Value("${security.jwt.token.time}") private Long tokenTime;

  public String createToken(Authentication auth){
    Algorithm algorithm = Algorithm.HMAC256(privateKey);
    String username = auth.getPrincipal().toString();
    String authStr =
        auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

    return JWT.create()
        .withIssuer(userGenerator)
        .withSubject(username)
        .withClaim("authorities", authStr)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + tokenTime))
        .withJWTId(UUID.randomUUID().toString())
        .withNotBefore(new Date(System.currentTimeMillis()))
        .sign(algorithm);
  }

  public DecodedJWT decodeToken(String token){
    Algorithm algorithm = Algorithm.HMAC256(privateKey);
    JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer(userGenerator)
        .build();
    return verifier.verify(token);
  }

  public String getUsernameFromToken(DecodedJWT decodedJWT){
    return decodedJWT.getSubject();
  }

  public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(
      DecodedJWT decodedJWT){
    return AuthorityUtils.commaSeparatedStringToAuthorityList(
        decodedJWT.getClaim("authorities").asString()
    );
  }

}
