package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.Batiment;
import com.uasz.daos.emploitemps.model.Salle;
import com.uasz.daos.emploitemps.repository.BatimentRepository;
import com.uasz.daos.emploitemps.repository.SalleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SalleService {

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private BatimentRepository batimentRepository;

    /**
     * Récupère toutes les salles
     */
    public List<Salle> getAllSalles() {
        return salleRepository.findAll();
    }

    /**
     * Récupère une salle par ID
     */
    public Salle getSalleById(Long id) {
        return salleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'id: " + id));
    }

    /**
     * Crée une nouvelle salle
     */
    @Transactional
    public Salle createSalle(Salle salle) {
        // Vérifier que le bâtiment existe
        if (salle.getBatiment() != null && salle.getBatiment().getId() != null) {
            Batiment batiment = batimentRepository.findById(salle.getBatiment().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Bâtiment non trouvé"));
            salle.setBatiment(batiment);
        }
        return salleRepository.save(salle);
    }

    /**
     * Met à jour une salle
     */
    @Transactional
    public Salle updateSalle(Long id, Salle salleDetails) {
        Salle existingSalle = getSalleById(id);

        existingSalle.setLibelle(salleDetails.getLibelle());
        existingSalle.setCapacite(salleDetails.getCapacite());
        existingSalle.setDescription(salleDetails.getDescription());

        // Mettre à jour le bâtiment si fourni
        if (salleDetails.getBatiment() != null && salleDetails.getBatiment().getId() != null) {
            Batiment batiment = batimentRepository.findById(salleDetails.getBatiment().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Bâtiment non trouvé"));
            existingSalle.setBatiment(batiment);
        }

        return salleRepository.save(existingSalle);
    }

    /**
     * Supprime une salle
     */
    @Transactional
    public void deleteSalle(Long id) {
        if (!salleRepository.existsById(id)) {
            throw new EntityNotFoundException("Salle non trouvée avec l'id: " + id);
        }
        salleRepository.deleteById(id);
    }

    /**
     * Récupère les salles d'un bâtiment
     */
    public List<Salle> getSallesByBatiment(Long batimentId) {
        return salleRepository.findByBatimentIdOrderByLibelleAsc(batimentId);
    }

    /**
     * Recherche des salles par libellé
     */
    public List<Salle> searchSalles(String libelle) {
        return salleRepository.findByLibelleContainingIgnoreCase(libelle);
    }

    /**
     * Récupère les salles avec une capacité minimale
     */
    public List<Salle> getSallesByCapaciteMin(int capaciteMin) {
        return salleRepository.findByCapaciteGreaterThanEqualOrderByCapaciteAsc(capaciteMin);
    }

    // ========== GESTION DES DISPONIBILITÉS (USER STORY) ==========

    /**
     * Récupère les salles disponibles pour un créneau donné
     * User Story: "Récupérer les plannings des salles"
     *
     * @param date Date de la séance
     * @param heureDebut Heure de début
     * @param heureFin Heure de fin
     * @return Liste des salles disponibles
     */
    @Cacheable(value = "sallesDisponibles", key = "#date + '-' + #heureDebut + '-' + #heureFin")
    public List<Salle> getSallesDisponibles(LocalDate date, LocalTime heureDebut, LocalTime heureFin) {
        return salleRepository.findSallesDisponibles(date, heureDebut, heureFin);
    }

    /**
     * Récupère les salles disponibles avec capacité minimale
     *
     * @param date Date de la séance
     * @param heureDebut Heure de début
     * @param heureFin Heure de fin
     * @param capaciteMin Capacité minimale requise
     * @return Liste des salles disponibles avec la capacité requise
     */
    @Cacheable(value = "sallesDisponiblesCapacite",
            key = "#date + '-' + #heureDebut + '-' + #heureFin + '-' + #capaciteMin")
    public List<Salle> getSallesDisponiblesAvecCapacite(LocalDate date, LocalTime heureDebut,
                                                        LocalTime heureFin, int capaciteMin) {
        return salleRepository.findSallesDisponiblesAvecCapacite(date, heureDebut, heureFin, capaciteMin);
    }

    /**
     * Vérifie si une salle est disponible pour un créneau donné
     *
     * @param salleId ID de la salle
     * @param date Date de la séance
     * @param heureDebut Heure de début
     * @param heureFin Heure de fin
     * @return true si la salle est disponible, false sinon
     */
    public boolean isSalleDisponible(Long salleId, LocalDate date, LocalTime heureDebut, LocalTime heureFin) {
        List<Salle> sallesDisponibles = getSallesDisponibles(date, heureDebut, heureFin);
        return sallesDisponibles.stream().anyMatch(salle -> salle.getId().equals(salleId));
    }

    /**
     * Récupère les disponibilités d'une salle pour une journée complète
     * Retourne les créneaux occupés
     *
     * @param salleId ID de la salle
     * @param date Date concernée
     * @return Liste des séances occupant la salle ce jour-là
     */
    public List<Object> getDisponibilitesSalleJournee(Long salleId, LocalDate date) {
        // TODO: Implémenter la logique pour récupérer les créneaux occupés
        // Retourner une structure avec les créneaux libres et occupés
        throw new UnsupportedOperationException("À implémenter");
    }
}