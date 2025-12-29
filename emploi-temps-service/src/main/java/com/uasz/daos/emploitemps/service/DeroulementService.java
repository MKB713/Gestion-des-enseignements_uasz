package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.Deroulement;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.repository.DeroulementRepository;
import com.uasz.daos.emploitemps.repository.SeanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DeroulementService {

    @Autowired
    private DeroulementRepository deroulementRepository;

    @Autowired
    private SeanceRepository seanceRepository;

    // Corresponds to listerTout in controller
    public List<Deroulement> listerTout() {
        return deroulementRepository.findAll();
    }

    // Corresponds to chercher in controller
    public Deroulement chercher(Long id) {
        return deroulementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Déroulement non trouvé avec l'id: " + id));
    }

    // Corresponds to enregistrer in controller
    @Transactional
    public Deroulement enregistrer(Deroulement deroulement) {
        // The controller sends a deroulement object. We need to ensure the seance is attached.
        if (deroulement.getSeance() == null || deroulement.getSeance().getId() == null) {
            throw new IllegalArgumentException("L'ID de la séance est requis pour créer un déroulement.");
        }
        Seance seance = seanceRepository.findById(deroulement.getSeance().getId())
                .orElseThrow(() -> new EntityNotFoundException("Séance non trouvée avec l'id: " + deroulement.getSeance().getId()));
        deroulement.setSeance(seance);
        return deroulementRepository.save(deroulement);
    }

    // Corresponds to modifier in controller
    @Transactional
    public Deroulement modifier(Long id, Deroulement deroulementDetails) {
        Deroulement existingDeroulement = chercher(id);
        existingDeroulement.setStatut(deroulementDetails.getStatut());
        existingDeroulement.setVolumeHoraireEffectue(deroulementDetails.getVolumeHoraireEffectue());
        existingDeroulement.setCompteRendu(deroulementDetails.getCompteRendu());
        existingDeroulement.setDateValidation(deroulementDetails.getDateValidation());
        return deroulementRepository.save(existingDeroulement);
    }

    // Corresponds to supprimer in controller
    @Transactional
    public void supprimer(Long id) {
        if (!deroulementRepository.existsById(id)) {
            throw new EntityNotFoundException("Déroulement non trouvé avec l'id: " + id);
        }
        deroulementRepository.deleteById(id);
    }

    // Corresponds to getBySeance in controller
    public Deroulement getBySeance(Long seanceId) {
        return deroulementRepository.findBySeanceId(seanceId)
                .orElseThrow(() -> new EntityNotFoundException("Déroulement non trouvé pour la séance id: " + seanceId));
    }

    // Corresponds to getByEnseignant in controller - NEW METHOD
    public List<Deroulement> getByEnseignant(Long enseignantId) {
        // This requires a new method in the repository
        return deroulementRepository.findBySeanceEnseignantId(enseignantId);
    }

    // Corresponds to valider in controller
    @Transactional
    public Deroulement valider(Long id, String commentaire, String utilisateur) {
        Deroulement deroulement = chercher(id);
        if (!"EN_ATTENTE".equals(deroulement.getStatut())) {
             throw new IllegalStateException("Le déroulement n'est pas en attente de validation.");
        }
        deroulement.setStatut("VALIDE");
        deroulement.setCompteRendu(commentaire);
        deroulement.setDateValidation(new Date());
        // We might want to add a 'validateur' field to the Deroulement entity
        return deroulementRepository.save(deroulement);
    }

    // Corresponds to invalider in controller
    @Transactional
    public Deroulement invalider(Long id, String commentaire, String utilisateur) {
        Deroulement deroulement = chercher(id);
        deroulement.setStatut("INVALIDE");
        deroulement.setCompteRendu(commentaire);
        deroulement.setDateValidation(new Date());
        return deroulementRepository.save(deroulement);
    }

    // Corresponds to getValides in controller
    public List<Deroulement> getValides() {
        return deroulementRepository.findByStatutOrderByDateValidationDesc("VALIDE");
    }

    // Corresponds to getEnAttente in controller
    public List<Deroulement> getEnAttente() {
        return deroulementRepository.findByStatutOrderByDateValidationDesc("EN_ATTENTE");
    }
}
