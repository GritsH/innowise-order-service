package com.grits.orderservice.exception;

import org.springframework.http.HttpStatus;

public class ItemAlreadyExistsException extends GlobalServiceException {

    public ItemAlreadyExistsException(String name) {
        super("Item with name " + name + " already exists", HttpStatus.CONFLICT);
    }
}
