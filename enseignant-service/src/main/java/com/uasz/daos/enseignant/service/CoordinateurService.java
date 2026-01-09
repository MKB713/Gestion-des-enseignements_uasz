package com.uasz.daos.enseignant.service;

import com.uasz.daos.enseignant.dto.CoordinateurDTO;
import com.uasz.daos.enseignant.model.Coordinateur;

import com.uasz.daos.enseignant.repository.CoordinateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@SuppressWarnings("null")
public class CoordinateurService {

    @Autowired
    private CoordinateurRepository coordinateurRepository;

    @Autowired

    /**
     * Récupère tous les coordinateurs
     */
    public List<Coordinateur> getAllCoordinateurs() {
        return coordinateurRepository.findAll();
    }

    /**
     * Récupère un coordinateur par son ID
     */
    public Coordinateur getCoordinateurById(long id) {
        return coordinateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coordinateur non trouvé avec l'ID : " + id));
    }

    /**
     * Crée un nouveau coordinateur
     */
    public Coordinateur creerCoordinateur(CoordinateurDTO dto) {
        // Vérification de l'unicité de l'email
        if (coordinateurRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Un coordinateur avec cet email existe déjà : " + dto.getEmail());
        }

        Coordinateur coordinateur = new Coordinateur();
        coordinateur.setNom(dto.getNom());
        coordinateur.setPrenom(dto.getPrenom());
        coordinateur.setEmail(dto.getEmail());
        coordinateur.setTelephone(dto.getTelephone());

        // Associer la formation si fournie
        if (dto.getFormationId() != null) {
            coordinateur.setFormationId(dto.getFormationId());
        }

        // Associer l'enseignant si fourni
        if (dto.getEnseignantId() != null) {
            coordinateur.setEnseignantId(dto.getEnseignantId());
        }

        coordinateur.setDateDebutFonction(
                dto.getDateDebutFonction() != null ? dto.getDateDebutFonction() : LocalDate.now());
        coordinateur.setDateFinFonction(dto.getDateFinFonction());
        coordinateur.setActif(dto.getActif() != null ? dto.getActif() : true);
        coordinateur.setRemarques(dto.getRemarques());

        return coordinateurRepository.save(coordinateur);
    }

    /**
     * Met à jour un coordinateur existant
     */
    public Coordinateur modifierCoordinateur(long id, CoordinateurDTO dto) {
        Coordinateur coordinateur = getCoordinateurById(id);

        // Vérification de l'email si modifié
        if (!coordinateur.getEmail().equals(dto.getEmail())) {
            if (coordinateurRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Un coordinateur avec cet email existe déjà : " + dto.getEmail());
            }
            coordinateur.setEmail(dto.getEmail());
        }

        coordinateur.setNom(dto.getNom());
        coordinateur.setPrenom(dto.getPrenom());
        coordinateur.setTelephone(dto.getTelephone());

        // Mise à jour de la formation
        if (dto.getFormationId() != null) {
            coordinateur.setFormationId(dto.getFormationId());
        } else {
            coordinateur.setFormationId(null);
        }

        // Mise à jour de l'enseignant
        if (dto.getEnseignantId() != null) {
            coordinateur.setEnseignantId(dto.getEnseignantId());
        } else {
            coordinateur.setEnseignantId(null);
        }

        if (dto.getDateDebutFonction() != null) {
            coordinateur.setDateDebutFonction(dto.getDateDebutFonction());
        }
        coordinateur.setDateFinFonction(dto.getDateFinFonction());

        if (dto.getActif() != null) {
            coordinateur.setActif(dto.getActif());
        }
        coordinateur.setRemarques(dto.getRemarques());

        return coordinateurRepository.save(coordinateur);
    }

    /**
     * Désactive un coordinateur
     */
    public void desactiverCoordinateur(long id) {
        Coordinateur coordinateur = getCoordinateurById(id);
        coordinateur.setActif(false);
        coordinateur.setDateFinFonction(LocalDate.now());
        coordinateurRepository.save(coordinateur);
    }

    /**
     * Réactive un coordinateur
     */
    public void reactiverCoordinateur(long id) {
        Coordinateur coordinateur = getCoordinateurById(id);
        coordinateur.setActif(true);
        coordinateur.setDateFinFonction(null);
        coordinateurRepository.save(coordinateur);
    }

    /**
     * Récupère les coordinateurs actifs
     */
    public List<Coordinateur> getCoordinateursActifs() {
        return coordinateurRepository.findByActifTrue();
    }

    /**
     * Recherche des coordinateurs
     */
    public List<Coordinateur> rechercherCoordinateurs(String term) {
        return coordinateurRepository.rechercherCoordinateurs(term);
    }

    /**
     * Récupère le coordinateur actif d'une formation
     */
    public Coordinateur getCoordinateurActifFormation(Long formationId) {
        return coordinateurRepository.findCoordinateurActifByFormation(formationId)
                .orElse(null);
    }

    /**
     * Supprime un coordinateur
     */
    public void supprimerCoordinateur(long id) {
        Coordinateur coordinateur = getCoordinateurById(id);
        coordinateurRepository.delete(coordinateur);
    }
}
