package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;

// ========== GenerationRequest.java ==========

public class GenerationRequest {
    private String dateDebut;
    private String dateFin;
    private List<EnseignantDTO> enseignants;
    private List<SalleDTO> salles;
    private List<ECDTO> ecs;
    private List<ClasseDTO> classes;
    private List<RepartitionDTO> repartitions;
    private Map<String, Object> contraintesGlobales;

    // Constructeurs, Getters et Setters
    public GenerationRequest() {}

    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }

    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }

    public List<EnseignantDTO> getEnseignants() { return enseignants; }
    public void setEnseignants(List<EnseignantDTO> enseignants) { this.enseignants = enseignants; }

    public List<SalleDTO> getSalles() { return salles; }
    public void setSalles(List<SalleDTO> salles) { this.salles = salles; }

    public List<ECDTO> getEcs() { return ecs; }
    public void setEcs(List<ECDTO> ecs) { this.ecs = ecs; }

    public List<ClasseDTO> getClasses() { return classes; }
    public void setClasses(List<ClasseDTO> classes) { this.classes = classes; }

    public List<RepartitionDTO> getRepartitions() { return repartitions; }
    public void setRepartitions(List<RepartitionDTO> repartitions) { this.repartitions = repartitions; }

    public Map<String, Object> getContraintesGlobales() { return contraintesGlobales; }
    public void setContraintesGlobales(Map<String, Object> contraintesGlobales) {
        this.contraintesGlobales = contraintesGlobales;
    }
}