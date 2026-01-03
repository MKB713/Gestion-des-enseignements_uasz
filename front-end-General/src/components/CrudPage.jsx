import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Search } from 'lucide-react';
import { api } from '../config/api';

/**
 * Generic CRUD component for managing entities
 * @param {Object} props
 * @param {string} props.title - Page title
 * @param {string} props.listEndpoint - API endpoint for listing items
 * @param {Function} props.getByIdEndpoint - Function that returns endpoint for single item
 * @param {string} props.createEndpoint - API endpoint for creating items
 * @param {Function} props.updateEndpoint - Function that returns endpoint for updating
 * @param {Function} props.deleteEndpoint - Function that returns endpoint for deleting
 * @param {Array} props.columns - Table columns configuration
 * @param {Array} props.formFields - Form fields configuration
 * @param {Function} props.renderCell - Custom cell renderer (optional)
 */
const CrudPage = ({
    title,
    listEndpoint,
    getByIdEndpoint,
    createEndpoint,
    updateEndpoint,
    deleteEndpoint,
    columns,
    formFields,
    renderCell,
    cssClass = ''
}) => {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [editingItem, setEditingItem] = useState(null);
    const [formData, setFormData] = useState({});

    useEffect(() => {
        // Initialize form data with empty values
        const initialData = {};
        formFields.forEach(field => {
            initialData[field.name] = field.defaultValue || '';
        });
        setFormData(initialData);
    }, [formFields]);

    useEffect(() => {
        fetchItems();
    }, [listEndpoint]);

    const fetchItems = async () => {
        try {
            setLoading(true);
            const data = await api.get(listEndpoint);
            setItems(Array.isArray(data) ? data : []);
            setError('');
        } catch (err) {
            setError(`Erreur lors du chargement: ${err.message}`);
            console.error('Error fetching items:', err);
            setItems([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingItem) {
                await api.put(updateEndpoint(editingItem.id), formData);
            } else {
                await api.post(createEndpoint, formData);
            }
            fetchItems();
            closeModal();
        } catch (err) {
            setError(`Erreur lors de l'enregistrement: ${err.message}`);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Êtes-vous sûr de vouloir supprimer cet élément ?')) {
            return;
        }
        try {
            await api.delete(deleteEndpoint(id));
            fetchItems();
        } catch (err) {
            setError(`Erreur lors de la suppression: ${err.message}`);
        }
    };

    const openModal = (item = null) => {
        if (item) {
            setEditingItem(item);
            const newFormData = {};
            formFields.forEach(field => {
                newFormData[field.name] = item[field.name] || field.defaultValue || '';
            });
            setFormData(newFormData);
        } else {
            setEditingItem(null);
            const newFormData = {};
            formFields.forEach(field => {
                newFormData[field.name] = field.defaultValue || '';
            });
            setFormData(newFormData);
        }
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setEditingItem(null);
    };

    const filteredItems = items.filter(item => {
        const searchString = searchTerm.toLowerCase();
        return columns.some(col => {
            const value = item[col.key];
            return value?.toString().toLowerCase().includes(searchString);
        });
    });

    if (loading) {
        return <div className="loading">Chargement...</div>;
    }

    return (
        <div className={`page-container ${cssClass}`}>
            <div className="page-header">
                <h1>{title}</h1>
                <button className="btn-primary" onClick={() => openModal()}>
                    <Plus size={20} />
                    Nouveau
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
                    placeholder="Rechercher..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            <div className="table-container">
                <table className="data-table">
                    <thead>
                        <tr>
                            {columns.map(col => (
                                <th key={col.key}>{col.label}</th>
                            ))}
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredItems.length === 0 ? (
                            <tr>
                                <td colSpan={columns.length + 1} className="no-data">
                                    Aucun élément trouvé
                                </td>
                            </tr>
                        ) : (
                            filteredItems.map((item) => (
                                <tr key={item.id}>
                                    {columns.map(col => (
                                        <td key={col.key}>
                                            {renderCell ? renderCell(item, col.key) : (item[col.key] || '-')}
                                        </td>
                                    ))}
                                    <td className="actions">
                                        <button
                                            className="btn-icon btn-edit"
                                            onClick={() => openModal(item)}
                                            title="Modifier"
                                        >
                                            <Edit2 size={18} />
                                        </button>
                                        <button
                                            className="btn-icon btn-delete"
                                            onClick={() => handleDelete(item.id)}
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
                            <h2>{editingItem ? 'Modifier' : 'Nouveau'}</h2>
                            <button className="btn-close" onClick={closeModal}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit}>
                            {formFields.map(field => (
                                <div className="form-group" key={field.name}>
                                    <label htmlFor={field.name}>
                                        {field.label}
                                        {field.required && ' *'}
                                    </label>
                                    {field.type === 'textarea' ? (
                                        <textarea
                                            id={field.name}
                                            value={formData[field.name] || ''}
                                            onChange={(e) => setFormData({ ...formData, [field.name]: e.target.value })}
                                            required={field.required}
                                            rows={field.rows || 4}
                                        />
                                    ) : field.type === 'select' ? (
                                        <select
                                            id={field.name}
                                            value={formData[field.name] || ''}
                                            onChange={(e) => setFormData({ ...formData, [field.name]: e.target.value })}
                                            required={field.required}
                                        >
                                            <option value="">Sélectionner...</option>
                                            {field.options?.map(opt => (
                                                <option key={opt.value} value={opt.value}>
                                                    {opt.label}
                                                </option>
                                            ))}
                                        </select>
                                    ) : (
                                        <input
                                            type={field.type || 'text'}
                                            id={field.name}
                                            value={formData[field.name] || ''}
                                            onChange={(e) => setFormData({ ...formData, [field.name]: e.target.value })}
                                            required={field.required}
                                        />
                                    )}
                                </div>
                            ))}
                            <div className="modal-footer">
                                <button type="button" className="btn-secondary" onClick={closeModal}>
                                    Annuler
                                </button>
                                <button type="submit" className="btn-primary">
                                    {editingItem ? 'Mettre à jour' : 'Créer'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CrudPage;
