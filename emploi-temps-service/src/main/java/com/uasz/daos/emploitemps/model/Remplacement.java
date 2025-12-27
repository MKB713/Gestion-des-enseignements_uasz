package com.uasz.daos.emploitemps.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité pour gérer les remplacements d'enseignants sur des séances
 * Permet de tracker qui remplace qui, quand, et pourquoi
 */
@Entity
@Table(name = "remplacement")
public class Remplacement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance;

    // ID de l'enseignant remplacé (référence externe vers microservice enseignant)
    @Column(name = "enseignant_remplace_id", nullable = false)
    private Long enseignantRemplaceId;

    // ID de l'enseignant remplaçant (référence externe vers microservice enseignant)
    @Column(name = "enseignant_remplacant_id", nullable = false)
    private Long enseignantRemplacantId;

    @Column(name = "date_remplacement", nullable = false)
    private LocalDateTime dateRemplacement;

    // Si true = remplacement temporaire (1 séance), si false = remplacement définitif
    @Column(nullable = false)
    private Boolean temporaire = true;

    // Date de début du remplacement (pour remplacements sur période)
    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    // Date de fin du remplacement (null = définitif ou 1 séance)
    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(length = 500)
    private String raison; // Ex: "Enseignant malade", "Congé", etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRemplacement statut = StatutRemplacement.EN_ATTENTE;

    // Qui a créé la demande de remplacement
    @Column(name = "cree_par")
    private String creePar;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // Date de réponse du remplaçant
    @Column(name = "date_reponse")
    private LocalDateTime dateReponse;

    @Column(name = "commentaire_remplacant", length = 500)
    private String commentaireRemplacant;

    // ========== LIFECYCLE CALLBACKS ==========

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateRemplacement = LocalDateTime.now();
        if (statut == null) {
            statut = StatutRemplacement.EN_ATTENTE;
        }
    }

    // ========== MÉTHODES MÉTIER ==========

    public void accepter(String commentaire) {
        this.statut = StatutRemplacement.ACCEPTE;
        this.dateReponse = LocalDateTime.now();
        this.commentaireRemplacant = commentaire;
    }

    public void refuser(String commentaire) {
        this.statut = StatutRemplacement.REFUSE;
        this.dateReponse = LocalDateTime.now();
        this.commentaireRemplacant = commentaire;
    }

    public void annuler() {
        this.statut = StatutRemplacement.ANNULE;
    }

    public boolean estActif() {
        return this.statut == StatutRemplacement.ACCEPTE;
    }

    public boolean estEnAttente() {
        return this.statut == StatutRemplacement.EN_ATTENTE;
    }

    // ========== CONSTRUCTEURS ==========

    public Remplacement() {
    }

    public Remplacement(Seance seance, Long enseignantRemplaceId, Long enseignantRemplacantId,
                        String raison, Boolean temporaire, String creePar) {
        this.seance = seance;
        this.enseignantRemplaceId = enseignantRemplaceId;
        this.enseignantRemplacantId = enseignantRemplacantId;
        this.raison = raison;
        this.temporaire = temporaire;
        this.creePar = creePar;
    }

    // ========== GETTERS ET SETTERS ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
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
}
