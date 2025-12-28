import axios from 'axios';

const API_URL = `${import.meta.env.VITE_API_BASE_URL}/salles`;

/**
 * Service pour la gestion des salles.
 */
const SalleService = {
  /**
   * Récupère la liste de toutes les salles.
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  getAllSalles: () => {
    return axios.get(API_URL);
  },

  // D'autres fonctions (getById, create, update, delete) pourront être ajoutées ici si nécessaire.
};

export default SalleService;
