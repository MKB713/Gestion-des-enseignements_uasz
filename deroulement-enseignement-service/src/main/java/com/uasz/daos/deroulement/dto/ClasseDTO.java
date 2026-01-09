package com.uasz.daos.deroulement.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ClasseDTO {

    @NotBlank(message = "Le code ne peut pas être vide")
    @Size(max = 50, message = "Le code ne peut pas dépasser 50 caractères")
    private String code;

    @NotBlank(message = "Le libellé ne peut pas être vide")
    @Size(max = 255, message = "Le libellé ne peut pas dépasser 255 caractères")
    private String libelle;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    private Long filiereId;

    private Long niveauId;

    @Size(max = 50, message = "L'année académique ne peut pas dépasser 50 caractères")
    private String anneeAcademique;

    @Positive(message = "L'effectif maximum doit être un nombre positif")
    private Integer effectifMax;

    public ClasseDTO() {
    }

    public ClasseDTO(String code, String libelle, String description, Long filiereId, Long niveauId,
            String anneeAcademique, Integer effectifMax) {
        this.code = code;
        this.libelle = libelle;
        this.description = description;
        this.filiereId = filiereId;
        this.niveauId = niveauId;
        this.anneeAcademique = anneeAcademique;
        this.effectifMax = effectifMax;
    }

    // Getters and Setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFiliereId() {
        return filiereId;
    }

    public void setFiliereId(Long filiereId) {
        this.filiereId = filiereId;
    }

    public Long getNiveauId() {
        return niveauId;
    }

    public void setNiveauId(Long niveauId) {
        this.niveauId = niveauId;
    }

    public String getAnneeAcademique() {
        return anneeAcademique;
    }

    public void setAnneeAcademique(String anneeAcademique) {
        this.anneeAcademique = anneeAcademique;
    }

    public Integer getEffectifMax() {
        return effectifMax;
    }

    public void setEffectifMax(Integer effectifMax) {
        this.effectifMax = effectifMax;
    }

    @Override
    public String toString() {
        return "ClasseDTO{" +
                "code='" + code + '\'' +
                ", libelle='" + libelle + '\'' +
                ", description='" + description + '\'' +
                ", filiereId=" + filiereId +
                ", niveauId=" + niveauId +
                ", anneeAcademique='" + anneeAcademique + '\'' +
                ", effectifMax=" + effectifMax +
                '}';
    }
}
