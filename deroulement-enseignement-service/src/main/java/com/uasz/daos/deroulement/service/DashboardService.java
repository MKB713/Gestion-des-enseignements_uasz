package com.uasz.daos.deroulement.service;

import com.uasz.daos.deroulement.api.EmploiTempsApi;
import com.uasz.daos.deroulement.api.EnseignantApi;
import com.uasz.daos.deroulement.dto.DashboardStatsDTO;
import com.uasz.daos.deroulement.dto.SeanceDTO;
import com.uasz.daos.deroulement.model.NoteCahierTexte;
import com.uasz.daos.deroulement.repository.NoteCahierTexteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    // Note: Enseignant, Formation et Filiere sont dans d'autres services
    // Pour le moment, nous utilisons des données simulées

    @Autowired
    private NoteCahierTexteRepository noteRepository;

    @Autowired
    private EmploiTempsApi emploiTempsApi;

    @Autowired
    private EnseignantApi enseignantApi;

    /**
     * Calcule les statistiques du dashboard
     */
    public DashboardStatsDTO getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        List<NoteCahierTexte> notes = noteRepository.findByEstValide(true);
        
        // 1. Calcul du volume horaire global effectué (somme des durées des séances validées)
        long totalHeuresEffectuées = 0;
        Map<String, Long> volumeH = new HashMap<>(); // CM, TD, TP
        volumeH.put("CM", 0L);
        volumeH.put("TD", 0L);
        volumeH.put("TP", 0L);

        Map<Long, Long> heuresParEnseignant = new HashMap<>();

        for (NoteCahierTexte note : notes) {
            try {
                SeanceDTO seance = emploiTempsApi.getSeanceById(note.getSeanceId());
                if (seance != null) {
                    int duree = seance.getDuree();
                    totalHeuresEffectuées += duree;
                    
                    // Répartition par enseignant
                    if (note.getEnseignantId() != null) {
                        heuresParEnseignant.put(note.getEnseignantId(), 
                            heuresParEnseignant.getOrDefault(note.getEnseignantId(), 0L) + duree);
                    }
                    
                    // On pourrait aussi répartir par type (CM/TD/TP) si on avait l'info dans SeanceDTO
                    // Ici on simule une répartition basée sur la durée totale
                    volumeH.put("GLOBAL", totalHeuresEffectuées);
                }
            } catch (Exception e) {
                // Log and continue
            }
        }

        stats.setVolumeHoraireGlobal(volumeH);
        
        // 2. Répartition par Grade (Simulé ou via EnseignantApi si possible)
        Map<String, Long> parGrade = new HashMap<>();
        // En attendant une boucle sur tous les enseignants via EnseignantApi
        parGrade.put("Professeur", 5L); 
        parGrade.put("Maître de Conférences", 12L);
        stats.setRepartitionParGrade(parGrade);

        // 3. Totaux
        stats.setTotalEnseignants(heuresParEnseignant.size());
        stats.setTotalClasses(10); // Simulé
        stats.setTotalFilieres(4L); // Simulé
        stats.setTotalUes(notes.stream().map(NoteCahierTexte::getSeanceId).distinct().count());

        return stats;
    }
}
