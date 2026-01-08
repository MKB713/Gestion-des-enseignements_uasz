import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './CoordinatorPages.css';

const UE = () => {
    const columns = [
        { key: 'code', label: 'Code' },
        { key: 'libelle', label: 'Libellé' },
        { key: 'credits', label: 'Crédits' },
        { key: 'type', label: 'Type' }
    ];

    const formFields = [
        { name: 'code', label: 'Code', type: 'text', required: true },
        { name: 'libelle', label: 'Libellé', type: 'text', required: true },
        { name: 'credits', label: 'Crédits ECTS', type: 'number', required: true },
        {
            name: 'type',
            label: 'Type',
            type: 'select',
            required: true,
            options: [
                { value: 'FONDAMENTALE', label: 'Fondamentale' },
                { value: 'COMPLEMENTAIRE', label: 'Complémentaire' },
                { value: 'OPTIONNELLE', label: 'Optionnelle' }
            ]
        },
        { name: 'description', label: 'Description', type: 'textarea', required: false }
    ];

    return (
        <CrudPage
            title="Gestion des Unités d'Enseignement (UE)"
            listEndpoint={API_ENDPOINTS.MAQUETTES.UES}
            createEndpoint={API_ENDPOINTS.MAQUETTES.UES}
            updateEndpoint={API_ENDPOINTS.MAQUETTES.UE_BY_ID}
            deleteEndpoint={API_ENDPOINTS.MAQUETTES.UE_BY_ID}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default UE;
