package com.company.web.smart_garage.exceptions;

public class UnauthorizedOperation extends RuntimeException {
    public UnauthorizedOperation(String message) {
        super(message);
    }
}
