import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css";

const AdminBatiments = () => {
    const [batiments, setBatiments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentBatiment, setCurrentBatiment] = useState(null);

    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        position: "",
        description: ""
    });

    useEffect(() => {
        fetchBatiments();
    }, []);

    const fetchBatiments = async () => {
        try {
            setLoading(true);
            const data = await apiRequest(API_ENDPOINTS.BATIMENTS.LIST);
            setBatiments(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement:", err);
            setError("Erreur lors du chargement des bâtiments.");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (batiment = null) => {
        if (batiment) {
            setIsEditing(true);
            setCurrentBatiment(batiment);
            setFormData({
                code: batiment.code || "",
                libelle: batiment.libelle,
                position: batiment.position || "",
                description: batiment.description || ""
            });
        } else {
            setIsEditing(false);
            setCurrentBatiment(null);
            setFormData({
                code: "",
                libelle: "",
                position: "",
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
                await apiRequest(API_ENDPOINTS.BATIMENTS.UPDATE(currentBatiment.id), {
                    method: "PUT",
                    body: JSON.stringify(formData)
                });
            } else {
                await apiRequest(API_ENDPOINTS.BATIMENTS.CREATE, {
                    method: "POST",
                    body: JSON.stringify(formData)
                });
            }
            fetchBatiments();
            closeModal();
            alert(isEditing ? "Bâtiment modifié !" : "Bâtiment ajouté !");
        } catch (err) {
            console.error("Erreur enregistrement:", err);
            alert("Erreur: " + (err.message || "Impossible d'enregistrer."));
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer ce bâtiment ?")) {
            try {
                await apiRequest(API_ENDPOINTS.BATIMENTS.DELETE(id), { method: "DELETE" });
                fetchBatiments();
            } catch (err) {
                alert("Erreur lors de la suppression.");
            }
        }
    };

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Bâtiments</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouveau Bâtiment
                </button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Libellé</th>
                        <th>Position</th>
                        <th>Description</th>
                        <th>Salles</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {batiments.length === 0 ? (
                        <tr><td colSpan="6" style={{ textAlign: "center" }}>Aucun bâtiment trouvé.</td></tr>
                    ) : (
                        batiments.map((bat) => (
                            <tr key={bat.id}>
                                <td><span className="badge badge-secondary">{bat.code}</span></td>
                                <td><strong>{bat.libelle}</strong></td>
                                <td>{bat.position}</td>
                                <td>{bat.description}</td>
                                <td>
                                    <span className="badge badge-info">
                                        {bat.salles ? bat.salles.length : 0} salles
                                    </span>
                                </td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(bat)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(bat.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier Bâtiment" : "Nouveau Bâtiment"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Code</label>
                                <input type="text" name="code" value={formData.code} onChange={handleInputChange} required placeholder="Ex: BAT-A" />
                            </div>

                            <div className="form-group">
                                <label>Libellé</label>
                                <input type="text" name="libelle" value={formData.libelle} onChange={handleInputChange} required placeholder="Ex: Batiment A" />
                            </div>

                            <div className="form-group">
                                <label>Position</label>
                                <input type="text" name="position" value={formData.position} onChange={handleInputChange} placeholder="Ex: Campus Principal, Bloc Nord" />
                            </div>

                            <div className="form-group">
                                <label>Description</label>
                                <textarea name="description" value={formData.description} onChange={handleInputChange} rows="3"></textarea>
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

export default AdminBatiments;
