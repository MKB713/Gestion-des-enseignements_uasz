package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.dto.SeanceDTO;
import com.uasz.daos.emploitemps.model.*;
import com.uasz.daos.emploitemps.exception.ConflictException;
import com.uasz.daos.emploitemps.repository.SalleRepository;
import com.uasz.daos.emploitemps.repository.SeanceRepository;
import com.uasz.daos.emploitemps.repository.HistoriqueSeanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeanceService {

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private HistoriqueSeanceRepository historiqueRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Seance> getAllSeances() {
        return seanceRepository.findAll();
    }

    public Seance getSeanceById(Long id) {
        return seanceRepository.findById(id).orElse(null);
    }

    @Transactional
    public Seance createSeance(SeanceDTO seanceDTO) {
        return createSeance(seanceDTO, "system"); // Utilisateur par défaut
    }

    @Transactional
    public Seance createSeance(SeanceDTO seanceDTO, String utilisateur) {
        // Vérifier les conflits pour l'enseignant
        List<Seance> teacherConflicts = seanceRepository.findByEnseignantIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfter(
                seanceDTO.getEnseignantId(), seanceDTO.getDateSeance(), seanceDTO.getHeureFin(), seanceDTO.getHeureDebut());
        if (!teacherConflicts.isEmpty()) {
            throw new ConflictException("Conflit d'horaire : L'enseignant est déjà occupé à ce créneau.");
        }

        // Vérifier les conflits pour la salle
        List<Seance> roomConflicts = seanceRepository.findBySalleIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfter(
                seanceDTO.getSalleId(), seanceDTO.getDateSeance(), seanceDTO.getHeureFin(), seanceDTO.getHeureDebut());
        if (!roomConflicts.isEmpty()) {
            throw new ConflictException("Conflit d'horaire : La salle est déjà occupée à ce créneau.");
        }

        // Vérifier les conflits pour la classe
        if (seanceDTO.getClasseId() != null) {
            List<Seance> classConflicts = seanceRepository.findByClasseIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfter(
                    seanceDTO.getClasseId(), seanceDTO.getDateSeance(), seanceDTO.getHeureFin(), seanceDTO.getHeureDebut());
            if (!classConflicts.isEmpty()) {
                throw new ConflictException("Conflit d'horaire : La classe est déjà occupée à ce créneau.");
            }
        }

        // Créer la séance
        Seance seance = new Seance();
        seance.setDateSeance(seanceDTO.getDateSeance());
        seance.setHeureDebut(seanceDTO.getHeureDebut());
        seance.setHeureFin(seanceDTO.getHeureFin());
        seance.setEnseignantId(seanceDTO.getEnseignantId());
        seance.setCreePar(utilisateur);
        seance.setStatut(StatutSeance.PLANIFIEE);

        // Set typeSeance from DTO, default to COURS if not provided or invalid
        if (seanceDTO.getTypeSeance() != null) {
            try {
                seance.setTypeSeance(TypeSeance.valueOf(seanceDTO.getTypeSeance().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Log error or handle invalid type, default to COURS
                seance.setTypeSeance(TypeSeance.COURS);
            }
        } else {
            seance.setTypeSeance(TypeSeance.COURS);
        }

        Salle salle = salleRepository.findById(seanceDTO.getSalleId())
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'id: " + seanceDTO.getSalleId()));
        seance.setSalle(salle);
        seance.setEcId(seanceDTO.getEcId());
        seance.setClasseId(seanceDTO.getClasseId()); // Set classeId

        Seance savedSeance = seanceRepository.save(seance);

        // Créer l'historique de création
        HistoriqueSeance historique = new HistoriqueSeance(
                savedSeance.getId(),
                TypeModification.CREATION,
                utilisateur
        );
        historique.setNouvellesValeurs(savedSeance);
        historique.setCommentaire("Séance créée");
        historiqueRepository.save(historique);

        // Envoyer notification de création
        notificationService.notifierCreationSeance(savedSeance);

        return savedSeance;
    }

    @Transactional
    public Seance updateSeance(Long id, SeanceDTO seanceDTO) {
        return updateSeance(id, seanceDTO, "system");
    }

    @Transactional
    public Seance updateSeance(Long id, SeanceDTO seanceDTO, String utilisateur) {
        Seance existingSeance = seanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Séance non trouvée avec l'id: " + id));

        // Vérifier si la séance est modifiable
        if (!existingSeance.estModifiable()) {
            throw new IllegalStateException("Cette séance ne peut plus être modifiée (statut: " + existingSeance.getStatut() + ")");
        }

        // Créer l'historique AVANT modification
        HistoriqueSeance historique = HistoriqueSeance.creerDepuisSeance(
                existingSeance,
                TypeModification.MODIFICATION,
                utilisateur
        );

        // Vérifier les conflits pour l'enseignant
        List<Seance> teacherConflicts = seanceRepository.findByEnseignantIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfterAndIdNot(
                seanceDTO.getEnseignantId(), seanceDTO.getDateSeance(), seanceDTO.getHeureFin(), seanceDTO.getHeureDebut(), id);
        if (!teacherConflicts.isEmpty()) {
            throw new ConflictException("Conflit d'horaire : L'enseignant est déjà occupé à ce créneau.");
        }

        // Vérifier les conflits pour la salle
        List<Seance> roomConflicts = seanceRepository.findBySalleIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfterAndIdNot(
                seanceDTO.getSalleId(), seanceDTO.getDateSeance(), seanceDTO.getHeureFin(), seanceDTO.getHeureDebut(), id);
        if (!roomConflicts.isEmpty()) {
            throw new ConflictException("Conflit d'horaire : La salle est déjà occupée à ce créneau.");
        }

        // Vérifier les conflits pour la classe
        if (seanceDTO.getClasseId() != null) {
            List<Seance> classConflicts = seanceRepository.findByClasseIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfterAndIdNot(
                    seanceDTO.getClasseId(), seanceDTO.getDateSeance(), seanceDTO.getHeureFin(), seanceDTO.getHeureDebut(), id);
            if (!classConflicts.isEmpty()) {
                throw new ConflictException("Conflit d'horaire : La classe est déjà occupée à ce créneau.");
            }
        }

        // Mettre à jour l'entité existante
        existingSeance.setDateSeance(seanceDTO.getDateSeance());
        existingSeance.setHeureDebut(seanceDTO.getHeureDebut());
        existingSeance.setHeureFin(seanceDTO.getHeureFin());
        existingSeance.setEnseignantId(seanceDTO.getEnseignantId());
        existingSeance.setModifiePar(utilisateur);

        // Set typeSeance from DTO, default to existing if not provided or invalid
        if (seanceDTO.getTypeSeance() != null) {
            try {
                existingSeance.setTypeSeance(TypeSeance.valueOf(seanceDTO.getTypeSeance().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Log error or handle invalid type, keep existing type
            }
        }

        Salle salle = salleRepository.findById(seanceDTO.getSalleId())
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'id: " + seanceDTO.getSalleId()));
        existingSeance.setSalle(salle);
        existingSeance.setEcId(seanceDTO.getEcId());
        existingSeance.setClasseId(seanceDTO.getClasseId()); // Set classeId

        Seance updatedSeance = seanceRepository.save(existingSeance);

        // Compléter et sauvegarder l'historique APRÈS modification
        historique.setNouvellesValeurs(updatedSeance);
        historique.setCommentaire("Séance modifiée");
        historiqueRepository.save(historique);

        // Envoyer notification de modification
        String details = String.format("Nouvelle date: %s, %s-%s, Salle: %s",
                updatedSeance.getDateSeance(),
                updatedSeance.getHeureDebut(),
                updatedSeance.getHeureFin(),
                updatedSeance.getSalle() != null ? updatedSeance.getSalle().getLibelle() : "N/A");
        notificationService.notifierModificationSeance(updatedSeance, details);

        return updatedSeance;
    }

    @Transactional
    public void deleteSeance(Long id) {
        deleteSeance(id, null, "system");
    }

    /**
     * Suppression logique (soft delete) avec raison
     */
    @Transactional
    public void deleteSeance(Long id, String raisonAnnulation, String utilisateur) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Séance non trouvée avec l'id: " + id));

        // Créer l'historique d'annulation
        HistoriqueSeance historique = HistoriqueSeance.creerDepuisSeance(
                seance,
                TypeModification.ANNULATION,
                utilisateur
        );
        historique.setCommentaire(raisonAnnulation != null ? raisonAnnulation : "Séance annulée");

        // Marquer comme annulée (soft delete)
        seance.annuler(raisonAnnulation, utilisateur);
        seanceRepository.save(seance);

        historiqueRepository.save(historique);

        // Envoyer notification d'annulation
        notificationService.notifierAnnulationSeance(seance, raisonAnnulation != null ? raisonAnnulation : "Aucune raison spécifiée");
    }

    /**
     * Récupérer l'historique d'une séance
     */
    public List<HistoriqueSeance> getHistoriqueSeance(Long seanceId) {
        return historiqueRepository.findBySeanceIdOrderByDateModificationDesc(seanceId);
    }

    /**
     * Récupérer toutes les séances annulées
     */
    public List<Seance> getSeancesAnnulees() {
        // Pour récupérer les séances annulées, il faut désactiver temporairement le filtre @Where
        // Ou créer une méthode native SQL
        return seanceRepository.findAll().stream()
                .filter(Seance::estAnnulee)
                .toList();
    }

    public List<Seance> getBySalle(Long salleId) {
        return seanceRepository.findBySalleId(salleId);
    }

    public List<Seance> getByEnseignant(Long enseignantId) {
        return seanceRepository.findByEnseignantId(enseignantId);
    }
}