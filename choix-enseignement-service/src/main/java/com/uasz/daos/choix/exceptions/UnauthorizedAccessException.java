package com.uasz.daos.choix.exceptions;

import lombok.Getter;

@Getter
public class UnauthorizedAccessException extends RuntimeException {
    private final Long enseignantId;

    public UnauthorizedAccessException(Long enseignantId) {
        super("L'enseignant " + enseignantId + " n'est pas autorisé à effectuer cette opération");
        this.enseignantId = enseignantId;
    }
}