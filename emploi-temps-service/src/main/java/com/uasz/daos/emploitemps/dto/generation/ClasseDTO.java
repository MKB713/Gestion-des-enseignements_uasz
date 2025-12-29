package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;

public class ClasseDTO {
    private Long id;
    private String nom;
    private Integer effectif;
    private List<Long> ecIds;

    public ClasseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getEffectif() { return effectif; }
    public void setEffectif(Integer effectif) { this.effectif = effectif; }

    public List<Long> getEcIds() { return ecIds; }
    public void setEcIds(List<Long> ecIds) { this.ecIds = ecIds; }
}