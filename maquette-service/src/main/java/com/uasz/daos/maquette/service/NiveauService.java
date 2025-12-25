package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.model.Niveau;
import com.uasz.daos.maquette.enums.Cycle;
import com.uasz.daos.maquette.repository.NiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NiveauService {

    @Autowired
    private NiveauRepository niveauRepository;

    public List<Niveau> getAllNiveaux() {
        return niveauRepository.findAll();
    }

    public Optional<Niveau> getNiveauById(Long id) {
        return niveauRepository.findById(id);
    }

    public List<Niveau> getByCycle(Cycle cycle) {
        return niveauRepository.findByCycle(cycle);
    }

    @Transactional
    public Niveau saveNiveau(Niveau niveau) {
        // Validation des contraintes métier
        validateNiveau(niveau);

        // Vérifier l'unicité du numéro dans le même cycle
        if (niveau.getId() == null) {
            // Nouveau niveau - vérifier si le numéro existe déjà dans ce cycle
            Optional<Niveau> existing = niveauRepository.findByNumeroAndCycle(niveau.getNumero(), niveau.getCycle());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Un niveau avec le numéro " + niveau.getNumero() +
                        " existe déjà dans le cycle " + niveau.getCycle());
            }
        } else {
            // Modification - vérifier conflit avec d'autres niveaux
            Optional<Niveau> existing = niveauRepository.findByNumeroAndCycle(niveau.getNumero(), niveau.getCycle());
            if (existing.isPresent() && !existing.get().getId().equals(niveau.getId())) {
                throw new IllegalArgumentException("Un autre niveau utilise déjà le numéro " +
                        niveau.getNumero() + " dans le cycle " + niveau.getCycle());
            }
        }

        return niveauRepository.save(niveau);
    }

    private void validateNiveau(Niveau niveau) {
        // Validation du numéro selon le cycle
        switch (niveau.getCycle()) {
            case LICENCE:
                if (niveau.getNumero() < 1 || niveau.getNumero() > 3) {
                    throw new IllegalArgumentException("Le numéro de niveau pour la licence doit être entre 1 et 3");
                }
                break;
            case MASTER:
                if (niveau.getNumero() < 1 || niveau.getNumero() > 2) {
                    throw new IllegalArgumentException("Le numéro de niveau pour le master doit être entre 1 et 2");
                }
                break;
            case DOCTORAT:
                if (niveau.getNumero() != 1) {
                    throw new IllegalArgumentException("Le numéro de niveau pour le doctorat doit être 1");
                }
                break;
        }
    }

    @Transactional
    public void deleteNiveau(Long id) {
        Niveau niveau = niveauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Niveau introuvable"));

        // Vérifier si le niveau est utilisé dans des formations
        // Vérifier si le niveau est utilisé dans des formations
        if (!niveau.getFormations().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer ce niveau car il est utilisé dans des formations");
        }

        niveauRepository.deleteById(id);
    }

    public Optional<Niveau> findByNumeroAndCycle(int numero, Cycle cycle) {
        return niveauRepository.findByNumeroAndCycle(numero, cycle);
    }
}
