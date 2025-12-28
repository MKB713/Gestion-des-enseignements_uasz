package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;

public class SeanceGenereeDTO {
    private String date;
    private String heureDebut;
    private String heureFin;
    private Integer duree;
    private Long salleId;
    private Long enseignantId;
    private Long ecId;
    private Long classeId;
    private String typeSeance;
    private Long repartitionId;

    public SeanceGenereeDTO() {}

    // Getters et Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public Long getSalleId() { return salleId; }
    public void setSalleId(Long salleId) { this.salleId = salleId; }

    public Long getEnseignantId() { return enseignantId; }
    public void setEnseignantId(Long enseignantId) { this.enseignantId = enseignantId; }

    public Long getEcId() { return ecId; }
    public void setEcId(Long ecId) { this.ecId = ecId; }

    public Long getClasseId() { return classeId; }
    public void setClasseId(Long classeId) { this.classeId = classeId; }

    public String getTypeSeance() { return typeSeance; }
    public void setTypeSeance(String typeSeance) { this.typeSeance = typeSeance; }

    public Long getRepartitionId() { return repartitionId; }
    public void setRepartitionId(Long repartitionId) { this.repartitionId = repartitionId; }
}