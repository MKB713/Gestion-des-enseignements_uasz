import React, { useState, useEffect } from 'react';
import EnseignementService from '../../services/EnseignementService.js';

const ModuleList = () => {
    const [modules, setModules] = useState([]);
    const [showArchived, setShowArchived] = useState(false); // Bascule Actif/Archive
    const [showModal, setShowModal] = useState(false);

    // Form State
    const [formData, setFormData] = useState({ id: '', code: '', libelle: '', cycle: '', niveau: '' });
    const [niveauxDispo, setNiveauxDispo] = useState([]);

    // Mapping des niveaux (comme dans ton script JS)
    const cyclesConfig = {
        'LICENCE': ['L1', 'L2', 'L3'],
        'MASTER': ['M1', 'M2'],
        'DOCTORAT': ['D1', 'D2', 'D3']
    };

    useEffect(() => {
        loadModules();
    }, [showArchived]); // Recharger si on change de mode (Actif <-> Archive)

    // Gestion dynamique des niveaux quand le cycle change
    useEffect(() => {
        if (formData.cycle && cyclesConfig[formData.cycle]) {
            setNiveauxDispo(cyclesConfig[formData.cycle]);
        } else {
            setNiveauxDispo([]);
        }
    }, [formData.cycle]);

    const loadModules = () => {
        EnseignementService.getModules(showArchived).then(res => setModules(res.data));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        EnseignementService.saveModule(formData).then(() => {
            setShowModal(false);
            loadModules();
            resetForm();
        });
    };

    const resetForm = () => setFormData({ id: '', code: '', libelle: '', cycle: '', niveau: '' });

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="fw-bold text-dark">{showArchived ? "Modules Archivés" : "Modules d'Enseignement"}</h2>
                <div className="d-flex gap-2">
                    <button className="btn btn-outline-secondary" onClick={() => setShowArchived(!showArchived)}>
                        <i className={`bi ${showArchived ? 'bi-list' : 'bi-archive'} me-2`}></i>
                        {showArchived ? "Voir Actifs" : "Voir Archives"}
                    </button>
                    {!showArchived && (
                        <button className="btn btn-primary" onClick={() => { resetForm(); setShowModal(true); }}>
                            <i className="bi bi-plus-lg me-2"></i>Nouveau Module
                        </button>
                    )}
                </div>
            </div>

            {/* LISTE */}
            <div className="card shadow-sm border-0">
                <table className="table table-hover align-middle mb-0">
                    <thead className="bg-light">
                    <tr>
                        <th className="ps-4">Module</th>
                        <th>Cycle</th>
                        <th>Niveau</th>
                        <th className="text-end pe-4">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {modules.map(m => (
                        <tr key={m.id}>
                            <td className="ps-4">
                                <div className="fw-bold">{m.libelle}</div>
                                <span className="badge bg-secondary opacity-50">{m.code}</span>
                            </td>
                            <td><span className="badge bg-info bg-opacity-10 text-info border border-info">{m.cycle}</span></td>
                            <td><span className="badge bg-warning bg-opacity-10 text-warning border border-warning">{m.niveau}</span></td>
                            <td className="text-end pe-4">
                                {showArchived ? (
                                    <button className="btn btn-sm btn-outline-success" onClick={() => EnseignementService.restoreModule(m.id).then(loadModules)}>
                                        <i className="bi bi-arrow-counterclockwise"></i> Restaurer
                                    </button>
                                ) : (
                                    <button className="btn btn-sm btn-light border" onClick={() => { setFormData(m); setShowModal(true); }}>
                                        <i className="bi bi-pencil text-primary"></i>
                                    </button>
                                    // Ajouter bouton supprimer/archiver ici
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {/* MODAL (Simplifié pour l'exemple, à styliser avec Bootstrap Classes) */}
            {showModal && (
                <div className="modal d-block" style={{backgroundColor: 'rgba(0,0,0,0.5)'}}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header bg-success text-white">
                                <h5 className="modal-title">Nouveau/Modif Module</h5>
                                <button className="btn-close" onClick={() => setShowModal(false)}></button>
                            </div>
                            <form onSubmit={handleSubmit} className="modal-body p-4">
                                <input type="text" className="form-control mb-3" placeholder="Code" value={formData.code}
                                       onChange={e => setFormData({...formData, code: e.target.value})} required />
                                <input type="text" className="form-control mb-3" placeholder="Libellé" value={formData.libelle}
                                       onChange={e => setFormData({...formData, libelle: e.target.value})} required />

                                <div className="row">
                                    <div className="col-6">
                                        <select className="form-select" value={formData.cycle} onChange={e => setFormData({...formData, cycle: e.target.value})} required>
                                            <option value="">-- Cycle --</option>
                                            <option value="LICENCE">LICENCE</option>
                                            <option value="MASTER">MASTER</option>
                                            <option value="DOCTORAT">DOCTORAT</option>
                                        </select>
                                    </div>
                                    <div className="col-6">
                                        <select className="form-select" value={formData.niveau} onChange={e => setFormData({...formData, niveau: e.target.value})} required>
                                            <option value="">-- Niveau --</option>
                                            {niveauxDispo.map(niv => <option key={niv} value={niv}>{niv}</option>)}
                                        </select>
                                    </div>
                                </div>
                                <div className="text-end mt-4">
                                    <button type="submit" className="btn btn-success">Enregistrer</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};
export default ModuleList;