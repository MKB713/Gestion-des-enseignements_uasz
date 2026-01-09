import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "../admin/AdminDepartments.css";

const MasterCahierTexte = () => {
    const [notes, setNotes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentNote, setCurrentNote] = useState(null);

    const [formData, setFormData] = useState({
        titre: "",
        contenu: "",
        seanceId: "", // Required
        enseignantId: "", // Optional/Required
        objectifsPedagogiques: "",
        activitesRealisees: "",
        travailDemande: "",
        observations: ""
    });

    useEffect(() => {
        fetchNotes();
    }, []);

    const fetchNotes = async () => {
        try {
            setLoading(true);
            const data = await apiRequest(API_ENDPOINTS.CAHIER_TEXTE.LIST);
            setNotes(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement cahier de texte:", err);
            setError("Erreur lors du chargement des notes.");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (note = null) => {
        if (note) {
            setIsEditing(true);
            setCurrentNote(note);
            setFormData({
                titre: note.titre,
                contenu: note.contenu,
                seanceId: note.seanceId,
                enseignantId: note.enseignantId || "",
                objectifsPedagogiques: note.objectifsPedagogiques || "",
                activitesRealisees: note.activitesRealisees || "",
                travailDemande: note.travailDemande || "",
                observations: note.observations || ""
            });
        } else {
            setIsEditing(false);
            setCurrentNote(null);
            setFormData({
                titre: "",
                contenu: "",
                seanceId: "",
                enseignantId: "",
                objectifsPedagogiques: "",
                activitesRealisees: "",
                travailDemande: "",
                observations: ""
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
                seanceId: parseInt(formData.seanceId),
                enseignantId: formData.enseignantId ? parseInt(formData.enseignantId) : null
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.CAHIER_TEXTE.UPDATE(currentNote.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.CAHIER_TEXTE.CREATE, {
                    method: "POST",
                    body: JSON.stringify(payload)
                });
            }
            fetchNotes();
            closeModal();
            alert(isEditing ? "Note modifiée !" : "Note ajoutée !");
        } catch (err) {
            console.error("Erreur enregistrement:", err);
            alert("Erreur: " + (err.message || "Impossible d'enregistrer."));
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Supprimer cette note ?")) {
            try {
                await apiRequest(API_ENDPOINTS.CAHIER_TEXTE.DELETE(id), { method: "DELETE" });
                fetchNotes();
            } catch (err) {
                alert("Erreur suppression.");
            }
        }
    };

    const handleValidate = async (id) => {
        try {
            await apiRequest(API_ENDPOINTS.CAHIER_TEXTE.VALIDATE(id), { method: "PATCH" });
            fetchNotes();
        } catch (err) {
            alert("Erreur validation.");
        }
    }

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Cahier de Texte (Master)</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouvelle Note
                </button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Titre</th>
                        <th>Contenu</th>
                        <th>Séance ID</th>
                        <th>Valide</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {notes.length === 0 ? (
                        <tr><td colSpan="5" style={{ textAlign: "center" }}>Aucune note trouvée.</td></tr>
                    ) : (
                        notes.map((note) => (
                            <tr key={note.id}>
                                <td>{note.titre}</td>
                                <td>{note.contenu.substring(0, 50)}...</td>
                                <td>{note.seanceId}</td>
                                <td>
                                    {note.estValide ? (
                                        <span className="badge badge-success">Oui</span>
                                    ) : (
                                        <button className="edit-btn" onClick={() => handleValidate(note.id)}>Valider</button>
                                    )}
                                </td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(note)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(note.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content" style={{ maxWidth: '800px' }}>
                        <h3>{isEditing ? "Modifier la Note" : "Nouvelle Note"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Titre *</label>
                                    <input type="text" name="titre" value={formData.titre} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group">
                                    <label>ID Séance (Emploi du temps) *</label>
                                    <input type="number" name="seanceId" value={formData.seanceId} onChange={handleInputChange} required />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Contenu *</label>
                                <textarea name="contenu" value={formData.contenu} onChange={handleInputChange} required rows="4"></textarea>
                            </div>

                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Objectifs Pédagogiques</label>
                                    <textarea name="objectifsPedagogiques" value={formData.objectifsPedagogiques} onChange={handleInputChange} rows="2"></textarea>
                                </div>
                                <div className="form-group">
                                    <label>Activités Réalisées</label>
                                    <textarea name="activitesRealisees" value={formData.activitesRealisees} onChange={handleInputChange} rows="2"></textarea>
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Travail Demandé</label>
                                <textarea name="travailDemande" value={formData.travailDemande} onChange={handleInputChange} rows="2"></textarea>
                            </div>

                            <div className="form-group">
                                <label>Observations</label>
                                <textarea name="observations" value={formData.observations} onChange={handleInputChange} rows="2"></textarea>
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

export default MasterCahierTexte;
