package com.uasz.daos.choix.services;

import com.uasz.daos.choix.dtos.ChoixCreateDTO;
import com.uasz.daos.choix.dtos.ChoixResponseDTO;
import com.uasz.daos.choix.dtos.ChoixUpdateDTO;
import com.uasz.daos.choix.dtos.PageResponseDTO;
import com.uasz.daos.choix.model.Choix;
import com.uasz.daos.choix.exceptions.*;
import com.uasz.daos.choix.mappers.ChoixMapper;
import com.uasz.daos.choix.repositories.ChoixRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChoixService {

    private final ChoixRepository choixRepository;
    private final ChoixMapper choixMapper;

    public ChoixResponseDTO ajouterChoix(ChoixCreateDTO dto) {
        log.info("Ajout d'un nouveau choix pour l'enseignant {} et l'enseignement {}",
                dto.getIdEnseignant(), dto.getIdEnseignement());

        if (choixRepository.existsByIdEnseignantAndIdEnseignement(
                dto.getIdEnseignant(), dto.getIdEnseignement())) {
            throw new ChoixAlreadyExistsException(dto.getIdEnseignant(), dto.getIdEnseignement());
        }

        Choix choix = choixMapper.toEntity(dto);
        Choix savedChoix = choixRepository.save(choix);

        log.info("Choix créé avec succès avec l'ID: {}", savedChoix.getId());
        return choixMapper.toResponseDTO(savedChoix);
    }

    public ChoixResponseDTO modifierChoix(Long id, ChoixUpdateDTO dto, Long idEnseignant) {
        log.info("Modification du choix {} par l'enseignant {}", id, idEnseignant);

        Choix choix = choixRepository.findByIdAndIdEnseignant(id, idEnseignant)
                .orElseThrow(() -> new ChoixNotFoundException(
                        "Choix non trouvé ou vous n'êtes pas autorisé à le modifier"));

        if (!choix.getIdEnseignement().equals(dto.getIdEnseignement()) &&
                choixRepository.existsByIdEnseignantAndIdEnseignement(
                        idEnseignant, dto.getIdEnseignement())) {
            throw new ChoixAlreadyExistsException(idEnseignant, dto.getIdEnseignement());
        }

        choix.setIdEnseignement(dto.getIdEnseignement());
        choix.setDateModification(LocalDateTime.now());

        Choix updatedChoix = choixRepository.save(choix);

        log.info("Choix {} modifié avec succès", id);
        return choixMapper.toResponseDTO(updatedChoix);
    }

    public void supprimerChoix(Long id, Long idEnseignant) {
        log.info("Suppression du choix {} par l'enseignant {}", id, idEnseignant);

        Choix choix = choixRepository.findByIdAndIdEnseignant(id, idEnseignant)
                .orElseThrow(() -> new ChoixNotFoundException(
                        "Choix non trouvé ou vous n'êtes pas autorisé à le supprimer"));

        choixRepository.delete(choix);
        log.info("Choix {} supprimé avec succès", id);
    }

    @Transactional(readOnly = true)
    public List<ChoixResponseDTO> rechercherChoixParEnseignant(Long idEnseignant) {
        log.info("Recherche des choix pour l'enseignant {}", idEnseignant);

        List<Choix> choixList = choixRepository.findByIdEnseignant(idEnseignant);

        if (choixList.isEmpty()) {
            throw new ChoixNotFoundException("Aucun choix trouvé pour l'enseignant: " + idEnseignant);
        }

        return choixList.stream()
                .map(choixMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ChoixResponseDTO> listerTousLesChoix(
            Long idEnseignant,
            Long idEnseignement,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Pageable pageable) {

        log.info("Listing de tous les choix avec pagination");

        Page<Choix> choixPage;

        if (idEnseignant != null || idEnseignement != null ||
                dateDebut != null || dateFin != null) {
            choixPage = choixRepository.findWithFilters(
                    idEnseignant, idEnseignement, dateDebut, dateFin, pageable);
        } else {
            choixPage = choixRepository.findAll(pageable);
        }

        List<ChoixResponseDTO> content = choixPage.getContent().stream()
                .map(choixMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ChoixResponseDTO>builder()
                .content(content)
                .pageNumber(choixPage.getNumber())
                .pageSize(choixPage.getSize())
                .totalElements(choixPage.getTotalElements())
                .totalPages(choixPage.getTotalPages())
                .last(choixPage.isLast())
                .first(choixPage.isFirst())
                .build();
    }

    @Transactional(readOnly = true)
    public ChoixResponseDTO getChoixById(Long id) {
        Choix choix = choixRepository.findById(id)
                .orElseThrow(() -> new ChoixNotFoundException(id));
        return choixMapper.toResponseDTO(choix);
    }
}