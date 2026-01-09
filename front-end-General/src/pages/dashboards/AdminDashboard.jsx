import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Users,
    GraduationCap,
    BookOpen,
    Building2,
    Calendar,
    ClipboardList,
    Layers,
    Settings,
    Shield
} from 'lucide-react';
import './../dashboards/MasterDashboard.css';
import EvolutionChart from '../../components/EvolutionChart';
import { apiRequest, API_ENDPOINTS } from '../../config/api';

// Mock Data for Evolution Chart (Harder to calculate dynamically without backend aggregation)
const adminChartData = [
    { name: 'Oct', value: 20 },
    { name: 'Nov', value: 45 },
    { name: 'Déc', value: 60 },
    { name: 'Jan', value: 75 },
    { name: 'Fév', value: 85 },
    { name: 'Mar', value: 92 },
    { name: 'Avr', value: 98 },
];

const ActionCard = ({ icon: Icon, title, subtitle, btnText, btnClass = '', iconColor = '#064e3b', onClick }) => (
    <div className="action-card">
        <div className="card-icon" style={{ color: iconColor }}>
            <Icon size={24} />
        </div>
        <h3>{title}</h3>
        <p>{subtitle}</p>
        <button
            className={`card-btn ${btnClass}`}
            style={{ backgroundColor: iconColor }}
            onClick={onClick}
        >
            {btnText}
        </button>
    </div>
);

const StatCard = ({ value, label, color }) => (
    <div className="stat-card" style={{ borderTopColor: color }}>
        <div className="stat-value" style={{ color: color }}>{value}</div>
        <div className="stat-label">{label}</div>
    </div>
);

const AdminDashboard = () => {
    const navigate = useNavigate();
    const [stats, setStats] = useState({
        etudiants: 0,
        enseignants: 0,
        formations: 0,
        departements: 0
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const [usersRes, enseignantsRes, formationsRes, deptRes] = await Promise.all([
                    apiRequest(API_ENDPOINTS.USERS.LIST).catch(() => []),
                    apiRequest(API_ENDPOINTS.ENSEIGNANTS.LIST).catch(() => []),
                    apiRequest(API_ENDPOINTS.FORMATIONS.LIST).catch(() => []),
                    apiRequest(API_ENDPOINTS.DEPARTMENTS.LIST).catch(() => [])
                ]);

                // Filter students from users list or use dedicated student endpoint if available
                // Assuming 'role' attribute exists on user
                const studentsCount = Array.isArray(usersRes) ? usersRes.filter(u => u.role === 'ETUDIANT').length : 0;
                // If usersRes list is mixed, filtering is good. If USERS.LIST returns all.

                setStats({
                    etudiants: studentsCount,
                    enseignants: Array.isArray(enseignantsRes) ? enseignantsRes.length : 0,
                    formations: Array.isArray(formationsRes) ? formationsRes.length : 0,
                    departements: Array.isArray(deptRes) ? deptRes.length : 0
                });
            } catch (error) {
                console.error("Erreur chargement statistiques:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchStats();
    }, []);

    return (
        <div className="master-dashboard">
            <div className="dashboard-title-section">
                <h1 className="dashboard-title">
                    <Shield size={32} className="mr-2" />
                    Espace Administration
                </h1>
            </div>

            <div className="welcome-banner" style={{ backgroundColor: '#f0fdf4', borderLeftColor: '#064e3b' }}>
                <h3 style={{ color: '#064e3b' }}>Accès Administrateur Global</h3>
                <p>Vous avez un contrôle complet sur l'ensemble du système, des utilisateurs et des structures académiques.</p>
            </div>

            {/* Global Stats */}
            <div className="stats-grid">
                <StatCard value={loading ? "..." : stats.etudiants} label="ETUDIANTS" color="#64748b" />
                <StatCard value={loading ? "..." : stats.enseignants} label="ENSEIGNANTS" color="#ca8a04" />
                <StatCard value={loading ? "..." : stats.formations} label="FORMATIONS" color="#1a5e34" />
                <StatCard value={loading ? "..." : stats.departements} label="DÉPARTEMENTS" color="#b91c1c" />
            </div>

            {/* Administration Section */}
            <div className="dashboard-section">
                <div className="section-header">
                    <Settings size={20} color="#b91c1c" />
                    <h2 className="section-title" style={{ color: '#b91c1c' }}>Administration Système</h2>
                </div>
                <div className="cards-grid">
                    <ActionCard
                        icon={Users}
                        title="Utilisateurs"
                        subtitle="Gestion des comptes"
                        btnText="Administrer"
                        iconColor="#b91c1c"
                        onClick={() => navigate('/admin/users')}
                    />
                    <ActionCard
                        icon={Building2}
                        title="Départements"
                        subtitle="Structure de l'établissement"
                        btnText="Configurer"
                        iconColor="#b91c1c"
                        onClick={() => navigate('/admin/departments')}
                    />
                </div>
            </div>

            {/* Maquette Pédagogique Section */}
            <div className="dashboard-section">
                <div className="section-header">
                    <GraduationCap size={20} color="#1a5e34" />
                    <h2 className="section-title" style={{ color: '#1a5e34' }}>Maquette Pédagogique</h2>
                </div>
                <div className="cards-grid">
                    <ActionCard
                        icon={GraduationCap}
                        title="Formations"
                        subtitle="Gestion des formations"
                        btnText="Gérer"
                        iconColor="#1a5e34"
                        onClick={() => navigate('/admin/formations')}
                    />
                    <ActionCard
                        icon={Layers}
                        title="Maquettes"
                        subtitle="Validation des maquettes"
                        btnText="Valider"
                        iconColor="#1a5e34"
                        onClick={() => navigate('/admin/maquettes')}
                    />
                    <ActionCard
                        icon={BookOpen}
                        title="Pédagogie"
                        subtitle="Modules, UE, EC"
                        btnText="Superviser"
                        iconColor="#1a5e34"
                        onClick={() => navigate('/admin/ues')}
                    />
                </div>
            </div>

            {/* Organisation Section */}
            <div className="dashboard-section">
                <div className="section-header">
                    <Calendar size={20} color="#ca8a04" />
                    <h2 className="section-title" style={{ color: '#ca8a04' }}>Organisation & Planning</h2>
                </div>
                <div className="cards-grid">
                    <ActionCard
                        icon={Calendar}
                        title="Emplois du Temps"
                        subtitle="Vue globale des plannings"
                        btnText="Consulter"
                        iconColor="#ca8a04"
                        onClick={() => navigate('/admin/plannings')}
                    />
                    <ActionCard
                        icon={Users}
                        title="Classes"
                        subtitle="Répartition des classes"
                        btnText="Gérer"
                        iconColor="#ca8a04"
                        onClick={() => navigate('/admin/classes')}
                    />
                    <ActionCard
                        icon={ClipboardList}
                        title="Cahier de Texte"
                        subtitle="Suivi des enseignements"
                        btnText="Auditer"
                        iconColor="#ca8a04"
                        onClick={() => navigate('/admin/cahier-texte')}
                    />
                </div>
            </div>

            {/* Evolution Chart (Moved to bottom) */}
            <div style={{ marginTop: '2rem' }}>
                <EvolutionChart
                    data={adminChartData}
                    title="Taux de Couverture des Enseignements"
                    subtitle="Progression globale des cours dispensés (Année académique)"
                    color="#064e3b"
                />
            </div>

        </div>
    );
};

export default AdminDashboard;
