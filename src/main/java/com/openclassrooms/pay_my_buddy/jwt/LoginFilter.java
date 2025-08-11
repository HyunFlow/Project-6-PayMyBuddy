package com.openclassrooms.pay_my_buddy.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.pay_my_buddy.dto.LoginRequestDTO;
import com.openclassrooms.pay_my_buddy.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    setFilterProcessesUrl("/auth/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
    try {
      LoginRequestDTO loginRequestDTO = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);

      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

      return authenticationManager.authenticate(authToken);

    } catch (IOException e) {
      throw new RuntimeException("Could not parse credentials", e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

    String email = authResult.getName();
    String roleString = authResult.getAuthorities().iterator().next().getAuthority();
    Role role = Role.valueOf(roleString);

    // Générer du jeton d'authentification.
    String accessToken = jwtUtil.createAccessToken(email, role);
    String refreshToken = jwtUtil.createRefreshToken(email, role);
    System.out.println(accessToken);
    System.out.println(refreshToken);

    Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(jwtUtil.getAccessTokenExpirationSeconds());
    response.addCookie(accessTokenCookie);

    Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(jwtUtil.getRefreshTokenExpirationSeconds());
    response.addCookie(refreshTokenCookie);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=utf-8");
    response.getWriter().write("{\"message\": \"Succès\"}");
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=utf-8");

    String errorMessage = "Échec de l'authentification";
    response.getWriter().write("{\"message\": \"" + errorMessage + "\"}");
  }

}
