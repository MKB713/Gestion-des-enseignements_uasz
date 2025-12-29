import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import EnseignantService from '../../services/EnseignantService';

const CoordinateurList = () => {
    const navigate = useNavigate();

    // --- ÉTATS ---
    const [coordinateurs, setCoordinateurs] = useState([]);
    const [displayedCoordinateurs, setDisplayedCoordinateurs] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterActif, setFilterActif] = useState('ALL'); // ALL, ACTIF, INACTIF
    const [loading, setLoading] = useState(true);

    // --- CHARGEMENT ---
    useEffect(() => {
        loadData();
    }, []);

    const loadData = () => {
        setLoading(true);
        EnseignantService.getAllCoordinateurs()
            .then(response => {
                setCoordinateurs(response.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erreur API:", err);
                setLoading(false);
            });
    };

    // --- FILTRAGE ---
    useEffect(() => {
        let result = coordinateurs;

        // Filtre par statut actif
        if (filterActif === 'ACTIF') {
            result = result.filter(c => c.actif === true);
        } else if (filterActif === 'INACTIF') {
            result = result.filter(c => c.actif === false);
        }

        // Filtre par recherche
        if (searchTerm.trim() !== '') {
            const term = searchTerm.toLowerCase();
            result = result.filter(c =>
                c.nom?.toLowerCase().includes(term) ||
                c.prenom?.toLowerCase().includes(term) ||
                c.email?.toLowerCase().includes(term)
            );
        }

        setDisplayedCoordinateurs(result);
    }, [coordinateurs, searchTerm, filterActif]);

    // --- ACTIONS ---
    const handleToggleActif = (coordinateur) => {
        const action = coordinateur.actif
            ? EnseignantService.desactiverCoordinateur
            : EnseignantService.reactiverCoordinateur;
        const message = coordinateur.actif ? "désactiver" : "réactiver";

        if (window.confirm(`Voulez-vous ${message} ce coordinateur ?`)) {
            action(coordinateur.id)
                .then(() => loadData())
                .catch(err => alert("Erreur lors de l'opération"));
        }
    };

    const handleDelete = (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer ce coordinateur ?")) {
            EnseignantService.deleteCoordinateur(id)
                .then(() => loadData())
                .catch(err => alert("Erreur lors de la suppression"));
        }
    };

    // --- UTILITAIRES ---
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('fr-FR');
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
                    <h2 className="fw-bold text-dark mb-0">Coordinateurs de Formation</h2>
                    <p className="text-muted mb-0">Gestion des coordinateurs pédagogiques.</p>
                </div>
                <button
                    className="btn btn-success px-4 py-2 shadow-sm fw-bold"
                    onClick={() => navigate('/ajouter-coordinateur')}
                >
                    <i className="bi bi-plus-lg me-2"></i>Nouveau Coordinateur
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
                                className={`btn ${filterActif === 'ALL' ? 'btn-success' : 'btn-outline-secondary'}`}
                                onClick={() => setFilterActif('ALL')}
                            >
                                Tous
                            </button>
                            <button
                                type="button"
                                className={`btn ${filterActif === 'ACTIF' ? 'btn-success' : 'btn-outline-secondary'}`}
                                onClick={() => setFilterActif('ACTIF')}
                            >
                                Actifs
                            </button>
                            <button
                                type="button"
                                className={`btn ${filterActif === 'INACTIF' ? 'btn-secondary' : 'btn-outline-secondary'}`}
                                onClick={() => setFilterActif('INACTIF')}
                            >
                                Inactifs
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
                            <th className="ps-4 py-3">COORDINATEUR</th>
                            <th>CONTACT</th>
                            <th>FORMATION</th>
                            <th>PÉRIODE</th>
                            <th className="text-center">STATUT</th>
                            <th className="text-end pe-4">ACTIONS</th>
                        </tr>
                        </thead>
                        <tbody>
                        {displayedCoordinateurs.length === 0 ? (
                            <tr>
                                <td colSpan="6" className="text-center py-5">
                                    <div className="text-muted opacity-50 mb-2">
                                        <i className="bi bi-person-x display-4"></i>
                                    </div>
                                    <h6 className="text-muted">Aucun coordinateur trouvé.</h6>
                                </td>
                            </tr>
                        ) : (
                            displayedCoordinateurs.map(coordinateur => (
                                <tr key={coordinateur.id}>
                                    {/* COLONNE 1 : COORDINATEUR */}
                                    <td className="ps-4 py-3">
                                        <div className="d-flex align-items-center">
                                            <div className="icon-box-success me-3">
                                                <i className="bi bi-person-workspace fs-5"></i>
                                            </div>
                                            <div>
                                                <div className="fw-bold text-dark">
                                                    {coordinateur.prenom} {coordinateur.nom?.toUpperCase()}
                                                </div>
                                            </div>
                                        </div>
                                    </td>

                                    {/* COLONNE 2 : CONTACT */}
                                    <td>
                                        <div className="text-dark small">
                                            <i className="bi bi-envelope me-2 text-muted"></i>
                                            {coordinateur.email}
                                        </div>
                                        <small className="text-muted">
                                            <i className="bi bi-telephone me-2"></i>
                                            {coordinateur.telephone || '-'}
                                        </small>
                                    </td>

                                    {/* COLONNE 3 : FORMATION */}
                                    <td className="text-muted">
                                        {coordinateur.formationId ? (
                                            <span className="badge bg-light text-dark border">
                                                    Formation #{coordinateur.formationId}
                                                </span>
                                        ) : '-'}
                                    </td>

                                    {/* COLONNE 4 : PÉRIODE */}
                                    <td className="text-muted small">
                                        <div>Début: {formatDate(coordinateur.dateDebutFonction)}</div>
                                        {coordinateur.dateFinFonction && (
                                            <div>Fin: {formatDate(coordinateur.dateFinFonction)}</div>
                                        )}
                                    </td>

                                    {/* COLONNE 5 : STATUT */}
                                    <td className="text-center">
                                        {coordinateur.actif ? (
                                            <span className="badge bg-success rounded-pill px-3">
                                                    <i className="bi bi-check-circle-fill small me-1"></i> Actif
                                                </span>
                                        ) : (
                                            <span className="badge bg-secondary rounded-pill px-3">
                                                    <i className="bi bi-circle-fill small me-1"></i> Inactif
                                                </span>
                                        )}
                                    </td>

                                    {/* COLONNE 6 : ACTIONS */}
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
                                                        onClick={() => navigate(`/modifier-coordinateur/${coordinateur.id}`)}
                                                    >
                                                        <i className="bi bi-pencil me-2 text-primary"></i> Modifier
                                                    </button>
                                                </li>
                                                <li>
                                                    <button
                                                        className="dropdown-item py-2"
                                                        onClick={() => handleToggleActif(coordinateur)}
                                                    >
                                                        <i className={`bi ${coordinateur.actif ? 'bi-pause-circle' : 'bi-play-circle'} me-2 text-warning`}></i>
                                                        {coordinateur.actif ? 'Désactiver' : 'Réactiver'}
                                                    </button>
                                                </li>
                                                <li><hr className="dropdown-divider" /></li>
                                                <li>
                                                    <button
                                                        className="dropdown-item py-2 text-danger"
                                                        onClick={() => handleDelete(coordinateur.id)}
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
                        Total : <strong>{displayedCoordinateurs.length}</strong> coordinateur(s)
                    </small>
                </div>
            </div>
        </div>
    );
};

export default CoordinateurList;