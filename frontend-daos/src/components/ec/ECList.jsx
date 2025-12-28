import React, { useState, useEffect } from 'react';
import EnseignementService from '../../services/EnseignementService.js';

const ECList = () => {
    // --- ÉTATS (STATE) ---
    const [ecs, setEcs] = useState([]);
    const [ues, setUes] = useState([]);
    const [modules, setModules] = useState([]);

    // Gestion de l'affichage
    const [showArchived, setShowArchived] = useState(false); // Mode Archive
    const [searchTerm, setSearchTerm] = useState(''); // Barre de recherche
    const [showModal, setShowModal] = useState(false);

    // Formulaire
    const initialFormState = {
        id: '', code: '', libelle: '',
        ue: { id: '' }, module: { id: '' },
        heureCm: 0, heureTd: 0, heureTp: 0,
        coefficient: 1, credit: 1, tpe: 0,
        actif: true
    };
    const [formData, setFormData] = useState(initialFormState);

    // --- EFFETS (LIFECYCLE) ---
    useEffect(() => {
        loadData();
    }, [showArchived]); // Recharger si on change de mode (Actif <-> Archive)

    const loadData = () => {
        // Charger les ECs (actifs ou archivés selon l'état)
        EnseignementService.getECs(showArchived)
            .then(res => setEcs(res.data))
            .catch(err => console.error("Erreur chargement ECs", err));

        // Charger les listes déroulantes (seulement si besoin)
        if (ues.length === 0) EnseignementService.getUEs().then(res => setUes(res.data));
        if (modules.length === 0) EnseignementService.getModules().then(res => setModules(res.data));
    };

    // --- LOGIQUE MÉTIER ---

    // Calcul dynamique du VHT pour le formulaire
    const totalHeuresForm = (parseInt(formData.heureCm) || 0) +
        (parseInt(formData.heureTd) || 0) +
        (parseInt(formData.heureTp) || 0);

    // Filtrage pour la barre de recherche
    const filteredEcs = ecs.filter(ec =>
        ec.libelle.toLowerCase().includes(searchTerm.toLowerCase()) ||
        ec.code.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleSave = (e) => {
        e.preventDefault();
        // Préparation de l'objet pour le backend (gestion des ID nulls)
        const payload = {
            ...formData,
            ue: formData.ue.id ? { id: formData.ue.id } : null,
            module: formData.module.id ? { id: formData.module.id } : null
        };

        EnseignementService.saveEC(payload).then(() => {
            setShowModal(false);
            loadData();
            setFormData(initialFormState);
        });
    };

    const handleEdit = (ec) => {
        // On remplit le formulaire avec les données existantes
        setFormData({
            ...ec,
            ue: ec.ue || { id: '' },
            module: ec.module || { id: '' }
        });
        setShowModal(true);
    };

    const handleDelete = (id) => {
        if(window.confirm("Voulez-vous vraiment archiver cet EC ?")) {
            EnseignementService.archiveEC(id).then(loadData);
        }
    };

    // --- RENDER ---
    return (
        <div className="container-fluid p-4">

            {/* EN-TÊTE */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark">{showArchived ? "Archives EC" : "Liste des ECs"}</h2>
                    <p className="text-muted mb-0">Gestion des éléments constitutifs et volumes horaires.</p>
                </div>
                <div className="d-flex gap-2">
                    <button
                        className={`btn ${showArchived ? 'btn-secondary' : 'btn-outline-secondary'} shadow-sm`}
                        onClick={() => setShowArchived(!showArchived)}
                    >
                        <i className={`bi ${showArchived ? 'bi-arrow-left' : 'bi-archive'} me-2`}></i>
                        {showArchived ? "Retour aux Actifs" : "Archives"}
                    </button>

                    {!showArchived && (
                        <button className="btn btn-success shadow-sm" onClick={() => { setFormData(initialFormState); setShowModal(true); }}>
                            <i className="bi bi-plus-circle me-2"></i>Nouvel EC
                        </button>
                    )}
                </div>
            </div>

            {/* BARRE DE RECHERCHE */}
            <div className="card mb-4 border-0 shadow-sm">
                <div className="card-body p-2">
                    <div className="input-group">
                        <span className="input-group-text bg-white border-0"><i className="bi bi-search text-muted"></i></span>
                        <input
                            type="text"
                            className="form-control border-0 ps-0"
                            placeholder="Rechercher par Code, Libellé..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>
            </div>

            {/* TABLEAU */}
            <div className="card shadow-sm border-0">
                <div className="card-body p-0">
                    <div className="table-responsive">
                        <table className="table table-hover align-middle mb-0 table-sm" style={{ fontSize: '0.9rem' }}>
                            <thead className="bg-light border-bottom">
                            <tr>
                                <th className="ps-4 py-3">EC (Code & Libellé)</th>
                                <th>Module</th>
                                <th style={{ color: '#1a5e34' }}>UE</th>
                                <th className="text-center">Crédit</th>
                                <th className="text-center">CM</th>
                                <th className="text-center">TD</th>
                                <th className="text-center">TP</th>
                                <th className="text-center bg-light border-start border-end fw-bold">CM+TD/TP</th>
                                <th className="text-center fw-bold text-primary">VHT</th>
                                <th className="text-center">Coef</th>
                                <th className="text-center">Statut</th>
                                <th className="text-end pe-4">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {filteredEcs.length === 0 ? (
                                <tr>
                                    <td colspan="12" className="text-center py-5 text-muted">
                                        <i className="bi bi-journal-x display-4 d-block mb-3 opacity-50"></i>
                                        Aucun EC trouvé.
                                    </td>
                                </tr>
                            ) : (
                                filteredEcs.map(ec => {
                                    // Calcul du total pour l'affichage ligne
                                    const totalLigne = (ec.heureCm||0) + (ec.heureTd||0) + (ec.heureTp||0);

                                    return (
                                        <tr key={ec.id}>
                                            <td className="ps-4">
                                                <span className="badge bg-secondary me-2">{ec.code}</span>
                                                <strong className="text-dark">{ec.libelle}</strong>
                                            </td>
                                            <td className="text-muted">{ec.module?.libelle || '-'}</td>
                                            <td className="fw-bold text-muted" style={{ fontSize: '0.85rem' }}>
                                                {ec.ue?.libelle || '-'}
                                            </td>
                                            <td className="text-center">{ec.credit}</td>
                                            <td className="text-center text-muted">{ec.heureCm}</td>
                                            <td className="text-center text-muted">{ec.heureTd}</td>
                                            <td className="text-center text-muted">{ec.heureTp}</td>

                                            {/* Colonne Total grise */}
                                            <td className="text-center bg-light border-start border-end text-dark fw-bold">
                                                {totalLigne}
                                            </td>

                                            {/* Colonne VHT Bleue */}
                                            <td className="text-center fw-bold text-primary">{totalLigne}</td>

                                            <td className="text-center">{ec.coefficient}</td>

                                            <td className="text-center">
                                                {ec.actif ? (
                                                    <span className="badge bg-success px-2">Actif</span>
                                                ) : (
                                                    <span className="badge bg-warning text-dark px-2">Inactif</span>
                                                )}
                                            </td>

                                            <td className="text-end pe-4">
                                                {!showArchived ? (
                                                    <div className="btn-group">
                                                        <button className="btn btn-sm btn-light border" onClick={() => handleEdit(ec)} title="Modifier">
                                                            <i className="bi bi-pencil text-primary"></i>
                                                        </button>
                                                        <button className="btn btn-sm btn-light border" onClick={() => handleDelete(ec.id)} title="Archiver">
                                                            <i className="bi bi-archive text-danger"></i>
                                                        </button>
                                                    </div>
                                                ) : (
                                                    <button className="btn btn-sm btn-outline-success" title="Restaurer">
                                                        <i className="bi bi-arrow-counterclockwise me-1"></i> Restaurer
                                                    </button>
                                                )}
                                            </td>
                                        </tr>
                                    )})
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className="card-footer bg-white border-top py-3">
                    <small className="text-muted">Total: <strong>{filteredEcs.length}</strong> EC(s)</small>
                </div>
            </div>

            {/* --- MODAL (Overlay) --- */}
            {showModal && (
                <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }} tabIndex="-1">
                    <div className="modal-dialog modal-lg modal-dialog-centered">
                        <div className="modal-content border-0 shadow-lg">
                            <div className="modal-header bg-success text-white">
                                <h5 className="modal-title fw-bold">
                                    {formData.id ? <><i className="bi bi-pencil-square me-2"></i>Modifier EC</> : <><i className="bi bi-plus-circle me-2"></i>Nouvel EC</>}
                                </h5>
                                <button type="button" className="btn-close btn-close-white" onClick={() => setShowModal(false)}></button>
                            </div>
                            <div className="modal-body p-4">
                                <form onSubmit={handleSave}>

                                    <div className="row mb-3">
                                        <div className="col-md-4">
                                            <label className="small fw-bold">Code <span className="text-danger">*</span></label>
                                            <input type="text" className="form-control" value={formData.code}
                                                   onChange={(e) => setFormData({...formData, code: e.target.value})} required />
                                        </div>
                                        <div className="col-md-8">
                                            <label className="small fw-bold">Libellé <span className="text-danger">*</span></label>
                                            <input type="text" className="form-control" value={formData.libelle}
                                                   onChange={(e) => setFormData({...formData, libelle: e.target.value})} required />
                                        </div>
                                    </div>

                                    <div className="row mb-3">
                                        <div className="col-md-6">
                                            <label className="small fw-bold">Module <span className="text-danger">*</span></label>
                                            <select className="form-select" value={formData.module.id}
                                                    onChange={(e) => setFormData({...formData, module: {id: e.target.value}})} required>
                                                <option value="">-- Choisir Module --</option>
                                                {modules.map(m => <option key={m.id} value={m.id}>{m.libelle}</option>)}
                                            </select>
                                        </div>
                                        <div className="col-md-6">
                                            <label className="small fw-bold">UE <span className="text-danger">*</span></label>
                                            <select className="form-select" value={formData.ue.id}
                                                    onChange={(e) => setFormData({...formData, ue: {id: e.target.value}})} required>
                                                <option value="">-- Choisir UE --</option>
                                                {ues.map(u => <option key={u.id} value={u.id}>{u.libelle}</option>)}
                                            </select>
                                        </div>
                                    </div>

                                    <h6 className="text-success border-bottom pb-2 mb-3 small fw-bold mt-4">VOLUMES HORAIRES</h6>
                                    <div className="row mb-3 bg-light p-3 border rounded mx-0">
                                        <div className="col-3">
                                            <label className="small">CM</label>
                                            <input type="number" className="form-control" min="0" value={formData.heureCm}
                                                   onChange={(e) => setFormData({...formData, heureCm: e.target.value})} />
                                        </div>
                                        <div className="col-3">
                                            <label className="small">TD</label>
                                            <input type="number" className="form-control" min="0" value={formData.heureTd}
                                                   onChange={(e) => setFormData({...formData, heureTd: e.target.value})} />
                                        </div>
                                        <div className="col-3">
                                            <label className="small">TP</label>
                                            <input type="number" className="form-control" min="0" value={formData.heureTp}
                                                   onChange={(e) => setFormData({...formData, heureTp: e.target.value})} />
                                        </div>
                                        <div className="col-3">
                                            <label className="small fw-bold text-primary">TOTAL</label>
                                            <input type="text" className="form-control fw-bold text-primary" readOnly value={totalHeuresForm} />
                                        </div>
                                    </div>

                                    <div className="row">
                                        <div className="col-md-4">
                                            <label className="small fw-bold">Coefficient</label>
                                            <input type="number" className="form-control" step="0.5" value={formData.coefficient}
                                                   onChange={(e) => setFormData({...formData, coefficient: e.target.value})} />
                                        </div>
                                        <div className="col-md-4">
                                            <label className="small fw-bold">Crédits</label>
                                            <input type="number" className="form-control" value={formData.credit}
                                                   onChange={(e) => setFormData({...formData, credit: e.target.value})} />
                                        </div>
                                        <div className="col-md-4">
                                            <label className="small fw-bold">TPE</label>
                                            <input type="number" className="form-control" value={formData.tpe}
                                                   onChange={(e) => setFormData({...formData, tpe: e.target.value})} />
                                        </div>
                                    </div>

                                    <div className="text-end mt-4">
                                        <button type="button" className="btn btn-light border me-2" onClick={() => setShowModal(false)}>Annuler</button>
                                        <button type="submit" className="btn btn-success px-4"><i className="bi bi-check-lg me-2"></i>Enregistrer</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ECList;