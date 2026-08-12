package com.grits.orderservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class OrderItemNotFoundException extends GlobalServiceException {

    public OrderItemNotFoundException(UUID id) {
        super("Order item with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
