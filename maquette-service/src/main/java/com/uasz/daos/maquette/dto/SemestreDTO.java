package com.uasz.daos.maquette.dto;

import lombok.Data;
import java.util.List;

@Data
public class SemestreDTO {
    private int numero;
    private String libelle;
    private List<UEDTO> ues;
}
