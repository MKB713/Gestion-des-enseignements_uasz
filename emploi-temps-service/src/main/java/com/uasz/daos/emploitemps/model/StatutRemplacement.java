package com.uasz.daos.emploitemps.model;

public enum StatutRemplacement {
    EN_ATTENTE,  // Demande envoyée, en attente de réponse
    ACCEPTE,     // Remplaçant a accepté
    REFUSE,      // Remplaçant a refusé
    ANNULE       // Remplacement annulé (enseignant titulaire revenu)
}
