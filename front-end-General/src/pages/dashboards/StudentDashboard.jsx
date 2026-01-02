import React from 'react';

const StudentDashboard = () => {
    return (
        <div>
            <h1>Mon Emploi du Temps</h1>
            <div className="card-placeholder">
                <p>Ici s'affichera l'emploi du temps de votre formation.</p>
                {/* Placeholder for Schedule Component */}
                <div style={{ padding: '2rem', background: 'white', marginTop: '1rem', borderRadius: '8px' }}>
                    <h3>Emploi du temps - Semaine en cours</h3>
                    <p>Lundi 08:00 - Algorithmique</p>
                    <p>Mardi 10:00 - Base de données</p>
                </div>
            </div>
        </div>
    );
};

export default StudentDashboard;
