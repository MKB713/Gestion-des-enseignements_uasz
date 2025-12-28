package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;

public class ConflitsResponse {
    private Boolean hasConflits;
    private Integer nbConflits;
    private List<String> conflits;

    public ConflitsResponse() {}

    public Boolean getHasConflits() { return hasConflits; }
    public void setHasConflits(Boolean hasConflits) { this.hasConflits = hasConflits; }

    public Integer getNbConflits() { return nbConflits; }
    public void setNbConflits(Integer nbConflits) { this.nbConflits = nbConflits; }

    public List<String> getConflits() { return conflits; }
    public void setConflits(List<String> conflits) { this.conflits = conflits; }
}