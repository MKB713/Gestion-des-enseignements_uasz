package com.uasz.daos.maquette.dto;

import lombok.Data;
import java.util.List;

@Data
public class UEDTO {
    private String code;
    private String libelle;
    private int credits;
    private double coefficientUE;
    private List<ECDTO> ecs;
}
