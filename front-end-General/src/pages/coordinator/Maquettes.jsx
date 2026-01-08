import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './CoordinatorPages.css';

const Maquettes = () => {
    const columns = [
        { key: 'code', label: 'Code' },
        { key: 'libelle', label: 'Libellé' },
        { key: 'anneeAcademique', label: 'Année Académique' },
        { key: 'statut', label: 'Statut' }
    ];

    const formFields = [
        { name: 'code', label: 'Code', type: 'text', required: true },
        { name: 'libelle', label: 'Libellé', type: 'text', required: true },
        { name: 'anneeAcademique', label: 'Année Académique', type: 'text', required: true },
        {
            name: 'statut',
            label: 'Statut',
            type: 'select',
            required: true,
            options: [
                { value: 'BROUILLON', label: 'Brouillon' },
                { value: 'VALIDE', label: 'Validé' },
                { value: 'ARCHIVE', label: 'Archivé' }
            ]
        },
        { name: 'description', label: 'Description', type: 'textarea', required: false }
    ];

    return (
        <CrudPage
            title="Gestion des Maquettes"
            listEndpoint={API_ENDPOINTS.MAQUETTES.LIST}
            createEndpoint={API_ENDPOINTS.MAQUETTES.CREATE}
            updateEndpoint={API_ENDPOINTS.MAQUETTES.UPDATE}
            deleteEndpoint={API_ENDPOINTS.MAQUETTES.DELETE}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default Maquettes;
