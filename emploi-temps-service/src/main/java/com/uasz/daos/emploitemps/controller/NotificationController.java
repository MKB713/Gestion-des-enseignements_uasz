package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Notification;
import com.uasz.daos.emploitemps.model.TypeDestinataire;
import com.uasz.daos.emploitemps.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour la gestion des notifications
 * User Story: "Recevoir des notifications de changement"
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * GET /api/notifications
     * Récupère toutes les notifications
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/{id}
     * Récupère une notification par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        try {
            Notification notification = notificationService.getNotificationById(id);
            return ResponseEntity.ok(notification);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/notifications/utilisateur/{id}
     * Récupère les notifications d'un utilisateur (enseignant ou étudiant)
     *
     * Params:
     * - type: ENSEIGNANT, ETUDIANT, RESPONSABLE
     */
    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<List<Notification>> getNotificationsByUtilisateur(
            @PathVariable Long id,
            @RequestParam String type) {

        try {
            TypeDestinataire typeDestinataire = TypeDestinataire.valueOf(type.toUpperCase());
            List<Notification> notifications = notificationService.getNotificationsByDestinataire(id, typeDestinataire);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/notifications/utilisateur/{id}/non-lues
     * Récupère les notifications non lues d'un utilisateur
     *
     * Params:
     * - type: ENSEIGNANT, ETUDIANT, RESPONSABLE
     */
    @GetMapping("/utilisateur/{id}/non-lues")
    public ResponseEntity<List<Notification>> getNotificationsNonLues(
            @PathVariable Long id,
            @RequestParam String type) {

        try {
            TypeDestinataire typeDestinataire = TypeDestinataire.valueOf(type.toUpperCase());
            List<Notification> notifications = notificationService.getNotificationsNonLues(id, typeDestinataire);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/notifications/utilisateur/{id}/count
     * Compte les notifications non lues d'un utilisateur
     *
     * Params:
     * - type: ENSEIGNANT, ETUDIANT, RESPONSABLE
     */
    @GetMapping("/utilisateur/{id}/count")
    public ResponseEntity<Map<String, Long>> countNotificationsNonLues(
            @PathVariable Long id,
            @RequestParam String type) {

        try {
            TypeDestinataire typeDestinataire = TypeDestinataire.valueOf(type.toUpperCase());
            long count = notificationService.countNotificationsNonLues(id, typeDestinataire);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/notifications/{id}/lire
     * Marque une notification comme lue
     */
    @PutMapping("/{id}/lire")
    public ResponseEntity<Notification> marquerCommeLu(@PathVariable Long id) {
        try {
            Notification notification = notificationService.marquerCommeLu(id);
            return ResponseEntity.ok(notification);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/notifications/utilisateur/{id}/lire-tout
     * Marque toutes les notifications d'un utilisateur comme lues
     *
     * Params:
     * - type: ENSEIGNANT, ETUDIANT, RESPONSABLE
     */
    @PutMapping("/utilisateur/{id}/lire-tout")
    public ResponseEntity<Map<String, String>> marquerToutesCommeLues(
            @PathVariable Long id,
            @RequestParam String type) {

        try {
            TypeDestinataire typeDestinataire = TypeDestinataire.valueOf(type.toUpperCase());
            notificationService.marquerToutesCommeLues(id, typeDestinataire);
            return ResponseEntity.ok(Map.of(
                    "message", "Toutes les notifications ont été marquées comme lues",
                    "utilisateurId", id.toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/notifications
     * Crée une nouvelle notification
     *
     * Body JSON attendu:
     * {
     *   "destinataireId": 123,
     *   "typeDestinataire": "ENSEIGNANT",
     *   "typeNotification": "CREATION",
     *   "seanceId": 456,
     *   "titre": "Nouvelle séance",
     *   "message": "Une nouvelle séance a été ajoutée",
     *   "priorite": "NORMALE",
     *   "canal": "APPLICATION"
     * }
     */
    @PostMapping
    public ResponseEntity<Notification> creerNotification(@RequestBody Notification notification) {
        try {
            Notification savedNotification = notificationService.creerNotification(notification);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * GET /api/notifications/seance/{id}
     * Récupère les notifications d'une séance
     */
    @GetMapping("/seance/{id}")
    public ResponseEntity<List<Notification>> getNotificationsBySeance(@PathVariable Long id) {
        List<Notification> notifications = notificationService.getNotificationsBySeance(id);
        return ResponseEntity.ok(notifications);
    }

    /**
     * DELETE /api/notifications/{id}
     * Supprime une notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> supprimerNotification(@PathVariable Long id) {
        try {
            notificationService.supprimerNotification(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Notification supprimée avec succès",
                    "notificationId", id.toString()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/notifications/non-envoyees
     * Récupère les notifications dont l'email n'a pas été envoyé
     * (Pour traitement batch)
     */
    @GetMapping("/non-envoyees")
    public ResponseEntity<List<Notification>> getNotificationsNonEnvoyees() {
        List<Notification> notifications = notificationService.getNotificationsNonEnvoyees();
        return ResponseEntity.ok(notifications);
    }

    /**
     * DELETE /api/notifications/nettoyage
     * Nettoie les anciennes notifications lues
     *
     * Params:
     * - jours: nombre de jours de conservation (défaut: 30)
     */
    @DeleteMapping("/nettoyage")
    public ResponseEntity<Map<String, String>> nettoyerAnciennesNotifications(
            @RequestParam(required = false, defaultValue = "30") int jours) {

        try {
            notificationService.nettoyerAnciennesNotifications(jours);
            return ResponseEntity.ok(Map.of(
                    "message", "Notifications anciennes supprimées",
                    "joursConservation", String.valueOf(jours)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}