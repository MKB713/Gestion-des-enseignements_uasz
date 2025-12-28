package com.uasz.daos.auth.enums;

public enum StatutEnseignant {
    PERMANENT("Enseignant permanent"),
    VACATAIRE("Enseignant vacataire"),
    CONTRACTUEL("Enseignant contractuel"),
    ASSOCIE("Enseignant associé");

    private final String libelle;

    StatutEnseignant(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}