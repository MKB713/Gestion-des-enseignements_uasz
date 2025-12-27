package com.uasz.daos.enseignant.service;

import com.uasz.daos.enseignant.model.Enseignant;
import com.uasz.daos.enseignant.enums.StatutEnseignant;
import com.uasz.daos.enseignant.repository.EnseignantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class EnseignantService {

    @Autowired
    private EnseignantRepository enseignantRepository;

    // ======================================================================
    // 1. UTILITAIRES MÉTIER (Privés)
    // ======================================================================

    /**
     * Nettoie une chaîne (enlève accents, espaces, met en minuscule)
     */
    private String cleanString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .trim()
                .replace(" ", "");
    }

    /**
     * Génère un email institutionnel unique : prenom.nom@univ-zig.sn
     * Gère les doublons en ajoutant un chiffre (prenom.nom1@...)
     */
    private String generateUniqueEmail(String prenom, String nom) {
        String cleanPrenom = cleanString(prenom);
        String cleanNom = cleanString(nom);
        String domain = "@univ-zig.sn";

        String baseEmail = cleanPrenom + "." + cleanNom;
        String candidateEmail = baseEmail + domain;

        int counter = 1;
        while (enseignantRepository.findByEmail(candidateEmail).isPresent()) {
            candidateEmail = baseEmail + counter + domain;
            counter++;
        }
        return candidateEmail;
    }

    /**
     * Génère le prochain matricule disponible pour l'année en cours
     */
    private Long generateNextMatricule() {
        int currentYear = Year.now().getValue();
        final long YEAR_BASE = (long) currentYear * 100000L;

        List<Enseignant> allEnseignants = enseignantRepository.findAll();

        Optional<Long> maxMatriculeThisYearOpt = allEnseignants.stream()
                .filter(e -> e.getMatricule() != null && e.getMatricule() >= YEAR_BASE && e.getMatricule() < YEAR_BASE + 100000)
                .map(Enseignant::getMatricule)
                .max(Comparator.naturalOrder());

        long nextRank = maxMatriculeThisYearOpt.map(maxMatricule -> (maxMatricule % 100000L) + 1L).orElse(1L);

        if (nextRank >= 100000L) {
            throw new RuntimeException("Erreur de séquence : Limite atteinte pour l'année " + currentYear);
        }

        return YEAR_BASE + nextRank;
    }

    // ======================================================================
    // 2. VALIDATIONS
    // ======================================================================

    private void validateDateEmbauche(LocalDate dateEmbauche) {
        if (dateEmbauche != null && dateEmbauche.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date d'embauche ne peut pas être une date future (" + dateEmbauche + ").");
        }
    }

    private void validateAge(LocalDate dateNaissance) {
        if (dateNaissance != null) {
            LocalDate dateMinimum = LocalDate.now().minusYears(25);
            if (dateNaissance.isAfter(dateMinimum)) {
                throw new IllegalArgumentException("L'enseignant doit être âgé d'au moins 25 ans.");
            }
        }
    }

    // ======================================================================
    // 3. CRUD (Lecture / Écriture)
    // ======================================================================

    public Enseignant getEnseignantById(Long id) {
        return enseignantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignant non trouvé avec l'id: " + id));
    }

    public List<Enseignant> getAllEnseignants() {
        return enseignantRepository.findByStatutEnseignantNot(StatutEnseignant.ARCHIVE);
    }

    public List<Enseignant> getAllEnseignantsArchives() {
        return enseignantRepository.findByStatutEnseignant(StatutEnseignant.ARCHIVE);
    }

    /**
     * Crée ou sauvegarde un enseignant.
     * @return L'objet sauvegardé (IMPORTANT pour le retour JSON au Front)
     */
    @Transactional
    public Enseignant saveEnseignant(Enseignant enseignant) {
        // Validations
        validateDateEmbauche(enseignant.getDateEmbauche());
        validateAge(enseignant.getDateNaissance());

        // Génération Email si absent
        if (enseignant.getEmail() == null || enseignant.getEmail().isEmpty()) {
            String generatedEmail = generateUniqueEmail(enseignant.getPrenom(), enseignant.getNom());
            enseignant.setEmail(generatedEmail);
        }

        // Initialisation pour création
        if (enseignant.getId() == null) {
            enseignant.setMatricule(generateNextMatricule());
            enseignant.setDateCreation(LocalDateTime.now());
            enseignant.setStatutEnseignant(StatutEnseignant.ACTIF);
            enseignant.setEstActif(true);
        }

        enseignant.setDateModification(LocalDateTime.now());
        return enseignantRepository.save(enseignant);
    }

    /**
     * Met à jour un enseignant existant.
     * @return L'objet mis à jour
     */
    @Transactional
    public Enseignant updateEnseignant(Long id, Enseignant enseignantForm) {
        Enseignant enseignant = getEnseignantById(id);

        // Validations
        validateDateEmbauche(enseignantForm.getDateEmbauche());
        validateAge(enseignantForm.getDateNaissance());

        // Mise à jour des champs modifiables
        enseignant.setNom(enseignantForm.getNom());
        enseignant.setPrenom(enseignantForm.getPrenom());
        enseignant.setSpecialite(enseignantForm.getSpecialite());
        enseignant.setDateNaissance(enseignantForm.getDateNaissance());
        enseignant.setLieuNaissance(enseignantForm.getLieuNaissance());
        enseignant.setDateEmbauche(enseignantForm.getDateEmbauche());
        enseignant.setGrade(enseignantForm.getGrade());
        enseignant.setStatut(enseignantForm.getStatut());
        enseignant.setTelephone(enseignantForm.getTelephone());
        enseignant.setAdresse(enseignantForm.getAdresse());

        enseignant.setDateModification(LocalDateTime.now());
        return enseignantRepository.save(enseignant);
    }

    // ======================================================================
    // 4. GESTION DES ÉTATS (Actions)
    // ======================================================================

    @Transactional
    public Enseignant archiverEnseignant(Long id) {
        Enseignant enseignant = getEnseignantById(id);
        enseignant.setStatutEnseignant(StatutEnseignant.ARCHIVE);
        enseignant.setEstActif(false);
        enseignant.setDateModification(LocalDateTime.now());
        return enseignantRepository.save(enseignant);
    }

    @Transactional
    public Enseignant desarchiverEnseignant(Long id) {
        Enseignant enseignant = getEnseignantById(id);
        enseignant.setStatutEnseignant(StatutEnseignant.ACTIF);
        enseignant.setEstActif(true);
        enseignant.setDateModification(LocalDateTime.now());
        return enseignantRepository.save(enseignant);
    }

    @Transactional
    public Enseignant activerEnseignant(Long id) {
        Enseignant enseignant = getEnseignantById(id);
        if (enseignant.getStatutEnseignant() == StatutEnseignant.ARCHIVE) {
            throw new RuntimeException("Impossible d'activer un enseignant archivé.");
        }
        enseignant.setEstActif(true);
        enseignant.setDateModification(LocalDateTime.now());
        return enseignantRepository.save(enseignant);
    }

    @Transactional
    public Enseignant desactiverEnseignant(Long id) {
        Enseignant enseignant = getEnseignantById(id);
        if (enseignant.getStatutEnseignant() == StatutEnseignant.ARCHIVE) {
            throw new RuntimeException("Impossible de désactiver un enseignant archivé.");
        }
        enseignant.setEstActif(false);
        enseignant.setDateModification(LocalDateTime.now());
        return enseignantRepository.save(enseignant);
    }
}