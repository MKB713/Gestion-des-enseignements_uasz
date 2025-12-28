package com.uasz.daos.choix.exceptions;

import lombok.Getter;

@Getter
public class MicroserviceConnectionException extends RuntimeException {
    private final String serviceName;

    public MicroserviceConnectionException(String serviceName, Throwable cause) {
        super("Erreur de connexion au microservice: " + serviceName, cause);
        this.serviceName = serviceName;
    }
}