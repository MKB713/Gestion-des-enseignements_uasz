package com.uasz.daos.auth.exception;

public class MatriculeAlreadyExistsException extends RuntimeException {
    public MatriculeAlreadyExistsException(String message) {
        super(message);
    }
}