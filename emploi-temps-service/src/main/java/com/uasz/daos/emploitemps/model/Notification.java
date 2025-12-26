package com.uasz.daos.emploitemps.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité pour gérer les notifications envoyées aux enseignants et étudiants
 * Permet de tracker l'historique des notifications et leur état (lu/non lu)
 */
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID du destinataire (enseignant ou étudiant dans leur microservice respectif)
    @Column(name = "destinataire_id", nullable = false)
    private Long destinataireId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_destinataire", nullable = false)
    private TypeDestinataire typeDestinataire; // ENSEIGNANT, ETUDIANT

    @Enumerated(EnumType.STRING)
    @Column(name = "type_notification", nullable = false)
    private TypeNotification typeNotification; // CREATION, MODIFICATION, ANNULATION, REMPLACEMENT

    // Séance concernée par la notification
    @Column(name = "seance_id")
    private Long seanceId;

    // Remplacement concerné (si type = REMPLACEMENT)
    @Column(name = "remplacement_id")
    private Long remplacementId;

    @Column(nullable = false, length = 100)
    private String titre;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private Boolean lu = false;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false)
    private CanalNotification canal = CanalNotification.APPLICATION; // EMAIL, SMS, APPLICATION

    @Column(name = "email_envoye")
    private Boolean emailEnvoye = false;

    @Column(name = "date_email")
    private LocalDateTime dateEmail;

    // Niveau de priorité
    @Enumerated(EnumType.STRING)
    private PrioriteNotification priorite = PrioriteNotification.NORMALE;

    // ========== LIFECYCLE CALLBACKS ==========

    @PrePersist
    protected void onCreate() {
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now();
        }
    }

    // ========== MÉTHODES MÉTIER ==========

    public void marquerCommeLu() {
        this.lu = true;
        this.dateLecture = LocalDateTime.now();
    }

    public void marquerEmailEnvoye() {
        this.emailEnvoye = true;
        this.dateEmail = LocalDateTime.now();
    }

    public boolean estNonLu() {
        return !this.lu;
    }

    public boolean estUrgent() {
        return this.priorite == PrioriteNotification.URGENTE;
    }

    /**
     * Crée une notification pour création de séance
     */
    public static Notification creerNotificationCreation(Long destinataireId, TypeDestinataire type,
                                                         Long seanceId, String details) {
        Notification notif = new Notification();
        notif.setDestinataireId(destinataireId);
        notif.setTypeDestinataire(type);
        notif.setTypeNotification(TypeNotification.CREATION);
        notif.setSeanceId(seanceId);
        notif.setTitre("Nouvelle séance ajoutée");
        notif.setMessage("Une nouvelle séance a été ajoutée à votre emploi du temps. " + details);
        notif.setPriorite(PrioriteNotification.NORMALE);
        return notif;
    }

    /**
     * Crée une notification pour modification de séance
     */
    public static Notification creerNotificationModification(Long destinataireId, TypeDestinataire type,
                                                             Long seanceId, String details) {
        Notification notif = new Notification();
        notif.setDestinataireId(destinataireId);
        notif.setTypeDestinataire(type);
        notif.setTypeNotification(TypeNotification.MODIFICATION);
        notif.setSeanceId(seanceId);
        notif.setTitre("Séance modifiée");
        notif.setMessage("Une séance de votre emploi du temps a été modifiée. " + details);
        notif.setPriorite(PrioriteNotification.IMPORTANTE);
        return notif;
    }

    /**
     * Crée une notification pour annulation de séance
     */
    public static Notification creerNotificationAnnulation(Long destinataireId, TypeDestinataire type,
                                                           Long seanceId, String raison) {
        Notification notif = new Notification();
        notif.setDestinataireId(destinataireId);
        notif.setTypeDestinataire(type);
        notif.setTypeNotification(TypeNotification.ANNULATION);
        notif.setSeanceId(seanceId);
        notif.setTitre("Séance annulée");
        notif.setMessage("Une séance a été annulée. Raison : " + raison);
        notif.setPriorite(PrioriteNotification.URGENTE);
        return notif;
    }

    /**
     * Crée une notification pour remplacement
     */
    public static Notification creerNotificationRemplacement(Long destinataireId, TypeDestinataire type,
                                                             Long seanceId, Long remplacementId, String details) {
        Notification notif = new Notification();
        notif.setDestinataireId(destinataireId);
        notif.setTypeDestinataire(type);
        notif.setTypeNotification(TypeNotification.REMPLACEMENT);
        notif.setSeanceId(seanceId);
        notif.setRemplacementId(remplacementId);
        notif.setTitre("Demande de remplacement");
        notif.setMessage(details);
        notif.setPriorite(PrioriteNotification.IMPORTANTE);
        return notif;
    }

    // ========== CONSTRUCTEURS ==========

    public Notification() {
    }

    // ========== GETTERS ET SETTERS ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDestinataireId() {
        return destinataireId;
    }

    public void setDestinataireId(Long destinataireId) {
        this.destinataireId = destinataireId;
    }

    public TypeDestinataire getTypeDestinataire() {
        return typeDestinataire;
    }

    public void setTypeDestinataire(TypeDestinataire typeDestinataire) {
        this.typeDestinataire = typeDestinataire;
    }

    public TypeNotification getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(TypeNotification typeNotification) {
        this.typeNotification = typeNotification;
    }

    public Long getSeanceId() {
        return seanceId;
    }

    public void setSeanceId(Long seanceId) {
        this.seanceId = seanceId;
    }

    public Long getRemplacementId() {
        return remplacementId;
    }

    public void setRemplacementId(Long remplacementId) {
        this.remplacementId = remplacementId;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getLu() {
        return lu;
    }

    public void setLu(Boolean lu) {
        this.lu = lu;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public LocalDateTime getDateLecture() {
        return dateLecture;
    }

    public void setDateLecture(LocalDateTime dateLecture) {
        this.dateLecture = dateLecture;
    }

    public CanalNotification getCanal() {
        return canal;
    }

    public void setCanal(CanalNotification canal) {
        this.canal = canal;
    }

    public Boolean getEmailEnvoye() {
        return emailEnvoye;
    }

    public void setEmailEnvoye(Boolean emailEnvoye) {
        this.emailEnvoye = emailEnvoye;
    }

    public LocalDateTime getDateEmail() {
        return dateEmail;
    }

    public void setDateEmail(LocalDateTime dateEmail) {
        this.dateEmail = dateEmail;
    }

    public PrioriteNotification getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteNotification priorite) {
        this.priorite = priorite;
    }
}
