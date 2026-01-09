import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css";

const AdminECs = () => {
    // States
    const [ecs, setECs] = useState([]);
    const [ues, setUEs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentEC, setCurrentEC] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");

    // Form Data with requested attributes
    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        cm: 0,
        td: 0,
        tp: 0,
        tpe: 0,
        coefficient: 0,
        description: "",
        ue: "" // UE selection
    });

    // Load Data
    useEffect(() => {
        fetchData();
        fetchUEs();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const data = await apiRequest(API_ENDPOINTS.ECS.LIST);
            setECs(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement ECs:", err);
            setError("Erreur lors du chargement des ECs.");
        } finally {
            setLoading(false);
        }
    };

    const fetchUEs = async () => {
        try {
            const data = await apiRequest(API_ENDPOINTS.UES.LIST);
            setUEs(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error("Erreur chargement UEs:", err);
        }
    };

    // Form Handling
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
                coefficient: ec.coefficient,
                description: ec.description || "",
                ue: ec.ue ? ec.ue.id : ""
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
                coefficient: 0,
                description: "",
                ue: ""
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
                coefficient: parseFloat(formData.coefficient),
                description: formData.description,
                ue: formData.ue ? { id: parseInt(formData.ue) } : null
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

    const filteredECs = ecs.filter(ec =>
        ec.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
        ec.libelle.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return <div className="loading">Chargement...</div>;

    // Calcul du Total Heures (CM + TD + TP) - TPE est souvent travail personnel
    const calculateTotal = (ec) => (ec.cm || 0) + (ec.td || 0) + (ec.tp || 0);

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Éléments Constitutifs (EC)</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouvel EC
                </button>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px', gap: '15px' }}>
                <div className="search-bar" style={{ flex: 1 }}>
                    <input
                        type="text"
                        placeholder="Rechercher par Code ou Libellé..."
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
                        <th>LIBELLÉ</th>
                        <th>UE PARENT</th>
                        <th>CM</th>
                        <th>TD</th>
                        <th>TP</th>
                        <th>TPE</th>
                        <th>COEFF</th>
                        <th>MODULES</th>
                        <th>ACTIONS</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredECs.length === 0 ? (
                        <tr><td colSpan="10" style={{ textAlign: "center" }}>Aucun EC trouvé.</td></tr>
                    ) : (
                        filteredECs.map((ec) => (
                            <tr key={ec.id}>
                                <td><span className="badge-code badge-blue">{ec.code}</span></td>
                                <td><strong>{ec.libelle}</strong></td>
                                <td>{ec.ue ? ec.ue.code : "-"}</td>
                                <td>{ec.cm}</td>
                                <td>{ec.td}</td>
                                <td>{ec.tp}</td>
                                <td>{ec.tpe}</td>
                                <td>{ec.coefficient}</td>
                                <td>
                                    {ec.modules && ec.modules.length > 0 ? (
                                        ec.modules.map(m => (
                                            <span key={m.id} className="badge-code badge-purple" style={{ marginRight: '3px' }}>
                                                {m.code}
                                            </span>
                                        ))
                                    ) : "-"}
                                </td>
                                <td className="actions-cell">
                                    <button className="edit-btn" onClick={() => openModal(ec)}>modifier</button>
                                    <button className="delete-btn" onClick={() => handleDelete(ec.id)}>supprimer</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content" style={{ maxWidth: '700px' }}>
                        <h3>{isEditing ? "Modifier l'EC" : "Nouvel EC"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group-row">
                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Code EC</label>
                                    <input type="text" name="code" value={formData.code} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group" style={{ flex: 2 }}>
                                    <label>Libellé</label>
                                    <input type="text" name="libelle" value={formData.libelle} onChange={handleInputChange} required />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>UE de rattachement</label>
                                <select name="ue" value={formData.ue} onChange={handleInputChange}>
                                    <option value="">Sélectionner une UE</option>
                                    {ues.map(u => (
                                        <option key={u.id} value={u.id}>{u.code} - {u.libelle}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="form-group-row" style={{ display: "flex", gap: "10px" }}>
                                <div className="form-group"><label>CM (h)</label><input type="number" name="cm" value={formData.cm} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>TD (h)</label><input type="number" name="td" value={formData.td} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>TP (h)</label><input type="number" name="tp" value={formData.tp} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>TPE (h)</label><input type="number" name="tpe" value={formData.tpe} onChange={handleInputChange} /></div>
                            </div>

                            <div className="form-group-row" style={{ display: "flex", gap: "10px" }}>
                                <div className="form-group">
                                    <label>Coefficient</label>
                                    <input type="number" step="0.1" name="coefficient" value={formData.coefficient} onChange={handleInputChange} />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Description</label>
                                <textarea name="description" value={formData.description} onChange={handleInputChange} rows="2" style={{ width: '100%', padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}></textarea>
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
