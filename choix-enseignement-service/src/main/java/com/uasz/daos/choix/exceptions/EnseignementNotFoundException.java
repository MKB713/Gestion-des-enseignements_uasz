package com.uasz.daos.choix.exceptions;

import lombok.Getter;

@Getter
public class EnseignementNotFoundException extends RuntimeException {
    private final Long enseignementId;

    public EnseignementNotFoundException(Long enseignementId) {
        super("Enseignement non trouvé avec l'ID: " + enseignementId);
        this.enseignementId = enseignementId;
    }
}