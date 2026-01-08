import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css"; // Reuse existing styles

const AdminEtudiants = () => {
    const [etudiants, setEtudiants] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentEtudiant, setCurrentEtudiant] = useState(null);

    const [formData, setFormData] = useState({
        nom: "",
        prenom: "",
        matricule: "",
        email: "",
        telephone: "",
        dateNaissance: ""
    });

    useEffect(() => {
        fetchEtudiants();
    }, []);

    const fetchEtudiants = async () => {
        try {
            setLoading(true);
            const data = await apiRequest(API_ENDPOINTS.DEROULEMENT_ETUDIANTS.LIST);
            setEtudiants(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement étudiants:", err);
            setError("Erreur lors du chargement des étudiants.");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (etudiant = null) => {
        if (etudiant) {
            setIsEditing(true);
            setCurrentEtudiant(etudiant);
            setFormData({
                nom: etudiant.nom,
                prenom: etudiant.prenom,
                matricule: etudiant.matricule,
                email: etudiant.email || "",
                telephone: etudiant.telephone || "",
                dateNaissance: etudiant.dateNaissance || ""
            });
        } else {
            setIsEditing(false);
            setCurrentEtudiant(null);
            setFormData({
                nom: "",
                prenom: "",
                matricule: "",
                email: "",
                telephone: "",
                dateNaissance: ""
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
                await apiRequest(API_ENDPOINTS.DEROULEMENT_ETUDIANTS.UPDATE(currentEtudiant.id), {
                    method: "PUT",
                    body: JSON.stringify(formData)
                });
            } else {
                await apiRequest(API_ENDPOINTS.DEROULEMENT_ETUDIANTS.CREATE, {
                    method: "POST",
                    body: JSON.stringify(formData)
                });
            }
            fetchEtudiants();
            closeModal();
            alert(isEditing ? "Étudiant modifié !" : "Étudiant ajouté !");
        } catch (err) {
            console.error("Erreur enregistrement:", err);
            alert("Erreur: " + (err.message || "Impossible d'enregistrer."));
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer cet étudiant ?")) {
            try {
                await apiRequest(API_ENDPOINTS.DEROULEMENT_ETUDIANTS.DELETE(id), { method: "DELETE" });
                fetchEtudiants();
            } catch (err) {
                alert("Erreur lors de la suppression.");
            }
        }
    };

    const handleToggleStatus = async (etudiant) => {
        // Assume backend has suspendre/reactiver logics
        // We check etudiant.statut (Enum) or similar.
        // Controller exposes /suspendre and /reactiver
        // Assuming 'ACTIF' is the active status.
        // Let's assume etudiant object has 'statut' == 'ACTIF' based on backend code if possible, or we check generic button actions.
        // Code says: model.addAttribute("etudiantsActifs", ...). Etudiant.java has Enum StatutEtudiant.

        try {
            // If we don't know status easily, we can provide two buttons or smarter toggle if we read 'statut'.
            // Assuming etudiant.statut is exposed string.
            if (etudiant.statut === 'ACTIF') {
                await apiRequest(API_ENDPOINTS.DEROULEMENT_ETUDIANTS.SUSPEND(etudiant.id), { method: "PATCH" });
            } else {
                await apiRequest(API_ENDPOINTS.DEROULEMENT_ETUDIANTS.REACTIVATE(etudiant.id), { method: "PATCH" });
            }
            fetchEtudiants();
        } catch (err) {
            alert("Erreur changement statut.");
        }
    }

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Étudiants (Scolarité)</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouvel Étudiant
                </button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Matricule</th>
                        <th>Prénom & Nom</th>
                        <th>Email</th>
                        <th>Statut</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {etudiants.length === 0 ? (
                        <tr><td colSpan="5" style={{ textAlign: "center" }}>Aucun étudiant trouvé.</td></tr>
                    ) : (
                        etudiants.map((etudiant) => (
                            <tr key={etudiant.id}>
                                <td>{etudiant.matricule}</td>
                                <td>{etudiant.prenom} {etudiant.nom}</td>
                                <td>{etudiant.email}</td>
                                <td>
                                    <span
                                        className={`badge ${etudiant.statut === 'ACTIF' ? 'badge-success' : 'badge-danger'}`}
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => handleToggleStatus(etudiant)}
                                    >
                                        {etudiant.statut}
                                    </span>
                                </td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(etudiant)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(etudiant.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier l'Étudiant" : "Nouvel Étudiant"}</h3>
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
                            <div className="form-group">
                                <label>Matricule</label>
                                <input type="text" name="matricule" value={formData.matricule} onChange={handleInputChange} required />
                            </div>
                            <div className="form-group">
                                <label>Email</label>
                                <input type="email" name="email" value={formData.email} onChange={handleInputChange} required />
                            </div>
                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Téléphone</label>
                                    <input type="text" name="telephone" value={formData.telephone} onChange={handleInputChange} />
                                </div>
                                <div className="form-group">
                                    <label>Date de Naissance</label>
                                    <input type="date" name="dateNaissance" value={formData.dateNaissance} onChange={handleInputChange} />
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

export default AdminEtudiants;
