import React, { useState } from 'react';
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
    School,
    Building2,
    GraduationCap,
    BookMarked,
    Layers,
    UserCheck,
    ClipboardList,
    MapPin,
    ChevronDown,
    ChevronRight,
    List
} from 'lucide-react';

const Sidebar = ({ user, logout }) => {
    const location = useLocation();
    const [openSubmenus, setOpenSubmenus] = useState({});

    const toggleSubmenu = (label) => {
        setOpenSubmenus(prev => ({
            ...prev,
            [label]: !prev[label]
        }));
    };

    const getLinks = (role) => {
        switch (role) {
            case 'ETUDIANT':
                return [
                    { path: '/student/dashboard', label: 'Mon Emploi du Temps', icon: Calendar }
                ];
            case 'ENSEIGNANT':
                return [
                    { path: '/teacher/dashboard', label: 'Tableau de bord', icon: LayoutDashboard },
                    { isHeader: true, label: 'MAQUETTE PÉDAGOGIQUE' },
                    { path: '/teacher/classes', label: 'Classes', icon: Users },
                    { path: '/teacher/maquettes', label: 'Maquettes', icon: GitBranch },
                    { path: '/teacher/pedagogie', label: 'Pédagogie', icon: BookOpen },
                    { isHeader: true, label: 'PLANNING' },
                    { path: '/teacher/emploi-temps', label: 'Emploi du Temps', icon: Calendar },
                    { path: '/teacher/cahier-texte', label: 'Cahier de Texte', icon: ClipboardList }
                ];
            case 'COORDONATEUR_DES_LICENCES':
                return [
                    { path: '/coordinator/dashboard', label: 'Tableau de bord', icon: LayoutDashboard },
                    { isHeader: true, label: 'MAQUETTE PÉDAGOGIQUE' },
                    { path: '/coordinator/formations', label: 'Formations', icon: GraduationCap },
                    { path: '/coordinator/filieres', label: 'Filières', icon: GitBranch },
                    { path: '/coordinator/classes', label: 'Classes', icon: Users },
                    { path: '/coordinator/structures', label: 'Structures', icon: Building2 },
                    { path: '/coordinator/maquettes', label: 'Maquettes', icon: FileText },
                    {
                        label: 'Pédagogie', icon: BookOpen,
                        submenu: [
                            { path: '/coordinator/modules', label: 'Modules', icon: BookMarked },
                            { path: '/coordinator/ues', label: 'Unités d\'Enseignement (UE)', icon: BookOpen },
                            { path: '/coordinator/ecs', label: 'Éléments Constitutifs (EC)', icon: FileText }
                        ]
                    },
                    { path: '/coordinator/enseignants', label: 'Enseignants', icon: UserCheck },
                    { isHeader: true, label: 'PLANNING' },
                    { path: '/coordinator/emploi-temps', label: 'Emploi du Temps', icon: Calendar },
                    { path: '/coordinator/cahier-texte', label: 'Cahier de Texte', icon: ClipboardList }
                ];
            case 'RESPONSABLE_MASTER':
                return [
                    { path: '/master/dashboard', label: 'Tableau de bord', icon: LayoutDashboard },
                    { isHeader: true, label: 'MAQUETTE PÉDAGOGIQUE' },
                    { path: '/master/formations', label: 'Formations', icon: GraduationCap },
                    { path: '/master/filieres', label: 'Filières', icon: GitBranch },
                    { path: '/master/classes', label: 'Classes', icon: Users },
                    {
                        label: 'Structure', icon: Building2,
                        submenu: [
                            { path: '/master/departements', label: 'Départements' },
                            { path: '/master/etablissements', label: 'Établissements' }
                        ]
                    },
                    { path: '/master/niveaux', label: 'Niveaux', icon: Layers },
                    { path: '/master/maquettes', label: 'Maquettes', icon: FileText },
                    {
                        label: 'Pédagogie', icon: BookOpen,
                        submenu: [
                            { path: '/master/modules', label: 'Modules' },
                            { path: '/master/ues', label: 'Unités d\'Enseignement (UE)' },
                            { path: '/master/ecs', label: 'Éléments Constitutifs (EC)' }
                        ]
                    },
                    { path: '/master/enseignants', label: 'Enseignants', icon: UserCheck },
                    { isHeader: true, label: 'PLANNING' },
                    { path: '/master/emploi-temps', label: 'Emploi du Temps', icon: Calendar },
                    { path: '/master/cahier-texte', label: 'Cahier de Texte', icon: ClipboardList }
                ];
            case 'ADMIN':
            case 'CHEF_DE_DEPARTEMENT':
                return [
                    { path: '/admin/dashboard', label: 'Vue d\'Ensemble', icon: LayoutDashboard },

                    { isHeader: true, label: 'ADMINISTRATION' },
                    { path: '/admin/users', label: 'Utilisateurs', icon: Users },
                    { path: '/admin/departments', label: 'Départements', icon: Building2 },
                    { path: '/admin/structures', label: 'Structures', icon: Building2 },
                    { path: '/admin/formations', label: 'Formations', icon: GraduationCap },
                    { path: '/admin/filieres', label: 'Filières', icon: GitBranch },
                    { path: '/admin/niveaux', label: 'Niveaux', icon: Layers },

                    {
                        label: 'Paramètres', icon: Settings,
                        submenu: [
                            { path: '/admin/ues', label: 'Unités d\'Enseignement (UE)', icon: BookOpen },
                            { path: '/admin/ecs', label: 'Éléments Constitutifs (EC)', icon: FileText },
                            { path: '/admin/modules', label: 'Modules', icon: BookMarked },
                            { path: '/admin/maquettes', label: 'Maquettes', icon: List },
                            { path: '/admin/maquette-details', label: 'Détails Maquette', icon: FileText },
                            { path: '/admin/classes', label: 'Classes', icon: Users },
                        ]
                    },
                    {
                        label: 'Infrastructure', icon: Building2,
                        submenu: [
                            { path: '/admin/batiments', label: 'Bâtiments', icon: MapPin },
                            { path: '/admin/salles', label: 'Salles', icon: School },
                        ]
                    },
                    {
                        label: 'Planning', icon: Calendar,
                        submenu: [
                            { path: '/admin/plannings', label: 'Emploi du Temps', icon: Calendar },
                            { path: '/admin/cahier-texte', label: 'Cahier de Texte', icon: ClipboardList },
                        ]
                    },
                    {
                        label: 'Scolarité', icon: GraduationCap,
                        submenu: [
                            { path: '/admin/enseignants', label: 'Enseignants', icon: UserCheck },
                            { path: '/admin/repartition', label: 'Répartition', icon: ClipboardList },
                            { path: '/admin/etudiants', label: 'Étudiants', icon: Users },
                            { path: '/admin/deroulement-classes', label: 'Classes (Année)', icon: School },
                        ]
                    }
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
                    {links.map((link, index) => {
                        if (link.isHeader) {
                            return (
                                <li key={`header-${index}`} className="sidebar-section-header">
                                    <span>{link.label}</span>
                                </li>
                            );
                        }

                        if (link.submenu) {
                            const isOpen = openSubmenus[link.label];
                            const Icon = link.icon;
                            return (
                                <li key={link.label || index}>
                                    <div
                                        className={`nav-link submenu-toggle ${isOpen ? 'active' : ''}`}
                                        onClick={() => toggleSubmenu(link.label)}
                                        style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}
                                    >
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                            {Icon && <Icon size={20} />}
                                            <span>{link.label}</span>
                                        </div>
                                        {isOpen ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
                                    </div>
                                    {isOpen && (
                                        <ul className="submenu" style={{ paddingLeft: '20px', listStyle: 'none', background: 'rgba(0,0,0,0.05)' }}>
                                            {link.submenu.map((subItem, subIndex) => {
                                                const SubIcon = subItem.icon;
                                                return (
                                                    <li key={subItem.path || subIndex}>
                                                        <Link
                                                            to={subItem.path}
                                                            className={`nav-link ${location.pathname === subItem.path ? 'active' : ''}`}
                                                            style={{ fontSize: '0.9em' }}
                                                        >
                                                            {SubIcon && <SubIcon size={18} />}
                                                            <span>{subItem.label}</span>
                                                        </Link>
                                                    </li>
                                                );
                                            })}
                                        </ul>
                                    )}
                                </li>
                            );
                        }

                        const Icon = link.icon;
                        return (
                            <li key={link.path || index}>
                                <Link
                                    to={link.path}
                                    className={`nav-link ${location.pathname === link.path ? 'active' : ''}`}
                                >
                                    {Icon && <Icon size={20} />}
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
