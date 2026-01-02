import React from 'react';

const AdminDashboard = () => {
    return (
        <div>
            <h1>Administration Système</h1>
            <div className="dashboard-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem', marginTop: '2rem' }}>
                <div className="card" style={{ padding: '1.5rem', background: 'white', borderRadius: '12px', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)' }}>
                    <h3>Utilisateurs</h3>
                    <p>Gérer les comptes et rôles</p>
                </div>
                <div className="card" style={{ padding: '1.5rem', background: 'white', borderRadius: '12px', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)' }}>
                    <h3>Départements</h3>
                    <p>Configuration structurelle</p>
                </div>
                <div className="card" style={{ padding: '1.5rem', background: 'white', borderRadius: '12px', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)' }}>
                    <h3>Maquettes</h3>
                    <p>Validation finale</p>
                </div>
                <div className="card" style={{ padding: '1.5rem', background: 'white', borderRadius: '12px', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)' }}>
                    <h3>Plannings</h3>
                    <p>Supervision globale</p>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
