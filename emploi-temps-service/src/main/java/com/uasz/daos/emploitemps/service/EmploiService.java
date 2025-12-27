package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.Emploi;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.repository.EmploiRepository;
import com.uasz.daos.emploitemps.repository.SeanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class EmploiService {

    @Autowired
    private EmploiRepository emploiRepository;

    @Autowired
    private SeanceRepository seanceRepository;

    /**
     * Récupère tous les emplois du temps
     */
    public List<Emploi> getAllEmplois() {
        return emploiRepository.findAll();
    }

    /**
     * Récupère un emploi du temps par ID
     */
    public Emploi getEmploiById(Long id) {
        return emploiRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Emploi du temps non trouvé avec l'id: " + id));
    }

    /**
     * Crée un nouvel emploi du temps
     */
    @Transactional
    public Emploi createEmploi(Emploi emploi) {
        if (emploi.getDateCreation() == null) {
            emploi.setDateCreation(new Date());
        }
        return emploiRepository.save(emploi);
    }

    /**
     * Met à jour un emploi du temps
     */
    @Transactional
    public Emploi updateEmploi(Long id, Emploi emploiDetails) {
        Emploi existingEmploi = getEmploiById(id);

        existingEmploi.setLibelle(emploiDetails.getLibelle());

        return emploiRepository.save(existingEmploi);
    }

    /**
     * Supprime un emploi du temps
     */
    @Transactional
    public void deleteEmploi(Long id) {
        if (!emploiRepository.existsById(id)) {
            throw new EntityNotFoundException("Emploi du temps non trouvé avec l'id: " + id);
        }
        emploiRepository.deleteById(id);
    }

    /**
     * Recherche des emplois du temps par libellé
     */
    public List<Emploi> searchEmplois(String libelle) {
        return emploiRepository.findByLibelleContainingIgnoreCase(libelle);
    }

    /**
     * Récupère un emploi du temps par libellé exact
     */
    public Emploi getEmploiByLibelle(String libelle) {
        return emploiRepository.findByLibelle(libelle)
                .orElseThrow(() -> new EntityNotFoundException("Emploi du temps non trouvé avec le libellé: " + libelle));
    }

    /**
     * Ajoute une séance à un emploi du temps
     */
    @Transactional
    public Emploi ajouterSeance(Long emploiId, Long seanceId) {
        Emploi emploi = getEmploiById(emploiId);
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new EntityNotFoundException("Séance non trouvée"));

        if (emploi.getSeances() == null) {
            emploi.setSeances(new java.util.ArrayList<>());
        }

        if (!emploi.getSeances().contains(seance)) {
            emploi.getSeances().add(seance);
            return emploiRepository.save(emploi);
        }

        return emploi;
    }

    /**
     * Retire une séance d'un emploi du temps
     */
    @Transactional
    public Emploi retirerSeance(Long emploiId, Long seanceId) {
        Emploi emploi = getEmploiById(emploiId);

        if (emploi.getSeances() != null) {
            emploi.getSeances().removeIf(seance -> seance.getId().equals(seanceId));
            return emploiRepository.save(emploi);
        }

        return emploi;
    }

    /**
     * Récupère les emplois du temps récents
     */
    public List<Emploi> getEmploisRecents() {
        return emploiRepository.findTop10ByOrderByDateCreationDesc();
    }

    /**
     * Récupère les emplois du temps entre deux dates
     */
    public List<Emploi> getEmploisByPeriode(Date dateDebut, Date dateFin) {
        return emploiRepository.findByDateCreationBetweenOrderByDateCreationDesc(dateDebut, dateFin);
    }
}