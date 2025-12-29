import React, { useState, useEffect } from 'react';
import EnseignementService from '../../services/EnseignementService.js';

const UEList = () => {
    // --- STATE ---
    const [ues, setUes] = useState([]);
    const [showArchived, setShowArchived] = useState(false); // Bascule Actif/Archive
    const [searchTerm, setSearchTerm] = useState('');
    const [showModal, setShowModal] = useState(false);

    const initialFormState = {
        id: '',
        code: '',
        libelle: '',
        credit: 1,
        coefficient: 1,
        active: true
    };
    const [formData, setFormData] = useState(initialFormState);

    // --- CHARGEMENT DES DONNÉES ---
    useEffect(() => {
        loadUes();
    }, [showArchived]);

    const loadUes = () => {
        EnseignementService.getUEs(showArchived)
            .then(res => setUes(res.data))
            .catch(err => console.error("Erreur chargement UEs", err));
    };

    // --- ACTIONS ---
    const handleSave = (e) => {
        e.preventDefault();
        EnseignementService.saveUE(formData).then(() => {
            setShowModal(false);
            loadUes();
            setFormData(initialFormState);
        });
    };

    const handleEdit = (ue) => {
        setFormData(ue);
        setShowModal(true);
    };

    const handleArchive = (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir archiver cette UE ?")) {
            EnseignementService.archiveUE(id).then(loadUes);
        }
    };

    const handleRestore = (id) => {
        if (window.confirm("Voulez-vous restaurer cette UE ?")) {
            // Suppose que ton service a une méthode restoreUE ou une logique d'update
            // Si restoreUE n'existe pas dans le service, il faudra l'ajouter
            EnseignementService.restoreUE(id).then(loadUes);
        }
    };

    const handleToggleStatus = (ue) => {
        const newStatus = !ue.active;
        const action = newStatus ? "activer" : "désactiver";
        if (window.confirm(`Voulez-vous ${action} l'UE ${ue.code} ?`)) {
            EnseignementService.toggleUeStatus(ue.id, newStatus).then(loadUes);
        }
    };

    // --- FILTRAGE ---
    const filteredUes = ues.filter(ue =>
        ue.libelle.toLowerCase().includes(searchTerm.toLowerCase()) ||
        ue.code.toLowerCase().includes(searchTerm.toLowerCase())
    );

    // --- UTILITAIRE DATE ---
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('fr-FR');
    };

    return (
        <div className="container-fluid p-4">

            {/* EN-TÊTE */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark">{showArchived ? "Archives des UE" : "Unités d'Enseignement"}</h2>
                    <p className="text-muted mb-0">Gestion des UE, crédits et coefficients.</p>
                </div>
                <div className="d-flex gap-2">
                    <button
                        className={`btn ${showArchived ? 'btn-secondary' : 'btn-outline-secondary'} shadow-sm`}
                        onClick={() => setShowArchived(!showArchived)}
                    >
                        <i className={`bi ${showArchived ? 'bi-arrow-left' : 'bi-archive'} me-2`}></i>
                        {showArchived ? "Retour aux Actifs" : "Voir Archives"}
                    </button>

                    {!showArchived && (
                        <button className="btn btn-success shadow-sm px-4" onClick={() => { setFormData(initialFormState); setShowModal(true); }}>
                            <i className="bi bi-plus-lg me-2"></i>Nouvelle UE
                        </button>
                    )}
                </div>
            </div>

            {/* BARRE DE RECHERCHE */}
            <div className="card mb-4 border-0 shadow-sm">
                <div className="card-body p-3">
                    <div className="input-group">
                        <span className="input-group-text bg-white border-end-0 ps-3"><i className="bi bi-search text-muted"></i></span>
                        <input
                            type="text"
                            className="form-control border-start-0 ps-0"
                            placeholder="Rechercher par Code ou Libellé..."
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
                        <table className="table table-hover align-middle mb-0">
                            <thead className="bg-light">
                            <tr className="text-uppercase text-muted small fw-bold">
                                <th className="ps-4 py-3">Code</th>
                                <th>Libellé</th>
                                <th className="text-center">Crédits</th>
                                <th className="text-center">Coef</th>
                                {!showArchived && <th>Date Création</th>}
                                <th className="text-center">Statut</th>
                                <th className="text-end pe-4">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {filteredUes.length === 0 ? (
                                <tr>
                                    <td colSpan="7" className="text-center py-5 text-muted">
                                        <i className="bi bi-journal-x display-4 d-block mb-3 opacity-50"></i>
                                        {showArchived ? "La corbeille est vide." : "Aucune UE trouvée."}
                                    </td>
                                </tr>
                            ) : (
                                filteredUes.map(ue => (
                                    <tr key={ue.id}>
                                        <td className="ps-4">
                                                <span className="badge bg-secondary bg-opacity-10 text-secondary border border-secondary font-monospace" style={{ fontSize: '0.8rem' }}>
                                                    {ue.code}
                                                </span>
                                        </td>
                                        <td className="fw-bold text-dark">{ue.libelle}</td>
                                        <td className="text-center">
                                            <span className="badge bg-info text-dark bg-opacity-25 border border-info">{ue.credit}</span>
                                        </td>
                                        <td className="text-center fw-bold">{ue.coefficient}</td>

                                        {!showArchived && (
                                            <td className="text-muted small">
                                                <i className="bi bi-calendar3 me-1"></i>
                                                {formatDate(ue.dateCreation)}
                                            </td>
                                        )}

                                        <td className="text-center">
                                            {showArchived ? (
                                                <span className="badge bg-warning text-dark border border-warning bg-opacity-25">
                                                        <i className="bi bi-archive-fill me-1 small"></i> Archivée
                                                     </span>
                                            ) : (
                                                ue.active ?
                                                    <span className="badge bg-success rounded-pill px-3"><i className="bi bi-check-circle-fill small me-1"></i> Active</span> :
                                                    <span className="badge bg-secondary rounded-pill px-3"><i className="bi bi-circle-fill small me-1"></i> Inactive</span>
                                            )}
                                        </td>

                                        <td className="text-end pe-4">
                                            <div className="btn-group">
                                                {showArchived ? (
                                                    <button className="btn btn-sm btn-outline-success border-0 fw-bold" onClick={() => handleRestore(ue.id)} title="Restaurer">
                                                        <i className="bi bi-arrow-counterclockwise me-1"></i> Restaurer
                                                    </button>
                                                ) : (
                                                    <>
                                                        <button className="btn btn-sm btn-light text-primary" onClick={() => handleEdit(ue)} title="Modifier">
                                                            <i className="bi bi-pencil"></i>
                                                        </button>

                                                        <button
                                                            className={`btn btn-sm btn-light ${ue.active ? 'text-warning' : 'text-success'}`}
                                                            onClick={() => handleToggleStatus(ue)}
                                                            title={ue.active ? "Désactiver" : "Activer"}
                                                        >
                                                            <i className={`bi ${ue.active ? 'bi-pause-circle' : 'bi-play-circle'}`}></i>
                                                        </button>

                                                        <button className="btn btn-sm btn-light text-danger" onClick={() => handleArchive(ue.id)} title="Archiver">
                                                            <i className="bi bi-archive"></i>
                                                        </button>
                                                    </>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            {/* MODAL */}
            {showModal && (
                <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }} tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content border-0 shadow-lg">
                            <div className="modal-header bg-success text-white">
                                <h5 className="modal-title fw-bold">
                                    {formData.id ? <><i className="bi bi-pencil-square me-2"></i>Modifier UE</> : <><i className="bi bi-plus-lg me-2"></i>Nouvelle UE</>}
                                </h5>
                                <button type="button" className="btn-close btn-close-white" onClick={() => setShowModal(false)}></button>
                            </div>
                            <div className="modal-body p-4">
                                <form onSubmit={handleSave}>

                                    <div className="mb-3">
                                        <label className="form-label small fw-bold text-muted">Code <span className="text-danger">*</span></label>
                                        <input type="text" className="form-control" placeholder="Ex: UE-INFO-L1"
                                               value={formData.code} onChange={(e) => setFormData({...formData, code: e.target.value})} required />
                                    </div>

                                    <div className="mb-3">
                                        <label className="form-label small fw-bold text-muted">Libellé <span className="text-danger">*</span></label>
                                        <input type="text" className="form-control" placeholder="Ex: Bases de données"
                                               value={formData.libelle} onChange={(e) => setFormData({...formData, libelle: e.target.value})} required />
                                    </div>

                                    <div className="row">
                                        <div className="col-md-6 mb-3">
                                            <label className="form-label small fw-bold text-muted">Crédits</label>
                                            <input type="number" className="form-control" min="1"
                                                   value={formData.credit} onChange={(e) => setFormData({...formData, credit: e.target.value})} required />
                                        </div>
                                        <div className="col-md-6 mb-3">
                                            <label className="form-label small fw-bold text-muted">Coefficient</label>
                                            <input type="number" className="form-control" min="1"
                                                   value={formData.coefficient} onChange={(e) => setFormData({...formData, coefficient: e.target.value})} required />
                                        </div>
                                    </div>

                                    <div className="text-end mt-4 pt-3 border-top">
                                        <button type="button" className="btn btn-light border me-2" onClick={() => setShowModal(false)}>Annuler</button>
                                        <button type="submit" className="btn btn-success px-4"><i className="bi bi-check-circle me-2"></i>Enregistrer</button>
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

export default UEList;