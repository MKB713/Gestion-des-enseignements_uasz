import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MaquetteService from '../../services/MaquetteService';

const FormationList = () => {
    const navigate = useNavigate();

    // --- ÉTATS ---
    const [allFormations, setAllFormations] = useState([]);
    const [displayedFormations, setDisplayedFormations] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [showArchives, setShowArchives] = useState(false);
    const [loading, setLoading] = useState(true);

    // --- CHARGEMENT ---
    useEffect(() => {
        loadData();
    }, []);

    const loadData = () => {
        setLoading(true);
        MaquetteService.getAllFormations()
            .then(response => {
                setAllFormations(response.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erreur API :", err);
                setLoading(false);
            });
    };

    // --- FILTRAGE ---
    useEffect(() => {
        let result = allFormations;

        // 1. Filtre Archives
        if (showArchives) {
            result = result.filter(f => f.archived === true || f.statut === 'ARCHIVEE');
        } else {
            result = result.filter(f => !f.archived && f.statut !== 'ARCHIVEE');
        }

        // 2. Filtre Recherche
        if (searchTerm.trim() !== '') {
            const term = searchTerm.toLowerCase();
            result = result.filter(f =>
                f.libelle.toLowerCase().includes(term) ||
                f.code.toLowerCase().includes(term)
            );
        }

        setDisplayedFormations(result);
    }, [allFormations, searchTerm, showArchives]);

    // --- ACTIONS ---
    const handleArchive = (id) => {
        const action = showArchives ? "restaurer" : "archiver";
        if(window.confirm(`Voulez-vous vraiment ${action} cette formation ?`)) {
            MaquetteService.archiverFormation(id)
                .then(() => loadData())
                .catch(err => alert("Erreur lors de l'opération."));
        }
    };

    // Fonction utilitaire pour formater la date
    const formatDate = (dateString) => {
        if (!dateString) return "21/11/2025"; // Date par défaut pour l'exemple visuel si null
        const options = { day: '2-digit', month: '2-digit', year: 'numeric' };
        return new Date(dateString).toLocaleDateString('fr-FR', options);
    };

    if (loading) return <div className="text-center mt-5"><div className="spinner-border text-success"></div></div>;

    return (
        <div className="container-fluid" style={{padding: '20px'}}>

            {/* --- EN-TÊTE --- */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark mb-0">Formations</h2>
                    <p className="text-muted mb-0">Gestion des offres de formation (Licence, Master, Doctorat).</p>
                </div>
                <button
                    className="btn btn-success px-4 py-2 shadow-sm fw-bold"
                    onClick={() => navigate('/ajouter-formation')}
                >
                    <i className="bi bi-plus-lg me-2"></i>Nouvelle Formation
                </button>
            </div>

            {/* --- BARRE DE FILTRES --- */}
            <div className="bg-white p-3 rounded shadow-sm mb-4 d-flex flex-wrap gap-3 align-items-center justify-content-between">
                <div className="flex-grow-1" style={{maxWidth: '600px'}}>
                    <div className="input-group">
                        <span className="input-group-text bg-white border-end-0 ps-3 text-muted">
                            <i className="bi bi-search"></i>
                        </span>
                        <input
                            type="text"
                            className="form-control border-start-0 ps-0 py-2"
                            placeholder="Rechercher par Code, Libellé ou Filière..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>
                <div>
                    <button
                        className={`btn ${showArchives ? 'btn-secondary' : 'btn-outline-secondary'} px-3 py-2`}
                        onClick={() => setShowArchives(!showArchives)}
                    >
                        <i className={`bi ${showArchives ? 'bi-folder-fill' : 'bi-archive'} me-2`}></i>
                        {showArchives ? "Voir les Actifs" : "Voir les Archives"}
                    </button>
                </div>
            </div>

            {/* --- TABLEAU --- */}
            <div className="card shadow-sm border-0">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead> {/* Le background vert est géré par academic.css */}
                        <tr>
                            <th className="ps-4 py-3">FORMATION</th>
                            <th>FILIÈRE & NIVEAU</th>
                            <th>DESCRIPTION</th>
                            <th>DATE CRÉATION</th>
                            <th className="text-center">STATUT</th>
                            <th className="text-end pe-4">ACTIONS</th>
                        </tr>
                        </thead>
                        <tbody>
                        {displayedFormations.length === 0 ? (
                            <tr>
                                <td colSpan="6" className="text-center py-5">
                                    <div className="text-muted opacity-50 mb-2">
                                        <i className="bi bi-inbox display-4"></i>
                                    </div>
                                    <h6 className="text-muted">Aucune formation trouvée.</h6>
                                </td>
                            </tr>
                        ) : (
                            displayedFormations.map(formation => (
                                <tr key={formation.id}>
                                    {/* 1. Colonne Formation avec Icône Verte */}
                                    <td className="ps-4 py-3">
                                        <div className="d-flex align-items-center">
                                            <div className="icon-box-success me-3">
                                                <i className="bi bi-mortarboard-fill fs-5"></i>
                                            </div>
                                            <div>
                                                <div className="fw-bold text-dark">{formation.libelle}</div>
                                                <span className="badge bg-light text-secondary border mt-1">
                                                    {formation.code}
                                                </span>
                                            </div>
                                        </div>
                                    </td>

                                    {/* 2. Colonne Filière & Niveau */}
                                    <td>
                                        <div className="fw-bold text-dark">
                                            {formation.filiere ? formation.filiere.libelle : 'Non défini'}
                                        </div>
                                        {formation.niveau && (
                                            <span className="badge bg-light text-dark border mt-1">
                                                {formation.niveau.numero}
                                            </span>
                                        )}
                                    </td>

                                    {/* 3. Description (Tronquée) */}
                                    <td className="text-muted">
                                        <span className="d-inline-block text-truncate" style={{maxWidth: '200px'}}>
                                            {formation.description || '-'}
                                        </span>
                                    </td>

                                    {/* 4. Date Création */}
                                    <td className="text-muted small">
                                        <i className="bi bi-calendar3 me-2"></i>
                                        {formatDate(formation.dateCreation)}
                                    </td>

                                    {/* 5. Statut (Pillule) */}
                                    <td className="text-center">
                                        {(formation.archived || formation.statut === 'ARCHIVEE') ? (
                                            <span className="badge-status-inactive">
                                                <i className="bi bi-dot me-1"></i>Inactive
                                            </span>
                                        ) : (
                                            <span className="badge-status-active">
                                                <i className="bi bi-dot me-1"></i>Active
                                            </span>
                                        )}
                                    </td>

                                    {/* 6. Actions (Menu 3 points) */}
                                    <td className="text-end pe-4">
                                        <div className="dropdown">
                                            <button className="btn btn-icon-only" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                                <i className="bi bi-three-dots-vertical"></i>
                                            </button>
                                            <ul className="dropdown-menu dropdown-menu-end shadow border-0">
                                                <li>
                                                    <button className="dropdown-item py-2" onClick={() => navigate(`/modifier-formation/${formation.id}`)}>
                                                        <i className="bi bi-pencil me-2 text-primary"></i> Modifier
                                                    </button>
                                                </li>
                                                <li><hr className="dropdown-divider" /></li>
                                                <li>
                                                    <button className="dropdown-item py-2 text-danger" onClick={() => handleArchive(formation.id)}>
                                                        <i className={`bi ${showArchives ? 'bi-arrow-counterclockwise' : 'bi-archive'} me-2`}></i>
                                                        {showArchives ? "Restaurer" : "Archiver"}
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
            </div>
        </div>
    );
};

export default FormationList;