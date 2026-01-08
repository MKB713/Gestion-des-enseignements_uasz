import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css"; // Reuse existing styles

const AdminFilieres = () => {
    const [filieres, setFilieres] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentFiliere, setCurrentFiliere] = useState(null);

    const [formData, setFormData] = useState({
        libelle: "",
        description: ""
    });

    useEffect(() => {
        fetchFilieres();
    }, []);

    const fetchFilieres = async () => {
        try {
            setLoading(true);
            const data = await apiRequest(API_ENDPOINTS.FILIERES.LIST);
            setFilieres(data || []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement filières:", err);
            setError("Erreur lors du chargement des filières.");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (filiere = null) => {
        if (filiere) {
            setIsEditing(true);
            setCurrentFiliere(filiere);
            setFormData({
                libelle: filiere.libelle,
                description: filiere.description
            });
        } else {
            setIsEditing(false);
            setCurrentFiliere(null);
            setFormData({
                libelle: "",
                description: ""
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
            if (isEditing) {
                await apiRequest(API_ENDPOINTS.FILIERES.UPDATE(currentFiliere.id), {
                    method: "PUT",
                    body: JSON.stringify(formData)
                });
            } else {
                await apiRequest(API_ENDPOINTS.FILIERES.CREATE, {
                    method: "POST",
                    body: JSON.stringify(formData)
                });
            }
            fetchFilieres();
            closeModal();
        } catch (err) {
            console.error("Erreur enregistrement:", err);
            const errorMessage = err.response?.data?.message || err.message || "Erreur inconnue";
            alert("Erreur lors de l'enregistrement: " + errorMessage);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer cette filière ?")) {
            try {
                await apiRequest(API_ENDPOINTS.FILIERES.DELETE(id), { method: "DELETE" });
                fetchFilieres();
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
                <h2>Gestion des Filières</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouvelle Filière
                </button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Libellé</th>
                        <th>Description</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {filieres.length === 0 ? (
                        <tr>
                            <td colSpan="3" style={{ textAlign: "center" }}>Aucune filière trouvée.</td>
                        </tr>
                    ) : (
                        filieres.map((filiere) => (
                            <tr key={filiere.id}>
                                <td>{filiere.libelle}</td>
                                <td>{filiere.description}</td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(filiere)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(filiere.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier la Filière" : "Nouvelle Filière"}</h3>
                        <form onSubmit={handleSubmit}>
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

export default AdminFilieres;
