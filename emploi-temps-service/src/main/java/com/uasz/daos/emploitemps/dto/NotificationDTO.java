package com.uasz.daos.emploitemps.dto;

import com.uasz.daos.emploitemps.model.CanalNotification;
import com.uasz.daos.emploitemps.model.PrioriteNotification;
import com.uasz.daos.emploitemps.model.TypeDestinataire;
import com.uasz.daos.emploitemps.model.TypeNotification;

import java.time.LocalDateTime;

/**
 * DTO pour les notifications
 */
public class NotificationDTO {

    private Long id;
    private Long destinataireId;
    private TypeDestinataire typeDestinataire;
    private TypeNotification typeNotification;
    private Long seanceId;
    private Long remplacementId;
    private String titre;
    private String message;
    private Boolean lu;
    private LocalDateTime dateEnvoi;
    private LocalDateTime dateLecture;
    private CanalNotification canal;
    private Boolean emailEnvoye;
    private LocalDateTime dateEmail;
    private PrioriteNotification priorite;

    // ========== CONSTRUCTEURS ==========

    public NotificationDTO() {
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