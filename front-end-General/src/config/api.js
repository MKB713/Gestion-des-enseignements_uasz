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
    base: `${API_BASE_URL}/api/maquette/maquettes`,
    LIST: `${API_BASE_URL}/api/maquette/maquettes`,
    BY_ID: (id) => `${API_BASE_URL}/api/maquette/maquettes/${id}`,
    CREATE: `${API_BASE_URL}/api/maquette/maquettes`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquette/maquettes/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquette/maquettes/${id}`,
    PUBLISH: (id) => `${API_BASE_URL}/api/maquette/maquettes/${id}/publier`,
  },

  // =====================
  // CLASSE SERVICE (Maquette Service)
  // Gateway: /api/maquette/classes
  // =====================
  CLASSES: {
    BASE: `${API_BASE_URL}/api/maquette/classes`,
    LIST: `${API_BASE_URL}/api/maquette/classes`,
    BY_ID: (id) => `${API_BASE_URL}/api/maquette/classes/${id}`,
    CREATE: `${API_BASE_URL}/api/maquette/classes`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquette/classes/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquette/classes/${id}`,
  },

  // =====================
  // DEPARTEMENT SERVICE (Maquette Service)
  // Gateway: /api/maquette/departements
  // =====================
  DEPARTMENTS: {
    BASE: `${API_BASE_URL}/api/maquette/departements`,
    LIST: `${API_BASE_URL}/api/maquette/departements`,
    CREATE: `${API_BASE_URL}/api/maquette/departements`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquette/departements/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquette/departements/${id}`,
  },

  // =====================
  // FORMATION SERVICE (Maquette Service)
  // Gateway: /api/maquette/formations
  // =====================
  FORMATIONS: {
    BASE: `${API_BASE_URL}/api/maquette/formations`,
    LIST: `${API_BASE_URL}/api/maquette/formations`,
    CREATE: `${API_BASE_URL}/api/maquette/formations`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquette/formations/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquette/formations/${id}`,
  },

  // =====================
  // FILIERE SERVICE (Maquette Service)
  // Gateway: /api/maquette/filieres
  // =====================
  FILIERES: {
    BASE: `${API_BASE_URL}/api/maquette/filieres`,
    LIST: `${API_BASE_URL}/api/maquette/filieres`,
    CREATE: `${API_BASE_URL}/api/maquette/filieres`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquette/filieres/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquette/filieres/${id}`,
  },

  // =====================
  // NIVEAU SERVICE (Maquette Service)
  // Gateway: /api/maquette/niveaux
  // =====================
  NIVEAUX: {
    BASE: `${API_BASE_URL}/api/maquette/niveaux`,
    LIST: `${API_BASE_URL}/api/maquette/niveaux`,
    CREATE: `${API_BASE_URL}/api/maquette/niveaux`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquette/niveaux/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquette/niveaux/${id}`,
  },

  // =====================
  // MODULE SERVICE (Maquette Service)
  // Gateway: /api/maquette/modules
  // =====================
  MODULES: {
    base: `${API_BASE_URL}/api/maquette/modules`,
    LIST: `${API_BASE_URL}/api/maquette/modules`,
    CREATE: `${API_BASE_URL}/api/maquette/modules`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquette/modules/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquette/modules/${id}`,
  },

  // =====================
  // UE SERVICE (Maquette Service)
  // Gateway: /api/maquette/ues
  // =====================
  UES: {
    base: `${API_BASE_URL}/api/maquette/ues`,
    LIST: `${API_BASE_URL}/api/maquette/ues`,
    CREATE: `${API_BASE_URL}/api/maquette/ues`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquette/ues/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquette/ues/${id}`,
  },

  // =====================
  // EC SERVICE (Maquette Service)
  // Gateway: /api/maquette/ecs
  // =====================
  ECS: {
    base: `${API_BASE_URL}/api/maquette/ecs`,
    LIST: `${API_BASE_URL}/api/maquette/ecs`,
    CREATE: `${API_BASE_URL}/api/maquette/ecs`,
    UPDATE: (id) => `${API_BASE_URL}/api/maquette/ecs/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/maquette/ecs/${id}`,
  },

  // ==================================
  // DEROULEMENT ENSEIGNEMENT SERVICE
  // Gateway: /api/deroulement-enseignements/**
  // ==================================
  // ==================================
  // CAHIER DE TEXTE SERVICE (Deroulement Enseignement)
  // Gateway: /api/notes-cahier/**
  // ==================================
  CAHIER_TEXTE: {
    base: `${API_BASE_URL}/api/notes-cahier`,
    LIST: `${API_BASE_URL}/api/notes-cahier`,
    BY_ID: (id) => `${API_BASE_URL}/api/notes-cahier/${id}`,
    CREATE: `${API_BASE_URL}/api/notes-cahier`,
    UPDATE: (id) => `${API_BASE_URL}/api/notes-cahier/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/notes-cahier/${id}`,
    VALIDATE: (id) => `${API_BASE_URL}/api/notes-cahier/${id}/valider`,
    EXPORT_PDF: `${API_BASE_URL}/api/notes-cahier/export/pdf`,
  },

  // ==================================
  // ETUDIANT SERVICE (Deroulement Enseignement)
  // Gateway: /api/etudiants/**
  // ==================================
  DEROULEMENT_ETUDIANTS: {
    base: `${API_BASE_URL}/api/etudiants`,
    LIST: `${API_BASE_URL}/api/etudiants`,
    BY_ID: (id) => `${API_BASE_URL}/api/etudiants/${id}`,
    CREATE: `${API_BASE_URL}/api/etudiants`,
    UPDATE: (id) => `${API_BASE_URL}/api/etudiants/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/etudiants/${id}`,
    SUSPEND: (id) => `${API_BASE_URL}/api/etudiants/${id}/suspendre`,
    REACTIVATE: (id) => `${API_BASE_URL}/api/etudiants/${id}/reactiver`,
  },

  // ==================================
  // CLASSE SERVICE (Deroulement Enseignement - Instances)
  // Gateway: /api/classes/** (Careful conflict with Maquette classes?)
  // Maquette uses /api/maquette/classes
  // Deroulement uses /api/classes (root?) or /api/deroulement/classes?
  // Controller @RequestMapping is root /api/classes
  // ==================================
  DEROULEMENT_CLASSES: {
    base: `${API_BASE_URL}/api/classes`,
    LIST: `${API_BASE_URL}/api/classes`,
    BY_ID: (id) => `${API_BASE_URL}/api/classes/${id}`,
    CREATE: `${API_BASE_URL}/api/classes`,
    UPDATE: (id) => `${API_BASE_URL}/api/classes/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/classes/${id}`, // Uses archive logic if DELETE provided? Controller maps DELETE too.
    ARCHIVE: (id) => `${API_BASE_URL}/api/classes/${id}/archive`,
    DESARCHIVE: (id) => `${API_BASE_URL}/api/classes/${id}/desarchiver`,
  },

  // ==================================
  // EMPLOI DU TEMPS SERVICE
  // ==================================
  EMPLOI_TEMPS: {
    base: `${API_BASE_URL}/api/emploi-du-temps`,
    WEEKLY: `${API_BASE_URL}/api/emploi-du-temps/semaine`, // ?date=...&filtrePar=...&filtreId=...
    SEMESTER: `${API_BASE_URL}/api/emploi-du-temps/semestre`,
    BY_TEACHER: (id) => `${API_BASE_URL}/api/emploi-du-temps/enseignant/${id}/semaine`,
    BY_ROOM: (id) => `${API_BASE_URL}/api/emploi-du-temps/salle/${id}/semaine`,
  },

  SEANCES: {
    base: `${API_BASE_URL}/api/seances`,
    LIST: `${API_BASE_URL}/api/seances`,
    SEARCH: `${API_BASE_URL}/api/emploi-du-temps/search`, // Uses EmploiController for search
    CREATE: `${API_BASE_URL}/api/seances`,
    UPDATE: (id) => `${API_BASE_URL}/api/seances/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/seances/${id}`,
    HISTORY: (id) => `${API_BASE_URL}/api/seances/${id}/historique`,
  },

  SALLES: {
    base: `${API_BASE_URL}/api/salles`,
    LIST: `${API_BASE_URL}/api/salles`,
    AVAILABLE: `${API_BASE_URL}/api/salles/disponibilites`,
    CREATE: `${API_BASE_URL}/api/salles`,
    UPDATE: (id) => `${API_BASE_URL}/api/salles/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/salles/${id}`,
  },

  BATIMENTS: {
    base: `${API_BASE_URL}/api/batiments`,
    LIST: `${API_BASE_URL}/api/batiments`,
    CREATE: `${API_BASE_URL}/api/batiments`,
    UPDATE: (id) => `${API_BASE_URL}/api/batiments/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/batiments/${id}`,
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
  console.log("DEBUG: Token used for request to", url, ":", token ? token.substring(0, 10) + "..." : "NONE");

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
