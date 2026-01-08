package com.uasz.daos.choix.mappers;

import com.uasz.daos.choix.dtos.ChoixCreateDTO;
import com.uasz.daos.choix.dtos.ChoixResponseDTO;
import com.uasz.daos.choix.model.Choix;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChoixMapper {

    public Choix toEntity(ChoixCreateDTO dto) {
        Choix choix = new Choix();
        choix.setIdEnseignant(dto.getIdEnseignant());
        choix.setIdEnseignement(dto.getIdEnseignement());
        choix.setDateChoix(LocalDateTime.now());
        return choix;
    }

    public ChoixResponseDTO toResponseDTO(Choix choix) {
        return ChoixResponseDTO.builder()
                .id(choix.getId())
                .idEnseignant(choix.getIdEnseignant())
                .idEnseignement(choix.getIdEnseignement())
                .dateChoix(choix.getDateChoix())
                .dateCreation(choix.getDateCreation())
                .dateModification(choix.getDateModification())
                .build();
    }

    public ChoixResponseDTO toResponseDTOWithDetails(
            Choix choix,
            String nomEnseignant,
            String prenomEnseignant,
            String libelleEnseignement) {

        ChoixResponseDTO dto = toResponseDTO(choix);
        dto.setNomEnseignant(nomEnseignant);
        dto.setPrenomEnseignant(prenomEnseignant);
        dto.setLibelleEnseignement(libelleEnseignement);
        return dto;
    }
}