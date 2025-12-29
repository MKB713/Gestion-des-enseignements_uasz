package com.uasz.daos.choix.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return """
               Choix Enseignement Service
               ==========================
               API Endpoints:
               - GET  /api/choix                  - Lister tous les choix
               - GET  /api/choix/{id}            - Récupérer un choix par ID
               - POST /api/choix                  - Créer un nouveau choix
               - PUT  /api/choix/{id}            - Modifier un choix
               - DELETE /api/choix/{id}          - Supprimer un choix
               - GET  /api/choix/enseignant/{id} - Rechercher par enseignant
               - GET  /api/choix/health          - Health check
               
               Service is running successfully!
               """;
    }
}