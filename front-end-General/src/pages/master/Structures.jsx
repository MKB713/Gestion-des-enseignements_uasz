import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './MasterPages.css';

const Structures = () => {
    const columns = [
        { key: 'code', label: 'Code' },
        { key: 'libelle', label: 'Libellé' },
        { key: 'type', label: 'Type' },
        { key: 'responsable', label: 'Responsable' }
    ];

    const formFields = [
        { name: 'code', label: 'Code', type: 'text', required: true },
        { name: 'libelle', label: 'Libellé', type: 'text', required: true },
        {
            name: 'type',
            label: 'Type',
            type: 'select',
            required: true,
            options: [
                { value: 'DEPARTEMENT', label: 'Département' },
                { value: 'UFR', label: 'UFR' },
                { value: 'INSTITUT', label: 'Institut' },
                { value: 'ECOLE', label: 'École' }
            ]
        },
        { name: 'responsable', label: 'Responsable', type: 'text', required: false },
        { name: 'description', label: 'Description', type: 'textarea', required: false }
    ];

    return (
        <CrudPage
            title="Gestion des Structures"
            listEndpoint={API_ENDPOINTS.MAQUETTES.STRUCTURES}
            createEndpoint={API_ENDPOINTS.MAQUETTES.STRUCTURES}
            updateEndpoint={API_ENDPOINTS.MAQUETTES.STRUCTURE_BY_ID}
            deleteEndpoint={API_ENDPOINTS.MAQUETTES.STRUCTURE_BY_ID}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default Structures;
