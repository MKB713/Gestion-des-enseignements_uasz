package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.dto.*;
import com.uasz.daos.maquette.model.*;
import com.uasz.daos.maquette.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class MaquetteService {

    private final MaquetteRepository maquetteRepository;
    private final FormationRepository formationRepository;
    private final MaquetteVersionRepository maquetteVersionRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Transactional
    public MaquetteResponseDTO ajouterMaquette(MaquetteRequestDTO requestDTO) {
        if (maquetteRepository.existsByCode(requestDTO.getCode())) {
            throw new IllegalArgumentException("Une maquette avec ce code existe déjà");
        }

        Formation formation = formationRepository.findById(requestDTO.getFormationId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formation non trouvée avec ID: " + requestDTO.getFormationId()));

        Maquette maquette = new Maquette();
        maquette.setCode(requestDTO.getCode());
        maquette.setLibelle(requestDTO.getLibelle());
        maquette.setDescription(requestDTO.getDescription());
        maquette.setStatut(StatutMaquette.BROUILLON);
        maquette.setVersion(1);
        maquette.setDateCreation(LocalDateTime.now());
        maquette.setDateModification(LocalDateTime.now());
        maquette.setFormation(formation);
        maquette.setActif(true);

        if (requestDTO.getSemestres() != null) {
            List<Semestre> semestres = requestDTO.getSemestres().stream()
                    .map(sDto -> mapToSemestreEntity(sDto, maquette))
                    .collect(Collectors.toList());
            maquette.setSemestres(semestres);
        }

        Maquette saved = maquetteRepository.save(maquette);
        log.info("Maquette hiérarchique créée avec ID: {}", saved.getId());

        return mapToResponseDTO(saved);
    }

    @Transactional
    public void publierMaquette(long id) {
        Maquette maquette = maquetteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maquette non trouvée"));
        maquette.setStatut(StatutMaquette.PUBLIEE);
        maquetteRepository.save(maquette);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public MaquetteResponseDTO modifierMaquette(long id, MaquetteRequestDTO requestDTO) {
        Maquette existing = maquetteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maquette non trouvée"));

        // Si publiée, on crée une nouvelle version et on archive l'ancienne
        if (existing.getStatut() == StatutMaquette.PUBLIEE) {
            saveSnapshot(existing);

            Maquette nouvelle = new Maquette();
            nouvelle.setCode(requestDTO.getCode());
            nouvelle.setLibelle(requestDTO.getLibelle());
            nouvelle.setDescription(requestDTO.getDescription());
            nouvelle.setStatut(StatutMaquette.BROUILLON);
            nouvelle.setVersion(existing.getVersion() + 1);
            nouvelle.setDateCreation(existing.getDateCreation());
            nouvelle.setDateModification(LocalDateTime.now());
            nouvelle.setFormation(existing.getFormation());
            nouvelle.setMaquetteParent(existing);
            nouvelle.setActif(true);

            if (requestDTO.getSemestres() != null) {
                nouvelle.setSemestres(requestDTO.getSemestres().stream()
                        .map(sDto -> mapToSemestreEntity(sDto, nouvelle))
                        .collect(Collectors.toList()));
            }

            existing.setActif(false);
            maquetteRepository.save(existing);
            return mapToResponseDTO(maquetteRepository.save(nouvelle));
        } else {
            // Sinon on modifie sur place
            existing.setLibelle(requestDTO.getLibelle());
            existing.setDescription(requestDTO.getDescription());
            existing.setDateModification(LocalDateTime.now());

            if (requestDTO.getSemestres() != null) {
                // On pourrait faire un merge plus fin, mais ici on remplace pour la simplicité
                // du scope
                existing.getSemestres().clear();
                existing.getSemestres().addAll(requestDTO.getSemestres().stream()
                        .map(sDto -> mapToSemestreEntity(sDto, existing))
                        .collect(Collectors.toList()));
            }
            return mapToResponseDTO(maquetteRepository.save(existing));
        }
    }

    private void saveSnapshot(Maquette maquette) {
        try {
            MaquetteVersion version = new MaquetteVersion();
            version.setMaquette(maquette);
            version.setNumeroVersion(maquette.getVersion());
            version.setDonneesJson(objectMapper.writeValueAsString(mapToResponseDTO(maquette)));
            version.setDateCreation(LocalDateTime.now());
            version.setUtilisateur("SYSTEM");
            maquetteVersionRepository.save(version);
        } catch (Exception e) {
            log.error("Erreur lors de la création du snapshot JSON", e);
        }
    }

    private Semestre mapToSemestreEntity(SemestreDTO dto, Maquette maquette) {
        Semestre s = new Semestre();
        s.setNumero(dto.getNumero());
        s.setLibelle(dto.getLibelle());
        s.setMaquette(maquette);
        if (dto.getUes() != null) {
            s.setUes(dto.getUes().stream()
                    .map(ueDto -> mapToUEEntity(ueDto, s))
                    .collect(Collectors.toList()));
            // Calculs automatiques
            s.setCreditsTotaux(s.getUes().stream().mapToInt(UE::getCredits).sum());
            s.setCoefficientsTotaux(s.getUes().stream().mapToDouble(UE::getCoefficientUE).sum());
        }
        return s;
    }

    private UE mapToUEEntity(UEDTO dto, Semestre semestre) {
        UE ue = new UE();
        ue.setCode(dto.getCode());
        ue.setLibelle(dto.getLibelle());
        ue.setCredits(dto.getCredits());
        ue.setCoefficientUE(dto.getCoefficientUE());
        ue.setSemestre(semestre);
        if (dto.getEcs() != null) {
            ue.setEcs(dto.getEcs().stream()
                    .map(ecDto -> mapToECEntity(ecDto, ue))
                    .collect(Collectors.toList()));
            // Calculs automatiques volumes horaires
            ue.setCm(ue.getEcs().stream().mapToInt(EC::getCm).sum());
            ue.setTd(ue.getEcs().stream().mapToInt(EC::getTd).sum());
            ue.setTp(ue.getEcs().stream().mapToInt(EC::getTp).sum());
            ue.setVht(ue.getEcs().stream().mapToInt(EC::getVht).sum());
        }
        return ue;
    }

    private EC mapToECEntity(ECDTO dto, UE ue) {
        EC ec = new EC();
        ec.setCode(dto.getCode());
        ec.setLibelle(dto.getLibelle());
        ec.setCm(dto.getCm());
        ec.setTd(dto.getTd());
        ec.setTp(dto.getTp());
        ec.setTpe(dto.getTpe());
        ec.setVht(dto.getVht());
        ec.setCoefficient(dto.getCoefficient());
        ec.setUe(ue);
        return ec;
    }

    @Transactional(readOnly = true)
    public MaquetteResponseDTO detailsMaquette(long id) {
        Maquette maquette = maquetteRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Maquette non trouvée ou inactive"));
        return mapToResponseDTO(maquette);
    }

    @Transactional(readOnly = true)
    public List<MaquetteResponseDTO> listerTout() {
        return maquetteRepository.findByActifTrue().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void supprimerMaquette(Long id) {
        if (id == null)
            throw new IllegalArgumentException("L'ID ne peut pas être nul");
        Maquette maquette = maquetteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maquette non trouvée"));
        if (maquette.getStatut() == StatutMaquette.PUBLIEE) {
            throw new IllegalStateException("Impossible de supprimer une maquette publiée");
        }
        maquette.setActif(false);
        maquetteRepository.save(maquette);
    }

    public MaquetteResponseDTO mapToResponseDTO(Maquette maquette) {
        MaquetteResponseDTO dto = new MaquetteResponseDTO();
        dto.setId(maquette.getId());
        dto.setCode(maquette.getCode());
        dto.setLibelle(maquette.getLibelle());
        dto.setDescription(maquette.getDescription());
        dto.setStatut(maquette.getStatut() != null ? maquette.getStatut().name() : null);
        dto.setVersion(maquette.getVersion());
        dto.setDateCreation(maquette.getDateCreation());
        dto.setDateModification(maquette.getDateModification());
        dto.setActif(maquette.isActif());

        if (maquette.getFormation() != null) {
            dto.setFormationId(maquette.getFormation().getId());
            dto.setFormationLibelle(maquette.getFormation().getLibelle());
        }

        if (maquette.getSemestres() != null) {
            dto.setSemestres(maquette.getSemestres().stream()
                    .map(this::mapToSemestreDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private SemestreDTO mapToSemestreDTO(Semestre s) {
        SemestreDTO dto = new SemestreDTO();
        dto.setNumero(s.getNumero());
        dto.setLibelle(s.getLibelle());
        if (s.getUes() != null) {
            dto.setUes(s.getUes().stream().map(this::mapToUEDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private UEDTO mapToUEDTO(UE ue) {
        UEDTO dto = new UEDTO();
        dto.setCode(ue.getCode());
        dto.setLibelle(ue.getLibelle());
        dto.setCredits(ue.getCredits());
        dto.setCoefficientUE(ue.getCoefficientUE());
        if (ue.getEcs() != null) {
            dto.setEcs(ue.getEcs().stream().map(this::mapToECDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private ECDTO mapToECDTO(EC ec) {
        ECDTO dto = new ECDTO();
        dto.setCode(ec.getCode());
        dto.setLibelle(ec.getLibelle());
        dto.setCm(ec.getCm());
        dto.setTd(ec.getTd());
        dto.setTp(ec.getTp());
        dto.setTpe(ec.getTpe());
        dto.setVht(ec.getVht());
        dto.setCoefficient(ec.getCoefficient());
        return dto;
    }

    public List<MaquetteVersion> listerVersions(long id) {
        Maquette maquette = maquetteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maquette non trouvée"));
        return maquetteVersionRepository.findByMaquetteOrderByNumeroVersionDesc(maquette);
    }
}
