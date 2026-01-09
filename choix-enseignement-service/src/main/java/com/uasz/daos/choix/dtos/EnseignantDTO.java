package com.uasz.daos.choix.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnseignantDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String matricule;
    private String email;
    private String grade;
    // On ne met QUE ce dont on a besoin pour afficher ou valider un choix
}
