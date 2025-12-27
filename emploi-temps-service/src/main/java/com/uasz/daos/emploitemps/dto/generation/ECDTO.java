package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;

public class ECDTO {
    private Long id;
    private String code;
    private String nom;
    private Integer volumeHoraireCM;
    private Integer volumeHoraireTD;
    private Integer volumeHoraireTP;

    public ECDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getVolumeHoraireCM() { return volumeHoraireCM; }
    public void setVolumeHoraireCM(Integer volumeHoraireCM) { this.volumeHoraireCM = volumeHoraireCM; }

    public Integer getVolumeHoraireTD() { return volumeHoraireTD; }
    public void setVolumeHoraireTD(Integer volumeHoraireTD) { this.volumeHoraireTD = volumeHoraireTD; }

    public Integer getVolumeHoraireTP() { return volumeHoraireTP; }
    public void setVolumeHoraireTP(Integer volumeHoraireTP) { this.volumeHoraireTP = volumeHoraireTP; }
}