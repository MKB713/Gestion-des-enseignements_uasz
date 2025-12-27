package com.uasz.daos.maquette.dto;

import lombok.Data;

@Data
public class ECDTO {
    private String code;
    private String libelle;
    private int cm;
    private int td;
    private int tp;
    private int tpe;
    private int vht;
    private double coefficient;
}
