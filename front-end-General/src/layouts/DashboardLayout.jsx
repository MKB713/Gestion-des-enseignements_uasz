import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import { useAuth } from '../context/AuthContext';
import { LogOut } from 'lucide-react';
import './DashboardLayout.css';

const DashboardLayout = () => {
    const { user, logout } = useAuth();

    return (
        <div className="dashboard-container">
            <Sidebar user={user} logout={logout} />
            <div className="dashboard-main">
                <header className="dashboard-header">
                    <div className="header-title">
                        <h2>Portail Académique UASZ</h2>
                    </div>
                    <div className="header-actions">
                        <div className="header-profile">
                            <div className="user-info">
                                <span className="user-name">{user?.name}</span>
                                <span className="user-role">{user?.role?.replace(/_/g, ' ')}</span>
                            </div>
                            <div className="avatar">
                                {user?.name?.charAt(0)}
                            </div>
                        </div>
                        <button onClick={logout} className="btn-header-logout" title="Déconnexion">
                            <LogOut size={20} />
                        </button>
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
