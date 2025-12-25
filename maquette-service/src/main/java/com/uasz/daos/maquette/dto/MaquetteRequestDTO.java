package com.uasz.daos.maquette.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class MaquetteRequestDTO {
    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    private String description;

    @NotNull(message = "L'ID de formation est obligatoire")
    private Long formationId;

    private List<SemestreDTO> semestres;
}
