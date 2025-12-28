package com.uasz.daos.emploitemps.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée quand une ressource demandée n'est pas trouvée
 * Retourne automatiquement un HTTP 404 Not Found
 *
 * Exemples d'utilisation :
 * - Séance inexistante
 * - Salle introuvable
 * - Emploi du temps non existant
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    /**
     * Constructeur avec message simple
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructeur avec nom de ressource, champ et valeur
     *
     * @param resourceName Nom de la ressource (ex: "Seance", "Salle")
     * @param fieldName Nom du champ recherché (ex: "id", "code")
     * @param fieldValue Valeur recherchée
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s non trouvé(e) avec %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructeur avec cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Getters
    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}