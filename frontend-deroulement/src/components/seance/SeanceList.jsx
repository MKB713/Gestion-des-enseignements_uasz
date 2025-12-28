import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import DeroulementService from '../../services/DeroulementService';
import MaquetteService from '../../services/MaquetteService';

function SeanceList() {
    const [seances, setSeances] = useState([]);
    const [modules, setModules] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [filterModule, setFilterModule] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        loadSeances();
        loadModules();
    }, []);

    const loadSeances = async () => {
        try {
            setLoading(true);
            const response = await DeroulementService.getAllSeances();
            setSeances(response.data);
            setError('');
        } catch (err) {
            setError('Erreur lors du chargement des séances');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const loadModules = async () => {
        try {
            const response = await MaquetteService.getAllModules();
            setModules(response.data);
        } catch (err) {
            console.error('Erreur lors du chargement des modules', err);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Êtes-vous sûr de vouloir supprimer cette séance?')) {
            try {
                await DeroulementService.deleteSeance(id);
                loadSeances();
            } catch (err) {
                setError('Erreur lors de la suppression');
            }
        }
    };

    const filteredSeances = seances.filter(seance => {
        const matchSearch = seance.titre?.toLowerCase().includes(searchTerm.toLowerCase());
        const matchModule = !filterModule || seance.moduleId === parseInt(filterModule);
        return matchSearch && matchModule;
    });

    if (loading) {
        return <div className="text-center mt-5"><div className="spinner-border"></div></div>;
    }

    return (
        <div className="container-fluid">
            <div className="row mb-4">
                <div className="col-md-8">
                    <h2><i className="bi bi-calendar-event"></i> Gestion des Séances</h2>
                </div>
                <div className="col-md-4 text-end">
                    <button 
                        className="btn btn-primary-custom"
                        onClick={() => navigate('/ajouter-seance')}
                    >
                        <i className="bi bi-plus"></i> Ajouter une Séance
                    </button>
                </div>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            <div className="row mb-4">
                <div className="col-md-6">
                    <input 
                        type="text"
                        className="form-control"
                        placeholder="Rechercher une séance..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
                <div className="col-md-6">
                    <select 
                        className="form-control"
                        value={filterModule}
                        onChange={(e) => setFilterModule(e.target.value)}
                    >
                        <option value="">Tous les modules</option>
                        {modules.map(module => (
                            <option key={module.id} value={module.id}>
                                {module.nom}
                            </option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="table-responsive">
                <table className="table table-custom table-hover">
                    <thead>
                        <tr>
                            <th>Titre</th>
                            <th>Module</th>
                            <th>Date</th>
                            <th>Durée (h)</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredSeances.length > 0 ? (
                            filteredSeances.map(seance => (
                                <tr key={seance.id}>
                                    <td>{seance.titre}</td>
                                    <td>{modules.find(m => m.id === seance.moduleId)?.nom}</td>
                                    <td>{new Date(seance.dateSeance).toLocaleDateString('fr-FR')}</td>
                                    <td>{seance.duree}</td>
                                    <td>
                                        <span className={`badge ${seance.statut === 'TERMINEE' ? 'bg-success' : seance.statut === 'EN_COURS' ? 'bg-info' : 'bg-secondary'}`}>
                                            {seance.statut}
                                        </span>
                                    </td>
                                    <td>
                                        <button 
                                            className="btn btn-sm btn-warning me-2"
                                            onClick={() => navigate(`/modifier-seance/${seance.id}`)}
                                        >
                                            <i className="bi bi-pencil"></i> Modifier
                                        </button>
                                        <button 
                                            className="btn btn-sm btn-danger"
                                            onClick={() => handleDelete(seance.id)}
                                        >
                                            <i className="bi bi-trash"></i> Supprimer
                                        </button>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="6" className="text-center text-muted">
                                    Aucune séance trouvée
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default SeanceList;
