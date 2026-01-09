package com.uasz.daos.deroulement.service;

import com.uasz.daos.deroulement.dto.ClasseDTO;
import com.uasz.daos.deroulement.model.Classe;
import com.uasz.daos.deroulement.repository.ClasseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class ClasseService {

    @Autowired
    private ClasseRepository classeRepository;

    // Note: Filiere et Niveau sont dans le service maquette-service
    // Nous utilisons uniquement les IDs

    /**
     * Récupère toutes les classes
     */
    public List<Classe> getAllClasses() {
        return classeRepository.findAll();
    }

    /**
     * Récupère une classe par son ID
     */
    public Classe getClasseById(Long id) {
        return classeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'ID : " + id));
    }

    /**
     * Récupère une classe par son code
     */
    public Optional<Classe> getClasseByCode(String code) {
        return classeRepository.findByCode(code);
    }

    /**
     * Crée une nouvelle classe
     */
    @Transactional
    public Classe creerClasse(ClasseDTO classeDTO) {
        // Vérifier si le code existe déjà
        Optional<Classe> existingClasse = classeRepository.findByCode(classeDTO.getCode());
        if (existingClasse.isPresent()) {
            throw new IllegalArgumentException("Une classe avec le code '" + classeDTO.getCode() + "' existe déjà.");
        }

        // Créer une nouvelle classe
        Classe classe = new Classe();
        classe.setCode(classeDTO.getCode());
        classe.setLibelle(classeDTO.getLibelle());
        classe.setDescription(classeDTO.getDescription());
        classe.setAnneeAcademique(classeDTO.getAnneeAcademique());
        classe.setEffectifMax(classeDTO.getEffectifMax());

        // Gérer la filière si fournie (utilisation de l'ID uniquement)
        if (classeDTO.getFiliereId() != null) {
            classe.setFiliereId(classeDTO.getFiliereId());
        }

        // Gérer le niveau si fourni (utilisation de l'ID uniquement)
        if (classeDTO.getNiveauId() != null) {
            classe.setNiveauId(classeDTO.getNiveauId());
        }

        // Initialiser les valeurs par défaut
        classe.setEstActive(true);
        classe.setEstArchivee(false);
        classe.setDateCreation(LocalDateTime.now());
        classe.setDateModification(LocalDateTime.now());

        return classeRepository.save(classe);
    }

    /**
     * Modifie une classe existante
     */
    @Transactional
    public Classe modifierClasse(Long id, ClasseDTO classeDTO) {
        Classe classe = getClasseById(id);

        // Vérifier si le code est modifié et s'il existe déjà pour une autre classe
        if (classeDTO.getCode() != null && !classeDTO.getCode().equals(classe.getCode())) {
            List<Classe> existingClasses = classeRepository.findByCodeAndIdIsNot(classeDTO.getCode(), id);
            if (!existingClasses.isEmpty()) {
                throw new IllegalArgumentException(
                        "Une autre classe avec le code '" + classeDTO.getCode() + "' existe déjà.");
            }
            classe.setCode(classeDTO.getCode());
        }

        // Mettre à jour les champs
        if (classeDTO.getLibelle() != null && !classeDTO.getLibelle().trim().isEmpty()) {
            classe.setLibelle(classeDTO.getLibelle());
        }

        if (classeDTO.getDescription() != null) {
            classe.setDescription(classeDTO.getDescription());
        }

        if (classeDTO.getAnneeAcademique() != null) {
            classe.setAnneeAcademique(classeDTO.getAnneeAcademique());
        }

        if (classeDTO.getEffectifMax() != null) {
            classe.setEffectifMax(classeDTO.getEffectifMax());
        }

        // Mettre à jour la filière si fournie (utilisation de l'ID uniquement)
        if (classeDTO.getFiliereId() != null) {
            classe.setFiliereId(classeDTO.getFiliereId());
        }

        // Mettre à jour le niveau si fourni (utilisation de l'ID uniquement)
        if (classeDTO.getNiveauId() != null) {
            classe.setNiveauId(classeDTO.getNiveauId());
        }

        classe.setDateModification(LocalDateTime.now());

        return classeRepository.save(classe);
    }

    /**
     * Archive une classe
     */
    @Transactional
    public Classe archiverClasse(Long id) {
        Classe classe = getClasseById(id);
        classe.setEstArchivee(true);
        classe.setEstActive(false);
        classe.setDateModification(LocalDateTime.now());
        return classeRepository.save(classe);
    }

    /**
     * Désarchive une classe
     */
    @Transactional
    public Classe desarchiverClasse(Long id) {
        Classe classe = getClasseById(id);
        classe.setEstArchivee(false);
        classe.setEstActive(true);
        classe.setDateModification(LocalDateTime.now());
        return classeRepository.save(classe);
    }

    /**
     * Active une classe
     */
    @Transactional
    public Classe activerClasse(Long id) {
        Classe classe = getClasseById(id);
        classe.setEstActive(true);
        classe.setDateModification(LocalDateTime.now());
        return classeRepository.save(classe);
    }

    /**
     * Désactive une classe
     */
    @Transactional
    public Classe desactiverClasse(Long id) {
        Classe classe = getClasseById(id);
        classe.setEstActive(false);
        classe.setDateModification(LocalDateTime.now());
        return classeRepository.save(classe);
    }

    /**
     * Récupère les classes actives
     */
    public List<Classe> getClassesActives() {
        return classeRepository.findByEstActive(true);
    }

    /**
     * Récupère les classes archivées
     */
    public List<Classe> getClassesArchivees() {
        return classeRepository.findByEstArchivee(true);
    }

    /**
     * Récupère les classes par filière
     */
    public List<Classe> getClassesByFiliere(Long filiereId) {
        return classeRepository.findByFiliereId(filiereId);
    }

    /**
     * Récupère les classes par niveau
     */
    public List<Classe> getClassesByNiveau(Long niveauId) {
        return classeRepository.findByNiveauId(niveauId);
    }

    /**
     * Récupère les classes par année académique
     */
    public List<Classe> getClassesByAnneeAcademique(String anneeAcademique) {
        return classeRepository.findByAnneeAcademique(anneeAcademique);
    }

    /**
     * Recherche des classes par code ou libellé
     */
    public List<Classe> rechercherClasses(String searchTerm) {
        return classeRepository.findByCodeContainingIgnoreCaseOrLibelleContainingIgnoreCase(searchTerm, searchTerm);
    }

    /**
     * Recherche des classes par filière et niveau
     */
    public List<Classe> rechercherClassesByFiliereAndNiveau(Long filiereId, Long niveauId) {
        return classeRepository.findByFiliereIdAndNiveauId(filiereId, niveauId);
    }

    /**
     * Supprime une classe (suppression physique)
     */
    @Transactional
    public void supprimerClasse(Long id) {
        Classe classe = getClasseById(id);
        classeRepository.delete(classe);
    }
}
