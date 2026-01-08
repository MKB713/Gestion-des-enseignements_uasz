import React, { useState, useEffect } from 'react';
import {
    Users,
    Search,
    Plus,
    Edit,
    Trash2,
    CheckCircle,
    XCircle,
    X
} from 'lucide-react';
import { api, API_ENDPOINTS } from '../../config/api';
import './AdminUsers.css';

const AdminUsers = () => {
    // State
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Form State (Simplified)
    const [formData, setFormData] = useState({
        prenom: '',
        nom: '',
        emailPersonnel: '', // Changed from email to emailPersonnel
        dateNaissance: '',
        role: 'ETUDIANT',
        telephone: '',
        adresse: ''
    });

    // Fetch Users
    const fetchUsers = async () => {
        try {
            setLoading(true);
            const response = await api.get(API_ENDPOINTS.USERS.LIST);
            // The api.get returns the JSON payload directly (Array)
            setUsers(Array.isArray(response) ? response : []);
            setError(null);
        } catch (err) {
            console.error("Erreur lors de la récupération des utilisateurs:", err);
            setError(`Erreur: ${err.message || "Impossible de charger la liste."}`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    // Handle Form Changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Handle Create User
    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validation Age (Min 25 ans)
        if (formData.dateNaissance) {
            const today = new Date();
            const birth = new Date(formData.dateNaissance);
            let age = today.getFullYear() - birth.getFullYear();
            const m = today.getMonth() - birth.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
                age--;
            }

            if (age < 25) {
                alert("L'utilisateur doit avoir au moins 25 ans.");
                return;
            }
        }

        try {
            setLoading(true);
            // Send to backend (Auto-generation enabled)
            await api.post(API_ENDPOINTS.AUTH.REGISTER, formData);

            // Success
            setIsModalOpen(false);
            setFormData({
                prenom: '', nom: '', emailPersonnel: '',
                dateNaissance: '', role: 'ETUDIANT',
                telephone: '', adresse: ''
            });
            fetchUsers(); // Refresh list
            alert("Utilisateur créé avec succès ! Les identifiants ont été envoyés par email.");

        } catch (err) {
            console.error("Erreur lors de la création:", err);
            alert("Erreur lors de la création de l'utilisateur. Vérifiez les champs.");
        } finally {
            setLoading(false);
        }
    };

    const getRoleBadgeClass = (role) => {
        switch (role) {
            case 'ADMIN': return 'badge-role-admin';
            case 'ENSEIGNANT': return 'badge-role-enseignant';
            case 'ETUDIANT': return 'badge-role-etudiant';
            default: return '';
        }
    };

    return (
        <div className="admin-users-container">
            {/* Header */}
            <div className="page-header">
                <div className="header-content">
                    <h1>
                        <Users size={32} className="text-primary-600" />
                        Gestion des Utilisateurs
                    </h1>
                    <p>Gérez les comptes, les rôles et les accès à la plateforme</p>
                </div>
                <button className="btn-primary" onClick={() => setIsModalOpen(true)}>
                    <Plus size={20} />
                    Nouvel Utilisateur
                </button>
            </div>

            {/* Controls */}
            <div className="controls-section">
                <div className="search-wrapper">
                    <Search className="search-icon" size={20} />
                    <input
                        type="text"
                        placeholder="Rechercher par nom, email ou matricule..."
                        className="search-input"
                    />
                </div>
                <select className="filter-select">
                    <option value="">Tous les rôles</option>
                    <option value="ADMIN">Administrateur</option>
                    <option value="ENSEIGNANT">Enseignant</option>
                    <option value="ETUDIANT">Etudiant</option>
                </select>
                <select className="filter-select">
                    <option value="">Tous les états</option>
                    <option value="active">Actif</option>
                    <option value="inactive">Inactif</option>
                </select>
            </div>

            {/* Table or Loading */}
            <div className="table-container">
                {loading && !isModalOpen ? (
                    <div style={{ padding: '2rem', textAlign: 'center', color: '#64748b' }}>
                        Chargement des utilisateurs...
                    </div>
                ) : error ? (
                    <div style={{ padding: '2rem', textAlign: 'center', color: '#ef4444' }}>
                        {error}
                    </div>
                ) : users.length === 0 ? (
                    <div style={{ padding: '2rem', textAlign: 'center', color: '#64748b' }}>
                        Aucun utilisateur trouvé.
                    </div>
                ) : (
                    <table className="users-table">
                        <thead>
                            <tr>
                                <th>Utilisateur</th>
                                <th>Rôle</th>
                                <th>Statut</th>
                                <th style={{ textAlign: 'right' }}>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map((user) => (
                                <tr key={user.id}>
                                    <td>
                                        <div className="user-profile">
                                            <div className="avatar-circle">
                                                {user.prenom ? user.prenom[0] : 'U'}{user.nom ? user.nom[0] : ''}
                                            </div>
                                            <div className="user-info">
                                                <span className="name">{user.prenom} {user.nom}</span>
                                                <span className="email">{user.email}</span>
                                                <span className="matricule" style={{ fontSize: '0.75rem', color: '#94a3b8' }}>{user.matricule}</span>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <span className={`badge ${getRoleBadgeClass(user.role)}`}>
                                            {user.role}
                                        </span>
                                    </td>
                                    <td>
                                        <span className={`status-indicator ${user.etat === 'ACTIF' ? 'status-active' : 'status-inactive'}`}>
                                            {user.etat === 'ACTIF' ? <CheckCircle size={16} /> : <XCircle size={16} />}
                                            {user.etat}
                                        </span>
                                    </td>
                                    <td>
                                        <div className="actions-cell">
                                            <button className="btn-icon edit" title="Modifier">
                                                <Edit size={18} />
                                            </button>
                                            <button className="btn-icon delete" title="Supprimer">
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

            {/* Create User Modal */}
            {isModalOpen && (
                <div className="modal-overlay" onClick={() => setIsModalOpen(false)}>
                    <div className="modal-content" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>Nouvel Utilisateur</h2>
                            <button className="close-btn" onClick={() => setIsModalOpen(false)}>
                                <X size={24} />
                            </button>
                        </div>
                        <form onSubmit={handleSubmit} className="modal-form">
                            <div className="alert-info" style={{ marginBottom: '1.5rem', padding: '1rem', background: '#ecfdf5', borderRadius: '8px', color: '#047857', fontSize: '0.9rem' }}>
                                Le mot de passe et le matricule seront générés automatiquement et envoyés à l'email personnel.
                            </div>

                            <div className="form-grid">
                                <div className="form-group">
                                    <label>Prénom</label>
                                    <input type="text" name="prenom" value={formData.prenom} onChange={handleInputChange} required />
                                </div>
                                <div className="form-group">
                                    <label>Nom</label>
                                    <input type="text" name="nom" value={formData.nom} onChange={handleInputChange} required />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Email Personnel (pour réception des accès)</label>
                                <input
                                    type="email"
                                    name="emailPersonnel"
                                    value={formData.emailPersonnel}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="exemple@box.com"
                                />
                            </div>

                            <div className="form-grid">
                                <div className="form-group">
                                    <label>Date de Naissance (Min. 25 ans)</label>
                                    <input
                                        type="date"
                                        name="dateNaissance"
                                        value={formData.dateNaissance}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Rôle</label>
                                    <select name="role" value={formData.role} onChange={handleInputChange}>
                                        <option value="ETUDIANT">Etudiant</option>
                                        <option value="ENSEIGNANT">Enseignant</option>
                                        <option value="ADMIN">Administrateur</option>
                                        <option value="RESPONSABLE_MASTER">Responsable Master</option>
                                        <option value="COORDONATEUR_DES_LICENCES">Coordonnateur</option>
                                    </select>
                                </div>
                            </div>

                            <div className="form-grid">
                                <div className="form-group">
                                    <label>Téléphone</label>
                                    <input type="text" name="telephone" value={formData.telephone} onChange={handleInputChange} />
                                </div>
                                <div className="form-group">
                                    <label>Adresse</label>
                                    <input type="text" name="adresse" value={formData.adresse} onChange={handleInputChange} />
                                </div>
                            </div>

                            <div className="form-actions">
                                <button type="button" className="btn-secondary" onClick={() => setIsModalOpen(false)}>Annuler</button>
                                <button type="submit" className="btn-primary" disabled={loading}>
                                    {loading ? 'Génération...' : 'Valider & Créer'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminUsers;
