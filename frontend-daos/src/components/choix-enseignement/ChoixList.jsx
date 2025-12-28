import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import choixService from '../../services/choixService';
import { toast } from 'react-toastify';

const ChoixList = () => {
    const [choixList, setChoixList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [pagination, setPagination] = useState({
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: false
    });

    // Filtres
    const [filters, setFilters] = useState({
        idEnseignant: '',
        idEnseignement: '',
        page: 0,
        size: 10,
        sortBy: 'dateCreation',
        sortDirection: 'DESC'
    });

    // Modal de confirmation de suppression
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [choixToDelete, setChoixToDelete] = useState(null);

    useEffect(() => {
        loadChoix();
    }, [filters.page, filters.size, filters.sortBy, filters.sortDirection]);

    const loadChoix = async () => {
        try {
            setLoading(true);
            const response = await choixService.getAllChoix(
                filters.page,
                filters.size,
                filters.sortBy,
                filters.sortDirection
            );
            setChoixList(response.content);
            setPagination({
                pageNumber: response.pageNumber,
                pageSize: response.pageSize,
                totalElements: response.totalElements,
                totalPages: response.totalPages,
                first: response.first,
                last: response.last
            });
        } catch (error) {
            toast.error('Erreur lors du chargement des choix');
            console.error('Error loading choix:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async (e) => {
        e.preventDefault();
        setFilters({ ...filters, page: 0 });
        loadChoix();
    };

    const handleDelete = async () => {
        if (!choixToDelete) return;

        try {
            await choixService.deleteChoix(choixToDelete.id);
            toast.success('Choix supprimé avec succès');
            setShowDeleteModal(false);
            setChoixToDelete(null);
            loadChoix();
        } catch (error) {
            toast.error('Erreur lors de la suppression');
            console.error('Error deleting choix:', error);
        }
    };

    const confirmDelete = (choix) => {
        setChoixToDelete(choix);
        setShowDeleteModal(true);
    };

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const handlePageChange = (newPage) => {
        setFilters({ ...filters, page: newPage });
    };

    return (
        <div className="container-fluid py-4">
            {/* En-tête */}
            <div className="row mb-4">
                <div className="col-12">
                    <div className="d-flex justify-content-between align-items-center">
                        <div>
                            <h2 className="fw-bold mb-1">
                                <i className="bi bi-check2-square me-2 text-success"></i>
                                Gestion des Choix d'Enseignement
                            </h2>
                            <p className="text-muted mb-0">
                                Liste des choix effectués par les enseignants
                            </p>
                        </div>
                        <Link to="/choix-enseignement/ajouter" className="btn btn-success">
                            <i className="bi bi-plus-circle me-2"></i>
                            Nouveau Choix
                        </Link>
                    </div>
                </div>
            </div>

            {/* Filtres de recherche */}
            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <form onSubmit={handleSearch}>
                        <div className="row g-3">
                            <div className="col-md-4">
                                <label className="form-label">ID Enseignant</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    placeholder="Rechercher par ID enseignant"
                                    value={filters.idEnseignant}
                                    onChange={(e) => setFilters({ ...filters, idEnseignant: e.target.value })}
                                />
                            </div>
                            <div className="col-md-4">
                                <label className="form-label">ID Enseignement</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    placeholder="Rechercher par ID enseignement"
                                    value={filters.idEnseignement}
                                    onChange={(e) => setFilters({ ...filters, idEnseignement: e.target.value })}
                                />
                            </div>
                            <div className="col-md-4 d-flex align-items-end">
                                <button type="submit" className="btn btn-primary me-2">
                                    <i className="bi bi-search me-2"></i>
                                    Rechercher
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={() => {
                                        setFilters({
                                            ...filters,
                                            idEnseignant: '',
                                            idEnseignement: '',
                                            page: 0
                                        });
                                        loadChoix();
                                    }}
                                >
                                    <i className="bi bi-arrow-clockwise"></i>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            {/* Tableau des choix */}
            <div className="card shadow-sm">
                <div className="card-body">
                    {loading ? (
                        <div className="text-center py-5">
                            <div className="spinner-border text-success" role="status">
                                <span className="visually-hidden">Chargement...</span>
                            </div>
                            <p className="mt-3 text-muted">Chargement des données...</p>
                        </div>
                    ) : choixList.length === 0 ? (
                        <div className="text-center py-5">
                            <i className="bi bi-inbox display-1 text-muted"></i>
                            <p className="mt-3 text-muted">Aucun choix trouvé</p>
                            <Link to="/choix-enseignement/ajouter" className="btn btn-success btn-sm">
                                <i className="bi bi-plus-circle me-2"></i>
                                Créer le premier choix
                            </Link>
                        </div>
                    ) : (
                        <>
                            <div className="table-responsive">
                                <table className="table table-hover align-middle">
                                    <thead className="table-light">
                                    <tr>
                                        <th style={{width: '80px'}}>ID</th>
                                        <th>ID Enseignant</th>
                                        <th>ID Enseignement</th>
                                        <th>Date du Choix</th>
                                        <th>Date de Création</th>
                                        <th>Dernière Modif.</th>
                                        <th style={{width: '150px'}} className="text-center">Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {choixList.map((choix) => (
                                        <tr key={choix.id}>
                                            <td className="fw-bold text-success">#{choix.id}</td>
                                            <td>
                                                    <span className="badge bg-primary">
                                                        Ens. {choix.idEnseignant}
                                                    </span>
                                            </td>
                                            <td>
                                                    <span className="badge bg-info">
                                                        Cours {choix.idEnseignement}
                                                    </span>
                                            </td>
                                            <td>
                                                <small className="text-muted">
                                                    <i className="bi bi-calendar-check me-1"></i>
                                                    {formatDate(choix.dateChoix)}
                                                </small>
                                            </td>
                                            <td>
                                                <small className="text-muted">
                                                    {formatDate(choix.dateCreation)}
                                                </small>
                                            </td>
                                            <td>
                                                <small className="text-muted">
                                                    {choix.dateModification ? formatDate(choix.dateModification) : '-'}
                                                </small>
                                            </td>
                                            <td>
                                                <div className="btn-group btn-group-sm" role="group">
                                                    <Link
                                                        to={`/choix-enseignement/detail/${choix.id}`}
                                                        className="btn btn-outline-info"
                                                        title="Voir détails"
                                                    >
                                                        <i className="bi bi-eye"></i>
                                                    </Link>
                                                    <Link
                                                        to={`/choix-enseignement/modifier/${choix.id}`}
                                                        className="btn btn-outline-warning"
                                                        title="Modifier"
                                                    >
                                                        <i className="bi bi-pencil"></i>
                                                    </Link>
                                                    <button
                                                        onClick={() => confirmDelete(choix)}
                                                        className="btn btn-outline-danger"
                                                        title="Supprimer"
                                                    >
                                                        <i className="bi bi-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>

                            {/* Pagination */}
                            <div className="d-flex justify-content-between align-items-center mt-4">
                                <div className="text-muted">
                                    Affichage de {pagination.pageNumber * pagination.pageSize + 1} à{' '}
                                    {Math.min((pagination.pageNumber + 1) * pagination.pageSize, pagination.totalElements)} sur{' '}
                                    {pagination.totalElements} résultats
                                </div>
                                <nav>
                                    <ul className="pagination pagination-sm mb-0">
                                        <li className={`page-item ${pagination.first ? 'disabled' : ''}`}>
                                            <button
                                                className="page-link"
                                                onClick={() => handlePageChange(pagination.pageNumber - 1)}
                                                disabled={pagination.first}
                                            >
                                                <i className="bi bi-chevron-left"></i>
                                            </button>
                                        </li>
                                        {[...Array(pagination.totalPages)].map((_, index) => (
                                            <li
                                                key={index}
                                                className={`page-item ${index === pagination.pageNumber ? 'active' : ''}`}
                                            >
                                                <button
                                                    className="page-link"
                                                    onClick={() => handlePageChange(index)}
                                                >
                                                    {index + 1}
                                                </button>
                                            </li>
                                        ))}
                                        <li className={`page-item ${pagination.last ? 'disabled' : ''}`}>
                                            <button
                                                className="page-link"
                                                onClick={() => handlePageChange(pagination.pageNumber + 1)}
                                                disabled={pagination.last}
                                            >
                                                <i className="bi bi-chevron-right"></i>
                                            </button>
                                        </li>
                                    </ul>
                                </nav>
                            </div>
                        </>
                    )}
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
                                {choixToDelete && (
                                    <div className="alert alert-warning">
                                        <strong>Choix #{choixToDelete.id}</strong>
                                        <br />
                                        Enseignant: {choixToDelete.idEnseignant}
                                        <br />
                                        Enseignement: {choixToDelete.idEnseignement}
                                    </div>
                                )}
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

export default ChoixList;