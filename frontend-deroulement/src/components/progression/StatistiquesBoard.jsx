import React, { useState, useEffect } from 'react';
import DeroulementService from '../../services/DeroulementService';

function StatistiquesBoard() {
    const [stats, setStats] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadStatistiques();
    }, []);

    const loadStatistiques = async () => {
        try {
            setLoading(true);
            const response = await DeroulementService.getStatistiques();
            setStats(response.data);
            setError('');
        } catch (err) {
            setError('Erreur lors du chargement des statistiques');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="text-center mt-5"><div className="spinner-border"></div></div>;
    }

    return (
        <div className="container-fluid">
            <div className="mb-4">
                <h2><i className="bi bi-pie-chart"></i> Statistiques et Rapports</h2>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {/* Cards de statistiques globales */}
            <div className="stats-container mb-4">
                <div className="stat-card">
                    <h3>Total Modules</h3>
                    <div className="stat-value">{stats.totalModules || 0}</div>
                </div>
                <div className="stat-card">
                    <h3>Total UE</h3>
                    <div className="stat-value">{stats.totalUE || 0}</div>
                </div>
                <div className="stat-card">
                    <h3>Total EC</h3>
                    <div className="stat-value">{stats.totalEC || 0}</div>
                </div>
                <div className="stat-card">
                    <h3>Séances Réalisées</h3>
                    <div className="stat-value">{stats.seancesRealisees || 0}</div>
                </div>
                <div className="stat-card">
                    <h3>Taux Moyen</h3>
                    <div className="stat-value">{(stats.tauxMoyen || 0).toFixed(1)}%</div>
                </div>
                <div className="stat-card">
                    <h3>Heures Réalisées</h3>
                    <div className="stat-value">{(stats.heuresRealisees || 0).toFixed(1)}h</div>
                </div>
            </div>

            {/* Détails par module */}
            {stats.modulesStats && stats.modulesStats.length > 0 && (
                <div className="card-custom p-4">
                    <h4 className="mb-4">Statistiques par Module</h4>
                    <div className="table-responsive">
                        <table className="table table-custom table-hover">
                            <thead>
                                <tr>
                                    <th>Module</th>
                                    <th>Heures Prévues</th>
                                    <th>Heures Réalisées</th>
                                    <th>Pourcentage</th>
                                    <th>Séances</th>
                                </tr>
                            </thead>
                            <tbody>
                                {stats.modulesStats.map((module, index) => (
                                    <tr key={index}>
                                        <td>{module.nomModule}</td>
                                        <td>{module.heuresPreves}</td>
                                        <td>{module.heuresRealisees}</td>
                                        <td>
                                            <div className="progress-bar-custom" style={{ width: '150px' }}>
                                                <div 
                                                    className="progress-fill"
                                                    style={{ width: `${module.pourcentage}%` }}
                                                >
                                                    {module.pourcentage.toFixed(1)}%
                                                </div>
                                            </div>
                                        </td>
                                        <td>{module.seancesRealisees}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Détails par enseignant */}
            {stats.enseignantsStats && stats.enseignantsStats.length > 0 && (
                <div className="card-custom p-4 mt-4">
                    <h4 className="mb-4">Performance par Enseignant</h4>
                    <div className="table-responsive">
                        <table className="table table-custom table-hover">
                            <thead>
                                <tr>
                                    <th>Enseignant</th>
                                    <th>Modules</th>
                                    <th>Séances Données</th>
                                    <th>Taux Moyen</th>
                                </tr>
                            </thead>
                            <tbody>
                                {stats.enseignantsStats.map((enseignant, index) => (
                                    <tr key={index}>
                                        <td>{enseignant.nomEnseignant}</td>
                                        <td>{enseignant.nbModules}</td>
                                        <td>{enseignant.seancesDonnees}</td>
                                        <td>
                                            <div className="progress-bar-custom" style={{ width: '150px' }}>
                                                <div 
                                                    className="progress-fill"
                                                    style={{ width: `${enseignant.tauxMoyen}%` }}
                                                >
                                                    {enseignant.tauxMoyen.toFixed(1)}%
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
}

export default StatistiquesBoard;
