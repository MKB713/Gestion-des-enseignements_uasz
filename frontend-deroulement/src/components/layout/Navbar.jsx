import React from 'react';
import { useLocation } from 'react-router-dom';

const Navbar = () => {
    const location = useLocation();

    const getTitles = () => {
        const titles = {
            '/dashboard': 'Tableau de Bord',
            '/lst-seances': 'Gestion des Séances',
            '/ajouter-seance': 'Ajouter une Séance',
            '/modifier-seance': 'Modifier une Séance',
            '/progression': 'Progression des Enseignements',
            '/statistiques': 'Statistiques et Rapports'
        };

        for (const [path, title] of Object.entries(titles)) {
            if (location.pathname.startsWith(path)) {
                return title;
            }
        }

        return 'Gestion du Déroulement des Enseignements';
    };

    return (
        <nav className="navbar-custom d-flex justify-content-between align-items-center px-4">
            <div>
                <h5 style={{ margin: 0, color: '#333' }}>{getTitles()}</h5>
            </div>
            <div className="d-flex gap-3">
                <button className="btn btn-sm btn-light">
                    <i className="bi bi-bell"></i> Notifications
                </button>
            </div>
        </nav>
    );
};

export default Navbar;
