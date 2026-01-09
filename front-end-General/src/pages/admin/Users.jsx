import React, { useState, useEffect } from 'react';
import {
    Users,
    Search,
    Plus,
    Edit,
    Trash2,
    CheckCircle,
    XCircle,
    X,
    Archive
} from 'lucide-react';
import { api, API_ENDPOINTS } from '../../config/api';
import './AdminUsers.css';

const AdminUsers = () => {
    // State
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentUser, setCurrentUser] = useState(null);

    // Form State
    const [formData, setFormData] = useState({
        prenom: '',
        nom: '',
        emailPersonnel: '',
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

    // Open Modal for Create or Edit
    const openModal = (user = null) => {
        if (user) {
            setIsEditing(true);
            setCurrentUser(user);
            setFormData({
                prenom: user.prenom || '',
                nom: user.nom || '',
                emailPersonnel: user.email || '',
                dateNaissance: user.dateNaissance ? new Date(user.dateNaissance).toISOString().split('T')[0] : '',
                role: user.role || 'ETUDIANT',
                telephone: user.telephone || '',
                adresse: user.adresse || ''
            });
        } else {
            setIsEditing(false);
            setCurrentUser(null);
            setFormData({
                prenom: '', nom: '', emailPersonnel: '',
                dateNaissance: '', role: 'ETUDIANT',
                telephone: '', adresse: ''
            });
        }
        setIsModalOpen(true);
    };

    // Handle Submit (Create or Update)
    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validation Age
        if (formData.dateNaissance) {
            const today = new Date();
            const birth = new Date(formData.dateNaissance);
            let age = today.getFullYear() - birth.getFullYear();
            const m = today.getMonth() - birth.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
                age--;
            }
            if (age < 16) {
                // Warning if too young
            }
        }

        try {
            setLoading(true);
            if (isEditing && currentUser) {
                // Update
                // Backend requires 'email' and 'matricule' in UserDTO.
                // formData has 'emailPersonnel' which maps to 'email'.
                const payload = {
                    ...formData,
                    id: currentUser.id,
                    email: formData.emailPersonnel,
                    matricule: currentUser.matricule,
                    etat: currentUser.etat
                };

                await api.put(API_ENDPOINTS.USERS.UPDATE(currentUser.id), payload);
                alert("Utilisateur mis à jour avec succès !");
            } else {
                // Create
                // RegisterRequest uses 'emailPersonnel'
                await api.post(API_ENDPOINTS.AUTH.REGISTER, formData);
                alert("Utilisateur créé avec succès !");
            }

            setIsModalOpen(false);
            fetchUsers();
        } catch (err) {
            console.error("Erreur lors de l'enregistrement:", err);
            const msg = err.response?.data?.message || err.message || "Erreur inconnue";
            alert("Erreur: " + msg);
        } finally {
            setLoading(false);
        }
    };

    // Toggle Status (Activate/Deactivate)
    const handleToggleStatus = async (user) => {
        const newStatus = user.etat === 'ACTIF' ? 'INACTIF' : 'ACTIF';
        if (window.confirm(`Voulez-vous vraiment ${newStatus === 'ACTIF' ? 'activer' : 'désactiver'} cet utilisateur ?`)) {
            try {
                const payload = { ...user, etat: newStatus };
                await api.put(API_ENDPOINTS.USERS.UPDATE(user.id), payload);
                fetchUsers();
            } catch (err) {
                console.error("Erreur changement statut:", err);
                alert("Erreur lors du changement de statut.");
            }
        }
    };

    // Handle Archive
    const handleArchive = async (user) => {
        if (window.confirm("Voulez-vous vraiment archiver cet utilisateur ? Il deviendra inactif.")) {
            try {
                const payload = { ...user, etat: 'INACTIF' };
                await api.put(API_ENDPOINTS.USERS.UPDATE(user.id), payload);
                fetchUsers();
            } catch (err) {
                console.error("Erreur lors de l'archivage:", err);
                alert("Erreur lors de l'archivage.");
            }
        }
    };

    // Handle Delete
    const handleDelete = async (user) => {
        if (window.confirm("ATTENTION : Voulez-vous vraiment SUPPRIMER définitivement cet utilisateur ?")) {
            try {
                await api.delete(API_ENDPOINTS.USERS.DELETE(user.id));
                fetchUsers();
                alert("Utilisateur supprimé avec succès.");
            } catch (err) {
                console.error("Erreur lors de la suppression:", err);
                const msg = err.response?.data?.message || "Impossible de supprimer (contraintes existantes).";
                alert("Erreur: " + msg);
            }
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
                {/* Changed button color to Green as requested */}
                <button className="btn-primary" style={{ backgroundColor: '#166534' }} onClick={() => openModal()}>
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
                                        <button
                                            className={`status-indicator ${user.etat === 'ACTIF' ? 'status-active' : 'status-inactive'}`}
                                            onClick={() => handleToggleStatus(user)}
                                            style={{ border: 'none', background: 'none', cursor: 'pointer' }}
                                            title={user.etat === 'ACTIF' ? "Désactiver" : "Activer"}
                                        >
                                            {user.etat === 'ACTIF' ? <CheckCircle size={16} /> : <XCircle size={16} />}
                                            {user.etat}
                                        </button>
                                    </td>
                                    <td>
                                        <div className="actions-cell">
                                            <button className="btn-icon edit" title="Modifier" onClick={() => openModal(user)}>
                                                <Edit size={18} />
                                            </button>
                                            <button className="btn-icon archive" title="Archiver" onClick={() => handleArchive(user)} style={{ color: '#ca8a04' }}>
                                                <Archive size={18} />
                                            </button>
                                            <button className="btn-icon delete" title="Supprimer" onClick={() => handleDelete(user)}>
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

            {/* Create/Edit User Modal */}
            {isModalOpen && (
                <div className="modal-overlay" onClick={() => setIsModalOpen(false)}>
                    <div className="modal-content" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>{isEditing ? "Modifier l'Utilisateur" : "Nouvel Utilisateur"}</h2>
                            <button className="close-btn" onClick={() => setIsModalOpen(false)}>
                                <X size={24} />
                            </button>
                        </div>
                        <form onSubmit={handleSubmit} className="modal-form">
                            {!isEditing && (
                                <div className="alert-info" style={{ marginBottom: '1.5rem', padding: '1rem', background: '#ecfdf5', borderRadius: '8px', color: '#047857', fontSize: '0.9rem' }}>
                                    Le mot de passe et le matricule seront générés automatiquement et envoyés à l'email personnel.
                                </div>
                            )}

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
                                <label>Email Personnel</label>
                                <input
                                    type="email"
                                    name="emailPersonnel"
                                    value={formData.emailPersonnel}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="exemple@box.com"
                                    disabled={isEditing}
                                />
                            </div>

                            <div className="form-grid">
                                <div className="form-group">
                                    <label>Date de Naissance</label>
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
                                    <select name="role" value={formData.role} onChange={handleInputChange} disabled={isEditing}>
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
                                    {loading ? 'Traitement...' : (isEditing ? 'Mettre à jour' : 'Valider & Créer')}
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
