import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css";

const AdminDeroulementClasses = () => {
    const [classes, setClasses] = useState([]);
    const [filieres, setFilieres] = useState([]);
    const [niveaux, setNiveaux] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentClasse, setCurrentClasse] = useState(null);

    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        description: "",
        anneeAcademique: "",
        effectifMax: "",
        filiereId: "",
        niveauId: ""
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [classesRes, filieresRes, niveauxRes] = await Promise.all([
                apiRequest(API_ENDPOINTS.DEROULEMENT_CLASSES.LIST).catch(() => []),
                apiRequest(API_ENDPOINTS.FILIERES.LIST).catch(() => []),
                apiRequest(API_ENDPOINTS.NIVEAUX.LIST).catch(() => [])
            ]);

            setClasses(Array.isArray(classesRes) ? classesRes : []);
            setFilieres(Array.isArray(filieresRes) ? filieresRes : []);
            setNiveaux(Array.isArray(niveauxRes) ? niveauxRes : []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement données:", err);
            setError("Erreur lors du chargement des classes.");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (classe = null) => {
        if (classe) {
            setIsEditing(true);
            setCurrentClasse(classe);
            setFormData({
                code: classe.code,
                libelle: classe.libelle,
                description: classe.description || "",
                anneeAcademique: classe.anneeAcademique || "",
                effectifMax: classe.effectifMax || "",
                filiereId: classe.filiereId || "",
                niveauId: classe.niveauId || ""
            });
        } else {
            setIsEditing(false);
            setCurrentClasse(null);
            setFormData({
                code: "",
                libelle: "",
                description: "",
                anneeAcademique: new Date().getFullYear() + "-" + (new Date().getFullYear() + 1), // Default current year
                effectifMax: "",
                filiereId: "",
                niveauId: ""
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
                effectifMax: parseInt(formData.effectifMax),
                filiereId: parseInt(formData.filiereId),
                niveauId: parseInt(formData.niveauId)
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.DEROULEMENT_CLASSES.UPDATE(currentClasse.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.DEROULEMENT_CLASSES.CREATE, {
                    method: "POST",
                    body: JSON.stringify(payload)
                });
            }
            fetchData();
            closeModal();
            alert(isEditing ? "Classe modifiée !" : "Classe ajoutée !");
        } catch (err) {
            console.error("Erreur enregistrement:", err);
            alert("Erreur: " + (err.message || "Impossible d'enregistrer."));
        }
    };

    const handleArchive = async (id, isArchived) => {
        try {
            if (isArchived) {
                await apiRequest(API_ENDPOINTS.DEROULEMENT_CLASSES.DESARCHIVE(id), { method: "PATCH" });
            } else {
                if (!window.confirm("Archiver cette classe ?")) return;
                await apiRequest(API_ENDPOINTS.DEROULEMENT_CLASSES.ARCHIVE(id), { method: "PATCH" });
            }
            fetchData();
        } catch (err) {
            alert("Erreur changement statut.");
        }
    };

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Classes (Année en cours)</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouvelle Classe
                </button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Libellé</th>
                        <th>Année</th>
                        <th>Effectif Max</th>
                        <th>État</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {classes.length === 0 ? (
                        <tr><td colSpan="6" style={{ textAlign: "center" }}>Aucune classe trouvée.</td></tr>
                    ) : (
                        classes.map((cls) => (
                            <tr key={cls.id}>
                                <td>{cls.code}</td>
                                <td>{cls.libelle}</td>
                                <td>{cls.anneeAcademique}</td>
                                <td>{cls.effectifMax}</td>
                                <td>
                                    <span
                                        className={`badge ${cls.estArchivee ? 'badge-danger' : 'badge-success'}`}
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => handleArchive(cls.id, cls.estArchivee)}
                                    >
                                        {cls.estArchivee ? "Archivée" : "Active"}
                                    </span>
                                </td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(cls)}>Modifier</button>
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
                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Code</label>
                                    <input type="text" name="code" value={formData.code} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group">
                                    <label>Année Académique</label>
                                    <input type="text" name="anneeAcademique" value={formData.anneeAcademique} onChange={handleInputChange} required placeholder="2023-2024" />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Libellé</label>
                                <input type="text" name="libelle" value={formData.libelle} onChange={handleInputChange} required />
                            </div>

                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Filière</label>
                                    <select name="filiereId" value={formData.filiereId} onChange={handleInputChange} required>
                                        <option value="">Sélectionner une filière</option>
                                        {filieres.map(f => <option key={f.id} value={f.id}>{f.libelle}</option>)}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label>Niveau</label>
                                    <select name="niveauId" value={formData.niveauId} onChange={handleInputChange} required>
                                        <option value="">Sélectionner un niveau</option>
                                        {niveaux.map(n => <option key={n.id} value={n.id}>{n.libelle}</option>)}
                                    </select>
                                </div>
                            </div>

                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Effectif Max</label>
                                    <input type="number" name="effectifMax" value={formData.effectifMax} onChange={handleInputChange} required />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Description</label>
                                <textarea name="description" value={formData.description} onChange={handleInputChange} rows="2"></textarea>
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

export default AdminDeroulementClasses;
