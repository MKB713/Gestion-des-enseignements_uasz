import React from 'react';

const CoordinatorDashboard = () => {
    return (
        <div>
            <h1>Gestion des Licences</h1>
            <div className="alert-info">
                Note: Vous avez les droits de modification sur les licences.
            </div>
            {/* Similar structure to Master but for Licences */}
            <div style={{ marginTop: '2rem' }}>
                <h3>Liste des Licences</h3>
                <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', marginTop: '1rem' }}>
                    <thead>
                        <tr style={{ borderBottom: '1px solid #ddd' }}>
                            <th style={{ padding: '10px' }}>Filière</th>
                            <th style={{ padding: '10px' }}>Niveau</th>
                            <th style={{ padding: '10px' }}>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td style={{ padding: '10px' }}>Informatique</td>
                            <td style={{ padding: '10px' }}>Licence 1</td>
                            <td style={{ padding: '10px' }}>
                                <button style={{ marginRight: '10px' }}>Modifier</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default CoordinatorDashboard;
