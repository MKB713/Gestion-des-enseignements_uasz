import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import { useAuth } from '../context/AuthContext';
import './DashboardLayout.css';

const DashboardLayout = () => {
    const { user, logout } = useAuth();

    return (
        <div className="dashboard-container">
            <Sidebar user={user} logout={logout} />
            <div className="dashboard-main">
                <header className="dashboard-header">
                    <div className="header-title">
                        <h2>Espace {user?.role.replace('_', ' ')}</h2>
                    </div>
                    <div className="header-profile">
                        <span>{user?.name}</span>
                        <div className="avatar">
                            {user?.name.charAt(0)}
                        </div>
                    </div>
                </header>
                <main className="dashboard-content">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default DashboardLayout;
