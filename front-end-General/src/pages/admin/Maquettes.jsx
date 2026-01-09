import React, { useState, useEffect } from "react";
import { Edit, Trash2 } from 'lucide-react';
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css";

const AdminMaquettes = () => {
    // State management
    const [maquettes, setMaquettes] = useState([]);
    const [modules, setModules] = useState([]);
    const [semestres, setSemestres] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentMaquette, setCurrentMaquette] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");

    // Form Data - Matching request
    const [formData, setFormData] = useState({
        code: "",
        libelle: "",
        module: "", // Selection (Module ID)
        credits: 0,
        coefficientUE: 0,
        cm: 0,
        td: 0,
        tp: 0,
        vht: 0, // CM+TP/TD = VHT (Input field)
        semestre: "", // Selection (Semestre ID)
        responsable: "",
        prerequis: "",
        objectifs: "",
        modalitesEvaluation: ""
    });

    useEffect(() => {
        fetchData();
        fetchDependencies();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const data = await apiRequest(API_ENDPOINTS.MAQUETTES.LIST);
            setMaquettes(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error("Erreur chargement maquettes:", err);
            setError("Erreur lors du chargement des maquettes.");
        } finally {
            setLoading(false);
        }
    };

    const fetchDependencies = async () => {
        try {
            const mods = await apiRequest(API_ENDPOINTS.MODULES.LIST);
            setModules(Array.isArray(mods) ? mods : []);
            const sems = await apiRequest(API_ENDPOINTS.SEMESTRES.LIST);
            setSemestres(Array.isArray(sems) ? sems : []);
        } catch (e) {
            console.error("Erreur chargement dépendances:", e);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const openModal = (maq = null) => {
        if (maq) {
            setIsEditing(true);
            setCurrentMaquette(maq);
            setFormData({
                code: maq.code,
                libelle: maq.libelle,
                module: maq.module ? maq.module.id : "",
                credits: maq.credits,
                coefficientUE: maq.coefficientUE,
                cm: maq.cm,
                td: maq.td,
                tp: maq.tp,
                vht: maq.vht,
                semestre: maq.semestre ? maq.semestre.id : "",
                responsable: maq.responsable || "",
                prerequis: maq.prerequis || "",
                objectifs: maq.objectifs || "",
                modalitesEvaluation: maq.modalitesEvaluation || ""
            });
        } else {
            setIsEditing(false);
            setCurrentMaquette(null);
            setFormData({
                code: "",
                libelle: "",
                module: "",
                credits: 0,
                coefficientUE: 0,
                cm: 0,
                td: 0,
                tp: 0,
                vht: 0,
                semestre: "",
                responsable: "",
                prerequis: "",
                objectifs: "",
                modalitesEvaluation: ""
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
                coefficientUE: parseFloat(formData.coefficientUE),
                cm: parseInt(formData.cm),
                td: parseInt(formData.td),
                tp: parseInt(formData.tp),
                vht: parseInt(formData.vht),
                responsable: formData.responsable,
                prerequis: formData.prerequis,
                objectifs: formData.objectifs,
                modalitesEvaluation: formData.modalitesEvaluation,
                module: formData.module ? { id: parseInt(formData.module) } : null,
                semestre: formData.semestre ? { id: parseInt(formData.semestre) } : null
            };

            if (isEditing) {
                await apiRequest(API_ENDPOINTS.MAQUETTES.UPDATE(currentMaquette.id), {
                    method: "PUT",
                    body: JSON.stringify(payload)
                });
            } else {
                await apiRequest(API_ENDPOINTS.MAQUETTES.CREATE, {
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
        if (window.confirm("Êtes-vous sûr de vouloir supprimer cette Maquette ?")) {
            try {
                await apiRequest(API_ENDPOINTS.MAQUETTES.DELETE(id), { method: "DELETE" });
                fetchData();
            } catch (err) {
                console.error("Erreur suppression:", err);
                alert("Erreur lors de la suppression.");
            }
        }
    };

    const filteredMaquettes = maquettes.filter(m =>
        m.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
        m.libelle.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return <div className="loading">Chargement...</div>;

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>Maquettes Pédagogiques</h2>
                <button className="add-btn" onClick={() => openModal()}>
                    + Nouvelle Entrée
                </button>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px', gap: '15px' }}>
                <div className="search-bar" style={{ flex: 1 }}>
                    <input type="text" placeholder="Rechercher..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
                        style={{ width: '100%', padding: '10px', border: '1px solid #ddd', borderRadius: '5px' }} />
                </div>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="departments-table">
                <thead>
                    <tr>
                        <th>CODE</th>
                        <th>LIBELLÉ</th>
                        <th>MODULE</th>
                        <th>CRÉDITS</th>
                        <th>CM</th>
                        <th>TD</th>
                        <th>TP</th>
                        <th>VHT</th>
                        <th>COEFF</th>
                        <th>SEMESTRE</th>
                        <th>RESPONSABLE</th>
                        <th>ACTIONS</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredMaquettes.length === 0 ? (
                        <tr><td colSpan="12" style={{ textAlign: "center" }}>Aucune donnée.</td></tr>
                    ) : (
                        filteredMaquettes.map((m) => (
                            <tr key={m.id}>
                                <td><span className="badge-code">{m.code}</span></td>
                                <td><strong>{m.libelle}</strong></td>
                                <td>{m.module ? m.module.code : "-"}</td>
                                <td><span className="badge-credits">{m.credits}</span></td>
                                <td>{m.cm}</td>
                                <td>{m.td}</td>
                                <td>{m.tp}</td>
                                <td>{m.vht}</td>
                                <td>{m.coefficientUE}</td>
                                <td>{m.semestre ? m.semestre.libelle : "-"}</td>
                                <td>{m.responsable}</td>
                                <td className="actions-cell">
                                    <button className="btn-icon edit" title="Modifier" onClick={() => openModal(m)}>
                                        <Edit size={18} />
                                    </button>
                                    <button className="btn-icon delete" title="Supprimer" onClick={() => handleDelete(m.id)}>
                                        <Trash2 size={18} />
                                    </button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content" style={{ maxWidth: '800px', maxHeight: '90vh', overflowY: 'auto' }}>
                        <h3>{isEditing ? "Modifier" : "Nouveau"}</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group-row">
                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Code</label>
                                    <input type="text" name="code" value={formData.code} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group" style={{ flex: 2 }}>
                                    <label>Libellé</label>
                                    <input type="text" name="libelle" value={formData.libelle} onChange={handleInputChange} required />
                                </div>
                            </div>

                            <div className="form-group-row">
                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Module</label>
                                    <select name="module" value={formData.module} onChange={handleInputChange}>
                                        <option value="">Sélectionner</option>
                                        {modules.map(mod => (
                                            <option key={mod.id} value={mod.id}>{mod.code} - {mod.libelle}</option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Semestre</label>
                                    <select name="semestre" value={formData.semestre} onChange={handleInputChange}>
                                        <option value="">Sélectionner</option>
                                        <option value="1">Semestre 1</option>
                                        <option value="2">Semestre 2</option>
                                        <option value="3">Semestre 3</option>
                                        <option value="4">Semestre 4</option>
                                        <option value="5">Semestre 5</option>
                                        <option value="6">Semestre 6</option>
                                    </select>
                                </div>
                            </div>

                            <div className="form-group-row" style={{ display: 'flex', gap: '10px' }}>
                                <div className="form-group"><label>Crédits</label><input type="number" name="credits" value={formData.credits} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>Coeff UE</label><input type="number" step="0.1" name="coefficientUE" value={formData.coefficientUE} onChange={handleInputChange} /></div>
                            </div>

                            <div className="form-group-row" style={{ display: 'flex', gap: '10px' }}>
                                <div className="form-group"><label>CM</label><input type="number" name="cm" value={formData.cm} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>TD</label><input type="number" name="td" value={formData.td} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>TP</label><input type="number" name="tp" value={formData.tp} onChange={handleInputChange} /></div>
                                <div className="form-group"><label>VHT</label><input type="number" name="vht" value={formData.vht} onChange={handleInputChange} /></div>
                            </div>

                            <div className="form-group">
                                <label>Responsable</label>
                                <input type="text" name="responsable" value={formData.responsable} onChange={handleInputChange} />
                            </div>

                            <div className="form-group">
                                <label>Prérequis</label>
                                <textarea name="prerequis" value={formData.prerequis} onChange={handleInputChange} rows="2"></textarea>
                            </div>
                            <div className="form-group">
                                <label>Objectifs</label>
                                <textarea name="objectifs" value={formData.objectifs} onChange={handleInputChange} rows="2"></textarea>
                            </div>
                            <div className="form-group">
                                <label>Modalités d'évaluation</label>
                                <textarea name="modalitesEvaluation" value={formData.modalitesEvaluation} onChange={handleInputChange} rows="2"></textarea>
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

export default AdminMaquettes;
