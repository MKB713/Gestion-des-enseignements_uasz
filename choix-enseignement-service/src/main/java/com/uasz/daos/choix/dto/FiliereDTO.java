package com.uasz.daos.choix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour recevoir les données de Filiere depuis le microservice Maquette
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiliereDTO {
    private Long id;
    private String nom;
    private String code;
}