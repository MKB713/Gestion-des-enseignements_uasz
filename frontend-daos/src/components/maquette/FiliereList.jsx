import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MaquetteService from '../../services/MaquetteService';

const FiliereList = () => {
    const navigate = useNavigate();
    const [filieres, setFilieres] = useState([]);
    const [displayedFilieres, setDisplayedFilieres] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = () => {
        setLoading(true);
        MaquetteService.getAllFilieres()
            .then(res => {
                setFilieres(res.data);
                setDisplayedFilieres(res.data);
                setLoading(false);
            })
            .catch(err => setLoading(false));
    };

    // Filtrage Recherche
    useEffect(() => {
        if (searchTerm.trim() === '') {
            setDisplayedFilieres(filieres);
        } else {
            const term = searchTerm.toLowerCase();
            setDisplayedFilieres(filieres.filter(f =>
                f.libelle.toLowerCase().includes(term) ||
                (f.description && f.description.toLowerCase().includes(term))
            ));
        }
    }, [searchTerm, filieres]);

    const handleDelete = (id) => {
        if(window.confirm("Êtes-vous sûr de vouloir supprimer cette filière ?")) {
            MaquetteService.deleteFiliere(id).then(() => loadData());
        }
    };

    if (loading) return <div className="text-center mt-5"><div className="spinner-border text-success"></div></div>;

    return (
        <div className="container-fluid" style={{padding: '20px'}}>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark mb-0">Filières</h2>
                    <p className="text-muted mb-0">Gestion des départements et filières.</p>
                </div>
                <button className="btn btn-success px-4 py-2 shadow-sm fw-bold" onClick={() => navigate('/ajouter-filiere')}>
                    <i className="bi bi-plus-lg me-2"></i>Nouvelle Filière
                </button>
            </div>

            <div className="bg-white p-3 rounded shadow-sm mb-4">
                <div className="input-group">
                    <span className="input-group-text bg-white border-end-0 ps-3 text-muted"><i className="bi bi-search"></i></span>
                    <input type="text" className="form-control border-start-0 ps-0 py-2" placeholder="Rechercher une filière..."
                           value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
                </div>
            </div>

            <div className="card shadow-sm border-0">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead>
                        <tr>
                            <th className="ps-4 py-3">INTITULÉ</th>
                            <th>DESCRIPTION</th>
                            <th className="text-end pe-4">ACTIONS</th>
                        </tr>
                        </thead>
                        <tbody>
                        {displayedFilieres.map(f => (
                            <tr key={f.id}>
                                <td className="ps-4 py-3">
                                    <div className="d-flex align-items-center">
                                        <div className="icon-box-success me-3">
                                            <i className="bi bi-building fs-5"></i>
                                        </div>
                                        <div className="fw-bold text-dark">{f.libelle}</div>
                                    </div>
                                </td>
                                <td className="text-muted">{f.description || '-'}</td>
                                <td className="text-end pe-4">
                                    <button className="btn btn-sm btn-light border text-primary me-2" onClick={() => navigate(`/modifier-filiere/${f.id}`)}>
                                        <i className="bi bi-pencil-square"></i>
                                    </button>
                                    <button className="btn btn-sm btn-light border text-danger" onClick={() => handleDelete(f.id)}>
                                        <i className="bi bi-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};
export default FiliereList;