import axios from 'axios';

// L'URL pointe vers l'API Gateway (port 8080)
// Le Gateway redirigera ensuite vers enseignant-service (port 8082)
const API_URL = 'http://localhost:8080/api/enseignants';

class EnseignantService {

    // Récupérer tous les enseignants
    getAllEnseignants() {
        return axios.get(API_URL);
    }

    // Récupérer les archives
    getArchives() {
        return axios.get(API_URL + '/archives');
    }

    // Récupérer un enseignant par ID
    getEnseignantById(id) {
        return axios.get(API_URL + '/' + id);
    }

    // Créer un enseignant
    createEnseignant(enseignant) {
        return axios.post(API_URL, enseignant);
    }

    // Modifier un enseignant
    updateEnseignant(id, enseignant) {
        return axios.put(API_URL + '/' + id, enseignant);
    }

    // --- ACTIONS SPÉCIFIQUES (PATCH) ---

    archiver(id) {
        return axios.patch(API_URL + '/' + id + '/archiver');
    }

    restaurer(id) {
        return axios.patch(API_URL + '/' + id + '/restaurer');
    }

    activer(id) {
        return axios.patch(API_URL + '/' + id + '/activer');
    }

    desactiver(id) {
        return axios.patch(API_URL + '/' + id + '/desactiver');
    }

    // --- LISTES DÉROULANTES ---

    getGrades() {
        return axios.get(API_URL + '/ref/grades');
    }

    getStatuts() {
        return axios.get(API_URL + '/ref/statuts');
    }
}

export default new EnseignantService();