import React, { useState, useEffect } from "react";
import { Edit, Trash2 } from 'lucide-react';
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./AdminDepartments.css";

const AdminMaquettes = () => {
    // State management
    const [maquettes, setMaquettes] = useState([]);
    const [modules, setModules] = useState([]);
    const [semestres, setSemestres] = useState([]);
    const [formations, setFormations] = useState([]);
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
        formationId: "",
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

    const [ecs, setEcs] = useState([]);
    const [selectedEcId, setSelectedEcId] = useState("");

    useEffect(() => {
        fetchData();
        fetchDependencies();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const response = await apiRequest(API_ENDPOINTS.MAQUETTES.LIST);
            setMaquettes(Array.isArray(response) ? response : (response.data || []));
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
            const forms = await apiRequest(API_ENDPOINTS.FORMATIONS.LIST);
            setFormations(Array.isArray(forms) ? forms : []);
            const ecsData = await apiRequest(API_ENDPOINTS.ECS.LIST);
            setEcs(Array.isArray(ecsData) ? ecsData : []);
        } catch (e) {
            console.error("Erreur chargement dépendances:", e);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    // Autofill when Module (UE) is selected
    const handleModuleChange = (e) => {
        const moduleId = e.target.value;
        setFormData(prev => ({ ...prev, module: moduleId }));

        if (moduleId) {
            const selectedModule = modules.find(m => m.id === parseInt(moduleId));
            if (selectedModule && selectedModule.ue) {
                setFormData(prev => ({
                    ...prev,
                    module: moduleId,
                    credits: selectedModule.ue.credits || prev.credits,
                    coefficientUE: selectedModule.ue.coefficientUE || prev.coefficientUE,
                    // Optional: if module implies other defaults
                }));
            }
        }
    };

    // Autofill when EC is selected
    const handleECChange = (e) => {
        const ecId = e.target.value;
        setSelectedEcId(ecId);

        if (ecId) {
            const selectedEc = ecs.find(ec => ec.id === parseInt(ecId));
            if (selectedEc) {
                setFormData(prev => ({
                    ...prev,
                    code: selectedEc.code,
                    libelle: selectedEc.libelle,
                    cm: selectedEc.cm,
                    td: selectedEc.td,
                    tp: selectedEc.tp,
                    vht: selectedEc.vht,
                    // If EC has coefficient, use it? Maquette has coeffUE. 
                    // Assuming EC doesn't override UE coeff usually, or user can edit manually.
                }));
            }
        }
    };

    const openModal = (maq = null) => {
        if (maq) {
            setIsEditing(true);
            setCurrentMaquette(maq);
            // Try to find if this maquette matches an EC by code/libelle to set dropdown?
            // It's a "nice to have", but risky if not exact match.
            // For now, reset EC selector to empty on Edit, or leave as is. 
            // Better to leave empty as we are editing the SNAPSHOT.
            setSelectedEcId("");

            setFormData({
                code: maq.code,
                libelle: maq.libelle,
                formationId: maq.formation ? maq.formation.id : "",
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
            setSelectedEcId("");
            setFormData({
                code: "",
                libelle: "",
                formationId: "",
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
                formationId: parseInt(formData.formationId),
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

    // Group Maquettes by Module (UE)
    const groupedMaquettes = filteredMaquettes.reduce((acc, curr) => {
        // Robust Grouping: Try Module.UE.ID, then Module.ID, then generic.
        // If module has a UE, that's the grouping key.
        const module = curr.module;
        let key = "orphan";
        let moduleName = "Sans Module";

        if (module) {
            if (module.ue) {
                key = `ue_${module.ue.id}`;
                // Keep the Module details for display? Or displayed generic UE?
                // The table header says "Module (UE)".
                // Usually we want to show the UE Code/Libelle there.
                // However, our data comes from 'module'.
                // Ideally we use module.ue.code + libelle if available.
                // But let's stick to module.code/libelle as before unless UE is better.
                // If multiple modules share UE, module.code might vary?
                // Step 12: Module has code/libelle. UE has code/libelle.
                // If we group by UE, we should display UE info.
            } else {
                key = `mod_${module.id}`;
            }
        } else {
            key = `single_${curr.id}`; // Fallback for no module
        }

        if (!acc[key]) {
            acc[key] = {
                module: module, // Store the reference module for header display
                ue: module ? module.ue : null,
                items: []
            };
        }
        acc[key].items.push(curr);
        return acc;
    }, {});

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
                        <th colSpan="3" style={{ textAlign: "center", backgroundColor: "#f8f9fa", borderBottom: "2px solid #dee2e6" }}>UNITÉ D'ENSEIGNEMENT (UE)</th>
                        <th colSpan="10" style={{ textAlign: "center", backgroundColor: "#e9ecef", borderBottom: "2px solid #dee2e6" }}>ÉLÉMENT CONSTITUTIF (EC)</th>
                    </tr>
                    <tr>
                        {/* UE Columns */}
                        <th style={{ width: '20%' }}>INTITULÉ</th>
                        <th style={{ width: '5%' }}>CRÉDITS</th>
                        <th style={{ width: '5%' }}>COEF UE</th>

                        {/* EC Columns */}
                        <th style={{ width: '20%' }}>INTITULÉ</th>
                        <th>CM</th>
                        <th>TD</th>
                        <th>TP</th>
                        <th>CM+TD/TP</th>
                        <th>TPE</th>
                        <th>VHT</th>
                        <th>COEFF</th>
                        <th>SEMESTRE</th>
                        <th>ACTIONS</th>
                    </tr>
                </thead>
                <tbody>
                    {Object.keys(groupedMaquettes).length === 0 ? (
                        <tr><td colSpan="13" style={{ textAlign: "center" }}>Aucune donnée.</td></tr>
                    ) : (
                        Object.values(groupedMaquettes).map((group, groupIndex) => (
                            <React.Fragment key={groupIndex}>
                                {group.items.map((m, itemIndex) => {
                                    const totalPresenciel = (m.cm || 0) + (m.td || 0) + (m.tp || 0);
                                    // TPE estimation if not present: VHT - Presenciel or from field
                                    // User example: cm:20, tp:10, td:15, pres:36 (?), tpe:24, vht:60.
                                    // 36+24=60. 
                                    // Here m.vht is from DB. m.cm/td/tp from DB.
                                    // We'll calculate TPE if not in model? No, just use DB fields.
                                    // Model Maquette.java DOES NOT have 'tpe' field based on my read! 
                                    // Wait, checking Step 6 View File...
                                    // Maquette.java: cm, td, tp, vht. NO TPE.
                                    // EC.java HAS tpe.
                                    // Since Maquette is acting as EC, maybe TPE is missing?
                                    // We can calculate TPE = VHT - (CM+TD+TP).
                                    const tpe = (m.vht || 0) - totalPresenciel;

                                    return (
                                        <tr key={m.id} style={{ borderBottom: itemIndex === group.items.length - 1 ? "2px solid #ddd" : "1px solid #eee" }}>
                                            {itemIndex === 0 && (
                                                <>
                                                    <td rowSpan={group.items.length} style={{ verticalAlign: "middle", backgroundColor: "#fff", borderRight: "1px solid #eee", fontWeight: "600" }}>
                                                        {group.ue ? (
                                                            <>
                                                                <div style={{ fontSize: '0.9em', color: '#666' }}>{group.ue.code}</div>
                                                                {group.ue.libelle}
                                                            </>
                                                        ) : (
                                                            group.module ? (
                                                                <>
                                                                    <div style={{ fontSize: '0.9em', color: '#666' }}>{group.module.code}</div>
                                                                    {group.module.libelle}
                                                                </>
                                                            ) : "Hors UE"
                                                        )}
                                                    </td>
                                                    <td rowSpan={group.items.length} style={{ verticalAlign: "middle", textAlign: "center", backgroundColor: "#fff", borderRight: "1px solid #eee" }}>
                                                        <span className="badge-credits">{m.credits}</span>
                                                    </td>
                                                    <td rowSpan={group.items.length} style={{ verticalAlign: "middle", textAlign: "center", backgroundColor: "#fff", borderRight: "1px solid #eee" }}>
                                                        {m.coefficientUE}
                                                    </td>
                                                </>
                                            )}
                                            {/* EC Fields */}
                                            <td style={{ borderLeft: "2px solid #f0f0f0" }}>
                                                <span style={{ fontSize: '0.9em', fontWeight: 'bold', color: '#555' }}>{m.code}</span><br />
                                                {m.libelle}
                                            </td>
                                            <td>{m.cm}</td>
                                            <td>{m.td}</td>
                                            <td>{m.tp}</td>
                                            <td style={{ fontWeight: 'bold', backgroundColor: '#fdfdfd' }}>{totalPresenciel}</td>
                                            <td>{tpe > 0 ? tpe : "-"}</td>
                                            <td style={{ fontWeight: 'bold' }}>{m.vht}</td>
                                            <td>-</td> {/* EC Coeff missing in Maquette model */}
                                            <td>{m.semestre ? m.semestre.libelle : "-"}</td>

                                            <td className="actions-cell">
                                                <button className="btn-icon edit" title="Modifier" onClick={() => openModal(m)}>
                                                    <Edit size={18} />
                                                </button>
                                                <button className="btn-icon delete" title="Supprimer" onClick={() => handleDelete(m.id)}>
                                                    <Trash2 size={18} />
                                                </button>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </React.Fragment>
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
                                    <label>Formation</label>
                                    <select name="formationId" value={formData.formationId} onChange={handleInputChange} required>
                                        <option value="">Sélectionner</option>
                                        {formations.map(f => (
                                            <option key={f.id} value={f.id}>{f.libelle}</option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Module / UE</label>
                                    <div style={{ display: 'flex', flexDirection: 'column' }}>
                                        <select name="module" value={formData.module} onChange={handleModuleChange}>
                                            <option value="">-- Sélectionner un Module/UE --</option>
                                            {modules.map(mod => (
                                                <option key={mod.id} value={mod.id}>
                                                    {mod.code} - {mod.libelle}
                                                    {mod.ue ? ` (UE: ${mod.ue.code})` : ""}
                                                </option>
                                            ))}
                                        </select>
                                        <small style={{ color: '#888', fontStyle: 'italic' }}>Définit les crédits et coef. UE</small>
                                    </div>
                                </div>

                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Élément Constitutif (EC)</label>
                                    <div style={{ display: 'flex', flexDirection: 'column' }}>
                                        <select name="ec" value={selectedEcId} onChange={handleECChange}>
                                            <option value="">-- Sélectionner un EC (Autofill) --</option>
                                            {ecs.map(ec => (
                                                <option key={ec.id} value={ec.id}>{ec.code} - {ec.libelle}</option>
                                            ))}
                                        </select>
                                        <small style={{ color: '#888', fontStyle: 'italic' }}>Remplit automatiquement les détails</small>
                                    </div>
                                </div>
                            </div>

                            <div className="form-group-row">
                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Code</label>
                                    <input type="text" name="code" value={formData.code} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group" style={{ flex: 2 }}>
                                    <label>Libellé</label>
                                    <input type="text" name="libelle" value={formData.libelle} onChange={handleInputChange} required />
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
