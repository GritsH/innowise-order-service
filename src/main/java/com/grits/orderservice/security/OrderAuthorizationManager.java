package com.grits.orderservice.security;

import com.grits.orderservice.client.UserServiceClient;
import com.grits.orderservice.dao.OrderDao;
import com.grits.orderservice.entity.Order;
import com.grits.orderservice.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class OrderAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final OrderDao orderDao;
    private final UserServiceClient userServiceClient;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication authentication = authenticationSupplier.get();

        if (SecurityHelper.isNotAuthenticated(authentication)) {
            return new AuthorizationDecision(false);
        }
        if (SecurityHelper.isAdmin(authentication)) {
            return new AuthorizationDecision(true);
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaimAsString("email");
        UserResponse user = userServiceClient.getUserByEmail(email);
        UUID currentUserId = user.getId();
        Map<String, String> variables = context.getVariables();

        if (variables.containsKey("id")) {
            UUID orderId = UUID.fromString(variables.get("id"));
            Order order = orderDao.getOrderById(orderId);
            return new AuthorizationDecision(order.getUserId().equals(currentUserId));
        }
        return new AuthorizationDecision(false);
    }
}
