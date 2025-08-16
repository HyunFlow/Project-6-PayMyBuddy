package com.openclassrooms.pay_my_buddy.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.pay_my_buddy.web.dto.auth.LoginRequest;
import com.openclassrooms.pay_my_buddy.domain.model.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final Validator validator;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
        Validator validator) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.validator = validator;
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) {
        try {
            if (request.getContentType() == null ||
                !request.getContentType().toLowerCase().contains("application/json")) {
                throw new AuthenticationServiceException("validation:Content-Type doit être application/json");
            }

            LoginRequest dto = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String msg = violations.iterator().next().getMessage();
                throw new AuthenticationServiceException("validation:" + msg);
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                dto.getEmail(), dto.getPassword());

            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("Could not parse credentials", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        String email = authResult.getName();
        String roleString = authResult.getAuthorities().iterator().next().getAuthority();
        Role role = Role.valueOf(roleString);

        // Générer du jeton d'authentification.
        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email, role);

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
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException failed) throws IOException {

        boolean isValidation = failed instanceof AuthenticationServiceException
            && failed.getMessage() != null
            && failed.getMessage().startsWith("validation:");
        String msg = isValidation
            ? failed.getMessage().substring("validation:".length())
            : "Adresse e-mail ou mot de passe invalide.";

        response.setStatus(isValidation ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"message\":\"" + msg + "\"}");
    }

}
