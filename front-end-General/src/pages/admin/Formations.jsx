import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css"; // Réutiliser le CSS existant

const AdminFormations = () => {
    const [formations, setFormations] = useState([]);
    const [filieres, setFilieres] = useState([]);
    const [niveaux, setNiveaux] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentFormation, setCurrentFormation] = useState(null);

    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        description: "",
        filiere: "",
        niveau: ""
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [formationsRes, filieresRes, niveauxRes] = await Promise.all([
                apiRequest(API_ENDPOINTS.FORMATIONS.LIST),
                apiRequest(API_ENDPOINTS.FILIERES.LIST),
                apiRequest(API_ENDPOINTS.NIVEAUX.LIST)
            ]);

            setFormations(formationsRes || []);
            setFilieres(filieresRes || []);
            setNiveaux(niveauxRes || []);
            setError(null);
        } catch (err) {
            console.error("Erreur lors du chargement des données:", err);
            setError("Erreur lors du chargement des données.");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (formation = null) => {
        if (formation) {
            setIsEditing(true);
            setCurrentFormation(formation);
            setFormData({
                code: formation.code,
                libelle: formation.libelle,
                description: formation.description,
                filiere: formation.filiere ? formation.filiere.id : "",
                niveau: formation.niveau ? formation.niveau.id : ""
            });
        } else {
            setIsEditing(false);
            setCurrentFormation(null);
            setFormData({
                code: "",
                libelle: "",
                description: "",
                filiere: "",
                niveau: ""
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
                description: formData.description,
                filiere: formData.filiere ? { id: parseInt(formData.filiere) } : null,
                niveau: formData.niveau ? { id: parseInt(formData.niveau) } : null
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.FORMATIONS.UPDATE(currentFormation.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.FORMATIONS.CREATE, {
                    method: "POST",
                    body: JSON.stringify(payload)
                });
            }

            fetchData();
            closeModal();
        } catch (err) {
            console.error("Erreur lors de l'enregistrement:", err);
            const errorMessage = err.response?.data?.message || err.message || "Erreur inconnue";
            alert("Erreur lors de l'enregistrement: " + errorMessage);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer cette formation ?")) {
            try {
                await apiRequest(API_ENDPOINTS.FORMATIONS.DELETE(id), { method: "DELETE" });
                fetchData();
            } catch (err) {
                console.error("Erreur lors de la suppression:", err);
                const errorMessage = err.response?.data?.message || err.message || "Impossible de supprimer cette formation. Elle est probablement liée à d'autres éléments.";
                alert("Erreur lors de la suppression: " + errorMessage);
            }
        }
    };

    if (loading) return <div className="loading">Chargement...</div>;
    if (error) return <div className="error">{error}</div>;

    const hasPrerequisites = filieres.length > 0 && niveaux.length > 0;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Formations</h2>
                <button className="add-btn" onClick={() => openModal()} disabled={!hasPrerequisites}>
                    + Nouvelle Formation
                </button>
            </div>

            {!hasPrerequisites && (
                <div className="warning-box" style={{ backgroundColor: "#fff3cd", color: "#856404", padding: "10px", marginBottom: "15px", borderRadius: "5px", border: "1px solid #ffeeba" }}>
                    <strong>Attention :</strong> Pour créer une formation, vous devez d'abord avoir au moins une <a href="/admin/filieres">Filière</a> et un <a href="/admin/niveaux">Niveau</a>.
                </div>
            )}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Libellé</th>
                        <th>Filière</th>
                        <th>Niveau</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {formations.map((formation) => (
                        <tr key={formation.id}>
                            <td>{formation.code}</td>
                            <td>{formation.libelle}</td>
                            <td>{formation.filiere ? formation.filiere.libelle : "-"}</td>
                            <td>{formation.niveau ? formation.niveau.libelle : "-"}</td>
                            <td className="actions-cell">
                                <button className="edit-btn" onClick={() => openModal(formation)}>Modifier</button>
                                <button className="delete-btn" onClick={() => handleDelete(formation.id)}>Supprimer</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier la Formation" : "Nouvelle Formation"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Code</label>
                                <input
                                    type="text"
                                    name="code"
                                    value={formData.code}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>Libellé</label>
                                <input
                                    type="text"
                                    name="libelle"
                                    value={formData.libelle}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>Filière</label>
                                <select
                                    name="filiere"
                                    value={formData.filiere}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="">Sélectionner une filière</option>
                                    {filieres.map(f => (
                                        <option key={f.id} value={f.id}>{f.libelle}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-group">
                                <label>Niveau</label>
                                <select
                                    name="niveau"
                                    value={formData.niveau}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="">Sélectionner un niveau</option>
                                    {niveaux.map(n => (
                                        <option key={n.id} value={n.id}>{n.libelle}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-group">
                                <label>Description</label>
                                <textarea
                                    name="description"
                                    value={formData.description}
                                    onChange={handleInputChange}
                                />
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

export default AdminFormations;
