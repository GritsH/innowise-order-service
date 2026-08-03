package com.grits.orderservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ItemNotFoundException extends GlobalServiceException {

    public ItemNotFoundException(UUID id) {
        super("Item with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
