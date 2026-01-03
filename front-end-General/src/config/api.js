// API Configuration for all microservices
import authService from '../services/authService';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// Microservices Endpoints (via API Gateway)
export const API_ENDPOINTS = {
  // Auth Service (Port: 8081)
  AUTH: {
    BASE: `${API_BASE_URL}/auth-service`,
    LOGIN: `${API_BASE_URL}/auth-service/api/auth/login`,
    REGISTER: `${API_BASE_URL}/auth-service/api/auth/register`,
    LOGOUT: `${API_BASE_URL}/auth-service/api/auth/logout`,
    REFRESH: `${API_BASE_URL}/auth-service/api/auth/refresh`,
  },

  // Enseignant Service (Port: 8082)
  ENSEIGNANTS: {
    BASE: `${API_BASE_URL}/enseignant-service`,
    LIST: `${API_BASE_URL}/enseignant-service/api/enseignants`,
    BY_ID: (id) => `${API_BASE_URL}/enseignant-service/api/enseignants/${id}`,
    CREATE: `${API_BASE_URL}/enseignant-service/api/enseignants`,
    UPDATE: (id) => `${API_BASE_URL}/enseignant-service/api/enseignants/${id}`,
    DELETE: (id) => `${API_BASE_URL}/enseignant-service/api/enseignants/${id}`,
  },

  // Maquette Service (Port: 8083)
  MAQUETTES: {
    BASE: `${API_BASE_URL}/maquette-service`,
    LIST: `${API_BASE_URL}/maquette-service/api/maquette/maquettes`,
    BY_ID: (id) => `${API_BASE_URL}/maquette-service/api/maquette/maquettes/${id}`,
    CREATE: `${API_BASE_URL}/maquette-service/api/maquette/maquettes`,
    UPDATE: (id) => `${API_BASE_URL}/maquette-service/api/maquette/maquettes/${id}`,
    DELETE: (id) => `${API_BASE_URL}/maquette-service/api/maquette/maquettes/${id}`,

    // Formations
    FORMATIONS: `${API_BASE_URL}/maquette-service/api/maquette/formations`,
    FORMATION_BY_ID: (id) => `${API_BASE_URL}/maquette-service/api/maquette/formations/${id}`,

    // Filières
    FILIERES: `${API_BASE_URL}/maquette-service/api/maquette/filieres`,
    FILIERE_BY_ID: (id) => `${API_BASE_URL}/maquette-service/api/maquette/filieres/${id}`,

    // Niveaux
    NIVEAUX: `${API_BASE_URL}/maquette-service/api/maquette/niveaux`,
    NIVEAU_BY_ID: (id) => `${API_BASE_URL}/maquette-service/api/maquette/niveaux/${id}`,

    // Modules
    MODULES: `${API_BASE_URL}/maquette-service/api/maquette/modules`,
    MODULE_BY_ID: (id) => `${API_BASE_URL}/maquette-service/api/maquette/modules/${id}`,

    // Unités d'Enseignement (UE)
    UES: `${API_BASE_URL}/maquette-service/api/maquette/ues`,
    UE_BY_ID: (id) => `${API_BASE_URL}/maquette-service/api/maquette/ues/${id}`,

    // Éléments Constitutifs (EC)
    ECS: `${API_BASE_URL}/maquette-service/api/maquette/ecs`,
    EC_BY_ID: (id) => `${API_BASE_URL}/maquette-service/api/maquette/ecs/${id}`,

    // Classes
    CLASSES: `${API_BASE_URL}/maquette-service/api/maquette/classes`,
    CLASSE_BY_ID: (id) => `${API_BASE_URL}/maquette-service/api/maquette/classes/${id}`,

    // Structures (Départements, UFR, etc.)
    STRUCTURES: `${API_BASE_URL}/maquette-service/api/maquette/structures`,
    STRUCTURE_BY_ID: (id) => `${API_BASE_URL}/maquette-service/api/maquette/structures/${id}`,
  },

  // Choix Enseignement Service (Port: 8084)
  CHOIX_ENSEIGNEMENT: {
    BASE: `${API_BASE_URL}/choix-enseignement-service`,
    LIST: `${API_BASE_URL}/choix-enseignement-service/api/choix`,
    BY_ID: (id) => `${API_BASE_URL}/choix-enseignement-service/api/choix/${id}`,
    CREATE: `${API_BASE_URL}/choix-enseignement-service/api/choix`,
    UPDATE: (id) => `${API_BASE_URL}/choix-enseignement-service/api/choix/${id}`,
    DELETE: (id) => `${API_BASE_URL}/choix-enseignement-service/api/choix/${id}`,
  },

  // Emploi du Temps Service (Port: 8085)
  EMPLOI_TEMPS: {
    BASE: `${API_BASE_URL}/emploi-temps-service`,
    LIST: `${API_BASE_URL}/emploi-temps-service/api/emploi-temps`,
    BY_ID: (id) => `${API_BASE_URL}/emploi-temps-service/api/emploi-temps/${id}`,
    BY_CLASSE: (classeId) => `${API_BASE_URL}/emploi-temps-service/api/emploi-temps/classe/${classeId}`,
    BY_ENSEIGNANT: (enseignantId) => `${API_BASE_URL}/emploi-temps-service/api/emploi-temps/enseignant/${enseignantId}`,
    CREATE: `${API_BASE_URL}/emploi-temps-service/api/emploi-temps`,
    UPDATE: (id) => `${API_BASE_URL}/emploi-temps-service/api/emploi-temps/${id}`,
    DELETE: (id) => `${API_BASE_URL}/emploi-temps-service/api/emploi-temps/${id}`,
  },

  // Déroulement Enseignement Service (Port: 8086) - Cahier de texte
  DEROULEMENT: {
    BASE: `${API_BASE_URL}/deroulement-enseignement-service`,
    LIST: `${API_BASE_URL}/deroulement-enseignement-service/api/deroulement`,
    BY_ID: (id) => `${API_BASE_URL}/deroulement-enseignement-service/api/deroulement/${id}`,

    // Cahier de texte
    CAHIER_TEXTE: `${API_BASE_URL}/deroulement-enseignement-service/api/cahier-texte`,
    CAHIER_TEXTE_BY_ID: (id) => `${API_BASE_URL}/deroulement-enseignement-service/api/cahier-texte/${id}`,
    CAHIER_TEXTE_BY_CLASSE: (classeId) => `${API_BASE_URL}/deroulement-enseignement-service/api/cahier-texte/classe/${classeId}`,
    CREATE_CAHIER_TEXTE: `${API_BASE_URL}/deroulement-enseignement-service/api/cahier-texte`,
    UPDATE_CAHIER_TEXTE: (id) => `${API_BASE_URL}/deroulement-enseignement-service/api/cahier-texte/${id}`,
    DELETE_CAHIER_TEXTE: (id) => `${API_BASE_URL}/deroulement-enseignement-service/api/cahier-texte/${id}`,
  },
};

// Track if token refresh is in progress to avoid multiple simultaneous refreshes
let isRefreshing = false;
let refreshSubscribers = [];

const subscribeTokenRefresh = (callback) => {
  refreshSubscribers.push(callback);
};

const onTokenRefreshed = (token) => {
  refreshSubscribers.forEach(callback => callback(token));
  refreshSubscribers = [];
};

// HTTP Request Helper with Authentication and Token Refresh
export const apiRequest = async (url, options = {}) => {
  const token = authService.getAccessToken();

  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  try {
    const response = await fetch(url, {
      ...options,
      headers,
    });

    // Handle 401 Unauthorized - try to refresh token
    if (response.status === 401) {
      if (!isRefreshing) {
        isRefreshing = true;
        try {
          const newToken = await authService.refreshToken();
          isRefreshing = false;
          onTokenRefreshed(newToken);

          // Retry original request with new token
          headers['Authorization'] = `Bearer ${newToken}`;
          const retryResponse = await fetch(url, { ...options, headers });

          if (!retryResponse.ok && retryResponse.status !== 401) {
            const error = await retryResponse.json().catch(() => ({ message: 'Une erreur est survenue' }));
            throw new Error(error.message || `Erreur HTTP: ${retryResponse.status}`);
          }

          return retryResponse.status === 204 ? null : await retryResponse.json();
        } catch (refreshError) {
          isRefreshing = false;
          // Refresh failed, redirect to login
          authService.clearAuth();
          window.location.href = '/login';
          throw new Error('Session expirée. Veuillez vous reconnecter.');
        }
      } else {
        // Wait for token refresh to complete
        return new Promise((resolve, reject) => {
          subscribeTokenRefresh(async (newToken) => {
            try {
              headers['Authorization'] = `Bearer ${newToken}`;
              const retryResponse = await fetch(url, { ...options, headers });

              if (!retryResponse.ok) {
                const error = await retryResponse.json().catch(() => ({ message: 'Une erreur est survenue' }));
                reject(new Error(error.message || `Erreur HTTP: ${retryResponse.status}`));
              }

              resolve(retryResponse.status === 204 ? null : await retryResponse.json());
            } catch (error) {
              reject(error);
            }
          });
        });
      }
    }

    if (!response.ok) {
      const error = await response.json().catch(() => ({ message: 'Une erreur est survenue' }));
      throw new Error(error.message || `Erreur HTTP: ${response.status}`);
    }

    // Handle 204 No Content
    return response.status === 204 ? null : await response.json();
  } catch (error) {
    console.error('API Request Error:', error);
    throw error;
  }
};

// Convenience methods
export const api = {
  get: (url, options = {}) => apiRequest(url, { ...options, method: 'GET' }),
  post: (url, data, options = {}) => apiRequest(url, {
    ...options,
    method: 'POST',
    body: JSON.stringify(data)
  }),
  put: (url, data, options = {}) => apiRequest(url, {
    ...options,
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (url, options = {}) => apiRequest(url, { ...options, method: 'DELETE' }),
};

export default API_ENDPOINTS;
