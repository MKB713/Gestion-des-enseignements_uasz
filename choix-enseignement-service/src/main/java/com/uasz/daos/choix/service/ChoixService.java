package com.uasz.daos.choix.service;

import com.uasz.daos.choix.dto.EnseignantDTO;
import com.uasz.daos.choix.dto.UEDTO;
import com.uasz.daos.choix.model.Choix;
import com.uasz.daos.choix.proxy.EnseignantProxy;
import com.uasz.daos.choix.proxy.MaquetteProxy; // <--- Nouveau
import com.uasz.daos.choix.repository.ChoixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChoixService {

    @Autowired
    private ChoixRepository choixRepository;

    @Autowired
    private EnseignantProxy enseignantProxy;

    @Autowired
    private MaquetteProxy maquetteProxy; // <--- Injection

    public Choix ajouterChoix(Long enseignantId, Long ueId) {
        // 1. Validation Enseignant (Appel Microservice Enseignant)
        EnseignantDTO prof = enseignantProxy.getEnseignantById(enseignantId);
        if (prof == null) {
            throw new RuntimeException("Erreur : Enseignant introuvable (ID: " + enseignantId + ")");
        }

        // 2. Validation UE (Appel Microservice Maquette)
        UEDTO matiere = maquetteProxy.getUEById(ueId);
        if (matiere == null) {
            throw new RuntimeException("Erreur : UE introuvable (ID: " + ueId + ")");
        }

        // 3. Création du Choix
        Choix choix = new Choix();
        choix.setIdEnseignant(enseignantId);
        choix.setIdUE(ueId);
        choix.setDateChoix(LocalDateTime.now());
        choix.setValide(false); // Par défaut non validé par le chef de département

        // Optionnel : Stocker les libellés pour éviter des requêtes lors de l'affichage simple
        // choix.setDescription(prof.getPrenom() + " " + prof.getNom() + " -> " + matiere.getLibelle());

        return choixRepository.save(choix);
    }
}