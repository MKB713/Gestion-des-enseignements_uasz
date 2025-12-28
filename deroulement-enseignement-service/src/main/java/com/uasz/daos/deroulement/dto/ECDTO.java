package com.uasz.daos.deroulement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ECDTO {
    private Long id;
    private String code;
    private String libelle;
    private int cm;
    private int td;
    private int tp;
    private int vht;
}
