import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MaquetteService from '../../services/MaquetteService';

const NiveauList = () => {
    const navigate = useNavigate();
    const [niveaux, setNiveaux] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = () => {
        setLoading(true);
        MaquetteService.getAllNiveaux()
            .then(res => {
                // Sécurité : on s'assure que c'est un tableau
                const data = Array.isArray(res.data) ? res.data : [];
                setNiveaux(data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erreur chargement niveaux:", err);
                setLoading(false);
            });
    };

    const handleDelete = (id) => {
        if(window.confirm("Supprimer ce niveau ?")) {
            MaquetteService.deleteNiveau(id).then(() => loadData());
        }
    };

    // Helper couleur (avec sécurité si cycle est null)
    const getBadgeColor = (cycle) => {
        if (!cycle) return 'bg-secondary bg-opacity-10 text-dark';
        const c = cycle.toUpperCase();
        if(c === 'LICENCE') return 'bg-info bg-opacity-10 text-info border-info';
        if(c === 'MASTER') return 'bg-warning bg-opacity-10 text-warning border-warning';
        if(c === 'DOCTORAT') return 'bg-danger bg-opacity-10 text-danger border-danger';
        return 'bg-secondary bg-opacity-10 text-dark';
    }

    if (loading) return <div className="text-center mt-5"><div className="spinner-border text-success"></div></div>;

    return (
        <div className="container-fluid" style={{padding: '20px'}}>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark mb-0">Niveaux d'étude</h2>
                    <p className="text-muted mb-0">Configuration des cycles (L1, L2, M1...).</p>
                </div>
                <button className="btn btn-success px-4 py-2 shadow-sm fw-bold" onClick={() => navigate('/ajouter-niveau')}>
                    <i className="bi bi-plus-lg me-2"></i>Nouveau Niveau
                </button>
            </div>

            <div className="card shadow-sm border-0">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead>
                        <tr>
                            <th className="ps-4 py-3">CYCLE</th>
                            <th>NIVEAU / ANNÉE</th>
                            <th>DESCRIPTION</th>
                            <th className="text-end pe-4">ACTIONS</th>
                        </tr>
                        </thead>
                        <tbody>
                        {niveaux.length === 0 ? (
                            <tr>
                                <td colSpan="4" className="text-center py-5 text-muted">
                                    Aucun niveau trouvé. Veuillez en ajouter.
                                </td>
                            </tr>
                        ) : (
                            niveaux.map(n => (
                                <tr key={n.id}>
                                    <td className="ps-4 py-3">
                                            <span className={`badge border ${getBadgeColor(n.cycle)}`}>
                                                {n.cycle || 'N/A'}
                                            </span>
                                    </td>
                                    <td>
                                        <div className="d-flex align-items-center">
                                            <div className="rounded-circle bg-light border fw-bold d-flex justify-content-center align-items-center me-2"
                                                 style={{width:'35px', height:'35px'}}>
                                                {n.numero || '?'}
                                            </div>
                                            <span className="fw-bold text-dark">ème Année</span>
                                        </div>
                                    </td>
                                    <td className="text-muted">{n.description || '-'}</td>
                                    <td className="text-end pe-4">
                                        <button className="btn btn-sm btn-light border text-primary me-2" onClick={() => navigate(`/modifier-niveau/${n.id}`)}>
                                            <i className="bi bi-pencil-square"></i>
                                        </button>
                                        <button className="btn btn-sm btn-light border text-danger" onClick={() => handleDelete(n.id)}>
                                            <i className="bi bi-trash"></i>
                                        </button>
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
export default NiveauList;