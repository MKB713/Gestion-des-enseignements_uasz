import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import MaquetteService from '../../services/MaquetteService';


const FormationForm = () => {
    const navigate = useNavigate();
    const { id } = useParams(); // Get ID from URL if editing

    // Form Data State
    const [formation, setFormation] = useState({
        code: '',
        libelle: '',
        description: '',
        filiere: null, // Object expected by backend
        niveau: null   // Object expected by backend
    });

    // Dropdown Data States
    const [filieres, setFilieres] = useState([]);
    const [niveaux, setNiveaux] = useState([]);
    const [loading, setLoading] = useState(true);

    // Load data on mount
    useEffect(() => {
        const fetchData = async () => {
            try {
                const [filResponse, nivResponse] = await Promise.all([
                    MaquetteService.getAllFilieres(),
                    MaquetteService.getAllNiveaux()
                ]);
                setFilieres(filResponse.data);
                setNiveaux(nivResponse.data);

                // If editing, load existing formation
                if (id) {
                    // We need a method getFormationById in MaquetteService
                    // For now, let's assume we can fetch it or filter from list
                    // Ideally: const f = await MaquetteService.getFormationById(id);
                    // setFormation(f.data);

                    // Temporary workaround if getFormationById isn't ready:
                    const all = await MaquetteService.getAllFormations();
                    const found = all.data.find(f => f.id === parseInt(id));
                    if (found) setFormation(found);
                }
            } catch (error) {
                console.error("Error loading data:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormation({ ...formation, [name]: value });
    };

    const handleSelectChange = (e, type) => {
        const value = e.target.value;
        const list = type === 'filiere' ? filieres : niveaux;
        const selectedObject = list.find(item => item.id === parseInt(value));

        setFormation({ ...formation, [type]: selectedObject });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Cet appel utilisera maintenant l'URL http://localhost:8080/api/maquette/...
            await MaquetteService.createFormation(formData);
            // ... reste du code (redirection ou message de succès)
        } catch (error) {
            console.error("Error saving formation:", error);
        }
    };

    if (loading) return <div className="text-center mt-5"><div className="spinner-border text-success"></div></div>;

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark">{id ? 'Modifier la Formation' : 'Nouvelle Formation'}</h2>
                    <p className="text-muted mb-0">Saisissez les informations de l'offre de formation.</p>
                </div>
                <button onClick={() => navigate('/lst-formations')} className="btn btn-outline-secondary">
                    <i className="bi bi-arrow-left me-2"></i>Retour à la liste
                </button>
            </div>

            <div className="row justify-content-center">
                <div className="col-lg-10">
                    <div className="card shadow-sm border-0">
                        <div className="card-header bg-white py-3 border-bottom border-3 border-success">
                            <h5 className="mb-0 text-success fw-bold">
                                <i className="bi bi-mortarboard me-2"></i>Détails de la Formation
                            </h5>
                        </div>
                        <div className="card-body p-4">

                            {/* Info Box */}
                            <div className="alert alert-info border-0 shadow-sm mb-4" style={{backgroundColor: 'rgba(13, 202, 240, 0.1)'}}>
                                <div className="d-flex align-items-start">
                                    <i className="bi bi-info-circle-fill text-info me-2 fs-5"></i>
                                    <div>
                                        <strong className="d-block mb-2">Prérequis pour créer une formation:</strong>
                                        <ul className="mb-0 small">
                                            <li>Au moins une <strong>Filière</strong> doit exister</li>
                                            <li>Au moins un <strong>Niveau</strong> doit exister</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>

                            <form onSubmit={handleSubmit}>
                                <div className="row mb-4">
                                    <div className="col-md-4 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">Code</label>
                                        <div className="input-group">
                                            <span className="input-group-text bg-light"><i className="bi bi-upc-scan"></i></span>
                                            <input
                                                type="text"
                                                className="form-control"
                                                name="code"
                                                value={formation.code}
                                                onChange={handleChange}
                                                placeholder="Ex: L1-INFO"
                                                required
                                            />
                                        </div>
                                        <div className="form-text text-muted">Doit être unique (Max 10 chars).</div>
                                    </div>
                                    <div className="col-md-8 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">Libellé complet</label>
                                        <div className="input-group">
                                            <span className="input-group-text bg-light"><i className="bi bi-tag"></i></span>
                                            <input
                                                type="text"
                                                className="form-control"
                                                name="libelle"
                                                value={formation.libelle}
                                                onChange={handleChange}
                                                placeholder="Ex: Licence 1 en Informatique"
                                                required
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="row mb-4">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">Filière de rattachement</label>
                                        <select
                                            className="form-select"
                                            onChange={(e) => handleSelectChange(e, 'filiere')}
                                            value={formation.filiere?.id || ''}
                                            required
                                        >
                                            <option value="">-- Sélectionner une filière --</option>
                                            {filieres.map(fil => (
                                                <option key={fil.id} value={fil.id}>{fil.libelle}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">Niveau Académique</label>
                                        <select
                                            className="form-select"
                                            onChange={(e) => handleSelectChange(e, 'niveau')}
                                            value={formation.niveau?.id || ''}
                                            required
                                        >
                                            <option value="">-- Sélectionner un niveau --</option>
                                            {niveaux.map(niv => (
                                                <option key={niv.id} value={niv.id}>{niv.cycle} {niv.numero}</option>
                                            ))}
                                        </select>
                                    </div>
                                </div>

                                <div className="mb-4">
                                    <label className="form-label fw-bold small text-uppercase text-muted">Description</label>
                                    <textarea
                                        className="form-control"
                                        name="description"
                                        value={formation.description}
                                        onChange={handleChange}
                                        rows="4"
                                        placeholder="Objectifs et détails de la formation..."
                                    ></textarea>
                                </div>

                                <div className="d-flex justify-content-end gap-2 pt-3 border-top">
                                    <button type="button" onClick={() => navigate('/lst-formations')} className="btn btn-light border px-4">Annuler</button>
                                    <button type="submit" className="btn btn-success px-4 text-white fw-bold">
                                        <i className="bi bi-check-lg me-2"></i>Enregistrer
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default FormationForm;