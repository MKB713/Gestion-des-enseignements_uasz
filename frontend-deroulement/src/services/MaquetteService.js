import axios from 'axios';
import AuthService from './AuthService';

const API_URL = 'http://localhost:8095/api';

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

const MaquetteService = {
    // Modules
    getAllModules: () => {
        return axiosInstance.get('/modules');
    },

    getModuleById: (id) => {
        return axiosInstance.get(`/modules/${id}`);
    },

    // UE
    getAllUE: () => {
        return axiosInstance.get('/ue');
    },

    getUEById: (id) => {
        return axiosInstance.get(`/ue/${id}`);
    },

    // EC
    getAllEC: () => {
        return axiosInstance.get('/ec');
    },

    getECById: (id) => {
        return axiosInstance.get(`/ec/${id}`);
    },

    // Formations
    getAllFormations: () => {
        return axiosInstance.get('/formations');
    },

    getFormationById: (id) => {
        return axiosInstance.get(`/formations/${id}`);
    }
};

export default MaquetteService;
