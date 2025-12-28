import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import MaquetteService from '../../services/MaquetteService';

const NiveauForm = () => {
    const navigate = useNavigate();
    const { id } = useParams();

    const [niveau, setNiveau] = useState({
        cycle: 'LICENCE',
        numero: 1,
        description: ''
    });

    useEffect(() => {
        if (id) {
            MaquetteService.getNiveauById(id)
                .then(res => setNiveau(res.data))
                .catch(err => console.error(err));
        }
    }, [id]);

    const handleSubmit = (e) => {
        e.preventDefault();

        // CORRECTION CRITIQUE : S'assurer que le numéro est bien un entier
        const payload = {
            ...niveau,
            numero: parseInt(niveau.numero)
        };

        console.log("Envoi payload:", payload);

        const action = id ? MaquetteService.updateNiveau(id, payload) : MaquetteService.createNiveau(payload);

        action
            .then(() => {
                navigate('/niveaux');
            })
            .catch(error => {
                console.error("Erreur API:", error);
                alert("Erreur: Impossible de créer le niveau. Vérifiez la console pour les détails.");
            });
    };

    return (
        <div className="container mt-4">
            <h2 className="fw-bold mb-4">{id ? 'Modifier le Niveau' : 'Nouveau Niveau'}</h2>
            <div className="card shadow-sm border-0">
                <div className="card-header bg-white py-3 border-bottom border-3 border-success">
                    <h5 className="mb-0 text-success fw-bold"><i className="bi bi-layers me-2"></i>Détails</h5>
                </div>
                <div className="card-body p-4">
                    <form onSubmit={handleSubmit}>
                        <div className="row mb-3">
                            <div className="col-md-6">
                                <label className="form-label fw-bold text-muted small">Cycle</label>
                                <select
                                    className="form-select"
                                    value={niveau.cycle}
                                    onChange={(e) => setNiveau({...niveau, cycle: e.target.value})}
                                >
                                    <option value="LICENCE">LICENCE</option>
                                    <option value="MASTER">MASTER</option>
                                    <option value="DOCTORAT">DOCTORAT</option>
                                </select>
                            </div>
                            <div className="col-md-6">
                                <label className="form-label fw-bold text-muted small">Numéro (Année)</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    min="1"
                                    max="5"
                                    required
                                    value={niveau.numero}
                                    // On garde la valeur en string pour l'affichage, mais on la convertira au submit
                                    onChange={(e) => setNiveau({...niveau, numero: e.target.value})}
                                />
                            </div>
                        </div>

                        <div className="mb-4">
                            <label className="form-label fw-bold text-muted small">Description (Optionnel)</label>
                            <textarea
                                className="form-control"
                                rows="2"
                                value={niveau.description}
                                onChange={(e) => setNiveau({...niveau, description: e.target.value})}
                            ></textarea>
                        </div>

                        <div className="d-flex justify-content-end gap-2">
                            <button type="button" onClick={() => navigate('/niveaux')} className="btn btn-light border">Annuler</button>
                            <button type="submit" className="btn btn-success fw-bold text-white">Enregistrer</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};
export default NiveauForm;