import axios from 'axios';
import AuthService from './AuthService';

const API_URL = 'http://localhost:8090/api/deroulement-enseignements';

const axiosInstance = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Ajouter le token à chaque requête
axiosInstance.interceptors.request.use((config) => {
    const token = AuthService.getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

const DeroulementService = {
    // Séances
    getAllSeances: () => {
        return axiosInstance.get('/seances');
    },

    getSeanceById: (id) => {
        return axiosInstance.get(`/seances/${id}`);
    },

    createSeance: (seance) => {
        return axiosInstance.post('/seances', seance);
    },

    updateSeance: (id, seance) => {
        return axiosInstance.put(`/seances/${id}`, seance);
    },

    deleteSeance: (id) => {
        return axiosInstance.delete(`/seances/${id}`);
    },

    // Progression
    getProgressionByModule: (moduleId) => {
        return axiosInstance.get(`/progression/module/${moduleId}`);
    },

    getProgressionByEC: (ecId) => {
        return axiosInstance.get(`/progression/ec/${ecId}`);
    },

    getProgressionByUE: (ueId) => {
        return axiosInstance.get(`/progression/ue/${ueId}`);
    },

    updateProgression: (id, progression) => {
        return axiosInstance.put(`/progression/${id}`, progression);
    },

    // Statistiques
    getStatistiques: () => {
        return axiosInstance.get('/statistiques');
    },

    getStatistiquesParEnseignant: (enseignantId) => {
        return axiosInstance.get(`/statistiques/enseignant/${enseignantId}`);
    },

    getStatistiquesParModule: (moduleId) => {
        return axiosInstance.get(`/statistiques/module/${moduleId}`);
    }
};

export default DeroulementService;
