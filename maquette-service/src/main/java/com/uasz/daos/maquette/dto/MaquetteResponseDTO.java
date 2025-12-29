package com.uasz.daos.maquette.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MaquetteResponseDTO {
    private Long id;
    private String code;
    private String libelle;
    private String description;
    private String statut;
    private Integer version;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private boolean actif;
    private Long formationId;
    private String formationLibelle;
    private List<SemestreDTO> semestres;
}
