package com.uasz.daos.choix.exceptions;

import lombok.Getter;

@Getter
public class ChoixAlreadyExistsException extends RuntimeException {
    private final Long idEnseignant;
    private final Long idEnseignement;

    public ChoixAlreadyExistsException(Long idEnseignant, Long idEnseignement) {
        super(String.format("L'enseignant %d a déjà choisi l'enseignement %d",
                idEnseignant, idEnseignement));
        this.idEnseignant = idEnseignant;
        this.idEnseignement = idEnseignement;
    }
}