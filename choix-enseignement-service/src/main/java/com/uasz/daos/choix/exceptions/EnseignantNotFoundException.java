package com.uasz.daos.choix.exceptions;

import lombok.Getter;

@Getter
public class EnseignantNotFoundException extends RuntimeException {
    private final Long enseignantId;

    public EnseignantNotFoundException(Long enseignantId) {
        super("Enseignant non trouvé avec l'ID: " + enseignantId);
        this.enseignantId = enseignantId;
    }
}