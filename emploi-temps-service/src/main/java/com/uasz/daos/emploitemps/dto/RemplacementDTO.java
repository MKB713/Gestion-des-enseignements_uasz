package com.uasz.daos.emploitemps.dto;

import com.uasz.daos.emploitemps.model.StatutRemplacement;

import java.time.LocalDateTime;

/**
 * DTO pour les remplacements d'enseignants
 */
public class RemplacementDTO {

    private Long id;
    private Long seanceId;
    private Long enseignantRemplaceId;
    private Long enseignantRemplacantId;
    private String enseignantRemplaceNom;
    private String enseignantRemplacantNom;
    private LocalDateTime dateRemplacement;
    private Boolean temporaire;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String raison;
    private StatutRemplacement statut;
    private String creePar;
    private LocalDateTime dateCreation;
    private LocalDateTime dateReponse;
    private String commentaireRemplacant;

    // Informations de la séance (pour affichage)
    private String seanceDate;
    private String seanceHoraire;
    private String seanceSalle;
    private String seanceEC;

    // ========== CONSTRUCTEURS ==========

    public RemplacementDTO() {
    }

    // ========== GETTERS ET SETTERS ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeanceId() {
        return seanceId;
    }

    public void setSeanceId(Long seanceId) {
        this.seanceId = seanceId;
    }

    public Long getEnseignantRemplaceId() {
        return enseignantRemplaceId;
    }

    public void setEnseignantRemplaceId(Long enseignantRemplaceId) {
        this.enseignantRemplaceId = enseignantRemplaceId;
    }

    public Long getEnseignantRemplacantId() {
        return enseignantRemplacantId;
    }

    public void setEnseignantRemplacantId(Long enseignantRemplacantId) {
        this.enseignantRemplacantId = enseignantRemplacantId;
    }

    public String getEnseignantRemplaceNom() {
        return enseignantRemplaceNom;
    }

    public void setEnseignantRemplaceNom(String enseignantRemplaceNom) {
        this.enseignantRemplaceNom = enseignantRemplaceNom;
    }

    public String getEnseignantRemplacantNom() {
        return enseignantRemplacantNom;
    }

    public void setEnseignantRemplacantNom(String enseignantRemplacantNom) {
        this.enseignantRemplacantNom = enseignantRemplacantNom;
    }

    public LocalDateTime getDateRemplacement() {
        return dateRemplacement;
    }

    public void setDateRemplacement(LocalDateTime dateRemplacement) {
        this.dateRemplacement = dateRemplacement;
    }

    public Boolean getTemporaire() {
        return temporaire;
    }

    public void setTemporaire(Boolean temporaire) {
        this.temporaire = temporaire;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    public StatutRemplacement getStatut() {
        return statut;
    }

    public void setStatut(StatutRemplacement statut) {
        this.statut = statut;
    }

    public String getCreePar() {
        return creePar;
    }

    public void setCreePar(String creePar) {
        this.creePar = creePar;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(LocalDateTime dateReponse) {
        this.dateReponse = dateReponse;
    }

    public String getCommentaireRemplacant() {
        return commentaireRemplacant;
    }

    public void setCommentaireRemplacant(String commentaireRemplacant) {
        this.commentaireRemplacant = commentaireRemplacant;
    }

    public String getSeanceDate() {
        return seanceDate;
    }

    public void setSeanceDate(String seanceDate) {
        this.seanceDate = seanceDate;
    }

    public String getSeanceHoraire() {
        return seanceHoraire;
    }

    public void setSeanceHoraire(String seanceHoraire) {
        this.seanceHoraire = seanceHoraire;
    }

    public String getSeanceSalle() {
        return seanceSalle;
    }

    public void setSeanceSalle(String seanceSalle) {
        this.seanceSalle = seanceSalle;
    }

    public String getSeanceEC() {
        return seanceEC;
    }

    public void setSeanceEC(String seanceEC) {
        this.seanceEC = seanceEC;
    }
}