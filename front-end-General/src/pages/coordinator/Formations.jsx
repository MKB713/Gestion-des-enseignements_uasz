import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Search } from 'lucide-react';
import { API_ENDPOINTS, api } from '../../config/api';
import './CoordinatorPages.css';

const Formations = () => {
    const [formations, setFormations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [editingFormation, setEditingFormation] = useState(null);
    const [formData, setFormData] = useState({
        libelle: '',
        code: '',
        description: ''
    });

    useEffect(() => {
        fetchFormations();
    }, []);

    const fetchFormations = async () => {
        try {
            setLoading(true);
            const data = await api.get(API_ENDPOINTS.MAQUETTES.FORMATIONS);
            setFormations(data);
            setError('');
        } catch (err) {
            setError('Erreur lors du chargement des formations: ' + err.message);
            console.error('Error fetching formations:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingFormation) {
                await api.put(
                    API_ENDPOINTS.MAQUETTES.FORMATION_BY_ID(editingFormation.id),
                    formData
                );
            } else {
                await api.post(API_ENDPOINTS.MAQUETTES.FORMATIONS, formData);
            }
            fetchFormations();
            closeModal();
        } catch (err) {
            setError('Erreur lors de l\'enregistrement: ' + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette formation ?')) {
            return;
        }
        try {
            await api.delete(API_ENDPOINTS.MAQUETTES.FORMATION_BY_ID(id));
            fetchFormations();
        } catch (err) {
            setError('Erreur lors de la suppression: ' + err.message);
        }
    };

    const openModal = (formation = null) => {
        if (formation) {
            setEditingFormation(formation);
            setFormData({
                libelle: formation.libelle || '',
                code: formation.code || '',
                description: formation.description || ''
            });
        } else {
            setEditingFormation(null);
            setFormData({ libelle: '', code: '', description: '' });
        }
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setEditingFormation(null);
        setFormData({ libelle: '', code: '', description: '' });
    };

    const filteredFormations = formations.filter(formation =>
        formation.libelle?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        formation.code?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) {
        return <div className="loading">Chargement...</div>;
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <h1>Gestion des Formations</h1>
                <button className="btn-primary" onClick={() => openModal()}>
                    <Plus size={20} />
                    Nouvelle Formation
                </button>
            </div>

            {error && (
                <div className="alert-error">
                    {error}
                </div>
            )}

            <div className="search-bar">
                <Search size={20} />
                <input
                    type="text"
                    placeholder="Rechercher une formation..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            <div className="table-container">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Libellé</th>
                            <th>Description</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredFormations.length === 0 ? (
                            <tr>
                                <td colSpan="4" className="no-data">
                                    Aucune formation trouvée
                                </td>
                            </tr>
                        ) : (
                            filteredFormations.map((formation) => (
                                <tr key={formation.id}>
                                    <td>{formation.code}</td>
                                    <td>{formation.libelle}</td>
                                    <td>{formation.description || '-'}</td>
                                    <td className="actions">
                                        <button
                                            className="btn-icon btn-edit"
                                            onClick={() => openModal(formation)}
                                            title="Modifier"
                                        >
                                            <Edit2 size={18} />
                                        </button>
                                        <button
                                            className="btn-icon btn-delete"
                                            onClick={() => handleDelete(formation.id)}
                                            title="Supprimer"
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {showModal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>{editingFormation ? 'Modifier la Formation' : 'Nouvelle Formation'}</h2>
                            <button className="btn-close" onClick={closeModal}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label htmlFor="code">Code *</label>
                                <input
                                    type="text"
                                    id="code"
                                    value={formData.code}
                                    onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="libelle">Libellé *</label>
                                <input
                                    type="text"
                                    id="libelle"
                                    value={formData.libelle}
                                    onChange={(e) => setFormData({ ...formData, libelle: e.target.value })}
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="description">Description</label>
                                <textarea
                                    id="description"
                                    value={formData.description}
                                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                    rows="4"
                                />
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn-secondary" onClick={closeModal}>
                                    Annuler
                                </button>
                                <button type="submit" className="btn-primary">
                                    {editingFormation ? 'Mettre à jour' : 'Créer'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Formations;
