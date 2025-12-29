import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import DeroulementService from '../../services/DeroulementService';
import MaquetteService from '../../services/MaquetteService';

function SeanceForm() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        titre: '',
        moduleId: '',
        dateSeance: '',
        duree: '',
        description: '',
        statut: 'PLANIFIEE'
    });
    const [modules, setModules] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        loadModules();
        if (id) {
            loadSeance();
        }
    }, [id]);

    const loadModules = async () => {
        try {
            const response = await MaquetteService.getAllModules();
            setModules(response.data);
        } catch (err) {
            console.error('Erreur lors du chargement des modules', err);
        }
    };

    const loadSeance = async () => {
        try {
            setLoading(true);
            const response = await DeroulementService.getSeanceById(id);
            const seance = response.data;
            setFormData({
                titre: seance.titre,
                moduleId: seance.moduleId,
                dateSeance: seance.dateSeance.split('T')[0],
                duree: seance.duree,
                description: seance.description || '',
                statut: seance.statut
            });
        } catch (err) {
            setError('Erreur lors du chargement de la séance');
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            if (id) {
                await DeroulementService.updateSeance(id, formData);
                alert('Séance modifiée avec succès!');
            } else {
                await DeroulementService.createSeance(formData);
                alert('Séance créée avec succès!');
            }
            navigate('/lst-seances');
        } catch (err) {
            setError('Erreur lors de la sauvegarde');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container">
            <div className="row">
                <div className="col-md-8 mx-auto">
                    <div className="card-custom p-4">
                        <h2 className="mb-4">
                            {id ? 'Modifier la Séance' : 'Créer une Nouvelle Séance'}
                        </h2>

                        {error && <div className="alert alert-danger">{error}</div>}

                        <form onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <label className="form-label">Titre de la séance *</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="titre"
                                    value={formData.titre}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Module *</label>
                                <select
                                    className="form-control"
                                    name="moduleId"
                                    value={formData.moduleId}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="">Sélectionner un module</option>
                                    {modules.map(module => (
                                        <option key={module.id} value={module.id}>
                                            {module.nom}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="row">
                                <div className="col-md-6 mb-3">
                                    <label className="form-label">Date de la séance *</label>
                                    <input
                                        type="date"
                                        className="form-control"
                                        name="dateSeance"
                                        value={formData.dateSeance}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="col-md-6 mb-3">
                                    <label className="form-label">Durée (heures) *</label>
                                    <input
                                        type="number"
                                        className="form-control"
                                        name="duree"
                                        value={formData.duree}
                                        onChange={handleChange}
                                        step="0.5"
                                        required
                                    />
                                </div>
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Statut *</label>
                                <select
                                    className="form-control"
                                    name="statut"
                                    value={formData.statut}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="PLANIFIEE">Planifiée</option>
                                    <option value="EN_COURS">En cours</option>
                                    <option value="TERMINEE">Terminée</option>
                                </select>
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Description</label>
                                <textarea
                                    className="form-control"
                                    name="description"
                                    value={formData.description}
                                    onChange={handleChange}
                                    rows="4"
                                ></textarea>
                            </div>

                            <div className="d-flex gap-2">
                                <button 
                                    type="submit" 
                                    className="btn btn-primary-custom"
                                    disabled={loading}
                                >
                                    {loading ? 'En cours...' : 'Enregistrer'}
                                </button>
                                <button 
                                    type="button" 
                                    className="btn btn-outline-secondary"
                                    onClick={() => navigate('/lst-seances')}
                                >
                                    Annuler
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default SeanceForm;
