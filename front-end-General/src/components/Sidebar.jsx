import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
    LayoutDashboard,
    Calendar,
    BookOpen,
    Users,
    Settings,
    LogOut,
    GitBranch,
    FileText,
    School
} from 'lucide-react';

const Sidebar = ({ user, logout }) => {
    const location = useLocation();

    const getLinks = (role) => {
        switch (role) {
            case 'ETUDIANT':
                return [
                    { path: '/student/dashboard', label: 'Mon Emploi du Temps', icon: Calendar }
                ];
            case 'ENSEIGNANT':
                return [
                    { path: '/teacher/dashboard', label: 'Vue Globale', icon: LayoutDashboard },
                    { path: '/teacher/classes', label: 'Mes Classes', icon: Users },
                    { path: '/teacher/maquettes', label: 'Maquettes', icon: GitBranch },
                    { path: '/teacher/pedagogie', label: 'Pédagogie', icon: BookOpen }, // Modules, UEs, ECs
                    { path: '/teacher/planning', label: 'Planning & Cahier de Texte', icon: Calendar }
                ];
            case 'COORDONATEUR_DES_LICENCES':
                return [
                    { path: '/coordinator/dashboard', label: 'Tableau de bord', icon: LayoutDashboard },
                    { path: '/coordinator/licences', label: 'Gestion Licences', icon: School }
                ];
            case 'RESPONSABLE_MASTER':
                return [
                    { path: '/master/dashboard', label: 'Tableau de bord', icon: LayoutDashboard },
                    { path: '/master/masters', label: 'Gestion Masters', icon: School }
                ];
            case 'ADMIN':
            case 'CHEF_DE_DEPARTEMENT':
                return [
                    { path: '/admin/dashboard', label: 'Vue d\'Ensemble', icon: LayoutDashboard },
                    { path: '/admin/users', label: 'Utilisateurs', icon: Users },
                    { path: '/admin/maquettes', label: 'Maquettes', icon: GitBranch },
                    { path: '/admin/plannings', label: 'Plannings', icon: Calendar },
                    { path: '/admin/departments', label: 'Départements', icon: Building2 }
                ];
            default:
                return [];
        }
    };

    const links = getLinks(user?.role);

    return (
        <aside className="sidebar">
            <div className="sidebar-brand">
                <img src="/images/logo.jpg" alt="UASZ" />
                <span className="brand-text">UASZ</span>
            </div>

            <nav className="sidebar-nav">
                <ul>
                    {links.map((link) => {
                        const Icon = link.icon;
                        return (
                            <li key={link.path}>
                                <Link
                                    to={link.path}
                                    className={`nav-link ${location.pathname === link.path ? 'active' : ''}`}
                                >
                                    <Icon size={20} />
                                    <span>{link.label}</span>
                                </Link>
                            </li>
                        );
                    })}
                </ul>
            </nav>

            <div className="sidebar-footer">
                <button onClick={logout} className="nav-link btn-logout">
                    <LogOut size={20} />
                    <span>Déconnexion</span>
                </button>
            </div>
        </aside>
    );
};

export default Sidebar;
import { Building2 } from 'lucide-react'; 
