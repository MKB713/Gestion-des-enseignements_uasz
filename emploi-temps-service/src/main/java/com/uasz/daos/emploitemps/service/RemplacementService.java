package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.*;
import com.uasz.daos.emploitemps.repository.RemplacementRepository;
import com.uasz.daos.emploitemps.repository.SeanceRepository;
import com.uasz.daos.emploitemps.repository.HistoriqueSeanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RemplacementService {

    @Autowired
    private RemplacementRepository remplacementRepository;

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private HistoriqueSeanceRepository historiqueRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Récupère tous les remplacements
     */
    public List<Remplacement> getAllRemplacements() {
        return remplacementRepository.findAll();
    }

    /**
     * Récupère un remplacement par ID
     */
    public Remplacement getRemplacementById(Long id) {
        return remplacementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Remplacement non trouvé avec l'id: " + id));
    }

    /**
     * Récupère les remplacements d'une séance
     */
    public List<Remplacement> getRemplacementsBySeance(Long seanceId) {
        return remplacementRepository.findBySeanceId(seanceId);
    }

    /**
     * Récupère les remplacements d'un enseignant (remplacé)
     */
    public List<Remplacement> getRemplacementsByEnseignantRemplace(Long enseignantId) {
        return remplacementRepository.findByEnseignantRemplaceIdOrderByDateRemplacementDesc(enseignantId);
    }

    /**
     * Récupère les remplacements d'un enseignant (remplaçant)
     */
    public List<Remplacement> getRemplacementsByEnseignantRemplacant(Long enseignantId) {
        return remplacementRepository.findByEnseignantRemplacantIdOrderByDateRemplacementDesc(enseignantId);
    }

    /**
     * Récupère les remplacements en attente pour un remplaçant
     */
    public List<Remplacement> getRemplacementsEnAttente(Long enseignantRemplacantId) {
        return remplacementRepository.findByEnseignantRemplacantIdAndStatutOrderByDateCreationDesc(
                enseignantRemplacantId, StatutRemplacement.EN_ATTENTE);
    }

    /**
     * Crée une demande de remplacement
     */
    @Transactional
    public Remplacement creerRemplacement(Long seanceId, Long enseignantRemplacantId,
                                          String raison, Boolean temporaire, String creePar) {
        // Vérifier que la séance existe
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new EntityNotFoundException("Séance non trouvée avec l'id: " + seanceId));

        // Vérifier qu'il n'y a pas déjà un remplacement actif
        if (remplacementRepository.existeRemplacementActifPourSeance(seanceId)) {
            throw new IllegalStateException("Cette séance a déjà un remplacement actif");
        }

        // Créer le remplacement
        Remplacement remplacement = new Remplacement(
                seance,
                seance.getEnseignantId(), // Enseignant remplacé
                enseignantRemplacantId,   // Enseignant remplaçant
                raison,
                temporaire,
                creePar
        );

        Remplacement savedRemplacement = remplacementRepository.save(remplacement);

        // Créer l'historique
        HistoriqueSeance historique = new HistoriqueSeance(
                seanceId,
                TypeModification.REMPLACEMENT,
                creePar
        );
        historique.setCommentaire("Demande de remplacement créée. Raison: " + raison);
        historique.setAncienEnseignantId(seance.getEnseignantId());
        historique.setNouvelEnseignantId(enseignantRemplacantId);
        historiqueRepository.save(historique);

        // Envoyer notification au remplaçant
        notificationService.notifierDemandeRemplacement(
                enseignantRemplacantId,
                seanceId,
                savedRemplacement.getId(),
                raison
        );

        return savedRemplacement;
    }

    /**
     * Remplacer un enseignant sur une séance (User Story principale)
     */
    @Transactional
    public Remplacement remplacerEnseignant(Long seanceId, Long enseignantRemplacantId,
                                            String raison, String creePar) {
        return creerRemplacement(seanceId, enseignantRemplacantId, raison, false, creePar);
    }

    /**
     * Accepter une demande de remplacement
     */
    @Transactional
    public Remplacement accepterRemplacement(Long remplacementId, String commentaire, String utilisateur) {
        Remplacement remplacement = getRemplacementById(remplacementId);

        if (remplacement.getStatut() != StatutRemplacement.EN_ATTENTE) {
            throw new IllegalStateException("Ce remplacement n'est plus en attente");
        }

        // Accepter le remplacement
        remplacement.accepter(commentaire);

        // Mettre à jour l'enseignant de la séance
        Seance seance = remplacement.getSeance();
        Long ancienEnseignantId = seance.getEnseignantId();
        seance.setEnseignantId(remplacement.getEnseignantRemplacantId());
        seance.setModifiePar(utilisateur);
        seanceRepository.save(seance);

        Remplacement savedRemplacement = remplacementRepository.save(remplacement);

        // Créer l'historique
        HistoriqueSeance historique = new HistoriqueSeance(
                seance.getId(),
                TypeModification.REMPLACEMENT,
                utilisateur
        );
        historique.setCommentaire("Remplacement accepté par l'enseignant remplaçant");
        historique.setAncienEnseignantId(ancienEnseignantId);
        historique.setNouvelEnseignantId(remplacement.getEnseignantRemplacantId());
        historiqueRepository.save(historique);

        // Notifier l'enseignant remplacé
        notificationService.notifierRemplacementAccepte(
                remplacement.getEnseignantRemplaceId(),
                seance.getId(),
                remplacementId
        );

        return savedRemplacement;
    }

    /**
     * Refuser une demande de remplacement
     */
    @Transactional
    public Remplacement refuserRemplacement(Long remplacementId, String commentaire, String utilisateur) {
        Remplacement remplacement = getRemplacementById(remplacementId);

        if (remplacement.getStatut() != StatutRemplacement.EN_ATTENTE) {
            throw new IllegalStateException("Ce remplacement n'est plus en attente");
        }

        remplacement.refuser(commentaire);
        Remplacement savedRemplacement = remplacementRepository.save(remplacement);

        // Notifier le créateur de la demande
        notificationService.notifierRemplacementRefuse(
                remplacement.getEnseignantRemplaceId(),
                remplacement.getSeance().getId(),
                remplacementId,
                commentaire
        );

        return savedRemplacement;
    }

    /**
     * Annuler un remplacement
     */
    @Transactional
    public void annulerRemplacement(Long remplacementId, String utilisateur) {
        Remplacement remplacement = getRemplacementById(remplacementId);

        // Si le remplacement était accepté, remettre l'ancien enseignant
        if (remplacement.getStatut() == StatutRemplacement.ACCEPTE) {
            Seance seance = remplacement.getSeance();
            seance.setEnseignantId(remplacement.getEnseignantRemplaceId());
            seance.setModifiePar(utilisateur);
            seanceRepository.save(seance);

            // Créer l'historique
            HistoriqueSeance historique = new HistoriqueSeance(
                    seance.getId(),
                    TypeModification.MODIFICATION,
                    utilisateur
            );
            historique.setCommentaire("Remplacement annulé - retour à l'enseignant initial");
            historique.setAncienEnseignantId(remplacement.getEnseignantRemplacantId());
            historique.setNouvelEnseignantId(remplacement.getEnseignantRemplaceId());
            historiqueRepository.save(historique);
        }

        remplacement.annuler();
        remplacementRepository.save(remplacement);

        // Notifier les parties concernées
        notificationService.notifierRemplacementAnnule(
                remplacement.getEnseignantRemplacantId(),
                remplacement.getSeance().getId(),
                remplacementId
        );
    }

    /**
     * Compte les remplacements d'une séance
     */
    public long countRemplacementsBySeance(Long seanceId) {
        return remplacementRepository.countBySeanceId(seanceId);
    }

    /**
     * Vérifie si une séance a un remplacement actif
     */
    public boolean hasRemplacementActif(Long seanceId) {
        return remplacementRepository.existeRemplacementActifPourSeance(seanceId);
    }

    /**
     * Récupère les remplacements par statut
     */
    public List<Remplacement> getRemplacementsByStatut(StatutRemplacement statut) {
        return remplacementRepository.findByStatutOrderByDateCreationDesc(statut);
    }
}