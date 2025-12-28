import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import choixService from '../../services/choixService';
import { toast } from 'react-toastify';

const ChoixForm = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const isEditMode = Boolean(id);

    const [formData, setFormData] = useState({
        idEnseignant: '',
        idEnseignement: ''
    });

    const [loading, setLoading] = useState(false);
    const [loadingData, setLoadingData] = useState(false);
    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (isEditMode) {
            loadChoixData();
        }
    }, [id]);

    const loadChoixData = async () => {
        try {
            setLoadingData(true);
            const data = await choixService.getChoixById(id);
            setFormData({
                idEnseignant: data.idEnseignant,
                idEnseignement: data.idEnseignement
            });
        } catch (error) {
            toast.error('Erreur lors du chargement du choix');
            navigate('/choix-enseignement');
        } finally {
            setLoadingData(false);
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!formData.idEnseignant || formData.idEnseignant <= 0) {
            newErrors.idEnseignant = "L'ID de l'enseignant est obligatoire";
        }

        if (!formData.idEnseignement || formData.idEnseignement <= 0) {
            newErrors.idEnseignement = "L'ID de l'enseignement est obligatoire";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
        // Effacer l'erreur lors de la saisie
        if (errors[name]) {
            setErrors({
                ...errors,
                [name]: null
            });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            toast.error('Veuillez corriger les erreurs du formulaire');
            return;
        }

        try {
            setLoading(true);

            if (isEditMode) {
                // Modification
                const updateData = { idEnseignement: parseInt(formData.idEnseignement) };
                await choixService.updateChoix(id, updateData);
                toast.success('Choix modifié avec succès');
            } else {
                // Création
                const createData = {
                    idEnseignant: parseInt(formData.idEnseignant),
                    idEnseignement: parseInt(formData.idEnseignement)
                };
                await choixService.createChoix(createData);
                toast.success('Choix créé avec succès');
            }

            navigate('/choix-enseignement');
        } catch (error) {
            console.error('Error:', error);
            if (error.message && error.message.includes('déjà choisi')) {
                toast.error('Cet enseignant a déjà choisi cet enseignement');
            } else {
                toast.error(isEditMode ? 'Erreur lors de la modification' : 'Erreur lors de la création');
            }
        } finally {
            setLoading(false);
        }
    };

    if (loadingData) {
        return (
            <div className="container-fluid py-4">
                <div className="text-center py-5">
                    <div className="spinner-border text-success" role="status">
                        <span className="visually-hidden">Chargement...</span>
                    </div>
                    <p className="mt-3 text-muted">Chargement des données...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="container-fluid py-4">
            {/* En-tête */}
            <div className="row mb-4">
                <div className="col-12">
                    <nav aria-label="breadcrumb">
                        <ol className="breadcrumb">
                            <li className="breadcrumb-item">
                                <Link to="/choix-enseignement" className="text-decoration-none">
                                    <i className="bi bi-house me-1"></i>
                                    Choix d'Enseignement
                                </Link>
                            </li>
                            <li className="breadcrumb-item active">
                                {isEditMode ? 'Modifier' : 'Nouveau'}
                            </li>
                        </ol>
                    </nav>
                    <h2 className="fw-bold mb-1">
                        <i className={`bi ${isEditMode ? 'bi-pencil-square' : 'bi-plus-circle'} me-2 text-success`}></i>
                        {isEditMode ? 'Modifier un choix' : 'Nouveau choix d\'enseignement'}
                    </h2>
                    <p className="text-muted mb-0">
                        {isEditMode ? 'Modifiez les informations du choix' : 'Enregistrez un nouveau choix d\'enseignement'}
                    </p>
                </div>
            </div>

            {/* Formulaire */}
            <div className="row justify-content-center">
                <div className="col-lg-8">
                    <div className="card shadow-sm">
                        <div className="card-header bg-success text-white">
                            <h5 className="mb-0">
                                <i className="bi bi-clipboard-data me-2"></i>
                                Informations du choix
                            </h5>
                        </div>
                        <div className="card-body p-4">
                            <form onSubmit={handleSubmit}>
                                {/* ID Enseignant */}
                                <div className="mb-4">
                                    <label htmlFor="idEnseignant" className="form-label fw-bold">
                                        <i className="bi bi-person-badge me-2"></i>
                                        ID Enseignant
                                        <span className="text-danger ms-1">*</span>
                                    </label>
                                    <input
                                        type="number"
                                        className={`form-control form-control-lg ${errors.idEnseignant ? 'is-invalid' : ''}`}
                                        id="idEnseignant"
                                        name="idEnseignant"
                                        placeholder="Ex: 1"
                                        value={formData.idEnseignant}
                                        onChange={handleChange}
                                        disabled={isEditMode} // En mode édition, on ne peut pas changer l'enseignant
                                        min="1"
                                    />
                                    {errors.idEnseignant && (
                                        <div className="invalid-feedback">
                                            <i className="bi bi-exclamation-circle me-1"></i>
                                            {errors.idEnseignant}
                                        </div>
                                    )}
                                    <small className="form-text text-muted">
                                        Identifiant unique de l'enseignant
                                        {isEditMode && ' (non modifiable)'}
                                    </small>
                                </div>

                                {/* ID Enseignement */}
                                <div className="mb-4">
                                    <label htmlFor="idEnseignement" className="form-label fw-bold">
                                        <i className="bi bi-book me-2"></i>
                                        ID Enseignement
                                        <span className="text-danger ms-1">*</span>
                                    </label>
                                    <input
                                        type="number"
                                        className={`form-control form-control-lg ${errors.idEnseignement ? 'is-invalid' : ''}`}
                                        id="idEnseignement"
                                        name="idEnseignement"
                                        placeholder="Ex: 101"
                                        value={formData.idEnseignement}
                                        onChange={handleChange}
                                        min="1"
                                    />
                                    {errors.idEnseignement && (
                                        <div className="invalid-feedback">
                                            <i className="bi bi-exclamation-circle me-1"></i>
                                            {errors.idEnseignement}
                                        </div>
                                    )}
                                    <small className="form-text text-muted">
                                        Identifiant du cours/enseignement choisi
                                    </small>
                                </div>

                                {/* Informations supplémentaires en mode édition */}
                                {isEditMode && (
                                    <div className="alert alert-info">
                                        <i className="bi bi-info-circle me-2"></i>
                                        <strong>Note :</strong> Seul l'enseignement peut être modifié.
                                        L'identifiant de l'enseignant est fixe.
                                    </div>
                                )}

                                {/* Boutons d'action */}
                                <div className="d-flex gap-2 mt-4 pt-3 border-top">
                                    <Link
                                        to="/choix-enseignement"
                                        className="btn btn-lg btn-outline-secondary"
                                    >
                                        <i className="bi bi-x-circle me-2"></i>
                                        Annuler
                                    </Link>
                                    <button
                                        type="submit"
                                        className="btn btn-lg btn-success flex-grow-1"
                                        disabled={loading}
                                    >
                                        {loading ? (
                                            <>
                                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                                {isEditMode ? 'Modification...' : 'Enregistrement...'}
                                            </>
                                        ) : (
                                            <>
                                                <i className={`bi ${isEditMode ? 'bi-check-circle' : 'bi-save'} me-2`}></i>
                                                {isEditMode ? 'Modifier' : 'Enregistrer'}
                                            </>
                                        )}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>

                    {/* Aide */}
                    <div className="card shadow-sm mt-3">
                        <div className="card-body">
                            <h6 className="fw-bold mb-3">
                                <i className="bi bi-question-circle text-primary me-2"></i>
                                Aide
                            </h6>
                            <ul className="mb-0 small text-muted">
                                <li>Les champs marqués d'un astérisque (*) sont obligatoires</li>
                                <li>L'ID enseignant doit correspondre à un enseignant existant</li>
                                <li>L'ID enseignement doit correspondre à un cours existant</li>
                                <li>Un enseignant ne peut pas choisir le même enseignement deux fois</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ChoixForm;