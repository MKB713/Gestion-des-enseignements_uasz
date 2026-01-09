import React, { useState, useEffect } from 'react';
import {
    Building2,
    Search,
    Plus,
    Edit,
    Trash2,
    X,
    Calendar
} from 'lucide-react';
import { api, API_ENDPOINTS } from '../../config/api';
import '../admin/AdminUsers.css'; // Reusing Users CSS for consistency or AdminDepartments.css if preferred. Source used this.

const MasterStructures = () => {
    // State
    const [departments, setDepartments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingDept, setEditingDept] = useState(null);

    // Form State
    const [formData, setFormData] = useState({
        libelle: '',
        description: ''
    });

    // Fetch Departments
    const fetchDepartments = async () => {
        try {
            setLoading(true);
            const response = await api.get(API_ENDPOINTS.DEPARTMENTS.LIST);
            setDepartments(Array.isArray(response) ? response : []);
            setError(null);
        } catch (err) {
            console.error("Erreur lors de la récupération des départements:", err);
            setError("Impossible de charger la liste des départements.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDepartments();
    }, []);

    // Handle Form Changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Open Modal
    const openModal = (dept = null) => {
        if (dept) {
            setEditingDept(dept);
            setFormData({
                libelle: dept.libelle,
                description: dept.description || ''
            });
        } else {
            setEditingDept(null);
            setFormData({
                libelle: '',
                description: ''
            });
        }
        setIsModalOpen(true);
    };

    // Handle Submit
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            setLoading(true);
            if (editingDept) {
                await api.put(API_ENDPOINTS.DEPARTMENTS.UPDATE(editingDept.id), formData);
                alert("Département modifié avec succès !");
            } else {
                await api.post(API_ENDPOINTS.DEPARTMENTS.CREATE, formData);
                alert("Département créé avec succès !");
            }
            setIsModalOpen(false);
            fetchDepartments();
        } catch (err) {
            console.error("Erreur lors de l'enregistrement:", err);
            // Show detailed error if available
            const errorMessage = err.response?.data?.message || err.message || "Erreur inconnue";
            alert("Erreur lors de l'enregistrement: " + errorMessage);
            setError("Erreur : " + errorMessage);
        } finally {
            setLoading(false);
        }
    };

    // Handle Delete
    const handleDelete = async (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer ce département ?")) {
            try {
                setLoading(true);
                await api.delete(API_ENDPOINTS.DEPARTMENTS.DELETE(id));
                alert("Département supprimé.");
                fetchDepartments();
            } catch (err) {
                console.error("Erreur lors de la suppression:", err);
                alert("Impossible de supprimer ce département.");
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div className="admin-users-container">
            {/* Header */}
            <div className="page-header">
                <div className="header-content">
                    <h1>
                        <Building2 size={32} className="text-primary-600" />
                        Gestion des Structures (Master)
                    </h1>
                    <p>Gérez les structures et départements</p>
                </div>
                <button className="btn-primary" onClick={() => openModal()}>
                    <Plus size={20} />
                    Nouveau Département
                </button>
            </div>

            {/* List */}
            <div className="table-container">
                {loading && !isModalOpen ? (
                    <div style={{ padding: '2rem', textAlign: 'center', color: '#64748b' }}>
                        Chargement...
                    </div>
                ) : error ? (
                    <div style={{ padding: '2rem', textAlign: 'center', color: '#ef4444' }}>
                        {error}
                    </div>
                ) : departments.length === 0 ? (
                    <div style={{ padding: '2rem', textAlign: 'center', color: '#64748b' }}>
                        Aucun département trouvé.
                    </div>
                ) : (
                    <table className="users-table">
                        <thead>
                            <tr>
                                <th>Libellé</th>
                                <th>Description</th>
                                <th>Date de Création</th>
                                <th style={{ textAlign: 'right' }}>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {departments.map((dept) => (
                                <tr key={dept.id}>
                                    <td style={{ fontWeight: 500 }}>{dept.libelle}</td>
                                    <td>{dept.description || '-'}</td>
                                    <td>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#64748b', fontSize: '0.9rem' }}>
                                            <Calendar size={14} />
                                            {dept.dateCreation ? new Date(dept.dateCreation).toLocaleDateString() : '-'}
                                        </div>
                                    </td>
                                    <td>
                                        <div className="actions-cell">
                                            <button className="btn-icon edit" title="Modifier" onClick={() => openModal(dept)}>
                                                <Edit size={18} />
                                            </button>
                                            <button className="btn-icon delete" title="Supprimer" onClick={() => handleDelete(dept.id)}>
                                                <Trash2 size={18} />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>

            {/* Modal */}
            {isModalOpen && (
                <div className="modal-overlay" onClick={() => setIsModalOpen(false)}>
                    <div className="modal-content" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>{editingDept ? 'Modifier Département' : 'Nouveau Département'}</h2>
                            <button className="close-btn" onClick={() => setIsModalOpen(false)}>
                                <X size={24} />
                            </button>
                        </div>
                        <form onSubmit={handleSubmit} className="modal-form">
                            <div className="form-group">
                                <label>Libellé du Département</label>
                                <input
                                    type="text"
                                    name="libelle"
                                    value={formData.libelle}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Ex: Informatique"
                                />
                            </div>
                            <div className="form-group">
                                <label>Description</label>
                                <textarea
                                    name="description"
                                    value={formData.description}
                                    onChange={handleInputChange}
                                    rows="3"
                                    placeholder="Description optionnelle..."
                                    style={{ width: '100%', padding: '0.75rem', borderRadius: '0.5rem', border: '1px solid #e2e8f0' }}
                                />
                            </div>

                            <div className="form-actions">
                                <button type="button" className="btn-secondary" onClick={() => setIsModalOpen(false)}>Annuler</button>
                                <button type="submit" className="btn-primary" disabled={loading}>
                                    {loading ? 'Enregistrement...' : 'Valider'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default MasterStructures;
