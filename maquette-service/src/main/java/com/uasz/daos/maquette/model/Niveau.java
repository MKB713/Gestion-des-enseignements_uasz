package com.uasz.daos.maquette.model;

import com.uasz.daos.maquette.enums.Cycle;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "niveaux")
public class Niveau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero;

    @Enumerated(EnumType.STRING)
    private Cycle cycle;

    @OneToMany(mappedBy = "niveau")
    private List<Formation> formations = new ArrayList<>();

    public Niveau() {
    }

    public Niveau(int numero, Cycle cycle) {
        this.numero = numero;
        this.cycle = cycle;
    }

    public List<Formation> getFormations() {
        return formations;
    }

    public void setFormations(List<Formation> formations) {
        this.formations = formations;
    }

    // Getters et setters...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Cycle getCycle() {
        return cycle;
    }

    public void setCycle(Cycle cycle) {
        this.cycle = cycle;
    }

    @Override
    public String toString() {
        return "Niveau " + numero + " - " + cycle;
    }
}
