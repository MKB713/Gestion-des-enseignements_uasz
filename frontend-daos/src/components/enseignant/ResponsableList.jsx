import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import EnseignantService from '../../services/EnseignantService';

const ResponsableList = () => {
    const navigate = useNavigate();

    // --- ÉTATS ---
    const [responsables, setResponsables] = useState([]);
    const [displayedResponsables, setDisplayedResponsables] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterType, setFilterType] = useState('ALL'); // ALL, LICENCE, MASTER
    const [loading, setLoading] = useState(true);

    // --- CHARGEMENT ---
    useEffect(() => {
        loadData();
    }, []);

    const loadData = () => {
        setLoading(true);
        EnseignantService.getAllResponsables()
            .then(response => {
                setResponsables(response.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erreur API:", err);
                setLoading(false);
            });
    };

    // --- FILTRAGE ---
    useEffect(() => {
        let result = responsables;

        // Filtre par type
        if (filterType !== 'ALL') {
            result = result.filter(r => r.type === filterType);
        }

        // Filtre par recherche
        if (searchTerm.trim() !== '') {
            const term = searchTerm.toLowerCase();
            result = result.filter(r =>
                r.nom?.toLowerCase().includes(term) ||
                r.prenom?.toLowerCase().includes(term) ||
                r.email?.toLowerCase().includes(term)
            );
        }

        setDisplayedResponsables(result);
    }, [responsables, searchTerm, filterType]);

    // --- ACTIONS ---
    const handleToggleActif = (responsable) => {
        const action = responsable.actif
            ? EnseignantService.desactiverResponsable
            : EnseignantService.reactiverResponsable;
        const message = responsable.actif ? "désactiver" : "réactiver";

        if (window.confirm(`Voulez-vous ${message} ce responsable ?`)) {
            action(responsable.id)
                .then(() => loadData())
                .catch(err => alert("Erreur lors de l'opération"));
        }
    };

    const handleDelete = (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer ce responsable ?")) {
            EnseignantService.deleteResponsable(id)
                .then(() => loadData())
                .catch(err => alert("Erreur lors de la suppression"));
        }
    };

    // --- UTILITAIRES ---
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('fr-FR');
    };

    const getTypeBadge = (type) => {
        if (type === 'LICENCE') {
            return <span className="badge bg-info bg-opacity-10 text-info border border-info">
                <i className="bi bi-mortarboard-fill me-1 small"></i> Licence
            </span>;
        } else {
            return <span className="badge bg-warning bg-opacity-10 text-warning border border-warning">
                <i className="bi bi-award-fill me-1 small"></i> Master
            </span>;
        }
    };

    if (loading) {
        return (
            <div className="d-flex justify-content-center mt-5">
                <div className="spinner-border text-success"></div>
            </div>
        );
    }

    return (
        <div className="container-fluid" style={{padding: '20px'}}>

            {/* EN-TÊTE */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark mb-0">Responsables de Formation</h2>
                    <p className="text-muted mb-0">Gestion des responsables Licence et Master.</p>
                </div>
                <button
                    className="btn btn-success px-4 py-2 shadow-sm fw-bold"
                    onClick={() => navigate('/ajouter-responsable')}
                >
                    <i className="bi bi-plus-lg me-2"></i>Nouveau Responsable
                </button>
            </div>

            {/* BARRE DE FILTRES */}
            <div className="bg-white p-3 rounded shadow-sm mb-4">
                <div className="row align-items-center">
                    <div className="col-md-8 mb-2 mb-md-0">
                        <div className="input-group">
                            <span className="input-group-text bg-white border-end-0 ps-3 text-muted">
                                <i className="bi bi-search"></i>
                            </span>
                            <input
                                type="text"
                                className="form-control border-start-0 ps-0 py-2"
                                placeholder="Rechercher par Nom, Prénom ou Email..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="col-md-4">
                        <div className="btn-group w-100" role="group">
                            <button
                                type="button"
                                className={`btn ${filterType === 'ALL' ? 'btn-success' : 'btn-outline-secondary'}`}
                                onClick={() => setFilterType('ALL')}
                            >
                                Tous
                            </button>
                            <button
                                type="button"
                                className={`btn ${filterType === 'LICENCE' ? 'btn-info' : 'btn-outline-secondary'}`}
                                onClick={() => setFilterType('LICENCE')}
                            >
                                Licence
                            </button>
                            <button
                                type="button"
                                className={`btn ${filterType === 'MASTER' ? 'btn-warning' : 'btn-outline-secondary'}`}
                                onClick={() => setFilterType('MASTER')}
                            >
                                Master
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {/* TABLEAU */}
            <div className="card shadow-sm border-0">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead>
                        <tr>
                            <th className="ps-4 py-3">RESPONSABLE</th>
                            <th>TYPE</th>
                            <th>CONTACT</th>
                            <th>FORMATION</th>
                            <th>PÉRIODE</th>
                            <th className="text-center">STATUT</th>
                            <th className="text-end pe-4">ACTIONS</th>
                        </tr>
                        </thead>
                        <tbody>
                        {displayedResponsables.length === 0 ? (
                            <tr>
                                <td colSpan="7" className="text-center py-5">
                                    <div className="text-muted opacity-50 mb-2">
                                        <i className="bi bi-person-x display-4"></i>
                                    </div>
                                    <h6 className="text-muted">Aucun responsable trouvé.</h6>
                                </td>
                            </tr>
                        ) : (
                            displayedResponsables.map(responsable => (
                                <tr key={responsable.id}>
                                    {/* COLONNE 1 : RESPONSABLE */}
                                    <td className="ps-4 py-3">
                                        <div className="d-flex align-items-center">
                                            <div className="icon-box-success me-3">
                                                <i className="bi bi-person-badge-fill fs-5"></i>
                                            </div>
                                            <div>
                                                <div className="fw-bold text-dark">
                                                    {responsable.prenom} {responsable.nom?.toUpperCase()}
                                                </div>
                                            </div>
                                        </div>
                                    </td>

                                    {/* COLONNE 2 : TYPE */}
                                    <td>{getTypeBadge(responsable.type)}</td>

                                    {/* COLONNE 3 : CONTACT */}
                                    <td>
                                        <div className="text-dark small">
                                            <i className="bi bi-envelope me-2 text-muted"></i>
                                            {responsable.email}
                                        </div>
                                        <small className="text-muted">
                                            <i className="bi bi-telephone me-2"></i>
                                            {responsable.telephone || '-'}
                                        </small>
                                    </td>

                                    {/* COLONNE 4 : FORMATION */}
                                    <td className="text-muted">
                                        {responsable.formationId ? (
                                            <span className="badge bg-light text-dark border">
                                                    Formation #{responsable.formationId}
                                                </span>
                                        ) : '-'}
                                    </td>

                                    {/* COLONNE 5 : PÉRIODE */}
                                    <td className="text-muted small">
                                        <div>Début: {formatDate(responsable.dateDebutFonction)}</div>
                                        {responsable.dateFinFonction && (
                                            <div>Fin: {formatDate(responsable.dateFinFonction)}</div>
                                        )}
                                    </td>

                                    {/* COLONNE 6 : STATUT */}
                                    <td className="text-center">
                                        {responsable.actif ? (
                                            <span className="badge bg-success rounded-pill px-3">
                                                    <i className="bi bi-check-circle-fill small me-1"></i> Actif
                                                </span>
                                        ) : (
                                            <span className="badge bg-secondary rounded-pill px-3">
                                                    <i className="bi bi-circle-fill small me-1"></i> Inactif
                                                </span>
                                        )}
                                    </td>

                                    {/* COLONNE 7 : ACTIONS */}
                                    <td className="text-end pe-4">
                                        <div className="dropdown">
                                            <button
                                                className="btn btn-icon-only"
                                                type="button"
                                                data-bs-toggle="dropdown"
                                            >
                                                <i className="bi bi-three-dots-vertical"></i>
                                            </button>
                                            <ul className="dropdown-menu dropdown-menu-end shadow border-0">
                                                <li>
                                                    <button
                                                        className="dropdown-item py-2"
                                                        onClick={() => navigate(`/modifier-responsable/${responsable.id}`)}
                                                    >
                                                        <i className="bi bi-pencil me-2 text-primary"></i> Modifier
                                                    </button>
                                                </li>
                                                <li>
                                                    <button
                                                        className="dropdown-item py-2"
                                                        onClick={() => handleToggleActif(responsable)}
                                                    >
                                                        <i className={`bi ${responsable.actif ? 'bi-pause-circle' : 'bi-play-circle'} me-2 text-warning`}></i>
                                                        {responsable.actif ? 'Désactiver' : 'Réactiver'}
                                                    </button>
                                                </li>
                                                <li><hr className="dropdown-divider" /></li>
                                                <li>
                                                    <button
                                                        className="dropdown-item py-2 text-danger"
                                                        onClick={() => handleDelete(responsable.id)}
                                                    >
                                                        <i className="bi bi-trash me-2"></i> Supprimer
                                                    </button>
                                                </li>
                                            </ul>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
                <div className="card-footer bg-white border-top py-3">
                    <small className="text-muted">
                        Total : <strong>{displayedResponsables.length}</strong> responsable(s)
                    </small>
                </div>
            </div>
        </div>
    );
};

export default ResponsableList;