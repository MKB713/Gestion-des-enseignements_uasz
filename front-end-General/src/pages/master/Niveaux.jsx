import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "../admin/AdminDepartments.css"; // Reuse existing styles

const MasterNiveaux = () => {
    const [niveaux, setNiveaux] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentNiveau, setCurrentNiveau] = useState(null);

    const [formData, setFormData] = useState({
        cycle: "LICENCE",
        numero: 1
    });

    const CYCLES = ["LICENCE", "MASTER", "DOCTORAT"];

    useEffect(() => {
        fetchNiveaux();
    }, []);

    const fetchNiveaux = async () => {
        try {
            setLoading(true);
            const data = await apiRequest(API_ENDPOINTS.NIVEAUX.LIST);
            setNiveaux(data || []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement niveaux:", err);
            setError("Erreur lors du chargement des niveaux.");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (niveau = null) => {
        if (niveau) {
            setIsEditing(true);
            setCurrentNiveau(niveau);
            setFormData({
                cycle: niveau.cycle,
                numero: niveau.numero
            });
        } else {
            setIsEditing(false);
            setCurrentNiveau(null);
            setFormData({
                cycle: "LICENCE",
                numero: 1
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
                cycle: formData.cycle,
                numero: parseInt(formData.numero)
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.NIVEAUX.UPDATE(currentNiveau.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.NIVEAUX.CREATE, {
                    method: "POST",
                    body: JSON.stringify(payload)
                });
            }
            fetchNiveaux();
            closeModal();
        } catch (err) {
            console.error("Erreur enregistrement:", err);
            const errorMessage = err.response?.data?.message || err.message || "Erreur inconnue";
            alert("Erreur lors de l'enregistrement: " + errorMessage);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer ce niveau ?")) {
            try {
                await apiRequest(API_ENDPOINTS.NIVEAUX.DELETE(id), { method: "DELETE" });
                fetchNiveaux();
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
                <h2>Gestion des Niveaux (Master)</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouveau Niveau
                </button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Cycle</th>
                        <th>Numéro (Année)</th>
                        <th>Libellé Complet</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {niveaux.length === 0 ? (
                        <tr>
                            <td colSpan="4" style={{ textAlign: "center" }}>Aucun niveau trouvé.</td>
                        </tr>
                    ) : (
                        niveaux.map((niveau) => (
                            <tr key={niveau.id}>
                                <td>{niveau.cycle}</td>
                                <td>{niveau.numero}</td>
                                <td>{niveau.cycle} {niveau.numero}</td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(niveau)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(niveau.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier le Niveau" : "Nouveau Niveau"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Cycle</label>
                                <select
                                    name="cycle"
                                    value={formData.cycle}
                                    onChange={handleInputChange}
                                    required
                                >
                                    {CYCLES.map(c => (
                                        <option key={c} value={c}>{c}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-group">
                                <label>Numéro (Année)</label>
                                <input
                                    type="number"
                                    name="numero"
                                    value={formData.numero}
                                    onChange={handleInputChange}
                                    min="1"
                                    max="5"
                                    required
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

export default MasterNiveaux;
