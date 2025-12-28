import axios from 'axios';

// URL de base : via le Gateway (port 8080) pour accéder au microservice enseignant
const API_BASE_URL = 'http://localhost:8080/api/enseignants';

const EnseignantService = {
    // ==================== ENSEIGNANTS ====================

    // Récupérer tous les enseignants actifs
    getAllEnseignants() {
        return axios.get(`${API_BASE_URL}/enseignants`);
    },

    // Récupérer tous les enseignants archivés
    getEnseignantsArchives() {
        return axios.get(`${API_BASE_URL}/enseignants/archives`);
    },

    // Récupérer un enseignant par ID
    getEnseignantById(id) {
        return axios.get(`${API_BASE_URL}/enseignants/${id}`);
    },

    // Créer un nouvel enseignant
    createEnseignant(enseignant) {
        return axios.post(`${API_BASE_URL}/enseignants`, enseignant);
    },

    // Mettre à jour un enseignant
    updateEnseignant(id, enseignant) {
        return axios.put(`${API_BASE_URL}/enseignants/${id}`, enseignant);
    },

    // Archiver un enseignant
    archiver(id) {
        return axios.patch(`${API_BASE_URL}/enseignants/${id}/archiver`);
    },

    // Restaurer (désarchiver) un enseignant
    restaurer(id) {
        return axios.patch(`${API_BASE_URL}/enseignants/${id}/restaurer`);
    },

    // Activer un enseignant
    activer(id) {
        return axios.patch(`${API_BASE_URL}/enseignants/${id}/activer`);
    },

    // Désactiver un enseignant
    desactiver(id) {
        return axios.patch(`${API_BASE_URL}/enseignants/${id}/desactiver`);
    },

    // Récupérer les grades (pour le formulaire)
    getGrades() {
        return axios.get(`${API_BASE_URL}/enseignants/ref/grades`);
    },

    // Récupérer les statuts (pour le formulaire)
    getStatuts() {
        return axios.get(`${API_BASE_URL}/enseignants/ref/statuts`);
    },

    // ==================== RESPONSABLES ====================

    // Récupérer tous les responsables
    getAllResponsables() {
        return axios.get(`${API_BASE_URL}/api/responsables`);
    },

    // Récupérer un responsable par ID
    getResponsableById(id) {
        return axios.get(`${API_BASE_URL}/api/responsables/${id}`);
    },

    // Créer un responsable
    createResponsable(responsable) {
        return axios.post(`${API_BASE_URL}/api/responsables`, responsable);
    },

    // Mettre à jour un responsable
    updateResponsable(id, responsable) {
        return axios.put(`${API_BASE_URL}/api/responsables/${id}`, responsable);
    },

    // Désactiver un responsable
    desactiverResponsable(id) {
        return axios.patch(`${API_BASE_URL}/api/responsables/${id}/desactiver`);
    },

    // Réactiver un responsable
    reactiverResponsable(id) {
        return axios.patch(`${API_BASE_URL}/api/responsables/${id}/reactiver`);
    },

    // Supprimer un responsable
    deleteResponsable(id) {
        return axios.delete(`${API_BASE_URL}/api/responsables/${id}`);
    },

    // Rechercher des responsables
    rechercherResponsables(term) {
        return axios.get(`${API_BASE_URL}/api/responsables/search?term=${term}`);
    },

    // Récupérer les responsables actifs
    getResponsablesActifs() {
        return axios.get(`${API_BASE_URL}/api/responsables/actifs`);
    },

    // ==================== COORDINATEURS ====================

    // Récupérer tous les coordinateurs
    getAllCoordinateurs() {
        return axios.get(`${API_BASE_URL}/api/coordinateurs`);
    },

    // Récupérer un coordinateur par ID
    getCoordinateurById(id) {
        return axios.get(`${API_BASE_URL}/api/coordinateurs/${id}`);
    },

    // Créer un coordinateur
    createCoordinateur(coordinateur) {
        return axios.post(`${API_BASE_URL}/api/coordinateurs`, coordinateur);
    },

    // Mettre à jour un coordinateur
    updateCoordinateur(id, coordinateur) {
        return axios.put(`${API_BASE_URL}/api/coordinateurs/${id}`, coordinateur);
    },

    // Désactiver un coordinateur
    desactiverCoordinateur(id) {
        return axios.patch(`${API_BASE_URL}/api/coordinateurs/${id}/desactiver`);
    },

    // Réactiver un coordinateur
    reactiverCoordinateur(id) {
        return axios.patch(`${API_BASE_URL}/api/coordinateurs/${id}/reactiver`);
    },

    // Supprimer un coordinateur
    deleteCoordinateur(id) {
        return axios.delete(`${API_BASE_URL}/api/coordinateurs/${id}`);
    },

    // Rechercher des coordinateurs
    rechercherCoordinateurs(term) {
        return axios.get(`${API_BASE_URL}/api/coordinateurs/search?term=${term}`);
    },

    // Récupérer les coordinateurs actifs
    getCoordinateursActifs() {
        return axios.get(`${API_BASE_URL}/api/coordinateurs/actifs`);
    }
};

export default EnseignantService;