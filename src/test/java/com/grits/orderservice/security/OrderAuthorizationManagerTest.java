package com.grits.orderservice.security;

import com.grits.orderservice.client.UserServiceClient;
import com.grits.orderservice.dao.OrderDao;
import com.grits.orderservice.entity.Order;
import com.grits.orderservice.model.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderAuthorizationManagerTest {


    @Mock
    private OrderDao orderDao;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private OrderAuthorizationManager authorizationManager;

    private RequestAuthorizationContext context;
    private Order order;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        context = mock(RequestAuthorizationContext.class);
        order = mock(Order.class);
        userResponse = mock(UserResponse.class);
    }

    @Test
    @DisplayName("should deny access when user is not authenticated")
    void denyAccessWhenUserIsNotAuthenticated() {
        AuthorizationDecision decision = authorizationManager.check(() -> null, context);

        assertThat(decision.isGranted()).isFalse();
    }

    @Test
    @DisplayName("should grant access when user is admin")
    void grantAccessWhenUserIsAdmin() {
        Authentication authentication = authenticatedUser(UUID.randomUUID(), "admin@gmail.com", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        AuthorizationDecision decision = authorizationManager.check(() -> authentication, context);

        assertThat(decision.isGranted()).isTrue();

        verifyNoInteractions(orderDao, userServiceClient);
    }

    @Test
    @DisplayName("should grant access when order belongs to current user")
    void grantAccessWhenOrderBelongsToCurrentUser() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Authentication authentication = authenticatedUser(userId, "john@gmail.com", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(context.getVariables()).thenReturn(Map.of("id", orderId.toString()));
        when(userServiceClient.getUserByEmail("john@gmail.com")).thenReturn(userResponse);
        when(userResponse.getId()).thenReturn(userId);
        when(orderDao.getOrderById(orderId)).thenReturn(order);
        when(order.getUserId()).thenReturn(userId);

        AuthorizationDecision decision = authorizationManager.check(() -> authentication, context);

        assertThat(decision.isGranted()).isTrue();
    }

    @Test
    @DisplayName("should deny access when order belongs to another user")
    void denyAccessWhenOrderBelongsToAnotherUser() {
        UUID orderId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID orderUserId = UUID.randomUUID();
        Authentication authentication = authenticatedUser(currentUserId, "john@gmail.com", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(context.getVariables()).thenReturn(Map.of("id", orderId.toString()));
        when(userServiceClient.getUserByEmail("john@gmail.com")).thenReturn(userResponse);
        when(userResponse.getId()).thenReturn(currentUserId);
        when(orderDao.getOrderById(orderId)).thenReturn(order);
        when(order.getUserId()).thenReturn(orderUserId);

        AuthorizationDecision decision = authorizationManager.check(() -> authentication, context);

        assertThat(decision.isGranted()).isFalse();
    }

    private Authentication authenticatedUser(UUID keycloakId, String email, Collection<GrantedAuthority> authorities) {
        Jwt jwt = Jwt.withTokenValue("dummy-token")
                .header("alg", "none")
                .subject(keycloakId.toString())
                .claim("email", email)
                .build();
        return new JwtAuthenticationToken(jwt, authorities);
    }
}