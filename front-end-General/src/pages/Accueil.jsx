import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import {
    LogIn,
    Info,
    Calendar,
    BookOpen,
    GitBranch,
    Users,
    Building2,
    ShieldCheck,
    IdCard,
    Presentation,
    UserCog,
    MapPin,
    Mail,
    Phone,
    ArrowRight
} from 'lucide-react';
import './Accueil.css';

const Accueil = () => {
    // Refs for animations
    const statsSectionRef = useRef(null);
    const countersRef = useRef([]);

    // Smooth scroll handler
    const handleSmoothScroll = (e, targetId) => {
        e.preventDefault();
        const targetElement = document.getElementById(targetId);
        if (targetElement) {
            targetElement.scrollIntoView({ behavior: 'smooth' });
        }
    };

    // Counter Animation Logic
    useEffect(() => {
        const animateCounter = (element) => {
            const target = parseInt(element.getAttribute('data-target'));
            const duration = 2000;
            const increment = target / (duration / 16);
            let current = 0;

            const timer = setInterval(() => {
                current += increment;
                if (current >= target) {
                    element.textContent = target + '+';
                    clearInterval(timer);
                } else {
                    element.textContent = Math.floor(current);
                }
            }, 16);
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const counter = entry.target;
                    if (!counter.classList.contains('animated')) {
                        animateCounter(counter);
                        counter.classList.add('animated');
                    }
                }
            });
        }, { threshold: 0.5 });

        // Observe all counters
        if (statsSectionRef.current) {
            const counters = statsSectionRef.current.querySelectorAll('.stat-number');
            counters.forEach(counter => observer.observe(counter));
        }

        return () => observer.disconnect();
    }, []);

    return (
        <div className="accueil-body">
            {/* Hero Section */}
            {/* Hero Banner (Image) */}
            <section className="hero-banner" style={{ backgroundImage: "url('/images/portail.png')" }}>
                <div className="cta-buttons-overlay">
                    <Link to="/login" className="btn-primary-custom">
                        <LogIn size={20} />
                        Accéder au Portail
                    </Link>
                    <a href="#features" onClick={(e) => handleSmoothScroll(e, 'features')} className="btn-secondary-custom">
                        <Info size={20} />
                        En savoir plus
                    </a>
                </div>
            </section>

            {/* Hero Text Content (Below Image) */}
            <section className="hero-text-section">
                <div className="hero-content">
                    <div className="hero-logo">
                        <img src="/images/logo.jpg" alt="Logo UASZ" />
                    </div>
                    <h1 className="hero-title">Portail Pédagogique</h1>
                    <h2 className="hero-subtitle">Université Assane Seck de Ziguinchor</h2>
                    <p className="hero-description">
                        Bienvenue sur votre plateforme numérique de gestion pédagogique.
                        Accédez à vos espaces de travail, consultez vos emplois du temps et gérez
                        votre parcours académique en toute simplicité.
                    </p>
                </div>
            </section>

            {/* Features Section */}
            <section id="features" className="features-section">
                <div className="container-custom">
                    <div className="section-title">
                        <h2>Fonctionnalités du Portail</h2>
                        <p>Un système complet pour la gestion académique</p>
                    </div>
                    <div className="grid-3">
                        <div className="feature-card">
                            <div className="feature-icon">
                                <Calendar size={32} />
                            </div>
                            <h4>Emplois du Temps</h4>
                            <p>Consultez et gérez les emplois du temps en temps réel. Accès personnalisé selon votre rôle (étudiant, enseignant, administrateur).</p>
                        </div>
                        <div className="feature-card">
                            <div className="feature-icon">
                                <BookOpen size={32} />
                            </div>
                            <h4>Cahier de Texte</h4>
                            <p>Suivez les contenus pédagogiques, les travaux dirigés et les notes de cours de manière organisée et structurée.</p>
                        </div>
                        <div className="feature-card">
                            <div className="feature-icon">
                                <GitBranch size={32} />
                            </div>
                            <h4>Maquettes Pédagogiques</h4>
                            <p>Gérez les formations, modules, unités d'enseignement (UE) et éléments constitutifs (EC) de manière hiérarchique.</p>
                        </div>
                        <div className="feature-card">
                            <div className="feature-icon">
                                <Users size={32} />
                            </div>
                            <h4>Gestion des Acteurs</h4>
                            <p>Administration complète des enseignants, étudiants, responsables et coordinateurs pédagogiques.</p>
                        </div>
                        <div className="feature-card">
                            <div className="feature-icon">
                                <Building2 size={32} />
                            </div>
                            <h4>Structure Académique</h4>
                            <p>Organisez les filières, niveaux, formations et classes selon l'architecture de votre établissement.</p>
                        </div>
                        <div className="feature-card">
                            <div className="feature-icon">
                                <ShieldCheck size={32} />
                            </div>
                            <h4>Sécurité & Rôles</h4>
                            <p>Système de gestion des droits d'accès basé sur les rôles (RBAC) pour une sécurité optimale des données.</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Stats Section */}
            <section className="stats-section" ref={statsSectionRef}>
                <div className="container-custom">
                    <div className="grid-4">
                        <div className="stat-card">
                            <div className="stat-number" data-target="1200">0</div>
                            <div className="stat-label">Étudiants</div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-number" data-target="150">0</div>
                            <div className="stat-label">Enseignants</div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-number" data-target="45">0</div>
                            <div className="stat-label">Formations</div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-number" data-target="25">0</div>
                            <div className="stat-label">Filières</div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Roles Section */}
            <section className="roles-section">
                <div className="container-custom">
                    <div className="section-title">
                        <h2>Accès par Profil</h2>
                        <p>Chaque utilisateur dispose d'un accès personnalisé selon son rôle</p>
                    </div>
                    <div className="grid-4">
                        <div className="role-card">
                            <div className="role-icon">
                                <IdCard size={40} />
                            </div>
                            <h5>Étudiant</h5>
                            <p>Consultation de l'emploi du temps de votre formation</p>
                        </div>
                        <div className="role-card">
                            <div className="role-icon">
                                <Presentation size={40} />
                            </div>
                            <h5>Enseignant</h5>
                            <p>Gestion du cahier de texte et consultation des EDT</p>
                        </div>
                        <div className="role-card">
                            <div className="role-icon">
                                <UserCog size={40} />
                            </div>
                            <h5>Coordinateur</h5>
                            <p>Gestion complète des licences et formations</p>
                        </div>
                        <div className="role-card">
                            <div className="role-icon">
                                <ShieldCheck size={40} />
                            </div>
                            <h5>Administrateur</h5>
                            <p>Accès complet à toutes les fonctionnalités</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer>
                <div className="container-custom">
                    <div className="footer-content">
                        <div>
                            <h5 className="fw-bold mb-3">Université Assane Seck</h5>
                            <p className="opacity-75">
                                Institution d'excellence dédiée à la formation et à la recherche scientifique.
                            </p>
                        </div>
                        <div>
                            <h5 className="fw-bold mb-3">Liens Rapides</h5>
                            <ul className="footer-links">
                                <li><Link to="/login"><ArrowRight size={16} /> Connexion</Link></li>
                                <li><a href="#features" onClick={(e) => handleSmoothScroll(e, 'features')}><ArrowRight size={16} /> Fonctionnalités</a></li>
                                <li><a href="#"><ArrowRight size={16} /> Documentation</a></li>
                                <li><a href="#"><ArrowRight size={16} /> Support</a></li>
                            </ul>
                        </div>
                        <div>
                            <h5 className="fw-bold mb-3">Contact</h5>
                            <ul className="footer-links">
                                <li><MapPin size={16} /> Ziguinchor, Sénégal</li>
                                <li><Mail size={16} /> contact@uasz.sn</li>
                                <li><Phone size={16} /> +221 33 991 68 09</li>
                            </ul>
                        </div>
                    </div>
                    <div className="footer-bottom">
                        <p className="mb-0">&copy; 2024 Université Assane Seck de Ziguinchor. Tous droits réservés.</p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Accueil;
