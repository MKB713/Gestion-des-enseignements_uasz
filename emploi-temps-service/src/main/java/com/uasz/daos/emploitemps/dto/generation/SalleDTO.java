package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;

public class SalleDTO {
    private Long id;
    private String libelle;
    private Integer capacite;
    private Long batimentId;

    public SalleDTO() {}

    public SalleDTO(Long id, String libelle, Integer capacite) {
        this.id = id;
        this.libelle = libelle;
        this.capacite = capacite;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public Long getBatimentId() {
        return batimentId;
    }

    public void setBatimentId(Long batimentId) {
        this.batimentId = batimentId;
    }
}
