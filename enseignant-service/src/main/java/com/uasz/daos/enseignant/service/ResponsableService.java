package com.uasz.daos.enseignant.service;

import com.uasz.daos.enseignant.dto.ResponsableDTO;

import com.uasz.daos.enseignant.model.Responsable;
import com.uasz.daos.enseignant.enums.TypeResponsable;
import com.uasz.daos.enseignant.repository.ResponsableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@SuppressWarnings("null")
public class ResponsableService {

    @Autowired
    private ResponsableRepository responsableRepository;

    @Autowired

    /**
     * Récupère tous les responsables
     */
    public List<Responsable> getAllResponsables() {
        return responsableRepository.findAll();
    }

    /**
     * Récupère un responsable par son ID
     */
    public Responsable getResponsableById(long id) {
        return responsableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Responsable non trouvé avec l'ID : " + id));
    }

    /**
     * Crée un nouveau responsable
     */
    public Responsable creerResponsable(ResponsableDTO dto) {
        // Vérification de l'unicité de l'email
        if (responsableRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Un responsable avec cet email existe déjà : " + dto.getEmail());
        }

        Responsable responsable = new Responsable();
        responsable.setNom(dto.getNom());
        responsable.setPrenom(dto.getPrenom());
        responsable.setEmail(dto.getEmail());
        responsable.setTelephone(dto.getTelephone());
        responsable.setType(dto.getType());

        // Associer la formation si fournie
        if (dto.getFormationId() != null) {
            responsable.setFormationId(dto.getFormationId());
        }

        // Associer l'enseignant si fourni
        if (dto.getEnseignantId() != null) {
            responsable.setEnseignantId(dto.getEnseignantId());
        }

        responsable.setDateDebutFonction(
                dto.getDateDebutFonction() != null ? dto.getDateDebutFonction() : LocalDate.now());
        responsable.setDateFinFonction(dto.getDateFinFonction());
        responsable.setActif(dto.getActif() != null ? dto.getActif() : true);
        responsable.setRemarques(dto.getRemarques());

        return responsableRepository.save(responsable);
    }

    /**
     * Met à jour un responsable existant
     */
    public Responsable modifierResponsable(long id, ResponsableDTO dto) {
        Responsable responsable = getResponsableById(id);

        // Vérification de l'email si modifié
        if (!responsable.getEmail().equals(dto.getEmail())) {
            if (responsableRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Un responsable avec cet email existe déjà : " + dto.getEmail());
            }
            responsable.setEmail(dto.getEmail());
        }

        responsable.setNom(dto.getNom());
        responsable.setPrenom(dto.getPrenom());
        responsable.setTelephone(dto.getTelephone());
        responsable.setType(dto.getType());

        // Mise à jour de la formation
        if (dto.getFormationId() != null) {
            responsable.setFormationId(dto.getFormationId());
        } else {
            responsable.setFormationId(null);
        }

        // Mise à jour de l'enseignant
        if (dto.getEnseignantId() != null) {
            responsable.setEnseignantId(dto.getEnseignantId());
        } else {
            responsable.setEnseignantId(null);
        }

        if (dto.getDateDebutFonction() != null) {
            responsable.setDateDebutFonction(dto.getDateDebutFonction());
        }
        responsable.setDateFinFonction(dto.getDateFinFonction());

        if (dto.getActif() != null) {
            responsable.setActif(dto.getActif());
        }
        responsable.setRemarques(dto.getRemarques());

        return responsableRepository.save(responsable);
    }

    /**
     * Désactive un responsable
     */
    public void desactiverResponsable(long id) {
        Responsable responsable = getResponsableById(id);
        responsable.setActif(false);
        responsable.setDateFinFonction(LocalDate.now());
        responsableRepository.save(responsable);
    }

    /**
     * Réactive un responsable
     */
    public void reactiverResponsable(long id) {
        Responsable responsable = getResponsableById(id);
        responsable.setActif(true);
        responsable.setDateFinFonction(null);
        responsableRepository.save(responsable);
    }

    /**
     * Récupère les responsables actifs
     */
    public List<Responsable> getResponsablesActifs() {
        return responsableRepository.findByActifTrue();
    }

    /**
     * Récupère les responsables par type
     */
    public List<Responsable> getResponsablesParType(TypeResponsable type) {
        return responsableRepository.findByType(type);
    }

    /**
     * Récupère les responsables de licence
     */
    public List<Responsable> getResponsablesLicence() {
        return responsableRepository.findByTypeAndActifTrue(TypeResponsable.LICENCE);
    }

    /**
     * Récupère les responsables de master
     */
    public List<Responsable> getResponsablesMaster() {
        return responsableRepository.findByTypeAndActifTrue(TypeResponsable.MASTER);
    }

    /**
     * Recherche des responsables
     */
    public List<Responsable> rechercherResponsables(String term) {
        return responsableRepository.rechercherResponsables(term);
    }

    /**
     * Récupère le responsable actif d'une formation
     */
    public Responsable getResponsableActifFormation(Long formationId) {
        return responsableRepository.findResponsableActifByFormation(formationId)
                .orElse(null);
    }

    /**
     * Supprime un responsable
     */
    public void supprimerResponsable(long id) {
        Responsable responsable = getResponsableById(id);
        responsableRepository.delete(responsable);
    }
}
