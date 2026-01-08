package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.model.Departement;
import com.uasz.daos.maquette.repository.DepartementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartementService {
    private final DepartementRepository departementRepository;

    public DepartementService(DepartementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }

    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    public Departement getDepartementById(Long id) {
        return departementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Departement non trouvé avec l'id : " + id));
    }

    public Departement createDepartement(Departement departement) {
        return departementRepository.save(departement);
    }

    public Departement updateDepartement(Long id, Departement departementDetails) {
        Departement departement = getDepartementById(id);
        departement.setLibelle(departementDetails.getLibelle());
        departement.setDescription(departementDetails.getDescription());
        return departementRepository.save(departement);
    }

    public void deleteDepartement(Long id) {
        Departement departement = getDepartementById(id);
        departementRepository.delete(departement);
    }
}
