import axios from 'axios';

const API_URL = "http://localhost:8080/api/maquette";

class MaquetteService {

    // --- FORMATIONS ---
    getAllFormations() { return axios.get(`${API_URL}/formations`); }
    getFormationById(id) { return axios.get(`${API_URL}/formations/${id}`); }
    createFormation(data) { return axios.post(`${API_URL}/formations`, data); }
    updateFormation(id, data) { return axios.put(`${API_URL}/formations/${id}`, data); }
    archiverFormation(id) { return axios.patch(`${API_URL}/formations/${id}/archiver`); }

    // --- FILIERES ---
    getAllFilieres() { return axios.get(`${API_URL}/filieres`); }
    getFiliereById(id) { return axios.get(`${API_URL}/filieres/${id}`); } // <--- AJOUTÉ
    createFiliere(data) { return axios.post(`${API_URL}/filieres`, data); } // <--- AJOUTÉ
    updateFiliere(id, data) { return axios.put(`${API_URL}/filieres/${id}`, data); } // <--- AJOUTÉ
    deleteFiliere(id) { return axios.delete(`${API_URL}/filieres/${id}`); }

    // --- NIVEAUX ---
    getAllNiveaux() { return axios.get(`${API_URL}/niveaux`); }
    getNiveauById(id) { return axios.get(`${API_URL}/niveaux/${id}`); } // <--- AJOUTÉ
    createNiveau(data) { return axios.post(`${API_URL}/niveaux`, data); } // <--- AJOUTÉ
    updateNiveau(id, data) { return axios.put(`${API_URL}/niveaux/${id}`, data); } // <--- AJOUTÉ
    deleteNiveau(id) { return axios.delete(`${API_URL}/niveaux/${id}`); }

    // --- MODULES ---
    getAllModules() { return axios.get(`${API_URL}/modules`); }
    deleteModule(id) { return axios.delete(`${API_URL}/modules/${id}`); }
    archiverModule(id) { return axios.patch(`${API_URL}/modules/${id}/archiver`); }

    // --- UES ---
    getAllUEs() { return axios.get(`${API_URL}/ues`); }
    archiverUE(id) { return axios.patch(`${API_URL}/ues/${id}/archiver`); }

    // --- ECS ---
    getAllECs() { return axios.get(`${API_URL}/ecs`); }
    archiverEC(id) { return axios.patch(`${API_URL}/ecs/${id}/archiver`); }
}

export default new MaquetteService();