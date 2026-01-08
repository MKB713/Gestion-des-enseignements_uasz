import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css";

const AdminSalles = () => {
    const [salles, setSalles] = useState([]);
    const [batiments, setBatiments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentSalle, setCurrentSalle] = useState(null);

    // Filter states
    const [selectedBatiment, setSelectedBatiment] = useState("");

    const [formData, setFormData] = useState({
        libelle: "",
        code: "",
        capacite: "",
        batimentId: ""
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [sallesRes, batimentsRes] = await Promise.all([
                apiRequest(API_ENDPOINTS.SALLES.LIST).catch(() => []),
                apiRequest(API_ENDPOINTS.BATIMENTS.LIST).catch(() => [])
            ]);
            setSalles(Array.isArray(sallesRes) ? sallesRes : []);
            setBatiments(Array.isArray(batimentsRes) ? batimentsRes : []);
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

    const openModal = (salle = null) => {
        if (salle) {
            setIsEditing(true);
            setCurrentSalle(salle);
            setFormData({
                libelle: salle.libelle,
                code: salle.code,
                capacite: salle.capacite,
                batimentId: salle.batiment ? salle.batiment.id : "" // Check if backend returns nested object or ID
            });
        } else {
            setIsEditing(false);
            setCurrentSalle(null);
            setFormData({
                libelle: "",
                code: "",
                capacite: "",
                batimentId: ""
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
                ...formData,
                capacite: parseInt(formData.capacite),
                batiment: formData.batimentId ? { id: parseInt(formData.batimentId) } : null // Adjust based on backend expectation (ID or Object)
                // If backend expects batimentId as flat field, allow that. Usually relationships need object or ID specifically.
                // Salle.java: ManyToOne Batiment. createSalle(@RequestBody Salle). Jackson maps object.
                // Better to send complete Batiment object or just ID if DTO used.
                // Controller uses Entity directly. So it expects { batiment: { id: ... } } usually.
            };

            // Clean payload
            if (!payload.batiment) delete payload.batiment;

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.SALLES.UPDATE(currentSalle.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.SALLES.CREATE, {
                    method: "POST",
                    body: JSON.stringify(payload)
                });
            }
            fetchData();
            closeModal();
            alert(isEditing ? "Salle modifiée !" : "Salle ajoutée !");
        } catch (err) {
            console.error("Erreur enregistrement:", err);
            alert("Erreur: " + (err.message || "Impossible d'enregistrer."));
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Supprimer cette salle ?")) {
            try {
                await apiRequest(API_ENDPOINTS.SALLES.DELETE(id), { method: "DELETE" });
                fetchData();
            } catch (err) {
                alert("Erreur suppression.");
            }
        }
    };

    // Derived state for filtered salles
    const filteredSalles = selectedBatiment
        ? salles.filter(s => s.batiment && s.batiment.id === parseInt(selectedBatiment))
        : salles;

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Salles</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouvelle Salle
                </button>
            </div>

            <div className="filters-section" style={{ marginBottom: '20px' }}>
                <select
                    value={selectedBatiment}
                    onChange={(e) => setSelectedBatiment(e.target.value)}
                    className="filter-select"
                >
                    <option value="">Tous les bâtiments</option>
                    {batiments.map(b => (
                        <option key={b.id} value={b.id}>{b.libelle}</option>
                    ))}
                </select>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Libellé</th>
                        <th>Capacité</th>
                        <th>Bâtiment</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredSalles.length === 0 ? (
                        <tr><td colSpan="5" style={{ textAlign: "center" }}>Aucune salle trouvée.</td></tr>
                    ) : (
                        filteredSalles.map((salle) => (
                            <tr key={salle.id}>
                                <td>{salle.code}</td>
                                <td>{salle.libelle}</td>
                                <td>{salle.capacite}</td>
                                <td>{salle.batiment ? salle.batiment.libelle : '-'}</td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(salle)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(salle.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier Salle" : "Nouvelle Salle"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Code</label>
                                    <input type="text" name="code" value={formData.code} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group">
                                    <label>Libellé</label>
                                    <input type="text" name="libelle" value={formData.libelle} onChange={handleInputChange} required />
                                </div>
                            </div>

                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Capacité</label>
                                    <input type="number" name="capacite" value={formData.capacite} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group">
                                    <label>Bâtiment</label>
                                    <select name="batimentId" value={formData.batimentId} onChange={handleInputChange} required>
                                        <option value="">Sélectionner</option>
                                        {batiments.map(b => <option key={b.id} value={b.id}>{b.libelle}</option>)}
                                    </select>
                                </div>
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

export default AdminSalles;
