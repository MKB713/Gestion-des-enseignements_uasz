package com.uasz.daos.choix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO pour recevoir les données de Formation depuis le microservice Maquette
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormationDTO {
    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Date dateCreation;
    private FiliereDTO filiere;
    private NiveauDTO niveau;
    private String statutFormation;
}