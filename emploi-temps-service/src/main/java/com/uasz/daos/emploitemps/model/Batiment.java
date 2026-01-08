package com.uasz.daos.emploitemps.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batiment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String libelle;
    private String position;
    private String description;

    @OneToMany(mappedBy = "batiment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Salle> salles;
}
