package com.uasz.daos.maquette.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "maquette_versions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaquetteVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "maquette_id")
    private Maquette maquette;

    private int numeroVersion;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String donneesJson;

    private String utilisateur;
    private LocalDateTime dateCreation;
    private String motif;
}
