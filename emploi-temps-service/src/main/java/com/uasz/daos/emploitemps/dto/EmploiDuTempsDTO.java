package com.uasz.daos.emploitemps.dto;

import java.time.LocalDate;
import java.util.*;

/**
 * DTO pour représenter un emploi du temps (hebdomadaire ou semestriel)
 * Organise les séances par jour pour affichage calendrier
 */
public class EmploiDuTempsDTO {

    private String titre;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String type; // "HEBDOMADAIRE" ou "SEMESTRIEL"
    private String filtrePar; // "CLASSE", "ENSEIGNANT", "SALLE"
    private Long filtreId;

    // Map : Date -> Liste de séances pour ce jour
    private Map<LocalDate, List<SeanceDTO>> seancesParJour;

    public EmploiDuTempsDTO() {
        this.seancesParJour = new TreeMap<>(); // TreeMap pour tri automatique par date
    }

    public EmploiDuTempsDTO(String titre, LocalDate dateDebut, LocalDate dateFin, String type) {
        this();
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
    }

    /**
     * Ajoute une séance à un jour spécifique
     */
    public void ajouterSeance(LocalDate date, SeanceDTO seance) {
        seancesParJour.computeIfAbsent(date, k -> new ArrayList<>()).add(seance);
    }

    /**
     * Récupère toutes les séances d'un jour donné
     */
    public List<SeanceDTO> getSeancesDuJour(LocalDate date) {
        return seancesParJour.getOrDefault(date, new ArrayList<>());
    }

    /**
     * Récupère toutes les dates qui ont des séances
     */
    public Set<LocalDate> getDatesAvecSeances() {
        return seancesParJour.keySet();
    }

    /**
     * Compte total des séances
     */
    public int getTotalSeances() {
        return seancesParJour.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    // Getters et Setters

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFiltrePar() {
        return filtrePar;
    }

    public void setFiltrePar(String filtrePar) {
        this.filtrePar = filtrePar;
    }

    public Long getFiltreId() {
        return filtreId;
    }

    public void setFiltreId(Long filtreId) {
        this.filtreId = filtreId;
    }

    public Map<LocalDate, List<SeanceDTO>> getSeancesParJour() {
        return seancesParJour;
    }

    public void setSeancesParJour(Map<LocalDate, List<SeanceDTO>> seancesParJour) {
        this.seancesParJour = seancesParJour;
    }
}