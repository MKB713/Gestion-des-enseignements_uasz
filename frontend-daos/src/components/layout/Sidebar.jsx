import React, { useState } from 'react';
import { Link, useLocation } from "react-router-dom";

const Sidebar = () => {
    const location = useLocation();

    // État pour gérer les menus déroulants
    const [menus, setMenus] = useState({
        enseignement: true,  
        structure: true,
        pedagogie: true,
        deroulement: false

    });

    const toggleMenu = (key) => {
        setMenus({ ...menus, [key]: !menus[key] });
    };

    // Fonction pour vérifier si un lien est actif
    const isActive = (path) => location.pathname === path ? 'active' : '';

    // Fonction pour vérifier si un menu parent doit être marqué actif
    const isParentActive = (paths) => paths.includes(location.pathname) ? 'active text-white bg-white bg-opacity-10' : '';

    return (
        <nav className="sidebar d-flex flex-column">
            {/* --- MARQUE / LOGO --- */}
            <div className="sidebar-brand text-center pt-3">
                <img
                    src="/images/logo-uasz.jpg"
                    alt="Logo UASZ"
                    className="university-logo mx-auto d-block"
                    onError={(e) => {
                        e.target.style.display = 'none';
                        e.target.nextSibling.style.display = 'flex';
                    }}
                />

                {/* Secours (Fallback) si l'image ne charge pas */}
                <div className="mx-auto bg-white align-items-center justify-content-center rounded-circle mb-2"
                     style={{width: '80px', height: '80px', border: '3px solid rgba(255,255,255,0.2)', display: 'none'}}>
                    <i className="bi bi-mortarboard-fill text-success fs-1"></i>
                </div>

                <h5 className="fw-bold text-white mb-0 mt-2">UASZ</h5>
                <small className="text-white-50">Ziguinchor</small>
            </div>

            {/* --- MENU --- */}
            <ul className="nav flex-column px-2 mt-4 w-100">

                {/* Tableau de bord */}
                <li className="nav-item mb-1">
                    <Link to="/dashboard" className={`nav-link ${isActive('/dashboard')}`}>
                        <i className="bi bi-speedometer2 me-2"></i> Tableau de bord
                    </Link>
                </li>

                {/* ==================== ENSEIGNEMENT (NOUVEAU) ==================== */}
                <li className="nav-item mb-1">
                    <a className={`nav-link d-flex justify-content-between align-items-center ${isParentActive([
                        '/lst-enseignants',
                        '/ajouter-enseignant',
                        '/modifier-enseignant',
                        '/lst-responsables',
                        '/ajouter-responsable',
                        '/modifier-responsable',
                        '/lst-coordinateurs',
                        '/ajouter-coordinateur',
                        '/modifier-coordinateur'
                    ])}`}
                       onClick={() => toggleMenu('enseignement')}
                       style={{cursor: 'pointer', color: 'rgba(255,255,255,0.8)'}}>
                        <span><i className="bi bi-person-video3 me-2"></i> Enseignement</span>
                        <i className={`bi bi-chevron-${menus.enseignement ? 'down' : 'right'} small`} style={{fontSize: '0.7rem'}}></i>
                    </a>

                    {/* Sous-menu Enseignement */}
                    <div className={`collapse ${menus.enseignement ? 'show' : ''}`}>
                        <ul className="nav flex-column ms-3 ps-2 border-start border-white-50 mt-1">
                            <li className="nav-item">
                                <Link to="/lst-enseignants" className={`nav-link py-1 small ${location.pathname === '/lst-enseignants' ? 'text-warning fw-bold' : 'text-white-50'}`}>
                                    Enseignants
                                </Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/lst-responsables" className={`nav-link py-1 small ${location.pathname === '/lst-responsables' ? 'text-warning fw-bold' : 'text-white-50'}`}>
                                    Responsables
                                </Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/lst-coordinateurs" className={`nav-link py-1 small ${location.pathname === '/lst-coordinateurs' ? 'text-warning fw-bold' : 'text-white-50'}`}>
                                    Coordinateurs
                                </Link>
                            </li>
                        </ul>
                    </div>
                </li>

                {/* --- STRUCTURE (Dropdown) --- */}
                <li className="nav-item mb-1">
                    <a className={`nav-link d-flex justify-content-between align-items-center ${isParentActive(['/lst-filieres', '/niveaux', '/lst-formations', '/lst-classes'])}`}
                       onClick={() => toggleMenu('structure')}
                       style={{cursor: 'pointer', color: 'rgba(255,255,255,0.8)'}}>
                        <span><i className="bi bi-building me-2"></i> Structure</span>
                        <i className={`bi bi-chevron-${menus.structure ? 'down' : 'right'} small`} style={{fontSize: '0.7rem'}}></i>
                    </a>

                    {/* Sous-menu Structure */}
                    <div className={`collapse ${menus.structure ? 'show' : ''}`}>
                        <ul className="nav flex-column ms-3 ps-2 border-start border-white-50 mt-1">
                            <li className="nav-item">
                                <Link to="/lst-filieres" className={`nav-link py-1 small ${location.pathname === '/lst-filieres' ? 'text-warning fw-bold' : 'text-white-50'}`}>
                                    Filières
                                </Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/niveaux" className={`nav-link py-1 small ${location.pathname === '/niveaux' ? 'text-warning fw-bold' : 'text-white-50'}`}>
                                    Niveaux
                                </Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/lst-formations" className={`nav-link py-1 small ${location.pathname === '/lst-formations' ? 'text-warning fw-bold' : 'text-white-50'}`}>
                                    Formations
                                </Link>
                            </li>
                        </ul>
                    </div>
                </li>

                {/* --- PÉDAGOGIE (Dropdown) --- */}
                <li className="nav-item mb-1">
                    <a className={`nav-link d-flex justify-content-between align-items-center ${isParentActive(['/lst-maquettes', '/lst-modules', '/lst-ues', '/lst-ecs'])}`}
                       onClick={() => toggleMenu('pedagogie')}
                       style={{cursor: 'pointer', color: 'rgba(255,255,255,0.8)'}}>
                        <span><i className="bi bi-journal-bookmark me-2"></i> Pédagogie</span>
                        <i className={`bi bi-chevron-${menus.pedagogie ? 'down' : 'right'} small`} style={{fontSize: '0.7rem'}}></i>
                    </a>

                    {/* Sous-menu Pédagogie */}
                    <div className={`collapse ${menus.pedagogie ? 'show' : ''}`}>
                        <ul className="nav flex-column ms-3 ps-2 border-start border-white-50 mt-1">
                            <li className="nav-item">
                                <Link to="/lst-modules" className={`nav-link py-1 small ${location.pathname === '/lst-modules' ? 'text-warning fw-bold' : 'text-white-50'}`}>
                                    Modules
                                </Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/lst-ues" className={`nav-link py-1 small ${location.pathname === '/lst-ues' ? 'text-warning fw-bold' : 'text-white-50'}`}>
                                    Unités d'Ens. (UE)
                                </Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/lst-ecs" className={`nav-link py-1 small ${location.pathname === '/lst-ecs' ? 'text-warning fw-bold' : 'text-white-50'}`}>
                                    Éléments (EC)
                                </Link>
                            </li>
                        </ul>
                    </div>
                </li>

                {/* Emploi du Temps */}
                <li className="nav-item mb-1">
                    <Link to="/emploi-du-temps" className={`nav-link ${isActive('/emploi-du-temps')}`}>
                        <i className="bi bi-calendar-week me-2"></i> Emploi du Temps
                    </Link>
                </li>

                {/* Recherche Séances */}
                <li className="nav-item mb-1">
                    <Link to="/seances/search" className={`nav-link ${isActive('/seances/search')}`}>
                        <i className="bi bi-search me-2"></i> Rechercher Séances
                    </Link>
                </li>
                {/* --- DÉROULEMENT ENSEIGNEMENT (Dropdown) --- */}
                </li>
                <li className="nav-item mb-1">
                    <a className={`nav-link d-flex justify-content-between align-items-center ${isParentActive(['/lst-seances', '/progression', '/statistiques'])}`}
                       onClick={() => toggleMenu('deroulement')}
                       style={{cursor: 'pointer', color: 'rgba(255,255,255,0.8)'}}>
                        <span><i className="bi bi-bar-chart me-2"></i> Déroulement</span>
                        <i className={`bi bi-chevron-${menus.deroulement ? 'down' : 'right'} small`} style={{fontSize: '0.7rem'}}></i>
                    </a>

                    {/* Sous-menu Déroulement */}
                    <div className={`collapse ${menus.deroulement ? 'show' : ''}`}>
                        <ul className="nav flex-column ms-3 ps-2 border-start border-white-50 mt-1">
                            <li className="nav-item">
                                <Link to="/lst-seances" className={`nav-link py-1 small ${location.pathname === '/lst-seances' ? 'text-warning fw-bold' : 'text-white-50'}`}>Séances</Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/progression" className={`nav-link py-1 small ${location.pathname === '/progression' ? 'text-warning fw-bold' : 'text-white-50'}`}>Progression</Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/statistiques" className={`nav-link py-1 small ${location.pathname === '/statistiques' ? 'text-warning fw-bold' : 'text-white-50'}`}>Statistiques</Link>
                            </li>
                        </ul>
                    </div>
                </li>

                {/* --- FOOTER UTILISATEUR (Dropup) --- */}
                <div className="mt-auto w-100 px-2 pb-3 pt-4">
                    <div className="dropup w-100">
                        <a href="#" className="d-flex align-items-center text-white text-decoration-none dropdown-toggle"
                           id="dropdownUser" data-bs-toggle="dropdown" aria-expanded="false">
                            <div className="rounded-circle bg-warning text-dark fw-bold d-flex justify-content-center align-items-center me-2"
                                 style={{width: '32px', height: '32px', fontSize: '0.8rem'}}>
                                AD
                            </div>
                            <div className="d-flex flex-column text-start me-auto" style={{lineHeight: '1.1'}}>
                                <span className="small fw-bold">Administrateur</span>
                                <span className="text-white-50" style={{fontSize: '0.7rem'}}>En ligne</span>
                            </div>
                        </a>
                        <ul className="dropdown-menu dropdown-menu-dark text-small shadow mb-2"
                            style={{backgroundColor: '#144a29', border: '1px solid rgba(255,255,255,0.1)'}}>
                            <li><Link className="dropdown-item" to="/profil"><i className="bi bi-person-circle me-2"></i> Mon Profil</Link></li>
                            <li><Link className="dropdown-item" to="/parametres"><i className="bi bi-gear-fill me-2"></i> Paramètres</Link></li>
                            <li><hr className="dropdown-divider bg-white bg-opacity-10"/></li>
                            <li><a className="dropdown-item text-warning" href="/login"><i className="bi bi-box-arrow-left me-2"></i> Déconnexion</a></li>
                        </ul>
                    </div>
                </div>
            </ul>
        </nav>
    );
};

export default Sidebar;