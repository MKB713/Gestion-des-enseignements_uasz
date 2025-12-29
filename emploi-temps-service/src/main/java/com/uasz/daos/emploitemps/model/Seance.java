package com.uasz.daos.emploitemps.model;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@SQLDelete(sql = "UPDATE seance SET statut = 'ANNULEE', date_suppression = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "statut != 'ANNULEE'")
public class Seance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateSeance;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private int duree;

    // ========== NOUVEAU : TYPE DE SÉANCE ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "type_seance", nullable = false)
    private TypeSeance typeSeance = TypeSeance.COURS; // CM, TD, TP, etc.

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    // Relations externes vers autres microservices
    @Column(name = "enseignant_id")
    private Long enseignantId;

    @Column(name = "ec_id")
    private Long ecId;

    @Column(name = "repartition_id")
    private Long repartitionId;

    @Column(name = "classe_id")
    private Long classeId; // Ajouté pour détection conflit classe

    @ManyToMany(mappedBy = "seances", fetch = FetchType.LAZY)
    private List<Emploi> emplois;

    @OneToOne(mappedBy = "seance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Deroulement deroulement;

    // ========== CHAMPS D'AUDIT ==========

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSeance statut = StatutSeance.PLANIFIEE;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "date_suppression")
    private LocalDateTime dateSuppression;

    @Column(name = "cree_par")
    private String creePar;

    @Column(name = "modifie_par")
    private String modifiePar;

    @Column(name = "supprime_par")
    private String supprimePar;

    @Column(name = "raison_annulation", length = 500)
    private String raisonAnnulation;

    // ========== LIFECYCLE CALLBACKS ==========

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        if (statut == null) {
            statut = StatutSeance.PLANIFIEE;
        }
        if (typeSeance == null) {
            typeSeance = TypeSeance.COURS;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    // ========== CONSTRUCTEURS ==========

    public Seance() {
    }

    public Seance(Long id, LocalDate dateSeance, LocalTime heureDebut, LocalTime heureFin,
                  int duree, Salle salle, Long enseignantId, Long ecId) {
        this.id = id;
        this.dateSeance = dateSeance;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.duree = duree;
        this.salle = salle;
        this.enseignantId = enseignantId;
        this.ecId = ecId;
    }

    // ========== MÉTHODES MÉTIER ==========

    public void annuler(String raisonAnnulation, String utilisateur) {
        this.statut = StatutSeance.ANNULEE;
        this.raisonAnnulation = raisonAnnulation;
        this.supprimePar = utilisateur;
        this.dateSuppression = LocalDateTime.now();
    }

    public boolean estAnnulee() {
        return this.statut == StatutSeance.ANNULEE;
    }

    public boolean estModifiable() {
        return this.statut == StatutSeance.PLANIFIEE || this.statut == StatutSeance.CONFIRMEE;
    }

    /**
     * Retourne la couleur associée au type de séance (pour affichage)
     */
    public String getCouleurType() {
        return this.typeSeance != null ? this.typeSeance.getCouleur() : "#6B7280";
    }

    // ========== GETTERS ET SETTERS ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateSeance() {
        return dateSeance;
    }

    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public TypeSeance getTypeSeance() {
        return typeSeance;
    }

    public void setTypeSeance(TypeSeance typeSeance) {
        this.typeSeance = typeSeance;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public Long getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(Long enseignantId) {
        this.enseignantId = enseignantId;
    }

    public Long getEcId() {
        return ecId;
    }

    public void setEcId(Long ecId) {
        this.ecId = ecId;
    }

    public Long getRepartitionId() {
        return repartitionId;
    }

    public void setRepartitionId(Long repartitionId) {
        this.repartitionId = repartitionId;
    }

    public Long getClasseId() {
        return classeId;
    }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }

    public List<Emploi> getEmplois() {
        return emplois;
    }

    public void setEmplois(List<Emploi> emplois) {
        this.emplois = emplois;
    }

    public Deroulement getDeroulement() {
        return deroulement;
    }

    public void setDeroulement(Deroulement deroulement) {
        this.deroulement = deroulement;
    }

    public StatutSeance getStatut() {
        return statut;
    }

    public void setStatut(StatutSeance statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public LocalDateTime getDateSuppression() {
        return dateSuppression;
    }

    public void setDateSuppression(LocalDateTime dateSuppression) {
        this.dateSuppression = dateSuppression;
    }

    public String getCreePar() {
        return creePar;
    }

    public void setCreePar(String creePar) {
        this.creePar = creePar;
    }

    public String getModifiePar() {
        return modifiePar;
    }

    public void setModifiePar(String modifiePar) {
        this.modifiePar = modifiePar;
    }

    public String getSupprimePar() {
        return supprimePar;
    }

    public void setSupprimePar(String supprimePar) {
        this.supprimePar = supprimePar;
    }

    public String getRaisonAnnulation() {
        return raisonAnnulation;
    }

    public void setRaisonAnnulation(String raisonAnnulation) {
        this.raisonAnnulation = raisonAnnulation;
    }
}