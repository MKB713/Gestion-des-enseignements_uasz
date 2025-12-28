import axios from 'axios';

const API_URL = `${import.meta.env.VITE_API_BASE_URL}/emploi-du-temps`;
const SEANCES_API_URL = `${import.meta.env.VITE_API_BASE_URL}/seances`; // Endpoint spécifique pour les séances

/**
 * Service pour la gestion des emplois du temps et des séances.
 */
const EmploiDuTempsService = {
  /**
   * Récupère l'emploi du temps pour une semaine donnée avec des filtres optionnels.
   * @param {Date} startDate - Le premier jour de la semaine.
   * @param {string} classId - ID de la classe pour filtrer.
   * @param {string} teacherId - ID de l'enseignant pour filtrer.
   * @param {string} roomId - ID de la salle pour filtrer.
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  getEmploiDuTempsSemaine: (startDate, classId, teacherId, roomId) => {
    const params = {
      startDate: startDate.toISOString().split('T')[0], // Format YYYY-MM-DD
    };
    if (classId) params.classId = classId;
    if (teacherId) params.teacherId = teacherId;
    if (roomId) params.roomId = roomId;

    return axios.get(`${API_URL}/semaine`, { params });
  },

  /**
   * Crée une nouvelle séance.
   * @param {object} seanceData - Les données de la séance à créer.
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  createSeance: (seanceData) => {
    return axios.post(SEANCES_API_URL, seanceData);
  },

  /**
   * Met à jour une séance existante.
   * @param {string} id - L'ID de la séance à mettre à jour.
   * @param {object} seanceData - Les données de la séance mises à jour.
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  updateSeance: (id, seanceData) => {
    return axios.put(`${SEANCES_API_URL}/${id}`, seanceData);
  },

  /**
   * Supprime une séance.
   * @param {string} id - L'ID de la séance à supprimer.
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  deleteSeance: (id) => {
    return axios.delete(`${SEANCES_API_URL}/${id}`);
  },

  /**
   * Récupère l'emploi du temps pour un mois donné avec des filtres optionnels.
   * @param {number} year - L'année du mois.
   * @param {number} month - Le mois (1-indexé).
   * @param {string} classId - ID de la classe pour filtrer.
   * @param {string} teacherId - ID de l'enseignant pour filtrer.
   * @param {string} roomId - ID de la salle pour filtrer.
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  getEmploiDuTempsSemestre: (year, month, classId, teacherId, roomId) => {
    const params = {
      year: year,
      month: month,
    };
    if (classId) params.classId = classId;
    if (teacherId) params.teacherId = teacherId;
    if (roomId) params.roomId = roomId;

    return axios.get(`${API_URL}/semestre`, { params });
  },
  /**
   * Exporte l'emploi du temps en PDF.
   * @param {string} viewType - Type de vue ('semaine' ou 'semestre').
   * @param {string} filterType - Type de filtre ('classe', 'enseignant', 'salle').
   * @param {string} filterId - ID de l'entité à filtrer.
   * @returns {Promise<axios.AxiosResponse<Blob>>} - Retourne le fichier PDF sous forme de Blob.
   */
  exportEmploiDuTempsPdf: (viewType, filterType, filterId) => {
    const params = {
      viewType: viewType,
      filterType: filterType,
      filterId: filterId,
    };
    // axios({
    //   url: `${API_URL}/export/pdf`,
    //   method: 'GET',
    //   responseType: 'blob', // important
    //   params: params
    // })
    return axios.get(`${API_URL}/export/pdf`, {
      params: params,
      responseType: 'blob' // Indique à Axios d'attendre une réponse binaire (Blob)
    });
  },
  /**
   * Recherche des séances avec des critères spécifiques.
   * @param {object} searchParams - Objet contenant les critères de recherche (ecId, enseignantId, salleId, classeId, date).
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  searchSeances: (searchParams) => {
    const params = {};
    if (searchParams.ecId) params.ecId = searchParams.ecId;
    if (searchParams.enseignantId) params.enseignantId = searchParams.enseignantId;
    if (searchParams.salleId) params.salleId = searchParams.salleId;
    if (searchParams.classeId) params.classeId = searchParams.classeId;
    if (searchParams.date) params.date = searchParams.date; // Format YYYY-MM-DD

    return axios.get(`${SEANCES_API_URL}/search`, { params });
  },
  /**
   * Remplace l'enseignant d'une séance spécifique.
   * @param {string} seanceId - L'ID de la séance à modifier.
   * @param {string} newEnseignantId - L'ID du nouvel enseignant.
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  replaceEnseignant: (seanceId, newEnseignantId) => {
    return axios.post(`${SEANCES_API_URL}/${seanceId}/remplacer`, { newEnseignantId });
  },
};

export default EmploiDuTempsService;
