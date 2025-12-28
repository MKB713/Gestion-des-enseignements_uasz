package com.uasz.daos.emploitemps.model;

public enum StatutSeance {
    PLANIFIEE,    // Séance créée mais pas encore confirmée
    CONFIRMEE,    // Séance confirmée
    EN_COURS,     // Séance en cours (optionnel)
    TERMINEE,     // Séance terminée (optionnel)
    ANNULEE       // Séance annulée (soft delete)
}
