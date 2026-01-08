package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.Batiment;
import com.uasz.daos.emploitemps.repository.BatimentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatimentService {

    @Autowired
    private BatimentRepository batimentRepository;

    public List<Batiment> getAllBatiments() {
        return batimentRepository.findAll();
    }

    public Batiment getBatimentById(Long id) {
        return batimentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Batiment introuvable avec l'ID : " + id));
    }

    public Batiment createBatiment(Batiment batiment) {
        return batimentRepository.save(batiment);
    }

    public Batiment updateBatiment(Long id, Batiment batimentDetails) {
        Batiment batiment = getBatimentById(id);
        
        batiment.setLibelle(batimentDetails.getLibelle());
        batiment.setCode(batimentDetails.getCode());
        batiment.setPosition(batimentDetails.getPosition());
        batiment.setDescription(batimentDetails.getDescription());
        
        return batimentRepository.save(batiment);
    }

    public void deleteBatiment(Long id) {
        if (!batimentRepository.existsById(id)) {
            throw new EntityNotFoundException("Batiment introuvable avec l'ID : " + id);
        }
        batimentRepository.deleteById(id);
    }
}
