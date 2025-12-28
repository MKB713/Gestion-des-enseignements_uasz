import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom'; // Pour la navigation (futur)
import EnseignantService from '../services/EnseignantService';

const EnseignantList = () => {
    // --- ÉTATS (DATA) ---
    const [enseignants, setEnseignants] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // --- CHARGEMENT INITIAL ---
    useEffect(() => {
        chargerEnseignants();
    }, []);

    const chargerEnseignants = () => {
        setLoading(true);
        EnseignantService.getAllEnseignants()
            .then(response => {
                setEnseignants(response.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erreur API:", err);
                setError("Impossible de contacter le serveur (Vérifiez le Gateway sur le port 8080).");
                setLoading(false);
            });
    };

    // --- ACTIONS (HANDLERS) ---

    const handleArchiver = (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir archiver cet enseignant ?")) {
            EnseignantService.archiver(id)
                .then(() => {
                    chargerEnseignants(); // On recharge la liste pour voir l'effet
                })
                .catch(err => alert("Erreur lors de l'archivage"));
        }
    };

    const toggleStatut = (enseignant) => {
        const action = enseignant.active ? EnseignantService.desactiver : EnseignantService.activer;
        const message = enseignant.active ? "Désactiver" : "Activer";

        if (window.confirm(`Voulez-vous ${message} ce compte ?`)) {
            action(enseignant.id)
                .then(() => chargerEnseignants())
                .catch(err => alert("Erreur lors du changement de statut"));
        }
    };

    // --- RENDER (AFFICHAGE) ---

    if (loading) return (
        <div className="d-flex justify-content-center mt-5">
            <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Chargement...</span>
            </div>
        </div>
    );

    if (error) return (
        <div className="container mt-5">
            <div className="alert alert-danger shadow-sm border-0">
                <i className="bi bi-exclamation-triangle-fill me-2"></i>
                <strong>Erreur :</strong> {error}
            </div>
        </div>
    );

    return (
        <div className="container mt-4">
            {/* EN-TÊTE */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark mb-1">Enseignants</h2>
                    <p className="text-muted mb-0">Gestion du personnel enseignant</p>
                </div>
                <div className="d-flex gap-2">
                    <button className="btn btn-outline-secondary">
                        <i className="bi bi-archive me-2"></i>Archives
                    </button>
                    {/* Le Link ne fonctionnera que quand on aura configuré le Router, pour l'instant c'est visuel */}
                    <button className="btn btn-success shadow-sm">
                        <i className="bi bi-plus-lg me-2"></i>Nouveau
                    </button>
                </div>
            </div>

            {/* TABLEAU (CARD) */}
            <div className="card shadow-sm border-0">
                <div className="card-body p-0">
                    <div className="table-responsive">
                        <table className="table table-hover align-middle mb-0">
                            <thead className="bg-light">
                            <tr>
                                <th className="ps-4 py-3">Identité</th>
                                <th>Grade & Spécialité</th>
                                <th>Contact</th>
                                <th className="text-center">Statut</th>
                                <th className="text-end pe-4">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {enseignants.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="text-center py-5 text-muted">
                                        <i className="bi bi-person-x display-4 mb-3 d-block opacity-50"></i>
                                        Aucun enseignant trouvé.
                                    </td>
                                </tr>
                            ) : (
                                enseignants.map(ens => (
                                    <tr key={ens.id}>
                                        {/* COLONNE 1 : IDENTITÉ */}
                                        <td className="ps-4">
                                            <div className="d-flex align-items-center">
                                                <div className="rounded-circle bg-primary bg-opacity-10 text-primary d-flex justify-content-center align-items-center me-3 fw-bold border border-primary border-opacity-10"
                                                     style={{width: '45px', height: '45px'}}>
                                                    {ens.prenom?.charAt(0)}{ens.nom?.charAt(0)}
                                                </div>
                                                <div>
                                                    <div className="fw-bold text-dark">{ens.prenom} {ens.nom?.toUpperCase()}</div>
                                                    <small className="text-muted font-monospace">{ens.matricule}</small>
                                                </div>
                                            </div>
                                        </td>

                                        {/* COLONNE 2 : INFO PRO */}
                                        <td>
                                            <div className="fw-bold text-dark">{ens.grade}</div>
                                            <small className="text-muted">{ens.specialite || 'Non spécifié'}</small>
                                        </td>

                                        {/* COLONNE 3 : CONTACT */}
                                        <td>
                                            <div className="text-dark"><i className="bi bi-envelope me-2 text-muted"></i>{ens.email}</div>
                                            <small className="text-muted"><i className="bi bi-telephone me-2"></i>{ens.telephone || '-'}</small>
                                        </td>

                                        {/* COLONNE 4 : STATUT */}
                                        <td className="text-center">
                                            {ens.active ? (
                                                <span className="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 rounded-pill px-3">
                                                        <i className="bi bi-check-circle-fill me-1 small"></i> Actif
                                                    </span>
                                            ) : (
                                                <span className="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 rounded-pill px-3">
                                                        <i className="bi bi-slash-circle me-1 small"></i> Inactif
                                                    </span>
                                            )}
                                        </td>

                                        {/* COLONNE 5 : ACTIONS */}
                                        <td className="text-end pe-4">
                                            <div className="btn-group">
                                                <button className="btn btn-sm btn-light text-primary border-0" title="Modifier">
                                                    <i className="bi bi-pencil"></i>
                                                </button>

                                                {/* Bouton Toggle Statut */}
                                                <button
                                                    className={`btn btn-sm btn-light border-0 ${ens.active ? 'text-warning' : 'text-success'}`}
                                                    onClick={() => toggleStatut(ens)}
                                                    title={ens.active ? "Désactiver" : "Activer"}
                                                >
                                                    <i className={`bi ${ens.active ? 'bi-pause-circle' : 'bi-play-circle'}`}></i>
                                                </button>

                                                <button
                                                    className="btn btn-sm btn-light text-danger border-0"
                                                    onClick={() => handleArchiver(ens.id)}
                                                    title="Archiver"
                                                >
                                                    <i className="bi bi-archive"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className="card-footer bg-white py-3 border-top">
                    <small className="text-muted">Total : <strong>{enseignants.length}</strong> enseignants</small>
                </div>
            </div>
        </div>
    );
};

export default EnseignantList;