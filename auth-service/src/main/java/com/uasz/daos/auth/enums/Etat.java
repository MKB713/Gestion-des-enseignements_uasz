package com.uasz.daos.auth.enums;

public enum Etat {
    ACTIF("Compte actif"),
    INACTIF("Compte inactif"),
    SUSPENDU("Compte suspendu"),
    ARCHIVE("Compte archivé"),
    EN_ATTENTE("En attente de validation");

    private final String description;

    Etat(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}