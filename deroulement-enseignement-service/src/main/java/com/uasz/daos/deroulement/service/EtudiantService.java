package com.uasz.daos.deroulement.service;

import com.uasz.daos.deroulement.dto.EtudiantDTO;
import com.uasz.daos.deroulement.model.Etudiant;
import com.uasz.daos.deroulement.enums.StatutEtudiant;
import com.uasz.daos.deroulement.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@SuppressWarnings("null")
public class EtudiantService {

    @Autowired
    private EtudiantRepository etudiantRepository;

    /**
     * Récupère tous les étudiants
     */
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    /**
     * Récupère un étudiant par son ID
     */
    public Etudiant getEtudiantById(Long id) {
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'ID : " + id));
    }

    /**
     * Récupère un étudiant par son matricule
     */
    public Etudiant getEtudiantByMatricule(String matricule) {
        return etudiantRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec le matricule : " + matricule));
    }

    /**
     * Crée un nouvel étudiant
     */
    public Etudiant creerEtudiant(EtudiantDTO dto) {
        // Vérification de l'unicité du matricule
        if (etudiantRepository.existsByMatricule(dto.getMatricule())) {
            throw new IllegalArgumentException("Un étudiant avec ce matricule existe déjà : " + dto.getMatricule());
        }

        // Vérification de l'unicité de l'email
        if (dto.getEmail() != null && etudiantRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Un étudiant avec cet email existe déjà : " + dto.getEmail());
        }

        Etudiant etudiant = new Etudiant();
        etudiant.setMatricule(dto.getMatricule());
        etudiant.setNom(dto.getNom());
        etudiant.setPrenom(dto.getPrenom());
        etudiant.setEmail(dto.getEmail());
        etudiant.setTelephone(dto.getTelephone());
        etudiant.setDateNaissance(dto.getDateNaissance());
        etudiant.setLieuNaissance(dto.getLieuNaissance());
        etudiant.setSexe(dto.getSexe());
        etudiant.setAdresse(dto.getAdresse());
        etudiant.setPhotoUrl(dto.getPhotoUrl());
        etudiant.setNumeroCNI(dto.getNumeroCNI());
        etudiant.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutEtudiant.ACTIF);
        etudiant.setDateInscription(dto.getDateInscription() != null ? dto.getDateInscription() : LocalDate.now());

        return etudiantRepository.save(etudiant);
    }

    /**
     * Met à jour un étudiant existant
     */
    public Etudiant modifierEtudiant(Long id, EtudiantDTO dto) {
        Etudiant etudiant = getEtudiantById(id);

        // Vérification du matricule si modifié
        if (!etudiant.getMatricule().equals(dto.getMatricule())) {
            if (etudiantRepository.existsByMatricule(dto.getMatricule())) {
                throw new IllegalArgumentException("Un étudiant avec ce matricule existe déjà : " + dto.getMatricule());
            }
            etudiant.setMatricule(dto.getMatricule());
        }

        // Vérification de l'email si modifié
        if (dto.getEmail() != null && !dto.getEmail().equals(etudiant.getEmail())) {
            if (etudiantRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Un étudiant avec cet email existe déjà : " + dto.getEmail());
            }
            etudiant.setEmail(dto.getEmail());
        }

        etudiant.setNom(dto.getNom());
        etudiant.setPrenom(dto.getPrenom());
        etudiant.setTelephone(dto.getTelephone());
        etudiant.setDateNaissance(dto.getDateNaissance());
        etudiant.setLieuNaissance(dto.getLieuNaissance());
        etudiant.setSexe(dto.getSexe());
        etudiant.setAdresse(dto.getAdresse());
        etudiant.setPhotoUrl(dto.getPhotoUrl());
        etudiant.setNumeroCNI(dto.getNumeroCNI());

        if (dto.getStatut() != null) {
            etudiant.setStatut(dto.getStatut());
        }

        return etudiantRepository.save(etudiant);
    }

    /**
     * Change le statut d'un étudiant
     */
    public Etudiant changerStatut(Long id, StatutEtudiant nouveauStatut) {
        Etudiant etudiant = getEtudiantById(id);
        etudiant.setStatut(nouveauStatut);
        return etudiantRepository.save(etudiant);
    }

    /**
     * Suspend un étudiant
     */
    public void suspendreEtudiant(Long id) {
        changerStatut(id, StatutEtudiant.SUSPENDU);
    }

    /**
     * Réactive un étudiant suspendu
     */
    public void reactiverEtudiant(Long id) {
        changerStatut(id, StatutEtudiant.ACTIF);
    }

    /**
     * Marque un étudiant comme diplômé
     */
    public void marquerCommeDiplome(Long id) {
        changerStatut(id, StatutEtudiant.DIPLOME);
    }

    /**
     * Recherche des étudiants
     */
    public List<Etudiant> rechercherEtudiants(String term) {
        return etudiantRepository.rechercherEtudiants(term);
    }

    /**
     * Récupère les étudiants actifs
     */
    public List<Etudiant> getEtudiantsActifs() {
        return etudiantRepository.findByStatut(StatutEtudiant.ACTIF);
    }

    /**
     * Récupère les étudiants par sexe
     */
    public List<Etudiant> getEtudiantsParSexe(String sexe) {
        return etudiantRepository.findBySexe(sexe);
    }

    /**
     * Compte le nombre d'étudiants actifs
     */
    public long compterEtudiantsActifs() {
        return etudiantRepository.countByStatut(StatutEtudiant.ACTIF);
    }

    /**
     * Compte le total des étudiants
     */
    public long compterTousLesEtudiants() {
        return etudiantRepository.count();
    }

    /**
     * Supprime un étudiant (à utiliser avec précaution)
     */
    public void supprimerEtudiant(Long id) {
        Etudiant etudiant = getEtudiantById(id);
        etudiantRepository.delete(etudiant);
    }
}
