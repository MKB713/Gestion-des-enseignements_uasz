package com.uasz.daos.maquette.model;

import com.uasz.daos.maquette.enums.Cycle;
import com.uasz.daos.maquette.enums.Niveau;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "module")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String libelle;

    @Enumerated(EnumType.STRING)
    private Cycle cycle;

    @Enumerated(EnumType.STRING)
    private Niveau niveau;

    private boolean archive = false;

    // --- C'EST CE CHAMP QUI MANQUAIT DANS VOTRE ERREUR ---
    @ManyToOne
    @JoinColumn(name = "ue_id")
    private UE ue;
    // -----------------------------------------------------

    @ManyToOne
    @JoinColumn(name = "ec_id")
    private EC ec;
}
