package com.uasz.daos.choix.services;

import com.uasz.daos.choix.model.Repartition;
import com.uasz.daos.choix.repositories.RepartitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@SuppressWarnings("null")
public class RepartitionService {

    private final RepartitionRepository repartitionRepository;

    public Repartition ajouterRepartition(Repartition repartition) {
        // Logique de validation possible ici
        // Ex: Vérifier si l'enseignant n'est pas déjà assigné pour le même type sur cet
        // EC si unicité requise
        return repartitionRepository.save(repartition);
    }

    public List<Repartition> listerParMaquette(Long maquetteId) {
        return repartitionRepository.findByMaquetteId(maquetteId);
    }

    public List<Repartition> listerParEnseignant(Long enseignantId) {
        return repartitionRepository.findByEnseignantId(enseignantId);
    }

    public void supprimerRepartition(Long id) {
        repartitionRepository.deleteById(id);
    }

    public Repartition modifierRepartition(Long id, Repartition nouvelle) {
        Repartition existante = repartitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Répartition non trouvée avec l'ID : " + id));

        existante.setEnseignantId(nouvelle.getEnseignantId());
        existante.setNombreGroupes(nouvelle.getNombreGroupes());
        // On ne change généralement pas l'EC ou le Type lors d'une modification simple,
        // sinon on supprime et on recrée.

        return repartitionRepository.save(existante);
    }
}
