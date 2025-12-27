package com.uasz.daos.choix.dto;

import lombok.Data;

@Data
public class UEDTO {
    private Long id;
    private String code;
    private String libelle;
    // Ajoutez crédit ou coefficient si nécessaire pour le calcul des heures
}