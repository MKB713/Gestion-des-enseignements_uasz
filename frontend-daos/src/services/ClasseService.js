import axios from 'axios';

const API_URL = `${import.meta.env.VITE_API_BASE_URL}/classes`;

/**
 * Service pour la gestion des classes.
 */
const ClasseService = {
  /**
   * Récupère la liste de toutes les classes.
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  getAllClasses: () => {
    return axios.get(API_URL);
  },

  // D'autres fonctions (getById, create, update, delete) pourront être ajoutées ici si nécessaire.
};

export default ClasseService;
