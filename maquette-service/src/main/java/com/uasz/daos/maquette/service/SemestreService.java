package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.model.Semestre;
import com.uasz.daos.maquette.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SemestreService {
    @Autowired
    private SemestreRepository semestreRepository;

    @Autowired
    private UERepository ueRepository;

    public List<Semestre> listerTout() {
        return semestreRepository.findAll();
    }

    public Semestre ajouterSemestre(Semestre semestre) {
        if (semestre.getLibelle() == null || semestre.getLibelle().isEmpty()) {
            throw new IllegalArgumentException("Le libellé du semestre est obligatoire");
        }
        return semestreRepository.save(semestre);
    }

    public Semestre modifierSemestre(Long id, Semestre semestreModifie) {
        return semestreRepository.findById(id).map(semestre -> {
            semestre.setLibelle(semestreModifie.getLibelle());
            semestre.setNumero(semestreModifie.getNumero());
            return semestreRepository.save(semestre);
        }).orElseThrow(() -> new RuntimeException("Semestre non trouvé"));
    }

    public void supprimerSemestre(Long id) {
        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));

        boolean hasUEs = ueRepository.existsBySemestre(semestre);
        if (hasUEs) {
            throw new IllegalStateException("Impossible de supprimer ce semestre car il contient des U.E.");
        }

        semestreRepository.deleteById(id);
    }

    public Semestre detailsSemestre(Long id) {
        return semestreRepository.findById(id).orElse(null);
    }
}
