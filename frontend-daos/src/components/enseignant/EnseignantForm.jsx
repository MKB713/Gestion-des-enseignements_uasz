import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import EnseignantService from '../../services/EnseignantService';

const EnseignantForm = () => {
    const navigate = useNavigate();
    const { id } = useParams();

    // --- ÉTATS ---
    const [enseignant, setEnseignant] = useState({
        nom: '',
        prenom: '',
        email: '',
        telephone: '',
        adresse: '',
        dateNaissance: '',
        lieuNaissance: '',
        dateEmbauche: '',
        grade: '',
        statut: 'PERMANENT',
        specialite: ''
    });

    const [grades, setGrades] = useState([]);
    const [statuts, setStatuts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});

    // --- CHARGEMENT INITIAL ---
    useEffect(() => {
        // Charger les listes de référence
        EnseignantService.getGrades()
            .then(res => setGrades(res.data))
            .catch(err => console.error("Erreur chargement grades:", err));

        EnseignantService.getStatuts()
            .then(res => setStatuts(res.data))
            .catch(err => console.error("Erreur chargement statuts:", err));

        // Si mode édition, charger l'enseignant
        if (id) {
            setLoading(true);
            EnseignantService.getEnseignantById(id)
                .then(res => {
                    setEnseignant(res.data);
                    setLoading(false);
                })
                .catch(err => {
                    console.error("Erreur chargement enseignant:", err);
                    setLoading(false);
                });
        }
    }, [id]);

    // --- GESTION DU FORMULAIRE ---
    const handleChange = (e) => {
        const { name, value } = e.target;
        setEnseignant({ ...enseignant, [name]: value });

        // Effacer l'erreur du champ modifié
        if (errors[name]) {
            setErrors({ ...errors, [name]: '' });
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!enseignant.nom?.trim()) newErrors.nom = "Le nom est obligatoire";
        if (!enseignant.prenom?.trim()) newErrors.prenom = "Le prénom est obligatoire";
        if (!enseignant.email?.trim()) newErrors.email = "L'email est obligatoire";
        if (!enseignant.grade) newErrors.grade = "Le grade est obligatoire";
        if (!enseignant.dateEmbauche) newErrors.dateEmbauche = "La date d'embauche est obligatoire";

        // Validation email
        if (enseignant.email && !/\S+@\S+\.\S+/.test(enseignant.email)) {
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

        const savePromise = id
            ? EnseignantService.updateEnseignant(id, enseignant)
            : EnseignantService.createEnseignant(enseignant);

        savePromise
            .then(() => {
                navigate('/lst-enseignants');
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
                        {id ? 'Modifier l\'Enseignant' : 'Nouvel Enseignant'}
                    </h2>
                    <p className="text-muted mb-0">Saisissez les informations de l'enseignant.</p>
                </div>
                <button
                    onClick={() => navigate('/lst-enseignants')}
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
                                <i className="bi bi-person-badge me-2"></i>Informations de l'Enseignant
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
                                            value={enseignant.nom}
                                            onChange={handleChange}
                                            placeholder="Ex: DIOP"
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
                                            value={enseignant.prenom}
                                            onChange={handleChange}
                                            placeholder="Ex: Amadou"
                                        />
                                        {errors.prenom && <div className="invalid-feedback">{errors.prenom}</div>}
                                    </div>
                                </div>

                                <div className="row mb-4">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Date de Naissance
                                        </label>
                                        <input
                                            type="date"
                                            className="form-control"
                                            name="dateNaissance"
                                            value={enseignant.dateNaissance || ''}
                                            onChange={handleChange}
                                        />
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Lieu de Naissance
                                        </label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            name="lieuNaissance"
                                            value={enseignant.lieuNaissance || ''}
                                            onChange={handleChange}
                                            placeholder="Ex: Ziguinchor"
                                        />
                                    </div>
                                </div>

                                {/* SECTION 2 : INFORMATIONS PROFESSIONNELLES */}
                                <h6 className="text-success border-bottom pb-2 mb-3 small fw-bold mt-4">
                                    <i className="bi bi-briefcase me-2"></i>INFORMATIONS PROFESSIONNELLES
                                </h6>
                                <div className="row mb-4">
                                    <div className="col-md-4 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Grade <span className="text-danger">*</span>
                                        </label>
                                        <select
                                            className={`form-select ${errors.grade ? 'is-invalid' : ''}`}
                                            name="grade"
                                            value={enseignant.grade}
                                            onChange={handleChange}
                                        >
                                            <option value="">-- Sélectionner un grade --</option>
                                            {grades.map((grade, index) => (
                                                <option key={index} value={grade}>{grade}</option>
                                            ))}
                                        </select>
                                        {errors.grade && <div className="invalid-feedback">{errors.grade}</div>}
                                    </div>
                                    <div className="col-md-4 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Statut <span className="text-danger">*</span>
                                        </label>
                                        <select
                                            className="form-select"
                                            name="statut"
                                            value={enseignant.statut}
                                            onChange={handleChange}
                                        >
                                            {statuts.map((statut) => (
                                                <option key={statut} value={statut}>{statut}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="col-md-4 mb-3">
                                        <label className="form-label fw-bold small text-uppercase text-muted">
                                            Date d'Embauche <span className="text-danger">*</span>
                                        </label>
                                        <input
                                            type="date"
                                            className={`form-control ${errors.dateEmbauche ? 'is-invalid' : ''}`}
                                            name="dateEmbauche"
                                            value={enseignant.dateEmbauche || ''}
                                            onChange={handleChange}
                                        />
                                        {errors.dateEmbauche && <div className="invalid-feedback">{errors.dateEmbauche}</div>}
                                    </div>
                                </div>

                                <div className="mb-4">
                                    <label className="form-label fw-bold small text-uppercase text-muted">
                                        Spécialité
                                    </label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="specialite"
                                        value={enseignant.specialite || ''}
                                        onChange={handleChange}
                                        placeholder="Ex: Intelligence Artificielle, Génie Logiciel..."
                                    />
                                </div>

                                {/* SECTION 3 : CONTACT */}
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
                                                value={enseignant.email}
                                                onChange={handleChange}
                                                placeholder="prenom.nom@univ-zig.sn"
                                            />
                                            {errors.email && <div className="invalid-feedback">{errors.email}</div>}
                                        </div>
                                        <div className="form-text text-muted">
                                            Si vide, un email sera généré automatiquement
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
                                                value={enseignant.telephone || ''}
                                                onChange={handleChange}
                                                placeholder="77 123 45 67"
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="mb-4">
                                    <label className="form-label fw-bold small text-uppercase text-muted">
                                        Adresse
                                    </label>
                                    <textarea
                                        className="form-control"
                                        name="adresse"
                                        value={enseignant.adresse || ''}
                                        onChange={handleChange}
                                        rows="2"
                                        placeholder="Adresse complète..."
                                    ></textarea>
                                </div>

                                {/* BOUTONS */}
                                <div className="d-flex justify-content-end gap-2 pt-3 border-top">
                                    <button
                                        type="button"
                                        onClick={() => navigate('/lst-enseignants')}
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

export default EnseignantForm;