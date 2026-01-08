import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css";

const AdminEnseignants = () => {
    const [enseignants, setEnseignants] = useState([]);
    const [grades, setGrades] = useState([]);
    const [statuts, setStatuts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentEnseignant, setCurrentEnseignant] = useState(null);

    const [formData, setFormData] = useState({
        nom: "",
        prenom: "",
        matricule: "", // Not present in create/update? Check Controller
        grade: "",
        statut: "" // Enum
    });

    useEffect(() => {
        fetchData();
        fetchReferences();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const data = await apiRequest(API_ENDPOINTS.ENSEIGNANTS.LIST);
            setEnseignants(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement enseignants:", err);
            setError("Erreur lors du chargement des enseignants.");
        } finally {
            setLoading(false);
        }
    };

    const fetchReferences = async () => {
        try {
            // Need to verify endpoints for references in EnseignantController or if they are exposed
            // Controller has /api/enseignants/ref/grades and /api/enseignants/ref/statuts
            // API_ENDPOINTS.ENSEIGNANTS doesn't have these explicitly named but we can construct them
            const gradesRes = await apiRequest(`${API_ENDPOINTS.ENSEIGNANTS.base}/ref/grades`).catch(() => []);
            const statutsRes = await apiRequest(`${API_ENDPOINTS.ENSEIGNANTS.base}/ref/statuts`).catch(() => []);

            setGrades(Array.isArray(gradesRes) ? gradesRes : ["Assistant", "Maître-Assistant", "Maître de Conférences", "Professeur Titulaire"]);
            setStatuts(Array.isArray(statutsRes) ? statutsRes : ["PERMANENT", "VACATAIRE"]);
        } catch (e) {
            console.warn("Could not fetch refs, using defaults");
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (enseignant = null) => {
        if (enseignant) {
            setIsEditing(true);
            setCurrentEnseignant(enseignant);
            setFormData({
                nom: enseignant.nom,
                prenom: enseignant.prenom,
                matricule: enseignant.matricule,
                grade: enseignant.grade,
                statut: enseignant.statut
            });
        } else {
            setIsEditing(false);
            setCurrentEnseignant(null);
            setFormData({
                nom: "",
                prenom: "",
                matricule: "",
                grade: "",
                statut: ""
            });
        }
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setError(null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const payload = {
                nom: formData.nom,
                prenom: formData.prenom,
                matricule: formData.matricule,
                grade: formData.grade,
                statut: formData.statut
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.ENSEIGNANTS.UPDATE(currentEnseignant.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.ENSEIGNANTS.CREATE, {
                    method: "POST",
                    body: JSON.stringify(payload)
                });
            }
            fetchData();
            closeModal();
        } catch (err) {
            console.error("Erreur enregistrement:", err);
            const errorMessage = err.response?.data?.message || err.message || "Erreur inconnue";
            alert("Erreur lors de l'enregistrement: " + errorMessage);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer cet enseignant ? (Archivage)")) {
            try {
                // The api.js DELETE might point to DELETE method, but controller uses PATCH /archive?
                // Controller: @PatchMapping("/{id}/archiver")
                // api.js DELETE: `${API_BASE_URL}/api/enseignants/${id}` often defaults to DELETE verb
                // API_ENDPOINTS.ENSEIGNANTS.DELETE is a function returning url.
                // We should use specific endpoint for archive if needed, or check if DELETE verb is mapped.
                // Controller does NOT map DELETE /{id}. It maps PATCH /{id}/archiver.
                // So we must manually call the archive endpoint.
                await apiRequest(`${API_ENDPOINTS.ENSEIGNANTS.base}/${id}/archiver`, { method: "PATCH" });
                fetchData();
            } catch (err) {
                console.error("Erreur suppression:", err);
                alert("Erreur lors de la suppression.");
            }
        }
    };

    const handleToggleStatus = async (enseignant) => {
        try {
            if (enseignant.actif) {
                await apiRequest(`${API_ENDPOINTS.ENSEIGNANTS.base}/${enseignant.id}/desactiver`, { method: "PATCH" }); // Update Controller path? @PatchMapping("/{id}/desactiver")
            } else {
                await apiRequest(`${API_ENDPOINTS.ENSEIGNANTS.base}/${enseignant.id}/activer`, { method: "PATCH" });
            }
            fetchData();
        } catch (err) {
            console.error("Erreur changement statut:", err);
        }
    };

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Enseignants</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouvel Enseignant
                </button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Matricule</th>
                        <th>Prénom & Nom</th>
                        <th>Grade</th>
                        <th>Statut</th>
                        <th>État</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {enseignants.length === 0 ? (
                        <tr><td colSpan="6" style={{ textAlign: "center" }}>Aucun enseignant trouvé.</td></tr>
                    ) : (
                        enseignants.map((enseignant) => (
                            <tr key={enseignant.id}>
                                <td>{enseignant.matricule}</td>
                                <td>{enseignant.prenom} {enseignant.nom}</td>
                                <td>{enseignant.grade}</td>
                                <td>{enseignant.statut}</td>
                                <td>
                                    <span
                                        className={`badge ${enseignant.actif ? 'badge-success' : 'badge-danger'}`}
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => handleToggleStatus(enseignant)}
                                        title="Cliquez pour changer l'état"
                                    >
                                        {enseignant.actif ? "Actif" : "Inactif"}
                                    </span>
                                </td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(enseignant)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(enseignant.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier l'Enseignant" : "Nouvel Enseignant"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Prénom</label>
                                    <input type="text" name="prenom" value={formData.prenom} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group">
                                    <label>Nom</label>
                                    <input type="text" name="nom" value={formData.nom} onChange={handleInputChange} required />
                                </div>
                            </div>
                            <div className="form-group">
                                <label>Matricule</label>
                                <input type="text" name="matricule" value={formData.matricule} onChange={handleInputChange} required />
                            </div>
                            <div className="form-group">
                                <label>Grade</label>
                                <select name="grade" value={formData.grade} onChange={handleInputChange} required>
                                    <option value="">Sélectionner un grade</option>
                                    {grades.map(g => <option key={g} value={g}>{g}</option>)}
                                </select>
                            </div>
                            <div className="form-group">
                                <label>Statut</label>
                                <select name="statut" value={formData.statut} onChange={handleInputChange} required>
                                    <option value="">Sélectionner un statut</option>
                                    {statuts.map(s => <option key={s} value={s}>{s}</option>)}
                                </select>
                            </div>
                            <div className="modal-actions">
                                <button type="button" onClick={closeModal} className="cancel-btn">Annuler</button>
                                <button type="submit" className="submit-btn">Enregistrer</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminEnseignants;
