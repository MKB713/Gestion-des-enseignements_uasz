package com.uasz.daos.choix.repository;

import com.uasz.daos.choix.model.Choix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoixRepository extends JpaRepository<Choix, Long> {

    // Trouver tous les voeux d'un enseignant
    List<Choix> findByIdEnseignant(Long idEnseignant);

    // Voir qui a choisi une UE spécifique (pour détecter les conflits)
    List<Choix> findByIdUE(Long idUE);

    // Trouver les choix validés ou non
    List<Choix> findByValide(boolean valide);

    // Trouver les choix d'un enseignant validés
    List<Choix> findByIdEnseignantAndValide(Long idEnseignant, boolean valide);
}