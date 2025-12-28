import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import AuthService from '../../services/AuthService';

const Sidebar = () => {
    const location = useLocation();
    const user = AuthService.getUser();

    const isActive = (path) => {
        return location.pathname === path ? 'active' : '';
    };

    return (
        <div className="sidebar">
            <div style={{ padding: '20px', borderBottom: '1px solid rgba(255,255,255,0.2)', marginBottom: '20px' }}>
                <h4 style={{ margin: 0, color: 'white' }}>
                    <i className="bi bi-book"></i> UASZ
                </h4>
                <small style={{ color: 'rgba(255,255,255,0.7)' }}>Déroulement Enseignements</small>
            </div>

            <nav className="nav flex-column">
                <Link 
                    to="/dashboard" 
                    className={`nav-link ${isActive('/dashboard')}`}
                >
                    <i className="bi bi-house"></i>
                    <span>Tableau de Bord</span>
                </Link>

                <div style={{ padding: '10px 20px', marginTop: '10px' }}>
                    <small style={{ color: 'rgba(255,255,255,0.6)', textTransform: 'uppercase', fontWeight: 'bold' }}>
                        Gestion
                    </small>
                </div>

                <Link 
                    to="/lst-seances" 
                    className={`nav-link ${isActive('/lst-seances')}`}
                >
                    <i className="bi bi-calendar-event"></i>
                    <span>Séances</span>
                </Link>

                <Link 
                    to="/progression" 
                    className={`nav-link ${isActive('/progression')}`}
                >
                    <i className="bi bi-bar-chart"></i>
                    <span>Progression</span>
                </Link>

                <Link 
                    to="/statistiques" 
                    className={`nav-link ${isActive('/statistiques')}`}
                >
                    <i className="bi bi-pie-chart"></i>
                    <span>Statistiques</span>
                </Link>
            </nav>

            <div style={{ 
                position: 'absolute', 
                bottom: '20px', 
                left: 0, 
                right: 0, 
                padding: '0 20px',
                borderTop: '1px solid rgba(255,255,255,0.2)',
                paddingTop: '20px'
            }}>
                <p style={{ fontSize: '12px', color: 'rgba(255,255,255,0.7)', margin: '0 0 10px 0' }}>
                    Utilisateur: <strong>{user.username || 'Admin'}</strong>
                </p>
                <button 
                    className="btn btn-sm btn-outline-light w-100"
                    onClick={() => {
                        AuthService.logout();
                        window.location.href = '/login';
                    }}
                >
                    <i className="bi bi-box-arrow-right"></i> Déconnexion
                </button>
            </div>
        </div>
    );
};

export default Sidebar;
