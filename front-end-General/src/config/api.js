// API Configuration for all microservices (via API Gateway)
import authService from '../services/authService';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// =======================
// API ENDPOINTS (GATEWAY)
// =======================
export const API_ENDPOINTS = {

  // =====================
  // AUTH SERVICE
  // Direct: http://localhost:8081/api/auth/**
  // Gateway: /api/auth/**
  // =====================
  AUTH: {
    BASE: 'http://localhost:8081/api/auth',
    LOGIN: 'http://localhost:8081/api/auth/login',
    REGISTER: 'http://localhost:8081/api/auth/register',
    LOGOUT: 'http://localhost:8081/api/auth/logout',
    REFRESH: 'http://localhost:8081/api/auth/refresh',
    VALIDATE: 'http://localhost:8081/api/auth/validate',
  },

  // =====================
  // USERS SERVICE (Auth Service)
  // Direct: http://localhost:8081/api/users/**
  // =====================
  USERS: {
    base: 'http://localhost:8081/api/users',
    LIST: 'http://localhost:8081/api/users',
    SEARCH: 'http://localhost:8081/api/users/search',
    BY_ID: (id) => `http://localhost:8081/api/users/${id}`,
    BY_ROLE: (role) => `http://localhost:8081/api/users/role/${role}`,
  },

  // =========================
  // ENSEIGNANT SERVICE
  // Gateway: /api/enseignants/**
  // =========================
  ENSEIGNANTS: {
    LIST: `${API_BASE_URL}/api/enseignants`,
    BY_ID: (id) => `${API_BASE_URL}/api/enseignants/${id}`,
    CREATE: `${API_BASE_URL}/api/enseignants`,
    UPDATE: (id) => `${API_BASE_URL}/api/enseignants/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/enseignants/${id}`,
  },

  // =====================
  // MAQUETTE SERVICE
  // Gateway: /api/maquettes/**
  // =====================
  MAQUETTES: {
    LIST: `${API_BASE_URL}/api/maquettes`,
    BY_ID: (id) => `${API_BASE_URL}/api/maquettes/${id}`,
    CREATE: `${API_BASE_URL}/api/maquettes`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquettes/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquettes/${id}`,
  },

  // ===============================
  // CHOIX ENSEIGNEMENT SERVICE
  // Gateway: /api/choix-enseignements/**
  // ===============================
  CHOIX_ENSEIGNEMENT: {
    LIST: `${API_BASE_URL}/api/choix-enseignements`,
    BY_ID: (id) => `${API_BASE_URL}/api/choix-enseignements/${id}`,
    CREATE: `${API_BASE_URL}/api/choix-enseignements`,
    UPDATE: (id) => `${API_BASE_URL}/api/choix-enseignements/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/choix-enseignements/${id}`,
  },

  // =========================
  // EMPLOI DU TEMPS SERVICE
  // Gateway: /api/emploi-temps/**
  // =========================
  EMPLOI_TEMPS: {
    LIST: `${API_BASE_URL}/api/emploi-temps`,
    BY_ID: (id) => `${API_BASE_URL}/api/emploi-temps/${id}`,
    CREATE: `${API_BASE_URL}/api/emploi-temps`,
    UPDATE: (id) => `${API_BASE_URL}/api/emploi-temps/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/emploi-temps/${id}`,
  },

  // ==================================
  // DEROULEMENT ENSEIGNEMENT SERVICE
  // Gateway: /api/deroulement-enseignements/**
  // ==================================
  DEROULEMENT: {
    LIST: `${API_BASE_URL}/api/deroulement-enseignements`,
    BY_ID: (id) => `${API_BASE_URL}/api/deroulement-enseignements/${id}`,
    CREATE: `${API_BASE_URL}/api/deroulement-enseignements`,
    UPDATE: (id) => `${API_BASE_URL}/api/deroulement-enseignements/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/deroulement-enseignements/${id}`,
  },
};

// ==================================================
// TOKEN REFRESH MANAGEMENT
// ==================================================
let isRefreshing = false;
let refreshSubscribers = [];

const subscribeTokenRefresh = (callback) => {
  refreshSubscribers.push(callback);
};

const onTokenRefreshed = (token) => {
  refreshSubscribers.forEach(callback => callback(token));
  refreshSubscribers = [];
};

// ==================================================
// HTTP REQUEST HELPER
// ==================================================
export const apiRequest = async (url, options = {}) => {
  const token = authService.getAccessToken();

  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(url, { ...options, headers });

  if (response.status === 401) {
    if (!isRefreshing) {
      isRefreshing = true;
      try {
        const newToken = await authService.refreshToken();
        isRefreshing = false;
        onTokenRefreshed(newToken);

        headers.Authorization = `Bearer ${newToken}`;
        const retryResponse = await fetch(url, { ...options, headers });
        return retryResponse.status === 204 ? null : await retryResponse.json();
      } catch {
        isRefreshing = false;
        authService.clearAuth();
        window.location.href = '/login';
        throw new Error('Session expirée');
      }
    }

    return new Promise((resolve, reject) => {
      subscribeTokenRefresh(async (newToken) => {
        try {
          headers.Authorization = `Bearer ${newToken}`;
          const retryResponse = await fetch(url, { ...options, headers });
          resolve(retryResponse.status === 204 ? null : await retryResponse.json());
        } catch (e) {
          reject(e);
        }
      });
    });
  }

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Erreur inconnue' }));
    throw new Error(error.message);
  }

  return response.status === 204 ? null : await response.json();
};

// =======================
// SHORTCUT METHODS
// =======================
export const api = {
  get: (url, options = {}) => apiRequest(url, { ...options, method: 'GET' }),
  post: (url, data, options = {}) =>
    apiRequest(url, { ...options, method: 'POST', body: JSON.stringify(data) }),
  put: (url, data, options = {}) =>
    apiRequest(url, { ...options, method: 'PUT', body: JSON.stringify(data) }),
  delete: (url, options = {}) =>
    apiRequest(url, { ...options, method: 'DELETE' }),
};

export default API_ENDPOINTS;
