import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './MasterPages.css';

const EC = () => {
    const columns = [
        { key: 'code', label: 'Code' },
        { key: 'libelle', label: 'Libellé' },
        { key: 'coefficient', label: 'Coefficient' },
        { key: 'volumeHoraire', label: 'Volume Horaire' }
    ];

    const formFields = [
        { name: 'code', label: 'Code', type: 'text', required: true },
        { name: 'libelle', label: 'Libellé', type: 'text', required: true },
        { name: 'coefficient', label: 'Coefficient', type: 'number', required: true },
        { name: 'volumeHoraire', label: 'Volume Horaire (h)', type: 'number', required: true },
        { name: 'cm', label: 'CM (h)', type: 'number', required: false },
        { name: 'td', label: 'TD (h)', type: 'number', required: false },
        { name: 'tp', label: 'TP (h)', type: 'number', required: false },
        { name: 'description', label: 'Description', type: 'textarea', required: false }
    ];

    return (
        <CrudPage
            title="Gestion des Éléments Constitutifs (EC)"
            listEndpoint={API_ENDPOINTS.MAQUETTES.ECS}
            createEndpoint={API_ENDPOINTS.MAQUETTES.ECS}
            updateEndpoint={API_ENDPOINTS.MAQUETTES.EC_BY_ID}
            deleteEndpoint={API_ENDPOINTS.MAQUETTES.EC_BY_ID}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default EC;
