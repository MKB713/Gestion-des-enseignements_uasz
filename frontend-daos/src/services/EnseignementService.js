import axios from 'axios';

const API_URL = "http://localhost:8080/api";

class EnseignementService {
    // --- MODULES ---
    getModules(archived = false) {
        return axios.get(`${API_URL}/modules?archived=${archived}`);
    }
    saveModule(module) {
        return axios.post(`${API_URL}/modules`, module);
    }
    deleteModule(id) {
        return axios.delete(`${API_URL}/modules/${id}`); // Soft delete (archivage)
    }
    restoreModule(id) {
        return axios.post(`${API_URL}/modules/${id}/restore`);
    }

    // --- UES ---
    getUEs(archived = false) {
        return axios.get(`${API_URL}/ues?archived=${archived}`);
    }
    saveUE(ue) {
        return axios.post(`${API_URL}/ues`, ue);
    }
    toggleUeStatus(id, status) { // Activer/Désactiver
        return axios.post(`${API_URL}/ues/${id}/status?active=${status}`);
    }
    archiveUE(id) {
        return axios.delete(`${API_URL}/ues/${id}`);
    }

    // --- ECS ---
    getECs(archived = false) {
        return axios.get(`${API_URL}/ecs?archived=${archived}`);
    }
    saveEC(ec) {
        return axios.post(`${API_URL}/ecs`, ec);
    }
    archiveEC(id) {
        return axios.delete(`${API_URL}/ecs/${id}`);
    }
}

export default new EnseignementService();