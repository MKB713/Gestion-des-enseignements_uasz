package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.api.EnseignantApi;
import com.uasz.daos.emploitemps.api.MaquetteApi;
import com.uasz.daos.emploitemps.dto.generation.*;
import com.uasz.daos.emploitemps.model.Salle;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.model.TypeSeance;
import com.uasz.daos.emploitemps.repository.SalleRepository;
import com.uasz.daos.emploitemps.repository.SeanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la génération automatique d'emplois du temps
 * Fait le pont entre Java et le service Python d'optimisation
 */
@Service
public class EmploiDuTempsGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(EmploiDuTempsGenerationService.class);

    @Autowired
    private PythonGeneratorClient pythonClient;

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private EnseignantApi enseignantApi;

    @Autowired
    private MaquetteApi maquetteApi;

    @Autowired
    private NotificationService notificationService;

    /**
     * Génère automatiquement un emploi du temps pour une période donnée
     *
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @param classeIds Liste des IDs de classes concernées
     * @param creePar Utilisateur qui lance la génération
     * @return Réponse avec les séances générées
     */
    @Transactional
    public GenerationResponse genererEmploiDuTempsAutomatique(
            LocalDate dateDebut,
            LocalDate dateFin,
            List<Long> classeIds,
            String creePar) {

        logger.info("🚀 Démarrage génération automatique emploi du temps");
        logger.info("📅 Période: {} à {}", dateDebut, dateFin);
        logger.info("🎓 Classes: {}", classeIds);

        try {
            // Étape 1: Vérifier que le service Python est accessible
            if (!pythonClient.isServiceAvailable()) {
                throw new RuntimeException("Service Python de génération non disponible");
            }

            // Étape 2: Récupérer les données nécessaires
            GenerationRequest request = construireRequete(dateDebut, dateFin, classeIds);

            // Étape 3: Appeler le service Python
            GenerationResponse response = pythonClient.genererEmploiDuTemps(request);

            // Étape 4: Sauvegarder les séances générées en base de données
            if (response.getSuccess()) {
                int nbSauvegardees = sauvegarderSeances(response.getSeances(), creePar);
                logger.info("✅ {} séances sauvegardées en base de données", nbSauvegardees);

                // Notification de succès
                // TODO: Notifier les responsables
            } else {
                logger.warn("⚠️ Génération partielle avec {} conflits", response.getConflits().size());
            }

            return response;

        } catch (Exception e) {
            logger.error("❌ Erreur lors de la génération automatique", e);
            throw new RuntimeException("Erreur lors de la génération: " + e.getMessage(), e);
        }
    }

    /**
     * Construit la requête pour le service Python
     */
    private GenerationRequest construireRequete(LocalDate dateDebut, LocalDate dateFin, List<Long> classeIds) {
        logger.info("📦 Construction de la requête de génération...");

        GenerationRequest request = new GenerationRequest();
        request.setDateDebut(dateDebut.toString());
        request.setDateFin(dateFin.toString());

        // Récupérer les salles
        List<Salle> salles = salleRepository.findAll();
        request.setSalles(salles.stream()
                .map(s -> {
                    SalleDTO dto = new SalleDTO();
                    dto.setId(s.getId());
                    dto.setLibelle(s.getLibelle());
                    dto.setCapacite(s.getCapacite());
                    dto.setBatimentId(s.getBatiment() != null ? s.getBatiment().getId() : null);
                    return dto;
                })
                .collect(Collectors.toList()));

        // Récupérer les enseignants via API
        try {
            List<EnseignantDTO> enseignants = enseignantApi.getAllEnseignants(); // Assuming this method exists
            request.setEnseignants(enseignants);
        } catch (Exception e) {
            logger.warn("Impossible de récupérer les enseignants via EnseignantApi: {}", e.getMessage());
            request.setEnseignants(new ArrayList<>());
        }

        // Récupérer les EC via MaquetteApi
        try {
            List<ECDTO> ecs = maquetteApi.getAllEcs(); // Assuming this method exists
            request.setEcs(ecs);
        } catch (Exception e) {
            logger.warn("Impossible de récupérer les ECs via MaquetteApi: {}", e.getMessage());
            request.setEcs(new ArrayList<>());
        }

        // Récupérer les classes via MaquetteApi
        try {
            List<ClasseDTO> classes = maquetteApi.getClassesByIds(classeIds); // Assuming this method exists
            request.setClasses(classes);
        } catch (Exception e) {
            logger.warn("Impossible de récupérer les classes via MaquetteApi: {}", e.getMessage());
            request.setClasses(new ArrayList<>());
        }

        // Récupérer les répartitions
        try {
            List<RepartitionDTO> repartitions = maquetteApi.getRepartitionsByClasseIds(classeIds); // Assuming this method exists
            request.setRepartitions(repartitions);
        } catch (Exception e) {
            logger.warn("Impossible de récupérer les répartitions via MaquetteApi: {}", e.getMessage());
            request.setRepartitions(new ArrayList<>());
        }

        request.setContraintesGlobales(new HashMap<>());

        logger.info("✅ Requête construite: {} salles, {} enseignants, {} EC, {} classes, {} répartitions",
                request.getSalles().size(),
                request.getEnseignants().size(),
                request.getEcs().size(),
                request.getClasses().size(),
                request.getRepartitions().size());

        return request;
    }

    /**
     * Sauvegarde les séances générées en base de données
     */
    private int sauvegarderSeances(List<SeanceGenereeDTO> seancesGenerees, String creePar) {
        logger.info("💾 Sauvegarde de {} séances en base de données...", seancesGenerees.size());

        int nbSauvegardees = 0;

        for (SeanceGenereeDTO seanceDTO : seancesGenerees) {
            try {
                Seance seance = new Seance();

                // Date et horaires
                seance.setDateSeance(LocalDate.parse(seanceDTO.getDate()));
                seance.setHeureDebut(LocalTime.parse(seanceDTO.getHeureDebut()));
                seance.setHeureFin(LocalTime.parse(seanceDTO.getHeureFin()));
                seance.setDuree(seanceDTO.getDuree());

                // Type de séance
                seance.setTypeSeance(TypeSeance.valueOf(seanceDTO.getTypeSeance()));

                // Relations
                Salle salle = salleRepository.findById(seanceDTO.getSalleId()).orElse(null);
                seance.setSalle(salle);
                seance.setEnseignantId(seanceDTO.getEnseignantId());
                seance.setEcId(seanceDTO.getEcId());
                seance.setClasseId(seanceDTO.getClasseId());
                seance.setRepartitionId(seanceDTO.getRepartitionId());

                // Audit
                seance.setCreePar(creePar);

                seanceRepository.save(seance);
                nbSauvegardees++;

            } catch (Exception e) {
                logger.error("Erreur sauvegarde séance: {}", e.getMessage());
            }
        }

        return nbSauvegardees;
    }

    /**
     * Teste la connexion avec le service Python
     */
    public boolean testerConnexionPython() {
        return pythonClient.isServiceAvailable();
    }

    /**
     * Vérifie les conflits dans les séances existantes
     */
    public ConflitsResponse verifierConflitsSeances(LocalDate dateDebut, LocalDate dateFin) {
        logger.info("🔍 Vérification des conflits pour la période {} - {}", dateDebut, dateFin);

        // Récupérer les séances de la période
        List<Seance> seances = seanceRepository.findByDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                dateDebut, dateFin);

        // Convertir en DTO
        List<SeanceGenereeDTO> seancesDTO = seances.stream()
                .map(this::convertirSeanceEnDTO)
                .collect(Collectors.toList());

        // Appeler Python pour vérifier
        return pythonClient.verifierConflits(seancesDTO);
    }

    private SeanceGenereeDTO convertirSeanceEnDTO(Seance seance) {
        SeanceGenereeDTO dto = new SeanceGenereeDTO();
        dto.setDate(seance.getDateSeance().toString());
        dto.setHeureDebut(seance.getHeureDebut().toString());
        dto.setHeureFin(seance.getHeureFin().toString());
        dto.setDuree(seance.getDuree());
        dto.setSalleId(seance.getSalle() != null ? seance.getSalle().getId() : null);
        dto.setEnseignantId(seance.getEnseignantId());
        dto.setEcId(seance.getEcId());
        dto.setClasseId(seance.getClasseId());
        dto.setTypeSeance(seance.getTypeSeance().name());
        dto.setRepartitionId(seance.getRepartitionId());
        return dto;
    }
}