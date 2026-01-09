import React, { useState, useEffect } from "react";
import {
    Users,
    Search,
    Plus,
    Edit,
    Trash2,
    CheckCircle,
    XCircle,
    X,
    Archive
} from 'lucide-react';
import { apiRequest, API_ENDPOINTS, api } from "../../config/api";
import "./AdminDepartments.css";

const AdminEnseignants = () => {
    const [enseignants, setEnseignants] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentEnseignant, setCurrentEnseignant] = useState(null);

    // Enums based on User Request and Code Logic
    const grades = ["PERMANENT", "VACATAIRE"]; // Maps to 'statut'
    const etats = ["ACTIF", "INACTIF", "ARCHIVE"]; // Maps to 'statutEnseignant'

    const [formData, setFormData] = useState({
        nom: "",
        prenom: "",
        emailPersonnel: "", // Creation only
        telephone: "",
        dateNaissance: "", // Required for User creation
        grade: "", // Maps to 'statut'
        etat: "ACTIF" // Maps to 'statutEnseignant'
    });

    useEffect(() => {
        fetchData();
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
                emailPersonnel: enseignant.mailPersonnel || "",
                telephone: enseignant.telephone || "",
                dateNaissance: enseignant.dateNaissance || "",
                grade: enseignant.statut,
                etat: enseignant.statutEnseignant || "ACTIF"
            });
        } else {
            setIsEditing(false);
            setCurrentEnseignant(null);
            setFormData({
                nom: "",
                prenom: "",
                emailPersonnel: "",
                telephone: "",
                dateNaissance: "",
                grade: "PERMANENT",
                etat: "ACTIF"
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

        // Validation Age
        if (formData.dateNaissance) {
            const birthDate = new Date(formData.dateNaissance);
            const today = new Date();
            let age = today.getFullYear() - birthDate.getFullYear();
            const m = today.getMonth() - birthDate.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            if (age < 25) {
                alert("L'enseignant doit avoir au moins 25 ans.");
                return;
            }
        }

        try {
            if (isEditing) {
                const payload = {
                    id: currentEnseignant.id,
                    nom: formData.nom,
                    prenom: formData.prenom,
                    telephone: formData.telephone,
                    mailPersonnel: formData.emailPersonnel,
                    statut: formData.grade,
                    statutEnseignant: formData.etat,
                    matricule: currentEnseignant.matricule,
                    email: currentEnseignant.email
                };

                await apiRequest(API_ENDPOINTS.ENSEIGNANTS.UPDATE(currentEnseignant.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                // Step 1: Create User
                const authPayload = {
                    nom: formData.nom,
                    prenom: formData.prenom,
                    emailPersonnel: formData.emailPersonnel,
                    telephone: formData.telephone,
                    dateNaissance: formData.dateNaissance,
                    role: "ENSEIGNANT"
                };

                const userResponse = await apiRequest(API_ENDPOINTS.USERS.LIST, {
                    method: "POST",
                    body: JSON.stringify(authPayload)
                });

                if (!userResponse || !userResponse.matricule) {
                    throw new Error("Echec de la création du compte utilisateur.");
                }

                // Step 2: Create Enseignant
                const enseignantPayload = {
                    matricule: parseInt(userResponse.matricule),
                    email: userResponse.email,
                    nom: formData.nom,
                    prenom: formData.prenom,
                    telephone: formData.telephone,
                    mailPersonnel: formData.emailPersonnel,
                    statut: formData.grade,
                    statutEnseignant: formData.etat,
                    dateNaissance: formData.dateNaissance
                };

                await apiRequest(API_ENDPOINTS.ENSEIGNANTS.CREATE, {
                    method: "POST",
                    body: JSON.stringify(enseignantPayload)
                });
            }

            fetchData();
            closeModal();
            alert(isEditing ? "Enseignant modifié !" : "Enseignant créé !");
        } catch (err) {
            console.error("Erreur enregistrement:", err);
            alert("Erreur: " + (err.message || "Erreur inconnue"));
        }
    };

    // Permanent Delete
    const handleDelete = async (enseignant) => {
        if (window.confirm("Êtes-vous sûr de vouloir SUPPRIMER définitivement cet enseignant ?")) {
            try {
                // Uses API_ENDPOINTS.ENSEIGNANTS.DELETE
                await api.delete(API_ENDPOINTS.ENSEIGNANTS.DELETE(enseignant.id));
                fetchData();
                alert("Enseignant supprimé.");
            } catch (err) {
                console.error("Erreur suppression:", err);
                alert("Erreur lors de la suppression.");
            }
        }
    };

    // Archive (sets inactive/archive)
    const handleArchive = async (enseignant) => {
        if (window.confirm("Voulez-vous archiver cet enseignant ? Il deviendra INACTIF.")) {
            try {
                // Call /archiver endpoint
                await apiRequest(`${API_ENDPOINTS.ENSEIGNANTS.base}/${enseignant.id}/archiver`, { method: "PATCH" });
                // Also ensure it is deactivated if not handled by backend
                // Or user requested "sera inactif".
                // We'll update state locally or re-fetch.
                fetchData();
                alert("Enseignant archivé.");
            } catch (err) {
                console.error("Erreur archivage:", err);
                alert("Erreur lors de l'archivage.");
            }
        }
    };

    const handleToggleStatus = async (enseignant) => {
        try {
            if (enseignant.estActif) {
                await apiRequest(`${API_ENDPOINTS.ENSEIGNANTS.base}/${enseignant.id}/desactiver`, { method: "PATCH" });
            } else {
                await apiRequest(`${API_ENDPOINTS.ENSEIGNANTS.base}/${enseignant.id}/activer`, { method: "PATCH" });
            }
            fetchData();
        } catch (err) {
            console.error("Erreur changement statut:", err);
        }
    };

    if (loading) return <div className="loading">Chargement...</div>;

    const today = new Date();
    const maxDate = new Date(today.getFullYear() - 25, today.getMonth(), today.getDate()).toISOString().split('T')[0];

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Enseignants</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    <Plus size={18} style={{ marginRight: '8px' }} /> Nouvel Enseignant
                </button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Matricule</th>
                        <th>Prénom & Nom</th>
                        <th>Email Professionnel</th>
                        <th>Type (Grade)</th>
                        <th>État</th>
                        <th style={{ textAlign: 'right' }}>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {enseignants.length === 0 ? (
                        <tr><td colSpan="6" style={{ textAlign: "center" }}>Aucun enseignant trouvé.</td></tr>
                    ) : (
                        enseignants.map((enseignant) => (
                            <tr key={enseignant.id}>
                                <td><span className="badge-code">{enseignant.matricule}</span></td>
                                <td><strong>{enseignant.prenom} {enseignant.nom}</strong></td>
                                <td>{enseignant.email}</td>
                                <td>{enseignant.statut}</td>
                                <td>
                                    <span
                                        className={`badge ${enseignant.estActif ? 'badge-success' : 'badge-danger'}`}
                                        style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '5px', width: 'fit-content' }}
                                        onClick={() => handleToggleStatus(enseignant)}
                                        title={"Statut: " + enseignant.statutEnseignant}
                                    >
                                        {enseignant.estActif ? <CheckCircle size={14} /> : <XCircle size={14} />}
                                        {enseignant.estActif ? "Actif" : "Inactif"}
                                    </span>
                                </td>
                                <td>
                                    <div className="actions-cell">
                                        <button className="btn-icon edit" title="Modifier" onClick={() => openModal(enseignant)}>
                                            <Edit size={18} />
                                        </button>
                                        <button className="btn-icon archive" title="Archiver" onClick={() => handleArchive(enseignant)} style={{ color: '#ca8a04' }}>
                                            <Archive size={18} />
                                        </button>
                                        <button className="btn-icon delete" title="Supprimer" onClick={() => handleDelete(enseignant)}>
                                            <Trash2 size={18} />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <div className="modal-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                            <h3>{isEditing ? "Modifier l'Enseignant" : "Nouvel Enseignant"}</h3>
                            <button className="close-btn" onClick={closeModal} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                                <X size={24} />
                            </button>
                        </div>

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

                            {!isEditing && (
                                <div className="form-group">
                                    <label>Email Personnel (pour envoi identifiants)</label>
                                    <input type="email" name="emailPersonnel" value={formData.emailPersonnel} onChange={handleInputChange} required />
                                </div>
                            )}

                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Téléphone</label>
                                    <input type="text" name="telephone" value={formData.telephone} onChange={handleInputChange} />
                                </div>
                                <div className="form-group">
                                    <label>Date de Naissance (+25 ans)</label>
                                    <input
                                        type="date"
                                        name="dateNaissance"
                                        value={formData.dateNaissance}
                                        onChange={handleInputChange}
                                        required={!isEditing}
                                        max={maxDate}
                                    />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Grade (Type)</label>
                                <select name="grade" value={formData.grade} onChange={handleInputChange} required>
                                    {grades.map(g => <option key={g} value={g}>{g}</option>)}
                                </select>
                            </div>
                            <div className="form-group">
                                <label>Statut</label>
                                <select name="etat" value={formData.etat} onChange={handleInputChange} required>
                                    {etats.map(s => <option key={s} value={s}>{s}</option>)}
                                </select>
                            </div>

                            {isEditing && (
                                <div className="form-group" style={{ background: '#f8fafc', padding: '10px', borderRadius: '5px' }}>
                                    <small style={{ color: '#64748b' }}>Note: Le Matricule et l'Email Professionnel ne sont pas modifiables ici.</small>
                                </div>
                            )}

                            <div className="modal-actions">
                                <button type="button" onClick={closeModal} className="cancel-btn">Annuler</button>
                                <button type="submit" className="submit-btn">{isEditing ? "Enregistrer" : "Créer et Envoyer"}</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminEnseignants;
