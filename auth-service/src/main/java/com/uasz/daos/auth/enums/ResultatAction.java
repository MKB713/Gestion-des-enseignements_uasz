package com.uasz.daos.auth.enums;

public enum ResultatAction {
    SUCCES("Action réussie"),
    ECHEC("Action échouée"),
    ERREUR("Erreur lors de l'action");

    private final String description;

    ResultatAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}