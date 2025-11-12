package com.ecmsp.productservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(UUID variantId, int requestedQuantity) {
        super(String.format("Insufficient stock for variant %s: requested quantity %d exceeds available stock",
                variantId, requestedQuantity));
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}