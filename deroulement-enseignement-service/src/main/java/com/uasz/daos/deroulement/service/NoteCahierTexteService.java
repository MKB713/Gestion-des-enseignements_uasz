package com.uasz.daos.deroulement.service;

import com.uasz.daos.deroulement.dto.NoteCahierTexteDTO;
import com.uasz.daos.deroulement.model.HistoriqueModificationNote;
import com.uasz.daos.deroulement.model.NoteCahierTexte;
import com.uasz.daos.deroulement.repository.HistoriqueModificationNoteRepository;
import com.uasz.daos.deroulement.repository.NoteCahierTexteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteCahierTexteService {

    @Autowired
    private NoteCahierTexteRepository noteCahierTexteRepository;

    @Autowired
    private HistoriqueModificationNoteRepository historiqueRepository;

    // Note: Seance et Enseignant sont dans d'autres services
    // Nous utilisons uniquement les IDs

    /**
     * Récupère toutes les notes du cahier de texte
     */
    public List<NoteCahierTexte> getAllNotes() {
        return noteCahierTexteRepository.findAll();
    }

    /**
     * Récupère une note par son ID
     */
    public NoteCahierTexte getNoteById(Long id) {
        return noteCahierTexteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note non trouvée avec l'ID : " + id));
    }

    /**
     * Ajoute une nouvelle note au cahier de texte
     */
    @Transactional
    public NoteCahierTexte ajouterNote(NoteCahierTexteDTO noteDTO) {
        // Créer une nouvelle note
        NoteCahierTexte note = new NoteCahierTexte();
        note.setTitre(noteDTO.getTitre());
        note.setContenu(noteDTO.getContenu());
        note.setSeanceId(noteDTO.getSeanceId());
        note.setObjectifsPedagogiques(noteDTO.getObjectifsPedagogiques());
        note.setActivitesRealisees(noteDTO.getActivitesRealisees());
        note.setTravailDemande(noteDTO.getTravailDemande());
        note.setObservations(noteDTO.getObservations());

        // Associer l'enseignant si fourni (utilisation de l'ID uniquement)
        if (noteDTO.getEnseignantId() != null) {
            note.setEnseignantId(noteDTO.getEnseignantId());
        }

        // Initialiser les valeurs par défaut
        note.setEstValide(false);
        note.setDateCreation(LocalDateTime.now());
        note.setDateModification(LocalDateTime.now());

        NoteCahierTexte noteSauvegardee = noteCahierTexteRepository.save(note);

        // Enregistrer la création dans l'historique
        enregistrerHistorique(noteSauvegardee, "CREATION", "note", "", "Note créée", noteDTO.getEnseignantId());

        return noteSauvegardee;
    }

    /**
     * Modifie une note existante
     * CRITERE : Seules les notes non validées peuvent être modifiées
     */
    @Transactional
    public NoteCahierTexte modifierNote(Long id, NoteCahierTexteDTO noteDTO) {
        NoteCahierTexte note = getNoteById(id);

        // VERIFICATION : Seules les notes non validées peuvent être modifiées
        if (note.isEstValide()) {
            throw new IllegalStateException("Cette note a été validée et ne peut plus être modifiée.");
        }

        // Enregistrer l'historique pour chaque modification
        // Modification du titre
        if (noteDTO.getTitre() != null && !noteDTO.getTitre().trim().isEmpty() && !noteDTO.getTitre().equals(note.getTitre())) {
            enregistrerHistorique(note, "MODIFICATION", "titre", note.getTitre(), noteDTO.getTitre(), noteDTO.getEnseignantId());
            note.setTitre(noteDTO.getTitre());
        }

        // Modification du contenu
        if (noteDTO.getContenu() != null && !noteDTO.getContenu().trim().isEmpty() && !noteDTO.getContenu().equals(note.getContenu())) {
            enregistrerHistorique(note, "MODIFICATION", "contenu", note.getContenu(), noteDTO.getContenu(), noteDTO.getEnseignantId());
            note.setContenu(noteDTO.getContenu());
        }

        // Modification de la séance (utilisation de l'ID uniquement)
        if (noteDTO.getSeanceId() != null && !noteDTO.getSeanceId().equals(note.getSeanceId())) {
            String ancienneSeanceId = note.getSeanceId() != null ? note.getSeanceId().toString() : "Aucune";
            enregistrerHistorique(note, "MODIFICATION", "seance", ancienneSeanceId, noteDTO.getSeanceId().toString(), noteDTO.getEnseignantId());
            note.setSeanceId(noteDTO.getSeanceId());
        }

        // Modification des objectifs pédagogiques
        if (noteDTO.getObjectifsPedagogiques() != null && !noteDTO.getObjectifsPedagogiques().equals(note.getObjectifsPedagogiques())) {
            enregistrerHistorique(note, "MODIFICATION", "objectifsPedagogiques", note.getObjectifsPedagogiques(), noteDTO.getObjectifsPedagogiques(), noteDTO.getEnseignantId());
        }
        note.setObjectifsPedagogiques(noteDTO.getObjectifsPedagogiques());

        // Modification des activités réalisées
        if (noteDTO.getActivitesRealisees() != null && !noteDTO.getActivitesRealisees().equals(note.getActivitesRealisees())) {
            enregistrerHistorique(note, "MODIFICATION", "activitesRealisees", note.getActivitesRealisees(), noteDTO.getActivitesRealisees(), noteDTO.getEnseignantId());
        }
        note.setActivitesRealisees(noteDTO.getActivitesRealisees());

        // Modification du travail demandé
        if (noteDTO.getTravailDemande() != null && !noteDTO.getTravailDemande().equals(note.getTravailDemande())) {
            enregistrerHistorique(note, "MODIFICATION", "travailDemande", note.getTravailDemande(), noteDTO.getTravailDemande(), noteDTO.getEnseignantId());
        }
        note.setTravailDemande(noteDTO.getTravailDemande());

        // Modification des observations
        if (noteDTO.getObservations() != null && !noteDTO.getObservations().equals(note.getObservations())) {
            enregistrerHistorique(note, "MODIFICATION", "observations", note.getObservations(), noteDTO.getObservations(), noteDTO.getEnseignantId());
        }
        note.setObservations(noteDTO.getObservations());

        note.setDateModification(LocalDateTime.now());

        return noteCahierTexteRepository.save(note);
    }

    /**
     * Enregistre une modification dans l'historique
     */
    private void enregistrerHistorique(NoteCahierTexte note, String typeModification, String champModifie,
                                      String ancienneValeur, String nouvelleValeur, Long enseignantId) {
        HistoriqueModificationNote historique = new HistoriqueModificationNote();
        historique.setNote(note);
        historique.setTypeModification(typeModification);
        historique.setChampModifie(champModifie);
        historique.setAncienneValeur(ancienneValeur != null ? ancienneValeur : "");
        historique.setNouvelleValeur(nouvelleValeur != null ? nouvelleValeur : "");
        historique.setDateModification(LocalDateTime.now());

        // Utilisation de l'ID uniquement pour l'enseignant
        if (enseignantId != null) {
            historique.setEnseignantModificateurId(enseignantId);
        }

        historiqueRepository.save(historique);
    }

    /**
     * Valide une note
     */
    @Transactional
    public NoteCahierTexte validerNote(Long id) {
        NoteCahierTexte note = getNoteById(id);
        note.setEstValide(true);
        note.setDateModification(LocalDateTime.now());

        NoteCahierTexte noteValidee = noteCahierTexteRepository.save(note);

        // Enregistrer la validation dans l'historique
        enregistrerHistorique(noteValidee, "VALIDATION", "statut", "Non validée", "Validée", null);

        return noteValidee;
    }

    /**
     * Récupère les notes par séance
     */
    public List<NoteCahierTexte> getNotesBySeance(Long seanceId) {
        return noteCahierTexteRepository.findBySeanceId(seanceId);
    }

    /**
     * Récupère les notes par enseignant
     */
    public List<NoteCahierTexte> getNotesByEnseignant(Long enseignantId) {
        return noteCahierTexteRepository.findByEnseignantId(enseignantId);
    }

    /**
     * Récupère les notes par classe
     * Note: Seance n'a pas de relation avec Classe, donc retourne une liste vide pour l'instant
     */
    public List<NoteCahierTexte> getNotesByClasse(Long classeId) {
        // TODO: Implémenter la logique correcte si Seance doit avoir une relation avec Classe
        return new java.util.ArrayList<>();
    }

    /**
     * Supprime une note
     */
    @Transactional
    public void supprimerNote(Long id) {
        NoteCahierTexte note = getNoteById(id);
        noteCahierTexteRepository.delete(note);
    }

    /**
     * Récupère l'historique des modifications d'une note
     */
    public List<HistoriqueModificationNote> getHistoriqueNote(Long noteId) {
        return historiqueRepository.findByNoteIdOrderByDateModificationDesc(noteId);
    }

    /**
     * Vérifie si une note peut être modifiée (non validée)
     */
    public boolean peutEtreModifiee(Long noteId) {
        NoteCahierTexte note = getNoteById(noteId);
        return !note.isEstValide();
    }

    /**
     * Récupère toutes les notes triées par date et EC
     */
    public List<NoteCahierTexte> getAllNotesTriees() {
        return noteCahierTexteRepository.findAllOrderByDateAndEC();
    }

    /**
     * Consulter le cahier de texte avec filtres
     */
    public List<NoteCahierTexte> consulterCahierTexte(Long enseignantId, Long seanceId) {
        return noteCahierTexteRepository.findWithFilters(enseignantId, seanceId);
    }
}
