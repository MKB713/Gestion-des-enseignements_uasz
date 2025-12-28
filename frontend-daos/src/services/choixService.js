// src/services/choixService.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8084/api/choix';

// Configuration axios avec intercepteurs
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Intercepteur pour ajouter le token/headers si nécessaire
api.interceptors.request.use(
    (config) => {
        // Ajouter l'ID enseignant depuis le localStorage pour les opérations qui en ont besoin
        const enseignantId = localStorage.getItem('enseignantId') || '1'; // Valeur par défaut pour les tests
        if (config.method !== 'get' && config.url.match(/\/\d+$/)) {
            config.headers['X-Enseignant-Id'] = enseignantId;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

const choixService = {
    /**
     * Récupérer tous les choix avec pagination
     */
    getAllChoix: async (page = 0, size = 10, sortBy = 'dateCreation', sortDirection = 'DESC') => {
        try {
            const response = await api.get('', {
                params: { page, size, sortBy, sortDirection }
            });
            return response.data;
        } catch (error) {
            console.error('Error fetching all choix:', error);
            throw error.response?.data || error.message;
        }
    },

    /**
     * Récupérer un choix par ID
     */
    getChoixById: async (id) => {
        try {
            const response = await api.get(`/${id}`);
            return response.data;
        } catch (error) {
            console.error(`Error fetching choix ${id}:`, error);
            throw error.response?.data || error.message;
        }
    },

    /**
     * Récupérer les choix d'un enseignant
     */
    getChoixByEnseignant: async (idEnseignant) => {
        try {
            const response = await api.get(`/enseignant/${idEnseignant}`);
            return response.data;
        } catch (error) {
            console.error(`Error fetching choix for enseignant ${idEnseignant}:`, error);
            throw error.response?.data || error.message;
        }
    },

    /**
     * Créer un nouveau choix
     */
    createChoix: async (choixData) => {
        try {
            const response = await api.post('', choixData);
            return response.data;
        } catch (error) {
            console.error('Error creating choix:', error);
            throw error.response?.data || error.message;
        }
    },

    /**
     * Modifier un choix
     */
    updateChoix: async (id, choixData) => {
        try {
            const response = await api.put(`/${id}`, choixData);
            return response.data;
        } catch (error) {
            console.error(`Error updating choix ${id}:`, error);
            throw error.response?.data || error.message;
        }
    },

    /**
     * Supprimer un choix
     */
    deleteChoix: async (id) => {
        try {
            await api.delete(`/${id}`);
            return true;
        } catch (error) {
            console.error(`Error deleting choix ${id}:`, error);
            throw error.response?.data || error.message;
        }
    },

    /**
     * Recherche avec filtres avancés
     */
    searchChoix: async (filters) => {
        try {
            const response = await api.get('', { params: filters });
            return response.data;
        } catch (error) {
            console.error('Error searching choix:', error);
            throw error.response?.data || error.message;
        }
    },

    /**
     * Health check
     */
    healthCheck: async () => {
        try {
            const response = await api.get('/health');
            return response.data;
        } catch (error) {
            console.error('Health check failed:', error);
            throw error.response?.data || error.message;
        }
    }
};

export default choixService;