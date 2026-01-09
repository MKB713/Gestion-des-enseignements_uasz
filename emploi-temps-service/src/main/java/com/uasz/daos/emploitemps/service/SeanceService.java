package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.repository.SeanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeanceService {
    @Autowired
    private SeanceRepository seanceRepository;

    public List<Seance> getAllSeances() {
        return seanceRepository.findAll();
    }

    public Seance getSeanceById(Long id) {
        return seanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Séance non trouvée"));
    }

    public Seance createSeance(Seance seance) {
        return seanceRepository.save(seance);
    }

    public Seance updateSeance(Long id, Seance seanceDetails) {
        Seance seance = getSeanceById(id);
        seance.setJour(seanceDetails.getJour());
        seance.setHeureDebut(seanceDetails.getHeureDebut());
        seance.setHeureFin(seanceDetails.getHeureFin());
        seance.setSalle(seanceDetails.getSalle());
        seance.setEcId(seanceDetails.getEcId());
        seance.setEnseignantId(seanceDetails.getEnseignantId());
        seance.setClasseId(seanceDetails.getClasseId());
        seance.setSemestreId(seanceDetails.getSemestreId());
        return seanceRepository.save(seance);
    }

    public void deleteSeance(Long id) {
        seanceRepository.deleteById(id);
    }

    public List<Seance> getSeancesByEnseignant(Long enseignantId) {
        return seanceRepository.findByEnseignantId(enseignantId);
    }

    public List<Seance> getSeancesBySalle(Long salleId) {
        return seanceRepository.findBySalleId(salleId);
    }
}
