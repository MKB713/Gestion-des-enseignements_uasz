package com.uasz.daos.choix.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChoixCreateDTO {

    @NotNull(message = "L'ID de l'enseignant est obligatoire")
    private Long idEnseignant;

    @NotNull(message = "L'ID de l'enseignement est obligatoire")
    private Long idEnseignement;
}