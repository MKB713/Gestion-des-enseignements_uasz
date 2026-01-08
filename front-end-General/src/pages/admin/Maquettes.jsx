import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css"; // Reuse existing styles

const AdminMaquettes = () => {
    const [maquettes, setMaquettes] = useState([]);
    const [formations, setFormations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentMaquette, setCurrentMaquette] = useState(null);

    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        description: "",
        formation: ""
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [maquettesRes, formationsRes] = await Promise.all([
                apiRequest(API_ENDPOINTS.MAQUETTES.LIST),
                apiRequest(API_ENDPOINTS.FORMATIONS.LIST)
            ]);
            // Maquette API returns standardized ApiResponse wrapped list
            // But API_ENDPOINTS.MAQUETTES.LIST called via apiRequest might return payload directly if wrapper logic in api.js handles it? 
            // The controller returns ApiResponse<List<...>>. api.js returns response.json().
            // So maquettesRes might be { success: true, message: "...", data: [...] }
            // Let's handle both cases just to be safe, or inspecting api.js again... 
            // api.js returns await response.json(); So it returns the wrapper.

            const maquettesData = maquettesRes.data ? maquettesRes.data : (Array.isArray(maquettesRes) ? maquettesRes : []);
            setMaquettes(maquettesData);
            setFormations(formationsRes || []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement données:", err);
            setError("Erreur lors du chargement des données.");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (maquette = null) => {
        if (maquette) {
            setIsEditing(true);
            setCurrentMaquette(maquette);
            setFormData({
                code: maquette.code,
                libelle: maquette.libelle,
                description: maquette.description,
                formation: maquette.formationId ? maquette.formationId : ""
            });
        } else {
            setIsEditing(false);
            setCurrentMaquette(null);
            setFormData({
                code: "",
                libelle: "",
                description: "",
                formation: ""
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
                formationId: formData.formation ? parseInt(formData.formation) : null
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.MAQUETTES.UPDATE(currentMaquette.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.MAQUETTES.CREATE, {
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
        if (window.confirm("Êtes-vous sûr de vouloir supprimer cette maquette ?")) {
            try {
                await apiRequest(API_ENDPOINTS.MAQUETTES.DELETE(id), { method: "DELETE" });
                fetchData();
            } catch (err) {
                console.error("Erreur suppression:", err);
                alert("Erreur lors de la suppression.");
            }
        }
    };

    const handlePublish = async (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir PUBLIER cette maquette ? Elle ne pourra plus être supprimée.")) {
            try {
                await apiRequest(API_ENDPOINTS.MAQUETTES.PUBLISH(id), { method: "POST" });
                fetchData();
            } catch (err) {
                console.error("Erreur publication:", err);
                alert("Erreur lors de la publication.");
            }
        }
    };

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Maquettes</h2>
                <button className="add-btn" onClick={() => openModal()} disabled={formations.length === 0}>
                    + Nouvelle Maquette
                </button>
            </div>

            {formations.length === 0 && (
                <div className="warning-box" style={{ backgroundColor: "#fff3cd", color: "#856404", padding: "10px", marginBottom: "15px", borderRadius: "5px", border: "1px solid #ffeeba" }}>
                    <strong>Attention :</strong> Vous devez créer des <a href="/admin/formations">Formations</a> avant de créer une maquette.
                </div>
            )}

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Libellé</th>
                        <th>Formation</th>
                        <th>Version</th>
                        <th>Statut</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {maquettes.length === 0 ? (
                        <tr><td colSpan="6" style={{ textAlign: "center" }}>Aucune maquette trouvée.</td></tr>
                    ) : (
                        maquettes.map((maquette) => (
                            <tr key={maquette.id}>
                                <td>{maquette.code}</td>
                                <td>{maquette.libelle}</td>
                                <td>{maquette.formationLibelle || "-"}</td>
                                <td>v{maquette.version}</td>
                                <td>
                                    {maquette.statut === "PUBLIEE" ?
                                        <span className="badge badge-success">PUBLIÉE</span> :
                                        (maquette.statut === "ARCHIVEE" ?
                                            <span className="badge badge-danger">ARCHIVÉE</span> :
                                            <span className="badge badge-warning">BROUILLON</span>
                                        )
                                    }
                                </td>
                                <td className="actions-cell">
                                    <button className="edit-btn" style={{ backgroundColor: "#17a2b8" }} onClick={() => window.location.href = `/admin/maquettes/${maquette.id}`}>Voir</button>
                                    <button className="edit-btn" onClick={() => openModal(maquette)}>Modifier</button>

                                    {maquette.statut === "BROUILLON" && (
                                        <button className="edit-btn" style={{ backgroundColor: "#28a745" }} onClick={() => handlePublish(maquette.id)}>Publier</button>
                                    )}

                                    {maquette.statut !== "PUBLIEE" && (
                                        <button className="delete-btn" onClick={() => handleDelete(maquette.id)}>Supprimer</button>
                                    )}
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier la Maquette" : "Nouvelle Maquette"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Code</label>
                                <input
                                    type="text"
                                    name="code"
                                    value={formData.code}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Ex: MAQ-L1-INFO-2026"
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
                                    placeholder="Ex: Maquette Licence 1 Informatique"
                                />
                            </div>
                            <div className="form-group">
                                <label>Formation</label>
                                <select
                                    name="formation"
                                    value={formData.formation}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="">Sélectionner une formation</option>
                                    {formations.map(f => (
                                        <option key={f.id} value={f.id}>{f.libelle}</option>
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

export default AdminMaquettes;
