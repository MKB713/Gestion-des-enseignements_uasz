import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "../admin/AdminDepartments.css";

const MasterClasses = () => {
    const [classes, setClasses] = useState([]);
    const [formations, setFormations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentClasse, setCurrentClasse] = useState(null);

    const [formData, setFormData] = useState({
        nom: "",
        semestre: "",
        description: "",
        archive: false,
        formation: ""
    });

    const SEMESTRES = ["Semestre 1", "Semestre 2", "Semestre 3", "Semestre 4", "Semestre 5", "Semestre 6"];

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [classesRes, formationsRes] = await Promise.all([
                apiRequest(API_ENDPOINTS.CLASSES.LIST),
                apiRequest(API_ENDPOINTS.FORMATIONS.LIST)
            ]);
            setClasses(classesRes || []);
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
        const { name, value, type, checked } = e.target;
        setFormData({ ...formData, [name]: type === "checkbox" ? checked : value });
    };

    const openModal = (classe = null) => {
        if (classe) {
            setIsEditing(true);
            setCurrentClasse(classe);
            setFormData({
                nom: classe.nom,
                semestre: classe.semestre,
                description: classe.description,
                archive: classe.archive,
                formation: classe.formation ? classe.formation.id : ""
            });
        } else {
            setIsEditing(false);
            setCurrentClasse(null);
            setFormData({
                nom: "",
                semestre: "",
                description: "",
                archive: false,
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
                nom: formData.nom,
                semestre: formData.semestre,
                description: formData.description,
                archive: formData.archive,
                formation: formData.formation ? { id: parseInt(formData.formation) } : null
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.CLASSES.UPDATE(currentClasse.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.CLASSES.CREATE, {
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
        if (window.confirm("Êtes-vous sûr de vouloir supprimer cette classe ?")) {
            try {
                await apiRequest(API_ENDPOINTS.CLASSES.DELETE(id), { method: "DELETE" });
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
                <h2>Gestion des Classes (Master)</h2>
                <button className="add-btn" onClick={() => openModal()} disabled={formations.length === 0}>
                    + Nouvelle Classe
                </button>
            </div>

            {formations.length === 0 && (
                <div className="warning-box" style={{ backgroundColor: "#fff3cd", color: "#856404", padding: "10px", marginBottom: "15px", borderRadius: "5px", border: "1px solid #ffeeba" }}>
                    <strong>Attention :</strong> Vous devez créer des <a href="/master/formations">Formations</a> avant de créer une classe.
                </div>
            )}

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Nom</th>
                        <th>Semestre</th>
                        <th>Formation</th>
                        <th>Statut</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {classes.length === 0 ? (
                        <tr><td colSpan="5" style={{ textAlign: "center" }}>Aucune classe trouvée.</td></tr>
                    ) : (
                        classes.map((classe) => (
                            <tr key={classe.id}>
                                <td>{classe.nom}</td>
                                <td>{classe.semestre}</td>
                                <td>{classe.formation ? classe.formation.libelle : "-"}</td>
                                <td>
                                    {classe.archive ?
                                        <span className="badge badge-danger">Archivée</span> :
                                        <span className="badge badge-success">Active</span>
                                    }
                                </td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(classe)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(classe.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier la Classe" : "Nouvelle Classe"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Nom</label>
                                <input
                                    type="text"
                                    name="nom"
                                    value={formData.nom}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Ex: L1 Info - Grp A"
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
                                <label>Semestre</label>
                                <select
                                    name="semestre"
                                    value={formData.semestre}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="">Sélectionner un semestre</option>
                                    {SEMESTRES.map(s => (
                                        <option key={s} value={s}>{s}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-group">
                                <label>
                                    <input
                                        type="checkbox"
                                        name="archive"
                                        checked={formData.archive}
                                        onChange={handleInputChange}
                                    />
                                    &nbsp;Archiver cette classe
                                </label>
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
                                <button type="submit" className="submit-btn" style={{ backgroundColor: '#006633' }}>Enregistrer</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default MasterClasses;
