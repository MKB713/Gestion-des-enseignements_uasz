import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './CoordinatorPages.css';

const Modules = () => {
    const columns = [
        { key: 'code', label: 'Code' },
        { key: 'libelle', label: 'Libellé' },
        { key: 'coefficient', label: 'Coefficient' },
        { key: 'credits', label: 'Crédits' }
    ];

    const formFields = [
        { name: 'code', label: 'Code', type: 'text', required: true },
        { name: 'libelle', label: 'Libellé', type: 'text', required: true },
        { name: 'coefficient', label: 'Coefficient', type: 'number', required: true },
        { name: 'credits', label: 'Crédits ECTS', type: 'number', required: true },
        { name: 'description', label: 'Description', type: 'textarea', required: false }
    ];

    return (
        <CrudPage
            title="Gestion des Modules"
            listEndpoint={API_ENDPOINTS.MAQUETTES.MODULES}
            createEndpoint={API_ENDPOINTS.MAQUETTES.MODULES}
            updateEndpoint={API_ENDPOINTS.MAQUETTES.MODULE_BY_ID}
            deleteEndpoint={API_ENDPOINTS.MAQUETTES.MODULE_BY_ID}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default Modules;
