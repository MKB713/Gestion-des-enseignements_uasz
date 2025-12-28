import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import EnseignantService from '../services/EnseignantService';

const EnseignantList = () => {
    const navigate = useNavigate();

    // --- ÉTATS ---
    const [allEnseignants, setAllEnseignants] = useState([]);
    const [displayedEnseignants, setDisplayedEnseignants] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [showArchives, setShowArchives] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // --- CHARGEMENT INITIAL ---
    useEffect(() => {
        loadData();
    }, [showArchives]);

    const loadData = () => {
        setLoading(true);
        setError(null);

        const apiCall = showArchives
            ? EnseignantService.getEnseignantsArchives()
            : EnseignantService.getAllEnseignants();

        apiCall
            .then(response => {
                setAllEnseignants(response.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erreur API:", err);
                setError("Impossible de contacter le serveur. Vérifiez que le Gateway est démarré sur le port 8080.");
                setLoading(false);
            });
    };

    // --- FILTRAGE ---
    useEffect(() => {
        let result = allEnseignants;

        if (searchTerm.trim() !== '') {
            const term = searchTerm.toLowerCase();
            result = result.filter(ens =>
                ens.nom?.toLowerCase().includes(term) ||
                ens.prenom?.toLowerCase().includes(term) ||
                ens.email?.toLowerCase().includes(term) ||
                ens.matricule?.toString().includes(term) ||
                ens.specialite?.toLowerCase().includes(term)
            );
        }

        setDisplayedEnseignants(result);
    }, [allEnseignants, searchTerm]);

    // --- ACTIONS ---
    const handleArchive = (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir archiver cet enseignant ?")) {
            EnseignantService.archiver(id)
                .then(() => loadData())
                .catch(err => alert("Erreur lors de l'archivage"));
        }
    };

    const handleRestaurer = (id) => {
        if (window.confirm("Voulez-vous restaurer cet enseignant ?")) {
            EnseignantService.restaurer(id)
                .then(() => loadData())
                .catch(err => alert("Erreur lors de la restauration"));
        }
    };

    const toggleStatut = (enseignant) => {
        const action = enseignant.estActif ? EnseignantService.desactiver : EnseignantService.activer;
        const message = enseignant.estActif ? "Désactiver" : "Activer";

        if (window.confirm(`Voulez-vous ${message} ce compte ?`)) {
            action(enseignant.id)
                .then(() => loadData())
                .catch(err => alert("Erreur lors du changement de statut"));
        }
    };

    // --- UTILITAIRES ---
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('fr-FR');
    };

    const getStatutBadge = (enseignant) => {
        if (enseignant.statutEnseignant === 'ARCHIVE') {
            return <span className="badge bg-warning text-dark border border-warning bg-opacity-25">
                <i className="bi bi-archive-fill me-1 small"></i> Archivé
            </span>;
        }

        if (enseignant.estActif) {
            return <span className="badge bg-success rounded-pill px-3">
                <i className="bi bi-check-circle-fill small me-1"></i> Actif
            </span>;
        } else {
            return <span className="badge bg-danger rounded-pill px-3">
                <i className="bi bi-slash-circle me-1 small"></i> Inactif
            </span>;
        }
    };

    // --- RENDER ---
    if (loading) return (
        <div className="d-flex justify-content-center mt-5">
            <div className="spinner-border text-success" role="status">
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
        <div className="container-fluid" style={{padding: '20px'}}>

            {/* EN-TÊTE */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark mb-0">
                        {showArchives ? "Enseignants Archivés" : "Enseignants"}
                    </h2>
                    <p className="text-muted mb-0">Gestion du personnel enseignant (permanents et vacataires).</p>
                </div>
                <div className="d-flex gap-2">
                    <button
                        className={`btn ${showArchives ? 'btn-secondary' : 'btn-outline-secondary'} shadow-sm`}
                        onClick={() => setShowArchives(!showArchives)}
                    >
                        <i className={`bi ${showArchives ? 'bi-arrow-left' : 'bi-archive'} me-2`}></i>
                        {showArchives ? "Retour aux Actifs" : "Voir Archives"}
                    </button>

                    {!showArchives && (
                        <button
                            className="btn btn-success px-4 py-2 shadow-sm fw-bold"
                            onClick={() => navigate('/ajouter-enseignant')}
                        >
                            <i className="bi bi-plus-lg me-2"></i>Nouvel Enseignant
                        </button>
                    )}
                </div>
            </div>

            {/* BARRE DE RECHERCHE */}
            <div className="bg-white p-3 rounded shadow-sm mb-4">
                <div className="input-group">
                    <span className="input-group-text bg-white border-end-0 ps-3 text-muted">
                        <i className="bi bi-search"></i>
                    </span>
                    <input
                        type="text"
                        className="form-control border-start-0 ps-0 py-2"
                        placeholder="Rechercher par Nom, Prénom, Email, Matricule, Spécialité..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
            </div>

            {/* TABLEAU */}
            <div className="card shadow-sm border-0">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead>
                        <tr>
                            <th className="ps-4 py-3">IDENTITÉ</th>
                            <th>GRADE & SPÉCIALITÉ</th>
                            <th>STATUT</th>
                            <th>CONTACT</th>
                            <th>DATE EMBAUCHE</th>
                            <th className="text-center">ÉTAT</th>
                            <th className="text-end pe-4">ACTIONS</th>
                        </tr>
                        </thead>
                        <tbody>
                        {displayedEnseignants.length === 0 ? (
                            <tr>
                                <td colSpan="7" className="text-center py-5">
                                    <div className="text-muted opacity-50 mb-2">
                                        <i className="bi bi-person-x display-4"></i>
                                    </div>
                                    <h6 className="text-muted">
                                        {showArchives ? "Aucun enseignant archivé." : "Aucun enseignant trouvé."}
                                    </h6>
                                </td>
                            </tr>
                        ) : (
                            displayedEnseignants.map(enseignant => (
                                <tr key={enseignant.id}>
                                    {/* COLONNE 1 : IDENTITÉ */}
                                    <td className="ps-4 py-3">
                                        <div className="d-flex align-items-center">
                                            <div className="icon-box-success me-3">
                                                <i className="bi bi-person-fill fs-5"></i>
                                            </div>
                                            <div>
                                                <div className="fw-bold text-dark">
                                                    {enseignant.prenom} {enseignant.nom?.toUpperCase()}
                                                </div>
                                                <span className="badge bg-light text-secondary border mt-1 font-monospace" style={{fontSize: '0.7rem'}}>
                                                        {enseignant.matricule}
                                                    </span>
                                            </div>
                                        </div>
                                    </td>

                                    {/* COLONNE 2 : GRADE & SPÉCIALITÉ */}
                                    <td>
                                        <div className="fw-bold text-dark">{enseignant.grade || '-'}</div>
                                        <small className="text-muted">{enseignant.specialite || 'Non spécifié'}</small>
                                    </td>

                                    {/* COLONNE 3 : STATUT */}
                                    <td>
                                        {enseignant.statut === 'PERMANENT' ? (
                                            <span className="badge bg-primary bg-opacity-10 text-primary border border-primary">
                                                    <i className="bi bi-star-fill me-1 small"></i> Permanent
                                                </span>
                                        ) : (
                                            <span className="badge bg-secondary bg-opacity-10 text-secondary border border-secondary">
                                                    <i className="bi bi-clock me-1 small"></i> Vacataire
                                                </span>
                                        )}
                                    </td>

                                    {/* COLONNE 4 : CONTACT */}
                                    <td>
                                        <div className="text-dark small">
                                            <i className="bi bi-envelope me-2 text-muted"></i>
                                            {enseignant.email}
                                        </div>
                                        <small className="text-muted">
                                            <i className="bi bi-telephone me-2"></i>
                                            {enseignant.telephone || '-'}
                                        </small>
                                    </td>

                                    {/* COLONNE 5 : DATE EMBAUCHE */}
                                    <td className="text-muted small">
                                        <i className="bi bi-calendar3 me-2"></i>
                                        {formatDate(enseignant.dateEmbauche)}
                                    </td>

                                    {/* COLONNE 6 : ÉTAT */}
                                    <td className="text-center">
                                        {getStatutBadge(enseignant)}
                                    </td>

                                    {/* COLONNE 7 : ACTIONS */}
                                    <td className="text-end pe-4">
                                        {showArchives ? (
                                            <button
                                                className="btn btn-sm btn-outline-success fw-bold"
                                                onClick={() => handleRestaurer(enseignant.id)}
                                                title="Restaurer"
                                            >
                                                <i className="bi bi-arrow-counterclockwise me-1"></i> Restaurer
                                            </button>
                                        ) : (
                                            <div className="dropdown">
                                                <button
                                                    className="btn btn-icon-only"
                                                    type="button"
                                                    data-bs-toggle="dropdown"
                                                    aria-expanded="false"
                                                >
                                                    <i className="bi bi-three-dots-vertical"></i>
                                                </button>
                                                <ul className="dropdown-menu dropdown-menu-end shadow border-0">
                                                    <li>
                                                        <button
                                                            className="dropdown-item py-2"
                                                            onClick={() => navigate(`/modifier-enseignant/${enseignant.id}`)}
                                                        >
                                                            <i className="bi bi-pencil me-2 text-primary"></i> Modifier
                                                        </button>
                                                    </li>
                                                    <li>
                                                        <button
                                                            className="dropdown-item py-2"
                                                            onClick={() => toggleStatut(enseignant)}
                                                        >
                                                            <i className={`bi ${enseignant.estActif ? 'bi-pause-circle' : 'bi-play-circle'} me-2 text-warning`}></i>
                                                            {enseignant.estActif ? 'Désactiver' : 'Activer'}
                                                        </button>
                                                    </li>
                                                    <li><hr className="dropdown-divider" /></li>
                                                    <li>
                                                        <button
                                                            className="dropdown-item py-2 text-danger"
                                                            onClick={() => handleArchive(enseignant.id)}
                                                        >
                                                            <i className="bi bi-archive me-2"></i> Archiver
                                                        </button>
                                                    </li>
                                                </ul>
                                            </div>
                                        )}
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
                <div className="card-footer bg-white border-top py-3">
                    <small className="text-muted">
                        Total : <strong>{displayedEnseignants.length}</strong> enseignant(s)
                    </small>
                </div>
            </div>
        </div>
    );
};

export default EnseignantList;