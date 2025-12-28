import React, { useState, useEffect } from 'react';
import DeroulementService from '../../services/DeroulementService';
import MaquetteService from '../../services/MaquetteService';

function ProgressionBoard() {
    const [progressions, setProgressions] = useState([]);
    const [modules, setModules] = useState([]);
    const [selectedModule, setSelectedModule] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadModules();
        loadProgressions();
    }, []);

    const loadModules = async () => {
        try {
            const response = await MaquetteService.getAllModules();
            setModules(response.data);
            if (response.data.length > 0) {
                setSelectedModule(response.data[0].id);
            }
        } catch (err) {
            console.error('Erreur lors du chargement des modules', err);
        }
    };

    const loadProgressions = async () => {
        try {
            setLoading(true);
            const response = await DeroulementService.getProgressionByModule(selectedModule);
            setProgressions(response.data);
            setError('');
        } catch (err) {
            setError('Erreur lors du chargement des progressions');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (selectedModule) {
            loadProgressions();
        }
    }, [selectedModule]);

    const getProgressColor = (percentage) => {
        if (percentage >= 80) return '#28a745';
        if (percentage >= 50) return '#ffc107';
        return '#dc3545';
    };

    if (loading) {
        return <div className="text-center mt-5"><div className="spinner-border"></div></div>;
    }

    return (
        <div className="container-fluid">
            <div className="mb-4">
                <h2><i className="bi bi-bar-chart"></i> Progression des Enseignements</h2>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            <div className="row mb-4">
                <div className="col-md-4">
                    <label className="form-label">Sélectionner un module:</label>
                    <select 
                        className="form-control"
                        value={selectedModule}
                        onChange={(e) => setSelectedModule(e.target.value)}
                    >
                        {modules.map(module => (
                            <option key={module.id} value={module.id}>
                                {module.nom}
                            </option>
                        ))}
                    </select>
                </div>
            </div>

            {progressions.length > 0 ? (
                <div className="row">
                    {progressions.map(progression => (
                        <div key={progression.id} className="col-md-6 mb-4">
                            <div className="card-custom p-4">
                                <div className="d-flex justify-content-between align-items-center mb-3">
                                    <h5>{progression.elementNom}</h5>
                                    <span className="badge bg-primary">{progression.typeElement}</span>
                                </div>

                                <p className="text-muted mb-2">
                                    Séances: {progression.seancesRealisees} / {progression.seancesPreves}
                                </p>

                                <div className="progress-bar-custom mb-3">
                                    <div 
                                        className="progress-fill"
                                        style={{
                                            width: `${progression.pourcentageProgression}%`,
                                            backgroundColor: getProgressColor(progression.pourcentageProgression)
                                        }}
                                    >
                                        {progression.pourcentageProgression}%
                                    </div>
                                </div>

                                <div className="row text-center text-muted small">
                                    <div className="col-4">
                                        <strong>{progression.heuresTotalesPreves}</strong>h
                                        <br />
                                        <small>Prévues</small>
                                    </div>
                                    <div className="col-4">
                                        <strong>{progression.heuresRealisees}</strong>h
                                        <br />
                                        <small>Réalisées</small>
                                    </div>
                                    <div className="col-4">
                                        <strong>{(progression.heuresTotalesPreves - progression.heuresRealisees).toFixed(1)}</strong>h
                                        <br />
                                        <small>Restantes</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <div className="alert alert-info text-center">
                    Aucune progression disponible pour ce module
                </div>
            )}
        </div>
    );
}

export default ProgressionBoard;
