package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.model.Filiere;
import com.uasz.daos.maquette.model.Formation;
import com.uasz.daos.maquette.model.Niveau;
import com.uasz.daos.maquette.enums.StatutFormation;
import com.uasz.daos.maquette.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@SuppressWarnings("null")
public class FormationService {

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private FiliereRepository filiereRepository;

    @Autowired
    private NiveauRepository niveauRepository;

    // --- LECTURE ---

    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    public List<Formation> getActiveFormations() {
        return formationRepository.findByStatutFormation(StatutFormation.ACTIVE);
    }

    // CORRECTION ICI : Retourne directement Formation (et lance une erreur si pas
    // trouvé)
    public Formation getFormationById(long id) {
        return formationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Formation introuvable avec l'ID : " + id));
    }

    public List<Formation> searchByLibelle(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return formationRepository.findByLibelleContainingIgnoreCase(query.trim());
    }

    // --- ECRITURE (CREATE / UPDATE) ---

    /**
     * Méthode principale appelée par le contrôleur.
     * Redirige vers create ou update selon si l'ID existe.
     */
    @Transactional
    public Formation save(Formation formation) {
        if (formation.getId() == null) {
            return createFormation(formation);
        } else {
            return updateFormation(formation.getId(), formation);
        }
    }

    @Transactional
    public Formation createFormation(Formation formation) {
        // Unicité Code
        if (formation.getCode() != null && formationRepository.findByCode(formation.getCode()).isPresent()) {
            throw new IllegalArgumentException("Ce code de formation existe déjà.");
        }
        // Unicité Libellé
        if (formation.getLibelle() != null && formationRepository.findByLibelle(formation.getLibelle()).isPresent()) {
            throw new IllegalArgumentException("Ce libellé de formation existe déjà.");
        }

        // Chargement des relations
        attachRelations(formation);

        if (formation.getDateCreation() == null) {
            formation.setDateCreation(new Date());
        }
        formation.setStatutFormation(StatutFormation.ACTIVE);
        return formationRepository.save(formation);
    }

    @Transactional
    public Formation updateFormation(long id, Formation updated) {
        Formation found = getFormationById(id);

        // Vérif unicité si modification du code
        if (updated.getCode() != null && !updated.getCode().equals(found.getCode())) {
            if (formationRepository.findByCode(updated.getCode()).isPresent()) {
                throw new IllegalArgumentException("Ce code est déjà utilisé par une autre formation.");
            }
            found.setCode(updated.getCode());
        }

        // Vérif unicité si modification du libellé
        if (updated.getLibelle() != null && !updated.getLibelle().equals(found.getLibelle())) {
            if (formationRepository.findByLibelle(updated.getLibelle()).isPresent()) {
                throw new IllegalArgumentException("Ce libellé est déjà utilisé par une autre formation.");
            }
            found.setLibelle(updated.getLibelle());
        }

        if (updated.getDescription() != null)
            found.setDescription(updated.getDescription());

        // Mise à jour des relations (Filière/Niveau)
        if (updated.getFiliere() != null)
            found.setFiliere(updated.getFiliere());
        if (updated.getNiveau() != null)
            found.setNiveau(updated.getNiveau());

        attachRelations(found); // Recharger les objets complets pour éviter les erreurs de Lazy Loading

        return formationRepository.save(found);
    }

    // Helper pour charger Filiere et Niveau depuis la DB
    private void attachRelations(Formation formation) {
        if (formation.getFiliere() != null && formation.getFiliere().getId() != null) {
            Filiere f = filiereRepository.findById(formation.getFiliere().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Filière invalide"));
            formation.setFiliere(f);
        }
        if (formation.getNiveau() != null && formation.getNiveau().getId() != null) {
            Niveau n = niveauRepository.findById(formation.getNiveau().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Niveau invalide"));
            formation.setNiveau(n);
        }
    }

    // --- GESTION STATUT ---

    @Transactional
    public Formation archiveFormation(long id) {
        Formation found = getFormationById(id);
        found.setStatutFormation(StatutFormation.ARCHIVE);
        return formationRepository.save(found);
    }

    @Transactional
    public Formation activerFormation(long id) {
        Formation found = getFormationById(id);
        found.setStatutFormation(StatutFormation.ACTIVE);
        return formationRepository.save(found);
    }
}
