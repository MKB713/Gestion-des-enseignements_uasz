package com.uasz.daos.choix.repositories;

import com.uasz.daos.choix.enums.TypeEnseignement;
import com.uasz.daos.choix.model.Repartition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepartitionRepository extends JpaRepository<Repartition, Long> {

    List<Repartition> findByMaquetteId(Long maquetteId);

    List<Repartition> findByEnseignantId(Long enseignantId);

    // Pour trouver une répartition spécifique (ex: Enseignant X sur EC Y pour le
    // CM)
    Optional<Repartition> findByEcIdAndTypeAndEnseignantId(Long ecId, TypeEnseignement type, Long enseignantId);

    // Pour valider s'il existe déjà un responsable pour un type donné sur un EC (si
    // unique)
    // Note: Pour TD/TP il peut y en avoir plusieurs si plusieurs groupes, mais pour
    // CM généralement un seul.
    // Cette méthode sert à lister tous les intervenants sur un EC et un type.
    List<Repartition> findByEcIdAndType(Long ecId, TypeEnseignement type);

    void deleteByMaquetteId(Long maquetteId);
}
