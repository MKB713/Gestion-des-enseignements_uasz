package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;
public class EnseignantDTO {
    private Long id;
    private String nom;
    private List<ContrainteHoraire> disponibilites;
    private Integer chargeMax;

    public EnseignantDTO() {}

    public EnseignantDTO(Long id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public List<ContrainteHoraire> getDisponibilites() { return disponibilites; }
    public void setDisponibilites(List<ContrainteHoraire> disponibilites) {
        this.disponibilites = disponibilites;
    }

    public Integer getChargeMax() { return chargeMax; }
    public void setChargeMax(Integer chargeMax) { this.chargeMax = chargeMax; }
}
