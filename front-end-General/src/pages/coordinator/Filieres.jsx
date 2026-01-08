import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './CoordinatorPages.css';

const Filieres = () => {
    const columns = [
        { key: 'code', label: 'Code' },
        { key: 'libelle', label: 'Libellé' },
        { key: 'description', label: 'Description' }
    ];

    const formFields = [
        { name: 'code', label: 'Code', type: 'text', required: true },
        { name: 'libelle', label: 'Libellé', type: 'text', required: true },
        { name: 'description', label: 'Description', type: 'textarea', required: false }
    ];

    return (
        <CrudPage
            title="Gestion des Filières"
            listEndpoint={API_ENDPOINTS.MAQUETTES.FILIERES}
            createEndpoint={API_ENDPOINTS.MAQUETTES.FILIERES}
            updateEndpoint={API_ENDPOINTS.MAQUETTES.FILIERE_BY_ID}
            deleteEndpoint={API_ENDPOINTS.MAQUETTES.FILIERE_BY_ID}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default Filieres;
