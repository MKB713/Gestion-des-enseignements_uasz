package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;
public class RepartitionDTO {
    private Long id;
    private Long ecId;
    private Long enseignantId;
    private String typeSeance;
    private Integer volumeHoraire;

    public RepartitionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEcId() { return ecId; }
    public void setEcId(Long ecId) { this.ecId = ecId; }

    public Long getEnseignantId() { return enseignantId; }
    public void setEnseignantId(Long enseignantId) { this.enseignantId = enseignantId; }

    public String getTypeSeance() { return typeSeance; }
    public void setTypeSeance(String typeSeance) { this.typeSeance = typeSeance; }

    public Integer getVolumeHoraire() { return volumeHoraire; }
    public void setVolumeHoraire(Integer volumeHoraire) { this.volumeHoraire = volumeHoraire; }
}