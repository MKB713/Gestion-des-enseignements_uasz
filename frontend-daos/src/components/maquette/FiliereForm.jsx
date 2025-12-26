import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import MaquetteService from '../../services/MaquetteService';

const FiliereForm = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [filiere, setFiliere] = useState({ libelle: '', description: '' });

    useEffect(() => {
        if (id) {
            MaquetteService.getFiliereById(id)
                .then(res => setFiliere(res.data))
                .catch(err => console.error(err));
        }
    }, [id]);

    const handleSubmit = (e) => {
        e.preventDefault(); // Empêche le rechargement de la page
        console.log("Données envoyées :", filiere); // Vérifiez la console (F12)

        const action = id ? MaquetteService.updateFiliere(id, filiere) : MaquetteService.createFiliere(filiere);

        action
            .then(() => {
                alert("Filière enregistrée avec succès !");
                navigate('/lst-filieres');
            })
            .catch(error => {
                console.error("Erreur API:", error);
                alert("Erreur lors de l'enregistrement. Vérifiez que le serveur est démarré.");
            });
    };

    return (
        <div className="container mt-4">
            <h2 className="fw-bold mb-4">{id ? 'Modifier la Filière' : 'Nouvelle Filière'}</h2>
            <div className="card shadow-sm border-0">
                <div className="card-header bg-white py-3 border-bottom border-3 border-success">
                    <h5 className="mb-0 text-success fw-bold"><i className="bi bi-building me-2"></i>Informations</h5>
                </div>
                <div className="card-body p-4">
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label className="form-label fw-bold text-muted small">Intitulé de la filière</label>
                            <input
                                type="text"
                                className="form-control"
                                required
                                value={filiere.libelle}
                                onChange={(e) => setFiliere({...filiere, libelle: e.target.value})}
                                placeholder="Ex: Informatique"
                            />
                        </div>
                        <div className="mb-4">
                            <label className="form-label fw-bold text-muted small">Description</label>
                            <textarea
                                className="form-control"
                                rows="3"
                                value={filiere.description}
                                onChange={(e) => setFiliere({...filiere, description: e.target.value})}
                                placeholder="Description courte..."
                            ></textarea>
                        </div>
                        <div className="d-flex justify-content-end gap-2">
                            <button type="button" onClick={() => navigate('/lst-filieres')} className="btn btn-light border">Annuler</button>
                            <button type="submit" className="btn btn-success fw-bold text-white">Enregistrer</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};
export default FiliereForm;