package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;
public class GenerationResponse {
    private Boolean success;
    private String message;
    private List<SeanceGenereeDTO> seances;
    private List<String> conflits;
    private Map<String, Object> statistiques;

    public GenerationResponse() {}

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<SeanceGenereeDTO> getSeances() { return seances; }
    public void setSeances(List<SeanceGenereeDTO> seances) { this.seances = seances; }

    public List<String> getConflits() { return conflits; }
    public void setConflits(List<String> conflits) { this.conflits = conflits; }

    public Map<String, Object> getStatistiques() { return statistiques; }
    public void setStatistiques(Map<String, Object> statistiques) { this.statistiques = statistiques; }
}