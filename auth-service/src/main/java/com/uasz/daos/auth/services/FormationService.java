package com.uasz.daos.auth.services;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class FormationService {

    public List<Object> getAllFormations() {
        // Stub - à implémenter avec un client Feign vers le microservice maquette
        // Pour l'instant, retourne une liste vide
        return new ArrayList<>();
    }

    public List<Object> getFormationsByRole(String role) {
        // Stub - à implémenter
        return new ArrayList<>();
    }

    public Object getFormationById(Long id) {
        // Stub - à implémenter
        return null;
    }
}