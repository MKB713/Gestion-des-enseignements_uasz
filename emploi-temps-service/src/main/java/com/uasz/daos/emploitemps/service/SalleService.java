package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.Salle;
import com.uasz.daos.emploitemps.repository.SalleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalleService {

    @Autowired
    private SalleRepository salleRepository;

    public List<Salle> getAllSalles() {
        return salleRepository.findAll();
    }

    public Salle getSalleById(Long id) {
        return salleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Salle introuvable avec l'ID : " + id));
    }

    public Salle createSalle(Salle salle) {
        return salleRepository.save(salle);
    }

    public Salle updateSalle(Long id, Salle salleDetails) {
        Salle salle = getSalleById(id);

        salle.setCode(salleDetails.getCode());
        salle.setLibelle(salleDetails.getLibelle());
        salle.setCapacite(salleDetails.getCapacite());
        salle.setDescription(salleDetails.getDescription());
        salle.setBatiment(salleDetails.getBatiment());

        return salleRepository.save(salle);
    }

    public void deleteSalle(Long id) {
        if (!salleRepository.existsById(id)) {
            throw new EntityNotFoundException("Salle introuvable avec l'ID : " + id);
        }
        salleRepository.deleteById(id);
    }
}
