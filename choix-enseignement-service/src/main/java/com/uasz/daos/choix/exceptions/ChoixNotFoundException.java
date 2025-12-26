package com.uasz.daos.choix.exceptions;

import lombok.Getter;

@Getter
public class ChoixNotFoundException extends RuntimeException {
    private final Long choixId;

    public ChoixNotFoundException(Long choixId) {
        super("Choix non trouvé avec l'ID: " + choixId);
        this.choixId = choixId;
    }

    public ChoixNotFoundException(String message) {
        super(message);
        this.choixId = null;
    }
}