package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.model.Filiere;
import com.uasz.daos.maquette.repository.FiliereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FiliereService {

    @Autowired
    private FiliereRepository filiereRepository;

    public List<Filiere> getAllFiliere() {
        return filiereRepository.findAll();
    }

    public Filiere getFiliereById(long id) {
        return filiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filière introuvable"));
    }

    @Transactional
    public Filiere save(Filiere filiere) {
        // Vérifier l'unicité du libellé
        if (filiere.getId() == null) {
            // Nouvelle filière
            filiereRepository.findByLibelle(filiere.getLibelle()).ifPresent(f -> {
                throw new IllegalArgumentException("Une filière avec ce libellé existe déjà.");
            });
        } else {
            // Modification
            filiereRepository.findByLibelle(filiere.getLibelle())
                    .ifPresent(f -> {
                        if (!f.getId().equals(filiere.getId())) {
                            throw new IllegalArgumentException("Une autre filière utilise déjà ce libellé.");
                        }
                    });
        }
        return filiereRepository.save(filiere);
    }

    @Transactional
    public void delete(long id) {

        // Vérifier si la filière est utilisée dans des formations
        // if (!filiere.getFormations().isEmpty()) {
        // throw new IllegalStateException("Impossible de supprimer cette filière car
        // elle est utilisée dans des formations.");
        // }
        filiereRepository.deleteById(id);
    }
}
