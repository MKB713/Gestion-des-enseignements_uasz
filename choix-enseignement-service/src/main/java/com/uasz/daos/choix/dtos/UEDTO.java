package com.uasz.daos.choix.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UEDTO {
    private Long id;
    private String code;
    private String libelle;
    // Ajoutez crédit ou coefficient si nécessaire pour le calcul des heures
}
