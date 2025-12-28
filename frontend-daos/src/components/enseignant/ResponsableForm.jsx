import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import EnseignantService from '../services/EnseignantService';
import MaquetteService from '../services/MaquetteService';

const ResponsableForm = () => {
    const navigate = useNavigate();
    const { id } = useParams();

    // --- ÉTATS ---
    const [responsable, setResponsable] = useState({
        nom: '',
        prenom: '',
        email: '',
        telephone: '',
        type: 'LICENCE',
        formationId: '',
        enseignantId: '',
        dateDebutFonction: '',
        dateFinFonction: '',
        actif: true,
        remarques: ''
    });

    const [formations, setFormations] = useState([]);
    const [enseignants, setEnseignants] = useState([]);
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});

    // --- CHARGEMENT INITIAL ---
    useEffect(() => {
        // Charger les formations
        MaquetteService.getAllFormations()
            .then(res => setFormations(res.data))
            .catch(err => console.error("Erreur chargement formations:", err));

        // Charger les enseignants
        EnseignantService.getAllEnseignants()
            .then(res => setEnseignants(res.data))
            .catch(err => console.error("Erreur chargement enseignants:", err));

        // Si mode édition, charger le responsable
        if (id) {
            setLoading(true);
            EnseignantService.getResponsableById(id)
                .then(res => {
                    const data = res.data;
                    setResponsable({
                        ...data,
                        formationId: data.formationId || '',
                        enseignantId: data.enseignantId || '',
                        dateDebutFonction: data.dateDebutFonction || '',
                        dateFinFonction: data.dateFinFonction || ''
                    });
                    setLoading(false);
                })
                .catch(err => {
                    console.error("Erreur chargement responsable:", err);
                    setLoading(false);
                });
        }
    }, [id]);

    // --- GESTION DU FORMULAIRE ---
    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;

        if (type === 'checkbox') {
            setResponsable({ ...responsable, [name]: checked });
        } else {
            setResponsable({ ...responsable, [name]: value });
        }

        // Effacer l'erreur du champ modifié
        if (errors[name]) {
            setErrors({ ...errors, [name]: '' });
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!responsable.nom?.trim()) newErrors.nom = "Le nom est obligatoire";
        if (!responsable.prenom?.trim()) newErrors.prenom = "Le prénom est obligatoire";
        if (!responsable.email?.trim()) newErrors.email = "L'email est obligatoire";
        if (!responsable.type) newErrors.type = "Le type est obligatoire";

        // Validation email
        if (responsable.email && !/\S+@\S+\.\S+/.test(responsable.email)) {
            newErrors.email = "Format d'email invalide";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        setLoading(true);

        // Préparer le payload (convertir les chaînes vides en null pour les IDs)
        const payload = {
            ...responsable,
            formationId: responsable.formationId || null,
            enseignantId: responsable.enseignantId || null
        };

        const savePromise = id
            ? EnseignantService.updateResponsable(id, payload)
            : EnseignantService.createResponsable(payload);

        savePromise
            .then(() => {
                navigate('/lst-responsables');
            })
            .catch(err => {
                console.error("Erreur sauvegarde:", err);
                alert("Une erreur est survenue lors de l'enregistrement.");
                setLoading(false);
            });
    };

    if (loading && id) {
        return (
            <div className="text-center mt-5">
                <div className="spinner-border text-success"></div>
            </div>
        );
    }

    return (
        <div className="container mt-4">
            {/* EN-TÊTE */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark">
                        {id ? 'Modifier le Responsable' : 'Nouveau Responsable'}
                    </h2>
                    <p className="text-muted mb-0">Saisissez les informations du responsable de formation.</p>
                </div>
                <button
                    onClick={() => navigate('/lst-responsables')}
                    className="btn btn-outline-secondary"
                >
                    <i className="bi bi-arrow-left me-2"></i>Retour à la liste
                </button>
            </div>

            <div className="row justify-content-center">
                <div className="col-lg-10">
                    <div className="card shadow-sm border-0">
                        <div className="card-header bg-white py-3 border-bottom border-3 border-success">
                            <h5 className="mb-0 text-success fw-bold">
                                <i className="bi bi-person-badge me-2"></i>Informations du Responsable
                            </h5>
                        </div>
                        <div className="card-body p-4">
                            <form onSubmit={handleSubmit}>

                                {/* SECTION 1 : IDENTITÉ */}
                                <h6 className="text-success border-bottom pb-2 mb-3 small fw-bold">
                                    <i className="bi bi-person-vcard me-2"></i>IDENTITÉ
                                </h6>
                                <div className="row mb-4">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Nom <span className="text-danger">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            className={`form-control ${errors.nom ? 'is-invalid' : ''}`}
                                            name="nom"
                                            value={responsable.nom}
                                            onChange={handleChange}
                                            placeholder="Ex: FALL"
                                        />
                                        {errors.nom && <div className="invalid-feedback">{errors.nom}</div>}
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Prénom <span className="text-danger">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            className={`form-control ${errors.prenom ? 'is-invalid' : ''}`}
                                            name="prenom"
                                            value={responsable.prenom}
                                            onChange={handleChange}
                                            placeholder="Ex: Fatou"
                                        />
                                        {errors.prenom && <div className="invalid-feedback">{errors.prenom}</div>}
                                    </div>
                                </div>

                                {/* SECTION 2 : CONTACT */}
                                <h6 className="text-success border-bottom pb-2 mb-3 small fw-bold mt-4">
                                    <i className="bi bi-telephone me-2"></i>CONTACT
                                </h6>
                                <div className="row mb-4">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Email <span className="text-danger">*</span>
                                        </label>
                                        <div className="input-group">
                                            <span className="input-group-text bg-light">
                                                <i className="bi bi-envelope"></i>
                                            </span>
                                            <input
                                                type="email"
                                                className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                                                name="email"
                                                value={responsable.email}
                                                onChange={handleChange}
                                                placeholder="responsable@univ-zig.sn"
                                            />
                                            {errors.email && <div className="invalid-feedback">{errors.email}</div>}
                                        </div>
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Téléphone
                                        </label>
                                        <div className="input-group">
                                            <span className="input-group-text bg-light">
                                                <i className="bi bi-telephone"></i>
                                            </span>
                                            <input
                                                type="tel"
                                                className="form-control"
                                                name="telephone"
                                                value={responsable.telephone || ''}
                                                onChange={handleChange}
                                                placeholder="77 123 45 67"
                                            />
                                        </div>
                                    </div>
                                </div>

                                {/* SECTION 3 : FONCTION */}
                                <h6 className="text-success border-bottom pb-2 mb-3 small fw-bold mt-4">
                                    <i className="bi bi-briefcase me-2"></i>FONCTION
                                </h6>
                                <div className="row mb-4">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Type de Responsable <span className="text-danger">*</span>
                                        </label>
                                        <select
                                            className={`form-select ${errors.type ? 'is-invalid' : ''}`}
                                            name="type"
                                            value={responsable.type}
                                            onChange={handleChange}
                                        >
                                            <option value="LICENCE">Responsable Licence</option>
                                            <option value="MASTER">Responsable Master</option>
                                        </select>
                                        {errors.type && <div className="invalid-feedback">{errors.type}</div>}
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Formation
                                        </label>
                                        <select
                                            className="form-select"
                                            name="formationId"
                                            value={responsable.formationId}
                                            onChange={handleChange}
                                        >
                                            <option value="">-- Sélectionner une formation --</option>
                                            {formations.map(formation => (
                                                <option key={formation.id} value={formation.id}>
                                                    {formation.libelle}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                </div>

                                <div className="mb-4">
                                    <label className="form-label fw-bold small text-uppercase text-muted">
                                        Enseignant Associé (Optionnel)
                                    </label>
                                    <select
                                        className="form-select"
                                        name="enseignantId"
                                        value={responsable.enseignantId}
                                        onChange={handleChange}
                                    >
                                        <option value="">-- Sélectionner un enseignant --</option>
                                        {enseignants.map(ens => (
                                            <option key={ens.id} value={ens.id}>
                                                {ens.prenom} {ens.nom} - {ens.grade}
                                            </option>
                                        ))}
                                    </select>
                                    <div className="form-text text-muted">
                                        Associer à un enseignant existant dans le système
                                    </div>
                                </div>

                                <div className="row mb-4">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Date Début Fonction
                                        </label>
                                        <input
                                            type="date"
                                            className="form-control"
                                            name="dateDebutFonction"
                                            value={responsable.dateDebutFonction || ''}
                                            onChange={handleChange}
                                        />
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Date Fin Fonction
                                        </label>
                                        <input
                                            type="date"
                                            className="form-control"
                                            name="dateFinFonction"
                                            value={responsable.dateFinFonction || ''}
                                            onChange={handleChange}
                                        />
                                    </div>
                                </div>

                                <div className="mb-4">
                                    <div className="form-check">
                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            name="actif"
                                            id="actif"
                                            checked={responsable.actif}
                                            onChange={handleChange}
                                        />
                                        <label className="form-check-label fw-bold" htmlFor="actif">
                                            Responsable Actif
                                        </label>
                                    </div>
                                </div>

                                <div className="mb-4">
                                    <label className="form-label fw-bold small text-uppercase text-muted">
                                        Remarques
                                    </label>
                                    <textarea
                                        className="form-control"
                                        name="remarques"
                                        value={responsable.remarques || ''}
                                        onChange={handleChange}
                                        rows="3"
                                        placeholder="Notes additionnelles..."
                                    ></textarea>
                                </div>

                                {/* BOUTONS */}
                                <div className="d-flex justify-content-end gap-2 pt-3 border-top">
                                    <button
                                        type="button"
                                        onClick={() => navigate('/lst-responsables')}
                                        className="btn btn-light border px-4"
                                    >
                                        Annuler
                                    </button>
                                    <button
                                        type="submit"
                                        className="btn btn-success px-4 text-white fw-bold"
                                        disabled={loading}
                                    >
                                        {loading ? (
                                            <>
                                                <span className="spinner-border spinner-border-sm me-2"></span>
                                                Enregistrement...
                                            </>
                                        ) : (
                                            <>
                                                <i className="bi bi-check-lg me-2"></i>Enregistrer
                                            </>
                                        )}
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

export default ResponsableForm;