import axios from 'axios';

const API_URL = "http://localhost:8083/api/maquette";
const token = localStorage.getItem("accessToken");
console.log(token);
class MaquetteService {

    // --- FORMATIONS ---
    getAllFormations() { return axios.get(`${API_URL}/formations`,{headers:{ Authorization: `Bearer ${token}` }}); }
    getFormationById(id) { return axios.get(`${API_URL}/formations/${id}`, {headers:{Authorization: `Bearer ${token}`}}); }
    createFormation(data) { return axios.post(`${API_URL}/formations`, data, {headers:{Authorization: `Bearer ${token}`}}); }
    updateFormation(id, data) { return axios.put(`${API_URL}/formations/${id}`, data, {headers:{Authorization: `Bearer ${token}`}}); }
    archiverFormation(id) {
        return axios.patch(
            `${API_URL}/formations/${id}/archiver`,
            null,
            { headers: { Authorization: `Bearer ${token}` } }
        );
    }

    // --- FILIERE ---
    getAllFilieres() { return axios.get(`${API_URL}/filieres`,{headers:{"Authorization":token}}); }
    getFiliereById(id) { return axios.get(`${API_URL}/filieres/${id}`,{headers:{"Authorization":token}}); } // <--- AJOUTÉ
    createFiliere(data) { return axios.post(`${API_URL}/filieres`, data,{headers:{"Authorization":token}}); } // <--- AJOUTÉ
    updateFiliere(id, data) { return axios.put(`${API_URL}/filieres/${id}`, data, {headers:{"Authorization":token}}); } // <--- AJOUTÉ
    deleteFiliere(id) { return axios.delete(`${API_URL}/filieres/${id}`,{headers:{"Authorization":token}}); }

    // --- NIVEAUX ---
    getAllNiveaux() { return axios.get(`${API_URL}/niveaux`, {headers:{"Authorization":token}}); }
    getNiveauById(id) { return axios.get(`${API_URL}/niveaux/${id}`); } // <--- AJOUTÉ
    createNiveau(data) { return axios.post(`${API_URL}/niveaux`, data, {headers:{"Authorization":token}}); } // <--- AJOUTÉ
    updateNiveau(id, data) { return axios.put(`${API_URL}/niveaux/${id}`, data,{headers:{"Authorization":token}}); } // <--- AJOUTÉ
    deleteNiveau(id) { return axios.delete(`${API_URL}/niveaux/${id}`,{headers:{"Authorization":token}}); }

    // --- MODULES ---
    getAllModules() { return axios.get(`${API_URL}/modules`, {headers:{"Authorization":token}}); }
    deleteModule(id) { return axios.delete(`${API_URL}/modules/${id}`,{headers:{"Authorization":token}}); }
    archiverModule(id) { return axios.patch(`${API_URL}/modules/${id}/archiver`,{headers:{"Authorization":token}}); }

    // --- UES ---
    getAllUEs() { return axios.get(`${API_URL}/ues`, {headers:{"Authorization":token}}); }
    archiverUE(id) { return axios.patch(`${API_URL}/ues/${id}/archiver`,{headers:{"Authorization":token}}); }

    // --- ECS ---
    getAllECs() { return axios.get(`${API_URL}/ecs`, {headers:{"Authorization":token}}); }
    archiverEC(id) { return axios.patch(`${API_URL}/ecs/${id}/archiver`,{headers:{"Authorization":token}}); }
}

export default new MaquetteService();