package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.dto.EmploiDuTempsDTO;
import com.uasz.daos.emploitemps.dto.SeanceDTO;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.repository.SeanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class EmploiDuTempsService {

    @Autowired
    private SeanceRepository seanceRepository;

    /**
     * Récupère l'emploi du temps hebdomadaire (lundi-samedi)
     *
     * @param dateReference Date de référence dans la semaine souhaitée
     * @param filtrePar Type de filtre : "ENSEIGNANT", "SALLE", "EC" ou null pour tout
     * @param filtreId ID de l'entité à filtrer (peut être null)
     * @return EmploiDuTempsDTO avec les séances organisées par jour
     */
    public EmploiDuTempsDTO getPlanningHebdomadaire(LocalDate dateReference, String filtrePar, Long filtreId) {
        // Calculer le lundi et samedi de la semaine
        LocalDate lundi = dateReference.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate samedi = dateReference.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        // Créer le DTO
        EmploiDuTempsDTO emploiDuTemps = new EmploiDuTempsDTO(
                "Emploi du temps hebdomadaire",
                lundi,
                samedi,
                "HEBDOMADAIRE"
        );
        emploiDuTemps.setFiltrePar(filtrePar);
        emploiDuTemps.setFiltreId(filtreId);

        // Récupérer les séances selon le filtre
        List<Seance> seances = getSeancesAvecFiltre(lundi, samedi, filtrePar, filtreId);

        // Organiser les séances par jour
        for (Seance seance : seances) {
            SeanceDTO seanceDTO = mapToSeanceDTO(seance);
            emploiDuTemps.ajouterSeance(seance.getDateSeance(), seanceDTO);
        }

        return emploiDuTemps;
    }

    /**
     * Récupère l'emploi du temps semestriel
     *
     * @param dateDebut Date de début du semestre
     * @param dateFin Date de fin du semestre
     * @param filtrePar Type de filtre : "ENSEIGNANT", "SALLE", "EC" ou null pour tout
     * @param filtreId ID de l'entité à filtrer (peut être null)
     * @return EmploiDuTempsDTO avec les séances organisées par jour
     */
    public EmploiDuTempsDTO getPlanningSemestriel(LocalDate dateDebut, LocalDate dateFin,
                                                  String filtrePar, Long filtreId) {
        EmploiDuTempsDTO emploiDuTemps = new EmploiDuTempsDTO(
                "Emploi du temps semestriel",
                dateDebut,
                dateFin,
                "SEMESTRIEL"
        );
        emploiDuTemps.setFiltrePar(filtrePar);
        emploiDuTemps.setFiltreId(filtreId);

        // Récupérer les séances selon le filtre
        List<Seance> seances = getSeancesAvecFiltre(dateDebut, dateFin, filtrePar, filtreId);

        // Organiser les séances par jour
        for (Seance seance : seances) {
            SeanceDTO seanceDTO = mapToSeanceDTO(seance);
            emploiDuTemps.ajouterSeance(seance.getDateSeance(), seanceDTO);
        }

        return emploiDuTemps;
    }

    /**
     * Recherche des séances selon des critères multiples
     */
    public List<Seance> rechercherSeances(Long enseignantId, Long salleId, Long ecId, Long classeId,
                                          LocalDate dateDebut, LocalDate dateFin) {
        return seanceRepository.rechercherSeances(enseignantId, salleId, ecId, classeId, dateDebut, dateFin);
    }

    /**
     * Méthode privée pour récupérer les séances avec filtre
     */
    private List<Seance> getSeancesAvecFiltre(LocalDate dateDebut, LocalDate dateFin,
                                              String filtrePar, Long filtreId) {
        if (filtrePar == null || filtreId == null) {
            // Pas de filtre : toutes les séances
            return seanceRepository.findByDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                    dateDebut, dateFin);
        }

        switch (filtrePar.toUpperCase()) {
            case "ENSEIGNANT":
                return seanceRepository.findByEnseignantIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                        filtreId, dateDebut, dateFin);

            case "SALLE":
                return seanceRepository.findBySalleIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                        filtreId, dateDebut, dateFin);

            case "EC":
                return seanceRepository.findByEcIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                        filtreId, dateDebut, dateFin);

            case "CLASSE": // Ajout du filtre par classe
                return seanceRepository.findByClasseIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                        filtreId, dateDebut, dateFin);

            default:
                // Filtre non reconnu : toutes les séances
                return seanceRepository.findByDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                        dateDebut, dateFin);
        }
    }

    /**
     * Mapper Seance -> SeanceDTO
     */
    private SeanceDTO mapToSeanceDTO(Seance seance) {
        SeanceDTO dto = new SeanceDTO();
        dto.setId(seance.getId());
        dto.setDateSeance(seance.getDateSeance());
        dto.setHeureDebut(seance.getHeureDebut());
        dto.setHeureFin(seance.getHeureFin());
        dto.setDuree(seance.getDuree());
        dto.setTypeSeance(seance.getTypeSeance() != null ? seance.getTypeSeance().name() : null); // Map typeSeance
        dto.setClasseId(seance.getClasseId()); // Map classeId

        if (seance.getSalle() != null) {
            dto.setSalleId(seance.getSalle().getId());
            dto.setSalleNom(seance.getSalle().getLibelle());
        }

        dto.setEnseignantId(seance.getEnseignantId());
        dto.setEcId(seance.getEcId());

        return dto;
    }
}