import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css";

const AdminECs = () => {
    const [ecs, setECs] = useState([]);
    const [ues, setUEs] = useState([]);
    const [modules, setModules] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentEC, setCurrentEC] = useState(null);

    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        cm: 0,
        td: 0,
        tp: 0,
        tpe: 0,
        vht: 0,
        coefficient: 0,
        ue: "",
        module: ""
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [ecsRes, uesRes, modulesRes] = await Promise.all([
                apiRequest(API_ENDPOINTS.ECS.LIST),
                apiRequest(API_ENDPOINTS.UES.LIST),
                apiRequest(API_ENDPOINTS.MODULES.LIST)
            ]);
            setECs(Array.isArray(ecsRes) ? ecsRes : []);
            setUEs(Array.isArray(uesRes) ? uesRes : []);
            setModules(Array.isArray(modulesRes) ? modulesRes : []);
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

    const openModal = (ec = null) => {
        if (ec) {
            setIsEditing(true);
            setCurrentEC(ec);
            setFormData({
                code: ec.code,
                libelle: ec.libelle,
                cm: ec.cm,
                td: ec.td,
                tp: ec.tp,
                tpe: ec.tpe,
                vht: ec.vht,
                coefficient: ec.coefficient,
                ue: ec.ue ? ec.ue.id : "",
                module: ec.module ? ec.module.id : ""
            });
        } else {
            setIsEditing(false);
            setCurrentEC(null);
            setFormData({
                code: "",
                libelle: "",
                cm: 0,
                td: 0,
                tp: 0,
                tpe: 0,
                vht: 0,
                coefficient: 0,
                ue: "",
                module: ""
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
                cm: parseInt(formData.cm),
                td: parseInt(formData.td),
                tp: parseInt(formData.tp),
                tpe: parseInt(formData.tpe),
                vht: parseInt(formData.vht),
                coefficient: parseFloat(formData.coefficient),
                ue: formData.ue ? { id: parseInt(formData.ue) } : null,
                module: formData.module ? { id: parseInt(formData.module) } : null
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.ECS.UPDATE(currentEC.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.ECS.CREATE, {
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
        if (window.confirm("Êtes-vous sûr de vouloir supprimer cet EC ?")) {
            try {
                await apiRequest(API_ENDPOINTS.ECS.DELETE(id), { method: "DELETE" });
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
                <h2>Gestion des Éléments Constitutifs (EC)</h2>
                <button className="add-btn" onClick={() => openModal()} disabled={ues.length === 0 && modules.length === 0}>
                    + Nouveau EC
                </button>
            </div>

            {(ues.length === 0 && modules.length === 0) && (
                <div className="warning-box" style={{ backgroundColor: "#fff3cd", color: "#856404", padding: "10px", marginBottom: "15px", borderRadius: "5px", border: "1px solid #ffeeba" }}>
                    <strong>Attention :</strong> Créez d'abord des UEs ou des Modules.
                </div>
            )}

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Libellé</th>
                        <th>UE</th>
                        <th>Module</th>
                        <th>VHT</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {ecs.length === 0 ? (
                        <tr><td colSpan="6" style={{ textAlign: "center" }}>Aucun EC trouvé.</td></tr>
                    ) : (
                        ecs.map((ec) => (
                            <tr key={ec.id}>
                                <td>{ec.code}</td>
                                <td>{ec.libelle}</td>
                                <td>{ec.ue ? ec.ue.code : "-"}</td>
                                <td>{ec.module ? ec.module.code : "-"}</td>
                                <td>{ec.vht}h</td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(ec)}>Modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(ec.id)}>Supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content" style={{ maxWidth: '600px' }}>
                        <h3>{isEditing ? "Modifier l'EC" : "Nouveau EC"}</h3>
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

                            <div className="form-group-row" style={{ display: "flex", gap: "10px" }}>
                                <div className="form-group">
                                    <label>UE</label>
                                    <select name="ue" value={formData.ue} onChange={handleInputChange}>
                                        <option value="">-- Aucune --</option>
                                        {ues.map(u => (<option key={u.id} value={u.id}>{u.code} - {u.libelle}</option>))}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label>Module</label>
                                    <select name="module" value={formData.module} onChange={handleInputChange}>
                                        <option value="">-- Aucun --</option>
                                        {modules.map(m => (<option key={m.id} value={m.id}>{m.code} - {m.libelle}</option>))}
                                    </select>
                                </div>
                            </div>

                            <div className="form-group-row" style={{ display: "flex", gap: "5px", flexWrap: "wrap" }}>
                                <div className="form-group" style={{ flex: 1 }}><label>CM (h)</label><input type="number" name="cm" value={formData.cm} onChange={handleInputChange} /></div>
                                <div className="form-group" style={{ flex: 1 }}><label>TD (h)</label><input type="number" name="td" value={formData.td} onChange={handleInputChange} /></div>
                                <div className="form-group" style={{ flex: 1 }}><label>TP (h)</label><input type="number" name="tp" value={formData.tp} onChange={handleInputChange} /></div>
                                <div className="form-group" style={{ flex: 1 }}><label>TPE (h)</label><input type="number" name="tpe" value={formData.tpe} onChange={handleInputChange} /></div>
                                <div className="form-group" style={{ flex: 1 }}><label>VHT (h)</label><input type="number" name="vht" value={formData.vht} onChange={handleInputChange} /></div>
                                <div className="form-group" style={{ flex: 1 }}><label>Coeff</label><input type="number" step="0.1" name="coefficient" value={formData.coefficient} onChange={handleInputChange} /></div>
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

export default AdminECs;
