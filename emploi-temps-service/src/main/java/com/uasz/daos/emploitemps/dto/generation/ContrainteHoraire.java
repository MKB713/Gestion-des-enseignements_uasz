package com.uasz.daos.emploitemps.dto.generation;

import java.util.List;
import java.util.Map;

public class ContrainteHoraire {
    private String jour;
    private String heureDebut;
    private String heureFin;

    public ContrainteHoraire() {}

    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }

    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }
}