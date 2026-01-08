package com.uasz.daos.choix.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour recevoir les données de Maquette depuis le microservice Maquette
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaquetteDTO {
    private Long id;
    private String nom;
    private FormationDTO formation;
}