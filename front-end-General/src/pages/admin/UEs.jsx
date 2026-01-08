import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css"; // Reuse existing styles

const AdminUEs = () => {
    const [ues, setUEs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentUE, setCurrentUE] = useState(null);

    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        credits: 0,
        coefficientUE: 0,
        cm: 0,
        td: 0,
        tp: 0,
        vht: 0,
        semestre: "" // TODO: Need Semestre List? 
    });

    // TODO: Fetch Semestres if needed. The model shows @ManyToOne Semestre.
    // However, usually UEs are attached to a Maquette's Semestre.
    // For now, I'll just list UEs and allow basic CRUD. 
    // Ideally UEs are created within a Maquette context, but standalone CRUD is also useful.

    useEffect(() => {
        fetchData();
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

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (ue = null) => {
        if (ue) {
            setIsEditing(true);
            setCurrentUE(ue);
            setFormData({
                code: ue.code,
                libelle: ue.libelle,
                credits: ue.credits,
                coefficientUE: ue.coefficientUE,
                cm: ue.cm,
                td: ue.td,
                tp: ue.tp,
                vht: ue.vht,
                semestre: ue.semestre ? ue.semestre.id : ""
            });
        } else {
            setIsEditing(false);
            setCurrentUE(null);
            setFormData({
                code: "",
                libelle: "",
                credits: 0,
                coefficientUE: 0,
                cm: 0,
                td: 0,
                tp: 0,
                vht: 0,
                semestre: ""
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
                credits: parseInt(formData.credits),
                coefficientUE: parseInt(formData.coefficientUE), // Model says double but let's parse
                cm: parseInt(formData.cm),
                td: parseInt(formData.td),
                tp: parseInt(formData.tp),
                vht: parseInt(formData.vht),
                // semestre: ... // If we had semestre list
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

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Gestion des Unités d'Enseignement (UE)</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouvelle UE
                </button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Libellé</th>
                        <th>Crédits</th>
                        <th>Coeff</th>
                        <th>VHT</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {ues.length === 0 ? (
                        <tr><td colSpan="6" style={{ textAlign: "center" }}>Aucune UE trouvée.</td></tr>
                    ) : (
                        ues.map((ue) => (
                            <tr key={ue.id}>
                                <td>{ue.code}</td>
                                <td>{ue.libelle}</td>
                                <td>{ue.credits}</td>
                                <td>{ue.coefficientUE}</td>
                                <td>{ue.vht}h</td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(ue)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(ue.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{isEditing ? "Modifier l'UE" : "Nouvelle UE"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group-row"> {/* CSS helper needed or default block */}
                                <div className="form-group">
                                    <label>Code</label>
                                    <input type="text" name="code" value={formData.code} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group">
                                    <label>Libellé</label>
                                    <input type="text" name="libelle" value={formData.libelle} onChange={handleInputChange} required />
                                </div>
                            </div>

                            <div className="form-group-row" style={{ display: "flex", gap: "10px" }}>
                                <div className="form-group">
                                    <label>Crédits</label>
                                    <input type="number" name="credits" value={formData.credits} onChange={handleInputChange} />
                                </div>
                                <div className="form-group">
                                    <label>Coefficient</label>
                                    <input type="number" name="coefficientUE" value={formData.coefficientUE} onChange={handleInputChange} />
                                </div>
                            </div>

                            <div className="form-group-row" style={{ display: "flex", gap: "10px" }}>
                                <div className="form-group"><label>CM (h)</label><input type="number" name="cm" value={formData.cm} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>TD (h)</label><input type="number" name="td" value={formData.td} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>TP (h)</label><input type="number" name="tp" value={formData.tp} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>VHT (h)</label><input type="number" name="vht" value={formData.vht} onChange={handleInputChange} /></div>
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
