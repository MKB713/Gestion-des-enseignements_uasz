import React from 'react';
import CrudPage from '../../components/CrudPage';
import { API_ENDPOINTS } from '../../config/api';
import './MasterPages.css';

const EmploiTemps = () => {
    const columns = [
        { key: 'jour', label: 'Jour' },
        { key: 'heureDebut', label: 'Heure Début' },
        { key: 'heureFin', label: 'Heure Fin' },
        { key: 'matiere', label: 'Matière' },
        { key: 'salle', label: 'Salle' }
    ];

    const formFields = [
        {
            name: 'jour',
            label: 'Jour',
            type: 'select',
            required: true,
            options: [
                { value: 'LUNDI', label: 'Lundi' },
                { value: 'MARDI', label: 'Mardi' },
                { value: 'MERCREDI', label: 'Mercredi' },
                { value: 'JEUDI', label: 'Jeudi' },
                { value: 'VENDREDI', label: 'Vendredi' },
                { value: 'SAMEDI', label: 'Samedi' }
            ]
        },
        { name: 'heureDebut', label: 'Heure de début', type: 'time', required: true },
        { name: 'heureFin', label: 'Heure de fin', type: 'time', required: true },
        { name: 'matiere', label: 'Matière/EC', type: 'text', required: true },
        { name: 'enseignant', label: 'Enseignant', type: 'text', required: false },
        { name: 'salle', label: 'Salle', type: 'text', required: true },
        { name: 'typeSeance', label: 'Type de séance', type: 'select', required: true,
          options: [
            { value: 'CM', label: 'Cours Magistral (CM)' },
            { value: 'TD', label: 'Travaux Dirigés (TD)' },
            { value: 'TP', label: 'Travaux Pratiques (TP)' }
          ]
        }
    ];

    return (
        <CrudPage
            title="Gestion de l'Emploi du Temps"
            listEndpoint={API_ENDPOINTS.EMPLOI_TEMPS.LIST}
            createEndpoint={API_ENDPOINTS.EMPLOI_TEMPS.CREATE}
            updateEndpoint={API_ENDPOINTS.EMPLOI_TEMPS.UPDATE}
            deleteEndpoint={API_ENDPOINTS.EMPLOI_TEMPS.DELETE}
            columns={columns}
            formFields={formFields}
        />
    );
};

export default EmploiTemps;
