import React from 'react';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import { Outlet } from 'react-router-dom';

const MainLayout = () => {
    return (
        <div className="d-flex">
            {/* 1. Sidebar Fixe (Largeur définie dans le CSS ou inline: 260px) */}
            <div style={{ width: '260px', flexShrink: 0 }}>
                <Sidebar />
            </div>

            {/* 2. Zone Principale */}
            <div style={{ flex: 1, backgroundColor: '#f8f9fa', minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
                <Navbar />

                {/* Zone de contenu dynamique (les pages s'afficheront ici) */}
                <div className="p-4" style={{ marginTop: '70px', overflowY: 'auto' }}>
                    <Outlet />
                </div>
            </div>
        </div>
    );
};

export default MainLayout;