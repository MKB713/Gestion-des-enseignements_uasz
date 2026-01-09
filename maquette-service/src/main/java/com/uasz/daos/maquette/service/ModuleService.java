package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.model.Module;
import com.uasz.daos.maquette.model.UE;
import com.uasz.daos.maquette.repository.ModuleRepository;
import com.uasz.daos.maquette.repository.UERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private UERepository ueRepository;

    // --- LECTURE ---

    public List<Module> getAllModules() {
        // Retourne uniquement les modules actifs (non archivés)
        return moduleRepository.findAll().stream()
                .filter(m -> !m.isArchive())
                .collect(Collectors.toList());
    }

    public List<Module> getArchivedModules() {
        // Retourne uniquement les archives
        return moduleRepository.findAll().stream()
                .filter(Module::isArchive)
                .collect(Collectors.toList());
    }

    public Module getModuleById(long id) {
        return moduleRepository.findById(id).orElse(null);
    }

    // --- ECRITURE ---

    @Transactional
    public Module addModule(Module module) {
        // Valider et charger l'UE si elle est spécifiée
        if (module.getUe() != null && module.getUe().getId() != null) {
            UE ue = ueRepository.findById(module.getUe().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "UE avec l'ID " + module.getUe().getId() + " n'existe pas"));
            module.setUe(ue);
        }

        module.setArchive(false); // Toujours actif à la création
        return moduleRepository.save(module);
    }

    @Transactional
    public Module updateModule(long id, Module moduleDetails) {
        Module module = getModuleById(id);
        if (module != null) {
            module.setCode(moduleDetails.getCode());
            module.setLibelle(moduleDetails.getLibelle());
            module.setCycle(moduleDetails.getCycle());
            module.setNiveau(moduleDetails.getNiveau());

            // Mettre à jour l'UE si spécifiée
            if (moduleDetails.getUe() != null && moduleDetails.getUe().getId() != null) {
                UE ue = ueRepository.findById(moduleDetails.getUe().getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "UE avec l'ID " + moduleDetails.getUe().getId() + " n'existe pas"));
                module.setUe(ue);
            } else {
                module.setUe(null);
            }

            // On ne touche pas à l'état archive ici
            return moduleRepository.save(module);
        }
        return null;
    }

    // --- GESTION DES ÉTATS (ARCHIVAGE) ---

    @Transactional
    public void archiveModule(long id) {
        Module module = getModuleById(id);
        if (module != null) {
            module.setArchive(true);
            moduleRepository.save(module);
        }
    }

    @Transactional
    public void unarchiveModule(long id) {
        Module module = getModuleById(id);
        if (module != null) {
            module.setArchive(false);
            moduleRepository.save(module);
        }
    }
}
