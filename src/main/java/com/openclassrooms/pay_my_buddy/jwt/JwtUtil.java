package com.openclassrooms.pay_my_buddy.jwt;

import com.openclassrooms.pay_my_buddy.model.Role;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey secretKey;

  private static final long ACCESS_TOKEN_EXPIRATION_TIME = 5 * 60 * 1000;
  private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1 * 24 * 60 * 60 * 1000;

  public JwtUtil(@Value("${spring.jwt.secret}") String secret) {

    this.secretKey = new SecretKeySpec(
        secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm()
    );
  }

  public String createAccessToken(String email, Role role) {

    return Jwts.builder()
        .subject(email)
        .claim("email", email)
        .claim("role", role.name())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
        .signWith(secretKey)
        .compact();
  }

  public String createRefreshToken(String email, Role role) {
    return Jwts.builder()
        .subject(email)
        .claim("email", email)
        .claim("role", role.name())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
        .signWith(secretKey)
        .compact();
  }

  public String getEmail(String token) {

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("email", String.class);
  }

  public Role getRole(String token) {

    String roleString = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("role", String.class);
    return Role.valueOf(roleString);
  }

  public Boolean isExpired(String token) {

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration()
        .before(new Date());
  }

  public boolean validateToken(String token) {
    try{
      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }

  }
  public int getAccessTokenExpirationSeconds() {
    return (int) (ACCESS_TOKEN_EXPIRATION_TIME / 1000);
  }

  public int getRefreshTokenExpirationSeconds() {
    return (int) (REFRESH_TOKEN_EXPIRATION_TIME / 1000);
  }

}
