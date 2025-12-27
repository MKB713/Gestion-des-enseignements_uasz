package com.uasz.daos.maquette.exception;

public class MatriculeAlreadyExistsException extends RuntimeException {
    public MatriculeAlreadyExistsException(String message) {
        super(message);
    }
}
