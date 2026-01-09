import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "../admin/AdminDepartments.css";

const MasterModules = () => {
    const [modules, setModules] = useState([]);
    const [ues, setUEs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentModule, setCurrentModule] = useState(null);

    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        ue: ""
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [modulesRes, uesRes] = await Promise.all([
                apiRequest(API_ENDPOINTS.MODULES.LIST),
                apiRequest(API_ENDPOINTS.UES.LIST)
            ]);
            setModules(Array.isArray(modulesRes) ? modulesRes : []);
            setUEs(Array.isArray(uesRes) ? uesRes : []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement:", err);
            setError("Erreur lors du chargement des données.");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (module = null) => {
        if (module) {
            setIsEditing(true);
            setCurrentModule(module);
            setFormData({
                code: module.code,
                libelle: module.libelle,
                ue: module.ue ? module.ue.id : ""
            });
        } else {
            setIsEditing(false);
            setCurrentModule(null);
            setFormData({
                code: "",
                libelle: "",
                ue: ""
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
                code: formData.code,
                libelle: formData.libelle,
                ue: formData.ue ? { id: parseInt(formData.ue) } : null
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.MODULES.UPDATE(currentModule.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.MODULES.CREATE, {
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
        if (window.confirm("Êtes-vous sûr de vouloir supprimer ce module ?")) {
            try {
                await apiRequest(API_ENDPOINTS.MODULES.DELETE(id), { method: "DELETE" });
                fetchData();
            } catch (err) {
                console.error("Erreur suppression:", err);
                alert("Erreur lors de la suppression.");
            }
        }
    };

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Modules (Master)</h2>
                <button className="add-btn" onClick={() => openModal()} disabled={ues.length === 0}>
                    + Nouveau Module
                </button>
            </div>

            {ues.length === 0 && (
                <div className="warning-box" style={{ backgroundColor: "#fff3cd", color: "#856404", padding: "10px", marginBottom: "15px", borderRadius: "5px", border: "1px solid #ffeeba" }}>
                    <strong>Attention :</strong> Vous devez créer des <a href="/master/ues">unités d'enseignement (UE)</a> avant de créer un module.
                </div>
            )}

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Libellé</th>
                        <th>UE Rattachée</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {modules.length === 0 ? (
                        <tr><td colSpan="4" style={{ textAlign: "center" }}>Aucun module trouvé.</td></tr>
                    ) : (
                        modules.map((module) => (
                            <tr key={module.id}>
                                <td>{module.code}</td>
                                <td>{module.libelle}</td>
                                <td>{module.ue ? module.ue.libelle : "-"}</td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(module)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(module.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier le Module" : "Nouveau Module"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Code</label>
                                <input type="text" name="code" value={formData.code} onChange={handleInputChange} required />
                            </div>
                            <div className="form-group">
                                <label>Libellé</label>
                                <input type="text" name="libelle" value={formData.libelle} onChange={handleInputChange} required />
                            </div>
                            <div className="form-group">
                                <label>UE Rattachée</label>
                                <select name="ue" value={formData.ue} onChange={handleInputChange} required>
                                    <option value="">Sélectionner une UE</option>
                                    {ues.map(u => (
                                        <option key={u.id} value={u.id}>{u.code} - {u.libelle}</option>
                                    ))}
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

export default MasterModules;
