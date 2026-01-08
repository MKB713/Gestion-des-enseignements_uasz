import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './CoordinatorPages.css';

const CahierTexte = () => {
    const columns = [
        { key: 'date', label: 'Date' },
        { key: 'matiere', label: 'Matière' },
        { key: 'classe', label: 'Classe' },
        { key: 'sujet', label: 'Sujet' },
        { key: 'duree', label: 'Durée (h)' }
    ];

    const formFields = [
        { name: 'date', label: 'Date du cours', type: 'date', required: true },
        { name: 'matiere', label: 'Matière/EC', type: 'text', required: true },
        { name: 'classe', label: 'Classe', type: 'text', required: true },
        { name: 'sujet', label: 'Sujet/Thème du cours', type: 'text', required: true },
        { name: 'duree', label: 'Durée (heures)', type: 'number', required: true },
        { name: 'contenu', label: 'Contenu détaillé', type: 'textarea', required: true, rows: 6 },
        { name: 'observations', label: 'Observations', type: 'textarea', required: false, rows: 3 }
    ];

    return (
        <CrudPage
            title="Cahier de Texte"
            listEndpoint={API_ENDPOINTS.DEROULEMENT.CAHIER_TEXTE}
            createEndpoint={API_ENDPOINTS.DEROULEMENT.CREATE_CAHIER_TEXTE}
            updateEndpoint={API_ENDPOINTS.DEROULEMENT.UPDATE_CAHIER_TEXTE}
            deleteEndpoint={API_ENDPOINTS.DEROULEMENT.DELETE_CAHIER_TEXTE}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default CahierTexte;
