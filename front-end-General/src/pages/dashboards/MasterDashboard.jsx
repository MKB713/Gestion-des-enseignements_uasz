import React from 'react';

const MasterDashboard = () => {
    return (
        <div>
            <h1>Gestion des Masters</h1>
            <div className="alert-info">
                Note: Vous avez les droits de modification, mais la suppression est soumise à validation.
            </div>
            <div style={{ marginTop: '2rem' }}>
                <h3>Liste des Masters</h3>
                {/* Table placeholder */}
                <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', marginTop: '1rem' }}>
                    <thead>
                        <tr style={{ borderBottom: '1px solid #ddd' }}>
                            <th style={{ padding: '10px' }}>Formation</th>
                            <th style={{ padding: '10px' }}>Responsable</th>
                            <th style={{ padding: '10px' }}>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td style={{ padding: '10px' }}>Master 1 Génie Logiciel</td>
                            <td style={{ padding: '10px' }}>Dr. Diop</td>
                            <td style={{ padding: '10px' }}>
                                <button style={{ marginRight: '10px' }}>Modifier</button>
                                <button disabled title="Suppression restreinte">Supprimer</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default MasterDashboard;
