import React from 'react';

const TeacherDashboard = () => {
    return (
        <div>
            <h1>Tableau de Bord Enseignant</h1>
            <div className="dashboard-grid">
                <div className="card">
                    <h3>Mes Classes</h3>
                    <p>L1 Informatique, L2 Mathématiques</p>
                </div>
                <div className="card">
                    <h3>Cahier de Texte</h3>
                    <p>Dernière modification: Hier à 14h</p>
                    <button className="btn-primary-custom" style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}>Modifier</button>
                </div>
                <div className="card">
                    <h3>Planning</h3>
                    <p>Prochain cours: Demain 08:00</p>
                </div>
            </div>
        </div>
    );
};

export default TeacherDashboard;
