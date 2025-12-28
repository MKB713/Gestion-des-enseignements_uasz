package com.uasz.daos.emploitemps.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SeanceDTO {
    private Long id;
    private LocalDate dateSeance;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private int duree; // Durée en minutes (AJOUTÉ)
    private Long salleId;
    private String salleNom;
    private Long enseignantId;
    private String enseignantNom;
    private Long ecId;
    private String ecNom;
    private String typeSeance; // Ajouté pour le type de séance (CM/TD/TP)
    private Long classeId;     // Ajouté pour l'ID de la classe

    // Getters and Setters

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

    public Long getSalleId() {
        return salleId;
    }

    public void setSalleId(Long salleId) {
        this.salleId = salleId;
    }

    public String getSalleNom() {
        return salleNom;
    }

    public void setSalleNom(String salleNom) {
        this.salleNom = salleNom;
    }

    public Long getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(Long enseignantId) {
        this.enseignantId = enseignantId;
    }

    public String getEnseignantNom() {
        return enseignantNom;
    }

    public void setEnseignantNom(String enseignantNom) {
        this.enseignantNom = enseignantNom;
    }

    public Long getEcId() {
        return ecId;
    }

    public void setEcId(Long ecId) {
        this.ecId = ecId;
    }

    public String getEcNom() {
        return ecNom;
    }

    public void setEcNom(String ecNom) {
        this.ecNom = ecNom;
    }

    public String getTypeSeance() {
        return typeSeance;
    }

    public void setTypeSeance(String typeSeance) {
        this.typeSeance = typeSeance;
    }

    public Long getClasseId() {
        return classeId;
    }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }
}