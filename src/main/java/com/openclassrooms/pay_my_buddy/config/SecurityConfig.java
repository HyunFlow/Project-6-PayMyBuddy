package com.openclassrooms.pay_my_buddy.config;

import com.openclassrooms.pay_my_buddy.security.jwt.JwtFilter;
import com.openclassrooms.pay_my_buddy.security.jwt.JwtUtil;
import com.openclassrooms.pay_my_buddy.security.jwt.LoginFilter;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Validator validator)
        throws Exception {
        http
            .csrf((auth) -> auth.disable())
            .formLogin((auth) -> auth.disable())
            .httpBasic((auth) -> auth.disable()
            );

        http
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("frame-ancestors 'self'"))
            );

        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/h2-console/**", "/", "/login", "/signup", "/css/**", "/js/**", "/images/**")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers("/admin").hasAuthority("ADMIN")
                .requestMatchers("/relations/new", "/relations", "/transfer",
                    "/accounts/*/transfer", "/profile").hasAnyAuthority("ADMIN", "USER")
                .anyRequest().authenticated()
            );

        http
            .logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("accessToken", "refreshToken")
                .logoutSuccessUrl("/login")
                .permitAll()
            );

        http.exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
                response.sendRedirect("/login");
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                request.getRequestDispatcher("/error/403").forward(request, response);
            })
        );

        http
            .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(
                new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,
                    validator), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
