package com.company.web.smart_garage.exceptions;

public class EntityNotFound extends RuntimeException {

    public EntityNotFound(String type, long id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFound(String type) {

    }

    public EntityNotFound(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found.", type, attribute, value));
    }
}
