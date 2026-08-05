package com.grits.orderservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@RequiredArgsConstructor
public class JwtTestUtils {

    public static RequestPostProcessor user(UUID keycloakId, String email) {
        return jwt().jwt(jwt -> jwt
                        .subject(keycloakId.toString())
                        .claim("email", email))
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public static RequestPostProcessor admin() {
        return jwt().jwt(jwt -> jwt
                        .subject(UUID.randomUUID().toString())
                        .claim("email", "admin@test.com"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
