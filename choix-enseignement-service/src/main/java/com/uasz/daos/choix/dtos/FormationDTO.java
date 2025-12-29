package com.uasz.daos.choix.dtos; // Le package est correct ici

import com.uasz.daos.choix.dtos.FiliereDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
// Plus besoin d'importer FiliereDTO s'ils sont dans le même package !

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormationDTO {
    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Date dateCreation;
    private FiliereDTO filiere; // Cela va fonctionner maintenant
    private com.uasz.daos.choix.dto.NiveauDTO niveau;   // ATTENTION : Vérifiez aussi le package de NiveauDTO !
    private String statutFormation;
}