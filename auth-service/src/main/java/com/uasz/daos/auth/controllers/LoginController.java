package com.uasz.daos.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LoginController {

    @GetMapping("/login")
    public ResponseEntity<?> index() {
        // Pour un microservice, cette endpoint doit retourner JSON
        return ResponseEntity.ok().body(Map.of(
                "message", "Veuillez utiliser POST /api/auth/login pour vous authentifier",
                "endpoints", Map.of(
                        "login", "POST /api/auth/login",
                        "register", "POST /api/auth/register"
                )
        ));
    }

    @GetMapping("/auth2")
    public ResponseEntity<?> auth2() {
        // Cette endpoint n'est plus utilisée pour le formulaire login
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Utilisez POST /api/auth/login avec JSON"
        ));
    }
}