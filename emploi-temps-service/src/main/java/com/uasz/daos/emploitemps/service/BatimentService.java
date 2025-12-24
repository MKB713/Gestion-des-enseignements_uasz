package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.Batiment;
import com.uasz.daos.emploitemps.repository.BatimentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BatimentService {

    @Autowired
    private BatimentRepository batimentRepository;

    /**
     * Récupère tous les bâtiments
     */
    public List<Batiment> getAllBatiments() {
        return batimentRepository.findAll();
    }

    /**
     * Récupère un bâtiment par ID
     */
    public Batiment getBatimentById(Long id) {
        return batimentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bâtiment non trouvé avec l'id: " + id));
    }

    /**
     * Crée un nouveau bâtiment
     */
    @Transactional
    public Batiment createBatiment(Batiment batiment) {
        return batimentRepository.save(batiment);
    }

    /**
     * Met à jour un bâtiment
     */
    @Transactional
    public Batiment updateBatiment(Long id, Batiment batimentDetails) {
        Batiment existingBatiment = getBatimentById(id);

        existingBatiment.setLibelle(batimentDetails.getLibelle());
        existingBatiment.setDescription(batimentDetails.getDescription());

        return batimentRepository.save(existingBatiment);
    }

    /**
     * Supprime un bâtiment
     */
    @Transactional
    public void deleteBatiment(Long id) {
        if (!batimentRepository.existsById(id)) {
            throw new EntityNotFoundException("Bâtiment non trouvé avec l'id: " + id);
        }
        batimentRepository.deleteById(id);
    }

    /**
     * Recherche des bâtiments par libellé
     */
    public List<Batiment> searchBatiments(String libelle) {
        return batimentRepository.findByLibelleContainingIgnoreCase(libelle);
    }

    /**
     * Récupère un bâtiment par libellé exact
     */
    public Batiment getBatimentByLibelle(String libelle) {
        return batimentRepository.findByLibelle(libelle)
                .orElseThrow(() -> new EntityNotFoundException("Bâtiment non trouvé avec le libellé: " + libelle));
    }

    /**
     * Compte le nombre de salles d'un bâtiment
     */
    public long countSallesByBatiment(Long batimentId) {
        return batimentRepository.countSallesByBatimentId(batimentId);
    }
}