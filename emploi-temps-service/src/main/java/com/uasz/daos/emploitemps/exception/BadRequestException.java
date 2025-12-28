package com.uasz.daos.emploitemps.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée quand une requête est mal formée ou contient des données invalides
 * Retourne automatiquement un HTTP 400 Bad Request
 *
 * Exemples d'utilisation :
 * - Dates invalides (date fin avant date début)
 * - Horaires incohérents (heure fin avant heure début)
 * - Données manquantes requises
 * - Format de données incorrect
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private String field;
    private Object rejectedValue;

    /**
     * Constructeur avec message simple
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructeur avec champ et valeur rejetée
     *
     * @param field Champ en erreur
     * @param rejectedValue Valeur rejetée
     * @param message Message d'erreur détaillé
     */
    public BadRequestException(String field, Object rejectedValue, String message) {
        super(String.format("Valeur invalide pour '%s' : %s. %s", field, rejectedValue, message));
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    /**
     * Constructeur avec cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    // Getters
    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}