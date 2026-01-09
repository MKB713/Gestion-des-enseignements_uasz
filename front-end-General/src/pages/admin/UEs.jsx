import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css";

const AdminUEs = () => {
    // State for UEs list and Semesters list
    const [ues, setUEs] = useState([]);
    const [semestres, setSemestres] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Modal and Editing states
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentUE, setCurrentUE] = useState(null);

    // Search state
    const [searchTerm, setSearchTerm] = useState("");

    // Form data with requested attributes
    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        credits: 0,
        coefficientUE: 0,
        vht: 0, // Maps to "Heures"
        semestre: "", // Maps to Semestre selection
        description: "" // Maps to Description
    });

    // Fetch data on mount
    useEffect(() => {
        fetchData();
        fetchSemestres();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const data = await apiRequest(API_ENDPOINTS.UES.LIST);
            setUEs(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement UEs:", err);
            setError("Erreur lors du chargement des UEs.");
        } finally {
            setLoading(false);
        }
    };

    const fetchSemestres = async () => {
        try {
            const data = await apiRequest(API_ENDPOINTS.SEMESTRES.LIST);
            setSemestres(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error("Erreur chargement Semestres:", err);
        }
    };

    // Handle form input changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    // Open/Close Modal
    const openModal = (ue = null) => {
        if (ue) {
            setIsEditing(true);
            setCurrentUE(ue);
            setFormData({
                code: ue.code,
                libelle: ue.libelle,
                credits: ue.credits,
                coefficientUE: ue.coefficientUE,
                vht: ue.vht,
                semestre: ue.semestre ? ue.semestre.id : "",
                description: ue.description || ""
            });
        } else {
            setIsEditing(false);
            setCurrentUE(null);
            setFormData({
                code: "",
                libelle: "",
                credits: 0,
                coefficientUE: 0,
                vht: 0,
                semestre: "",
                description: ""
            });
        }
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setError(null);
    };

    // Submit Form
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Construct payload with Semestre object if ID is provided
            // Assuming backend accepts 'semestre: { id: ... }' or handle it via service
            // Usually JSON binding maps nested object if set
            const payload = {
                code: formData.code,
                libelle: formData.libelle,
                credits: parseInt(formData.credits),
                coefficientUE: parseFloat(formData.coefficientUE),
                vht: parseInt(formData.vht),
                description: formData.description,
                semestre: formData.semestre ? { id: parseInt(formData.semestre) } : null
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.UES.UPDATE(currentUE.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.UES.CREATE, {
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
        if (window.confirm("Êtes-vous sûr de vouloir supprimer cette UE ?")) {
            try {
                await apiRequest(API_ENDPOINTS.UES.DELETE(id), { method: "DELETE" });
                fetchData();
            } catch (err) {
                console.error("Erreur suppression:", err);
                alert("Erreur lors de la suppression.");
            }
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return "-";
        return new Date(dateString).toLocaleDateString('fr-FR');
    };

    // Filter UEs
    const filteredUEs = ues.filter(ue =>
        ue.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
        ue.libelle.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Unités d'Enseignement</h2>
                <div className="actions-right">
                    <button className="add-btn" onClick={() => openModal()}>
                        + Nouvelle UE
                    </button>
                </div>
            </div>
            <p style={{ color: '#666', marginBottom: '20px' }}>Gestion des UE, Crédits, Heures, Coefficients et Semestres.</p>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px', gap: '15px' }}>
                <div className="search-bar" style={{ flex: 1 }}>
                    <input
                        type="text"
                        placeholder="Rechercher par Code ou Intitulé..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        style={{ width: '100%', padding: '10px', border: '1px solid #ddd', borderRadius: '5px' }}
                    />
                </div>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>CODE</th>
                        <th>INTITULÉ</th>
                        <th>CRÉDITS</th>
                        <th>HEURES</th>
                        <th>COEFF</th>
                        <th>SEMESTRE</th>
                        <th>DESCRIPTION</th>
                        <th>CONTENU (ECs / Modules)</th>
                        <th>DATE CRÉATION</th>
                        <th>ACTIONS</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredUEs.length === 0 ? (
                        <tr><td colSpan="10" style={{ textAlign: "center" }}>Aucune UE trouvée.</td></tr>
                    ) : (
                        filteredUEs.map((ue) => (
                            <tr key={ue.id}>
                                <td><span className="badge-code badge-gray">{ue.code}</span></td>
                                <td><strong>{ue.libelle}</strong></td>
                                <td><span className="badge-credits">{ue.credits}</span></td>
                                <td>{ue.vht}h</td>
                                <td>{ue.coefficientUE}</td>
                                <td>{ue.semestre ? ue.semestre.libelle : <span style={{ color: '#999' }}>-</span>}</td>
                                <td title={ue.description}>
                                    {ue.description ?
                                        (ue.description.length > 30 ? ue.description.substring(0, 30) + "..." : ue.description)
                                        : "-"
                                    }
                                </td>
                                <td>
                                    <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                                        {/* Display ECs */}
                                        {ue.ecs && ue.ecs.length > 0 && (
                                            <div style={{ fontSize: '0.8em' }}>
                                                <strong>ECs:</strong> {ue.ecs.map(ec => ec.code).join(", ")}
                                            </div>
                                        )}
                                        {/* Display Modules */}
                                        {ue.modules && ue.modules.length > 0 && (
                                            <div style={{ fontSize: '0.8em', color: '#666' }}>
                                                <strong>Modules:</strong> {ue.modules.map(m => m.code).join(", ")}
                                            </div>
                                        )}
                                        {(!ue.ecs || ue.ecs.length === 0) && (!ue.modules || ue.modules.length === 0) && (
                                            <span style={{ color: '#999', fontSize: '0.8em', fontStyle: 'italic' }}>Vide</span>
                                        )}
                                    </div>
                                </td>
                                <td>{formatDate(ue.dateCreation)}</td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(ue)}>modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(ue.id)}>supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content" style={{ maxWidth: '600px' }}>
                        <h3>{isEditing ? "Modifier l'UE" : "Nouvelle UE"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group-row">
                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Code UE</label>
                                    <input type="text" name="code" value={formData.code} onChange={handleInputChange} required placeholder="Ex: UE-MATH-01" />
                                </div>
                                <div className="form-group" style={{ flex: 2 }}>
                                    <label>Intitulé</label>
                                    <input type="text" name="libelle" value={formData.libelle} onChange={handleInputChange} required placeholder="Ex: Mathématiques Générales" />
                                </div>
                            </div>

                            <div className="form-group-row" style={{ display: "flex", gap: "10px" }}>
                                <div className="form-group">
                                    <label>Crédits</label>
                                    <input type="number" name="credits" value={formData.credits} onChange={handleInputChange} />
                                </div>
                                <div className="form-group">
                                    <label>Coefficient</label>
                                    <input type="number" step="0.1" name="coefficientUE" value={formData.coefficientUE} onChange={handleInputChange} />
                                </div>
                                <div className="form-group">
                                    <label>Heures (VHT)</label>
                                    <input type="number" name="vht" value={formData.vht} onChange={handleInputChange} />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Semestre</label>
                                <select name="semestre" value={formData.semestre} onChange={handleInputChange}>
                                    <option value="">Sélectionner un Semestre</option>
                                    {semestres.map(s => (
                                        <option key={s.id} value={s.id}>{s.libelle} ({s.maquette ? s.maquette.code : "Pas de maquette"})</option>
                                    ))}
                                </select>
                            </div>

                            <div className="form-group">
                                <label>Description</label>
                                <textarea name="description" value={formData.description} onChange={handleInputChange} rows="3" placeholder="Description de l'UE..." style={{ width: '100%', padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}></textarea>
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

export default AdminUEs;
