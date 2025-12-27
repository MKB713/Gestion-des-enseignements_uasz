package com.uasz.daos.emploitemps.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entité pour conserver l'historique complet des modifications de séances
 * Chaque modification crée une nouvelle entrée dans cette table
 */
@Entity
@Table(name = "historique_seance")
public class HistoriqueSeance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seance_id", nullable = false)
    private Long seanceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeModification typeModification;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @Column(name = "utilisateur")
    private String utilisateur; // Qui a fait la modification

    // ========== VALEURS AVANT MODIFICATION ==========

    @Column(name = "ancienne_date_seance")
    private LocalDate ancienneDateSeance;

    @Column(name = "ancienne_heure_debut")
    private LocalTime ancienneHeureDebut;

    @Column(name = "ancienne_heure_fin")
    private LocalTime ancienneHeureFin;

    @Column(name = "ancienne_salle_id")
    private Long ancienneSalleId;

    @Column(name = "ancien_enseignant_id")
    private Long ancienEnseignantId;

    @Column(name = "ancien_ec_id")
    private Long ancienEcId;

    // ========== NOUVELLES VALEURS ==========

    @Column(name = "nouvelle_date_seance")
    private LocalDate nouvelleDateSeance;

    @Column(name = "nouvelle_heure_debut")
    private LocalTime nouvelleHeureDebut;

    @Column(name = "nouvelle_heure_fin")
    private LocalTime nouvelleHeureFin;

    @Column(name = "nouvelle_salle_id")
    private Long nouvelleSalleId;

    @Column(name = "nouvel_enseignant_id")
    private Long nouvelEnseignantId;

    @Column(name = "nouvel_ec_id")
    private Long nouvelEcId;

    @Column(name = "commentaire", length = 500)
    private String commentaire;

    // ========== CONSTRUCTEURS ==========

    public HistoriqueSeance() {
        this.dateModification = LocalDateTime.now();
    }

    public HistoriqueSeance(Long seanceId, TypeModification typeModification, String utilisateur) {
        this();
        this.seanceId = seanceId;
        this.typeModification = typeModification;
        this.utilisateur = utilisateur;
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Crée un historique à partir d'une séance (état avant modification)
     */
    public static HistoriqueSeance creerDepuisSeance(Seance seance, TypeModification type, String utilisateur) {
        HistoriqueSeance historique = new HistoriqueSeance(seance.getId(), type, utilisateur);
        historique.setAncienneDateSeance(seance.getDateSeance());
        historique.setAncienneHeureDebut(seance.getHeureDebut());
        historique.setAncienneHeureFin(seance.getHeureFin());
        historique.setAncienneSalleId(seance.getSalle() != null ? seance.getSalle().getId() : null);
        historique.setAncienEnseignantId(seance.getEnseignantId());
        historique.setAncienEcId(seance.getEcId());
        return historique;
    }

    /**
     * Définit les nouvelles valeurs après modification
     */
    public void setNouvellesValeurs(Seance seance) {
        this.nouvelleDateSeance = seance.getDateSeance();
        this.nouvelleHeureDebut = seance.getHeureDebut();
        this.nouvelleHeureFin = seance.getHeureFin();
        this.nouvelleSalleId = seance.getSalle() != null ? seance.getSalle().getId() : null;
        this.nouvelEnseignantId = seance.getEnseignantId();
        this.nouvelEcId = seance.getEcId();
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

    public TypeModification getTypeModification() {
        return typeModification;
    }

    public void setTypeModification(TypeModification typeModification) {
        this.typeModification = typeModification;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDate getAncienneDateSeance() {
        return ancienneDateSeance;
    }

    public void setAncienneDateSeance(LocalDate ancienneDateSeance) {
        this.ancienneDateSeance = ancienneDateSeance;
    }

    public LocalTime getAncienneHeureDebut() {
        return ancienneHeureDebut;
    }

    public void setAncienneHeureDebut(LocalTime ancienneHeureDebut) {
        this.ancienneHeureDebut = ancienneHeureDebut;
    }

    public LocalTime getAncienneHeureFin() {
        return ancienneHeureFin;
    }

    public void setAncienneHeureFin(LocalTime ancienneHeureFin) {
        this.ancienneHeureFin = ancienneHeureFin;
    }

    public Long getAncienneSalleId() {
        return ancienneSalleId;
    }

    public void setAncienneSalleId(Long ancienneSalleId) {
        this.ancienneSalleId = ancienneSalleId;
    }

    public Long getAncienEnseignantId() {
        return ancienEnseignantId;
    }

    public void setAncienEnseignantId(Long ancienEnseignantId) {
        this.ancienEnseignantId = ancienEnseignantId;
    }

    public Long getAncienEcId() {
        return ancienEcId;
    }

    public void setAncienEcId(Long ancienEcId) {
        this.ancienEcId = ancienEcId;
    }

    public LocalDate getNouvelleDateSeance() {
        return nouvelleDateSeance;
    }

    public void setNouvelleDateSeance(LocalDate nouvelleDateSeance) {
        this.nouvelleDateSeance = nouvelleDateSeance;
    }

    public LocalTime getNouvelleHeureDebut() {
        return nouvelleHeureDebut;
    }

    public void setNouvelleHeureDebut(LocalTime nouvelleHeureDebut) {
        this.nouvelleHeureDebut = nouvelleHeureDebut;
    }

    public LocalTime getNouvelleHeureFin() {
        return nouvelleHeureFin;
    }

    public void setNouvelleHeureFin(LocalTime nouvelleHeureFin) {
        this.nouvelleHeureFin = nouvelleHeureFin;
    }

    public Long getNouvelleSalleId() {
        return nouvelleSalleId;
    }

    public void setNouvelleSalleId(Long nouvelleSalleId) {
        this.nouvelleSalleId = nouvelleSalleId;
    }

    public Long getNouvelEnseignantId() {
        return nouvelEnseignantId;
    }

    public void setNouvelEnseignantId(Long nouvelEnseignantId) {
        this.nouvelEnseignantId = nouvelEnseignantId;
    }

    public Long getNouvelEcId() {
        return nouvelEcId;
    }

    public void setNouvelEcId(Long nouvelEcId) {
        this.nouvelEcId = nouvelEcId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
