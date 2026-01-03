import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './CoordinatorPages.css';

const Classes = () => {
    const columns = [
        { key: 'code', label: 'Code' },
        { key: 'libelle', label: 'Libellé' },
        { key: 'effectif', label: 'Effectif' },
        { key: 'anneeAcademique', label: 'Année Académique' }
    ];

    const formFields = [
        { name: 'code', label: 'Code', type: 'text', required: true },
        { name: 'libelle', label: 'Libellé', type: 'text', required: true },
        { name: 'effectif', label: 'Effectif', type: 'number', required: false },
        { name: 'anneeAcademique', label: 'Année Académique', type: 'text', required: true },
        { name: 'description', label: 'Description', type: 'textarea', required: false }
    ];

    return (
        <CrudPage
            title="Gestion des Classes"
            listEndpoint={API_ENDPOINTS.MAQUETTES.CLASSES}
            createEndpoint={API_ENDPOINTS.MAQUETTES.CLASSES}
            updateEndpoint={API_ENDPOINTS.MAQUETTES.CLASSE_BY_ID}
            deleteEndpoint={API_ENDPOINTS.MAQUETTES.CLASSE_BY_ID}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default Classes;
