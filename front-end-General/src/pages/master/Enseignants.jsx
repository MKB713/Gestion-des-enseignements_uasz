import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './MasterPages.css';

const Enseignants = () => {
    const columns = [
        { key: 'matricule', label: 'Matricule' },
        { key: 'nom', label: 'Nom' },
        { key: 'prenom', label: 'Prénom' },
        { key: 'email', label: 'Email' },
        { key: 'grade', label: 'Grade' }
    ];

    const formFields = [
        { name: 'matricule', label: 'Matricule', type: 'text', required: true },
        { name: 'nom', label: 'Nom', type: 'text', required: true },
        { name: 'prenom', label: 'Prénom', type: 'text', required: true },
        { name: 'email', label: 'Email', type: 'email', required: true },
        { name: 'telephone', label: 'Téléphone', type: 'tel', required: false },
        {
            name: 'grade',
            label: 'Grade',
            type: 'select',
            required: true,
            options: [
                { value: 'PROFESSEUR', label: 'Professeur' },
                { value: 'MAITRE_CONFERENCE', label: 'Maître de Conférence' },
                { value: 'ASSISTANT', label: 'Assistant' },
                { value: 'VACATAIRE', label: 'Vacataire' }
            ]
        },
        { name: 'specialite', label: 'Spécialité', type: 'text', required: false }
    ];

    return (
        <CrudPage
            title="Gestion des Enseignants"
            listEndpoint={API_ENDPOINTS.ENSEIGNANTS.LIST}
            createEndpoint={API_ENDPOINTS.ENSEIGNANTS.CREATE}
            updateEndpoint={API_ENDPOINTS.ENSEIGNANTS.UPDATE}
            deleteEndpoint={API_ENDPOINTS.ENSEIGNANTS.DELETE}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default Enseignants;
