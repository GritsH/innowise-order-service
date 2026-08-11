package com.grits.orderservice.config;

import com.grits.orderservice.converter.KeycloakRoleConverter;
import com.grits.orderservice.security.OrderAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            OrderAuthorizationManager orderAuthorizationManager
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/v1/orders").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/v1/orders").hasRole(ROLE_USER)
                        .requestMatchers(HttpMethod.GET, "/v1/orders/user").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/v1/orders/{id}").access(orderAuthorizationManager)
                        .requestMatchers(HttpMethod.PUT, "/v1/orders/{id}").access(orderAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/v1/orders/{id}").access(orderAuthorizationManager)

                        .requestMatchers(HttpMethod.GET, "/v1/items").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/v1/items/{id}").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/v1/items").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/v1/items/{id}").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/v1/items/{id}").hasRole(ROLE_ADMIN)
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(KeycloakRoleConverter rolesConverter) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return converter;
    }
}
