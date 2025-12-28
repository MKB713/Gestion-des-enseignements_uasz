import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import choixService from '../../services/choixService';
import { toast } from 'react-toastify';

const ChoixDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [choix, setChoix] = useState(null);
    const [loading, setLoading] = useState(true);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    useEffect(() => {
        loadChoixDetail();
    }, [id]);

    const loadChoixDetail = async () => {
        try {
            setLoading(true);
            const data = await choixService.getChoixById(id);
            setChoix(data);
        } catch (error) {
            toast.error('Erreur lors du chargement du choix');
            navigate('/choix-enseignement');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        try {
            await choixService.deleteChoix(id);
            toast.success('Choix supprimé avec succès');
            navigate('/choix-enseignement');
        } catch (error) {
            toast.error('Erreur lors de la suppression');
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', {
            day: '2-digit',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    if (loading) {
        return (
            <div className="container-fluid py-4">
                <div className="text-center py-5">
                    <div className="spinner-border text-success" role="status">
                        <span className="visually-hidden">Chargement...</span>
                    </div>
                    <p className="mt-3 text-muted">Chargement des détails...</p>
                </div>
            </div>
        );
    }

    if (!choix) {
        return null;
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
                            <li className="breadcrumb-item active">Détails</li>
                        </ol>
                    </nav>
                    <div className="d-flex justify-content-between align-items-center">
                        <div>
                            <h2 className="fw-bold mb-1">
                                <i className="bi bi-eye me-2 text-success"></i>
                                Détails du choix #{choix.id}
                            </h2>
                            <p className="text-muted mb-0">
                                Informations complètes sur ce choix d'enseignement
                            </p>
                        </div>
                        <div className="btn-group">
                            <Link
                                to={`/choix-enseignement/modifier/${choix.id}`}
                                className="btn btn-warning"
                            >
                                <i className="bi bi-pencil me-2"></i>
                                Modifier
                            </Link>
                            <button
                                onClick={() => setShowDeleteModal(true)}
                                className="btn btn-danger"
                            >
                                <i className="bi bi-trash me-2"></i>
                                Supprimer
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div className="row">
                {/* Carte principale - Informations */}
                <div className="col-lg-8">
                    <div className="card shadow-sm mb-4">
                        <div className="card-header bg-success text-white">
                            <h5 className="mb-0">
                                <i className="bi bi-info-circle me-2"></i>
                                Informations du choix
                            </h5>
                        </div>
                        <div className="card-body">
                            <div className="row g-4">
                                <div className="col-md-6">
                                    <div className="d-flex align-items-start">
                                        <div className="bg-primary bg-opacity-10 rounded p-3 me-3">
                                            <i className="bi bi-hash text-primary fs-4"></i>
                                        </div>
                                        <div>
                                            <small className="text-muted d-block mb-1">ID du Choix</small>
                                            <h5 className="mb-0 fw-bold">#{choix.id}</h5>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-6">
                                    <div className="d-flex align-items-start">
                                        <div className="bg-info bg-opacity-10 rounded p-3 me-3">
                                            <i className="bi bi-person-badge text-info fs-4"></i>
                                        </div>
                                        <div>
                                            <small className="text-muted d-block mb-1">ID Enseignant</small>
                                            <h5 className="mb-0 fw-bold">
                                                <span className="badge bg-primary">{choix.idEnseignant}</span>
                                            </h5>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-6">
                                    <div className="d-flex align-items-start">
                                        <div className="bg-warning bg-opacity-10 rounded p-3 me-3">
                                            <i className="bi bi-book text-warning fs-4"></i>
                                        </div>
                                        <div>
                                            <small className="text-muted d-block mb-1">ID Enseignement</small>
                                            <h5 className="mb-0 fw-bold">
                                                <span className="badge bg-info">{choix.idEnseignement}</span>
                                            </h5>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-6">
                                    <div className="d-flex align-items-start">
                                        <div className="bg-success bg-opacity-10 rounded p-3 me-3">
                                            <i className="bi bi-calendar-check text-success fs-4"></i>
                                        </div>
                                        <div>
                                            <small className="text-muted d-block mb-1">Date du Choix</small>
                                            <h6 className="mb-0">{formatDate(choix.dateChoix)}</h6>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Informations enrichies (si disponibles) */}
                    {(choix.nomEnseignant || choix.libelleEnseignement) && (
                        <div className="card shadow-sm">
                            <div className="card-header bg-light">
                                <h5 className="mb-0">
                                    <i className="bi bi-star text-warning me-2"></i>
                                    Informations enrichies
                                </h5>
                            </div>
                            <div className="card-body">
                                {choix.nomEnseignant && (
                                    <div className="mb-3">
                                        <small className="text-muted d-block mb-1">Enseignant</small>
                                        <p className="mb-0 fw-bold">
                                            {choix.prenomEnseignant} {choix.nomEnseignant}
                                        </p>
                                    </div>
                                )}
                                {choix.libelleEnseignement && (
                                    <div>
                                        <small className="text-muted d-block mb-1">Enseignement</small>
                                        <p className="mb-0 fw-bold">{choix.libelleEnseignement}</p>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}
                </div>

                {/* Carte latérale - Métadonnées */}
                <div className="col-lg-4">
                    <div className="card shadow-sm mb-4">
                        <div className="card-header bg-light">
                            <h6 className="mb-0">
                                <i className="bi bi-clock-history me-2"></i>
                                Métadonnées
                            </h6>
                        </div>
                        <div className="card-body">
                            <div className="mb-3 pb-3 border-bottom">
                                <small className="text-muted d-block mb-2">
                                    <i className="bi bi-calendar-plus me-2"></i>
                                    Créé le
                                </small>
                                <p className="mb-0 fw-semibold">{formatDate(choix.dateCreation)}</p>
                            </div>

                            <div>
                                <small className="text-muted d-block mb-2">
                                    <i className="bi bi-calendar-event me-2"></i>
                                    Dernière modification
                                </small>
                                <p className="mb-0 fw-semibold">
                                    {choix.dateModification ? (
                                        formatDate(choix.dateModification)
                                    ) : (
                                        <span className="text-muted">Aucune modification</span>
                                    )}
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Actions rapides */}
                    <div className="card shadow-sm">
                        <div className="card-header bg-light">
                            <h6 className="mb-0">
                                <i className="bi bi-lightning me-2"></i>
                                Actions rapides
                            </h6>
                        </div>
                        <div className="card-body">
                            <div className="d-grid gap-2">
                                <Link
                                    to={`/choix-enseignement/modifier/${choix.id}`}
                                    className="btn btn-outline-warning"
                                >
                                    <i className="bi bi-pencil me-2"></i>
                                    Modifier ce choix
                                </Link>
                                <Link
                                    to={`/choix-enseignement/enseignant/${choix.idEnseignant}`}
                                    className="btn btn-outline-primary"
                                >
                                    <i className="bi bi-list-ul me-2"></i>
                                    Voir tous les choix de cet enseignant
                                </Link>
                                <button
                                    onClick={() => setShowDeleteModal(true)}
                                    className="btn btn-outline-danger"
                                >
                                    <i className="bi bi-trash me-2"></i>
                                    Supprimer ce choix
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Modal de confirmation de suppression */}
            {showDeleteModal && (
                <div className="modal show d-block" tabIndex="-1" style={{backgroundColor: 'rgba(0,0,0,0.5)'}}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header bg-danger text-white">
                                <h5 className="modal-title">
                                    <i className="bi bi-exclamation-triangle me-2"></i>
                                    Confirmer la suppression
                                </h5>
                                <button
                                    type="button"
                                    className="btn-close btn-close-white"
                                    onClick={() => setShowDeleteModal(false)}
                                ></button>
                            </div>
                            <div className="modal-body">
                                <p>Êtes-vous sûr de vouloir supprimer ce choix ?</p>
                                <div className="alert alert-warning">
                                    <strong>Choix #{choix.id}</strong>
                                    <br />
                                    Enseignant: {choix.idEnseignant}
                                    <br />
                                    Enseignement: {choix.idEnseignement}
                                </div>
                                <p className="text-danger mb-0">
                                    <small>Cette action est irréversible.</small>
                                </p>
                            </div>
                            <div className="modal-footer">
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() => setShowDeleteModal(false)}
                                >
                                    Annuler
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-danger"
                                    onClick={handleDelete}
                                >
                                    <i className="bi bi-trash me-2"></i>
                                    Supprimer
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ChoixDetail;