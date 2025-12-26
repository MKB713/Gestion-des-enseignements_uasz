package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.*;
import com.uasz.daos.emploitemps.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Récupère toutes les notifications
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    /**
     * Récupère une notification par ID
     */
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification non trouvée avec l'id: " + id));
    }

    /**
     * Récupère les notifications d'un destinataire (enseignant ou étudiant)
     */
    public List<Notification> getNotificationsByDestinataire(Long destinataireId, TypeDestinataire type) {
        return notificationRepository.findByDestinataireIdAndTypeDestinataireOrderByDateEnvoiDesc(
                destinataireId, type);
    }

    /**
     * Récupère les notifications non lues d'un destinataire
     */
    public List<Notification> getNotificationsNonLues(Long destinataireId, TypeDestinataire type) {
        return notificationRepository.findByDestinataireIdAndTypeDestinataireAndLuOrderByDateEnvoiDesc(
                destinataireId, type, false);
    }

    /**
     * Compte les notifications non lues
     */
    public long countNotificationsNonLues(Long destinataireId, TypeDestinataire type) {
        return notificationRepository.countByDestinataireIdAndTypeDestinataireAndLu(
                destinataireId, type, false);
    }

    /**
     * Marque une notification comme lue
     */
    @Transactional
    public Notification marquerCommeLu(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notification.marquerCommeLu();
        return notificationRepository.save(notification);
    }

    /**
     * Marque toutes les notifications d'un destinataire comme lues
     */
    @Transactional
    public void marquerToutesCommeLues(Long destinataireId, TypeDestinataire type) {
        List<Notification> notifications = getNotificationsNonLues(destinataireId, type);
        notifications.forEach(Notification::marquerCommeLu);
        notificationRepository.saveAll(notifications);
    }

    /**
     * Crée et envoie une notification
     */
    @Transactional
    public Notification creerNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);

        // Envoyer email de manière asynchrone si nécessaire
        if (notification.getCanal() == CanalNotification.EMAIL ||
                notification.getCanal() == CanalNotification.TOUS) {
            envoyerEmailAsync(saved);
        }

        return saved;
    }

    // ========== NOTIFICATIONS SPÉCIFIQUES POUR SÉANCES ==========

    /**
     * Notifie la création d'une séance
     */
    @Async
    @Transactional
    public void notifierCreationSeance(Seance seance) {
        // Notifier l'enseignant
        if (seance.getEnseignantId() != null) {
            Notification notifEnseignant = Notification.creerNotificationCreation(
                    seance.getEnseignantId(),
                    TypeDestinataire.ENSEIGNANT,
                    seance.getId(),
                    String.format("Date: %s, %s-%s, Salle: %s",
                            seance.getDateSeance(),
                            seance.getHeureDebut(),
                            seance.getHeureFin(),
                            seance.getSalle() != null ? seance.getSalle().getLibelle() : "N/A")
            );
            creerNotification(notifEnseignant);
        }

        // Notifier les étudiants de la classe
        if (seance.getClasseId() != null) {
            // Placeholder: Simuler la récupération des étudiants de la classe
            List<Long> studentIds = getStudentsByClasseId(seance.getClasseId()); // Simule un appel à un autre microservice
            for (Long studentId : studentIds) {
                Notification notifEtudiant = Notification.creerNotificationCreation(
                        studentId,
                        TypeDestinataire.ETUDIANT,
                        seance.getId(),
                        String.format("Une nouvelle séance a été planifiée pour votre classe. Date: %s, %s-%s, Salle: %s",
                                seance.getDateSeance(),
                                seance.getHeureDebut(),
                                seance.getHeureFin(),
                                seance.getSalle() != null ? seance.getSalle().getLibelle() : "N/A")
                );
                creerNotification(notifEtudiant);
            }
        }
    }

    /**
     * Notifie la modification d'une séance
     */
    @Async
    @Transactional
    public void notifierModificationSeance(Seance seance, String details) {
        // Notifier l'enseignant
        if (seance.getEnseignantId() != null) {
            Notification notifEnseignant = Notification.creerNotificationModification(
                    seance.getEnseignantId(),
                    TypeDestinataire.ENSEIGNANT,
                    seance.getId(),
                    details
            );
            creerNotification(notifEnseignant);
        }

        // Notifier les étudiants
        if (seance.getClasseId() != null) {
            // Placeholder: Simuler la récupération des étudiants de la classe
            List<Long> studentIds = getStudentsByClasseId(seance.getClasseId()); // Simule un appel à un autre microservice
            for (Long studentId : studentIds) {
                Notification notifEtudiant = Notification.creerNotificationModification(
                        studentId,
                        TypeDestinataire.ETUDIANT,
                        seance.getId(),
                        String.format("Une séance de votre classe a été modifiée. %s", details)
                );
                creerNotification(notifEtudiant);
            }
        }
    }

    /**
     * Notifie l'annulation d'une séance
     */
    @Async
    @Transactional
    public void notifierAnnulationSeance(Seance seance, String raison) {
        // Notifier l'enseignant
        if (seance.getEnseignantId() != null) {
            Notification notifEnseignant = Notification.creerNotificationAnnulation(
                    seance.getEnseignantId(),
                    TypeDestinataire.ENSEIGNANT,
                    seance.getId(),
                    raison
            );
            creerNotification(notifEnseignant);
        }

        // Notifier les étudiants
        if (seance.getClasseId() != null) {
            // Placeholder: Simuler la récupération des étudiants de la classe
            List<Long> studentIds = getStudentsByClasseId(seance.getClasseId()); // Simule un appel à un autre microservice
            for (Long studentId : studentIds) {
                Notification notifEtudiant = Notification.creerNotificationAnnulation(
                        studentId,
                        TypeDestinataire.ETUDIANT,
                        seance.getId(),
                        String.format("Une séance de votre classe a été annulée. Raison: %s", raison)
                );
                creerNotification(notifEtudiant);
            }
        }
    }

    // ========== NOTIFICATIONS POUR REMPLACEMENTS ==========

    /**
     * Notifie une demande de remplacement
     */
    @Async
    @Transactional
    public void notifierDemandeRemplacement(Long enseignantRemplacantId, Long seanceId,
                                            Long remplacementId, String raison) {
        Notification notification = Notification.creerNotificationRemplacement(
                enseignantRemplacantId,
                TypeDestinataire.ENSEIGNANT,
                seanceId,
                remplacementId,
                "Vous êtes demandé pour remplacer un enseignant. Raison: " + raison
        );
        creerNotification(notification);
    }

    /**
     * Notifie l'acceptation d'un remplacement
     */
    @Async
    @Transactional
    public void notifierRemplacementAccepte(Long enseignantRemplaceId, Long seanceId, Long remplacementId) {
        Notification notification = new Notification();
        notification.setDestinataireId(enseignantRemplaceId);
        notification.setTypeDestinataire(TypeDestinataire.ENSEIGNANT);
        notification.setTypeNotification(TypeNotification.REMPLACEMENT);
        notification.setSeanceId(seanceId);
        notification.setRemplacementId(remplacementId);
        notification.setTitre("Remplacement accepté");
        notification.setMessage("Votre demande de remplacement a été acceptée.");
        notification.setPriorite(PrioriteNotification.IMPORTANTE);
        creerNotification(notification);
    }

    /**
     * Notifie le refus d'un remplacement
     */
    @Async
    @Transactional
    public void notifierRemplacementRefuse(Long enseignantRemplaceId, Long seanceId,
                                           Long remplacementId, String commentaire) {
        Notification notification = new Notification();
        notification.setDestinataireId(enseignantRemplaceId);
        notification.setTypeDestinataire(TypeDestinataire.ENSEIGNANT);
        notification.setTypeNotification(TypeNotification.REMPLACEMENT);
        notification.setSeanceId(seanceId);
        notification.setRemplacementId(remplacementId);
        notification.setTitre("Remplacement refusé");
        notification.setMessage("Votre demande de remplacement a été refusée. Commentaire: " + commentaire);
        notification.setPriorite(PrioriteNotification.URGENTE);
        creerNotification(notification);
    }

    /**
     * Notifie l'annulation d'un remplacement
     */
    @Async
    @Transactional
    public void notifierRemplacementAnnule(Long enseignantRemplacantId, Long seanceId, Long remplacementId) {
        Notification notification = new Notification();
        notification.setDestinataireId(enseignantRemplacantId);
        notification.setTypeDestinataire(TypeDestinataire.ENSEIGNANT);
        notification.setTypeNotification(TypeNotification.REMPLACEMENT);
        notification.setSeanceId(seanceId);
        notification.setRemplacementId(remplacementId);
        notification.setTitre("Remplacement annulé");
        notification.setMessage("Le remplacement a été annulé.");
        notification.setPriorite(PrioriteNotification.IMPORTANTE);
        creerNotification(notification);
    }

    // ========== GESTION DES EMAILS ==========

    /**
     * Envoie un email de manière asynchrone
     */
    @Async
    protected void envoyerEmailAsync(Notification notification) {
        try {
            emailService.sendEmail(notification);
            // Marquer comme envoyé
            notification.marquerEmailEnvoye();
            notificationRepository.save(notification);
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
        }
    }

    // ========== PLACEHOLDER POUR INTÉGRATIONS EXTERNES ==========

    /**
     * Simule un appel à un autre microservice pour récupérer les IDs des étudiants
     * d'une classe donnée.
     * En réalité, cela devrait être un appel REST à un microservice de gestion des étudiants.
     */
    private List<Long> getStudentsByClasseId(Long classeId) {
        System.out.println("🔎 Appel simulé à un microservice externe pour récupérer les étudiants de la classe " + classeId);
        // Retourne des IDs d'étudiants fictifs pour la démonstration
        // En vrai: faire un WebClient.get().uri("/api/etudiants?classeId=" + classeId).retrieve().bodyToFlux(Long.class).collectList().block();
        return Collections.singletonList(99L);
    }

    /**
     * Récupère les notifications non envoyées par email (pour traitement batch)
     */
    public List<Notification> getNotificationsNonEnvoyees() {
        return notificationRepository.findByEmailEnvoyeOrderByDateEnvoiAsc(false);
    }

    /**
     * Supprime les anciennes notifications lues (cleanup)
     */
    @Transactional
    public void nettoyerAnciennesNotifications(int joursConservation) {
        LocalDateTime dateLimit = LocalDateTime.now().minusDays(joursConservation);
        notificationRepository.supprimerNotificationsAnciennesLues(dateLimit);
    }

    /**
     * Récupère les notifications d'une séance
     */
    public List<Notification> getNotificationsBySeance(Long seanceId) {
        return notificationRepository.findBySeanceIdOrderByDateEnvoiDesc(seanceId);
    }

    /**
     * Supprime une notification
     */
    @Transactional
    public void supprimerNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}