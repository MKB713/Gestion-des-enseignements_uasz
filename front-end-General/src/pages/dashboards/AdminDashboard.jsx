import React from 'react';
import {
    Users,
    GraduationCap,
    BookOpen,
    Building2,
    LayoutDashboard,
    Calendar,
    ClipboardList,
    Layers,
    FileText,
    Settings,
    Shield
} from 'lucide-react';
import './../dashboards/MasterDashboard.css'; // Reusing Master styles
import EvolutionChart from '../../components/EvolutionChart';

// Mock Data for Admin
const adminChartData = [
    { name: 'Oct', value: 20 },
    { name: 'Nov', value: 45 },
    { name: 'Déc', value: 60 },
    { name: 'Jan', value: 75 },
    { name: 'Fév', value: 85 },
    { name: 'Mar', value: 92 },
    { name: 'Avr', value: 98 },
    { name: 'Avr', value: 98 },
];

const adminDistributionData1 = [
    { name: 'Etudiants', value: 1250 },
    { name: 'Enseignants', value: 85 },
];

const adminDistributionData2 = [
    { name: 'Informatique', value: 450 },
    { name: 'Mathématiques', value: 300 },
    { name: 'Physique', value: 250 },
    { name: 'Chimie', value: 200 },
];
import DistributionChart from '../../components/DistributionChart';

const ActionCard = ({ icon: Icon, title, subtitle, btnText, btnClass = '', iconColor = '#064e3b' }) => (
    <div className="action-card">
        <div className="card-icon" style={{ color: iconColor }}>
            <Icon size={24} />
        </div>
        <h3>{title}</h3>
        <p>{subtitle}</p>
        <button className={`card-btn ${btnClass}`} style={{ backgroundColor: iconColor }}>{btnText}</button>
    </div>
);

const StatCard = ({ value, label, color }) => (
    <div className="stat-card" style={{ borderTopColor: color }}>
        <div className="stat-value" style={{ color: color }}>{value}</div>
        <div className="stat-label">{label}</div>
    </div>
);

const AdminDashboard = () => {
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
                <StatCard value="1,250" label="ETUDIANTS" color="#64748b" />
                <StatCard value="85" label="ENSEIGNANTS" color="#ca8a04" />
                <StatCard value="42" label="FORMATIONS" color="#1a5e34" />
                <StatCard value="12" label="DÉPARTEMENTS" color="#b91c1c" />
            </div>



            {/* Evolution Chart */}
            <EvolutionChart
                data={adminChartData}
                title="Taux de Couverture des Enseignements"
                subtitle="Progression globale des cours dispensés (Année académique)"
                color="#064e3b"
            />

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
                    />
                    <ActionCard
                        icon={Building2}
                        title="Départements"
                        subtitle="Structure de l'établissement"
                        btnText="Configurer"
                        iconColor="#b91c1c"
                    />
                    <ActionCard
                        icon={Building2}
                        title="Structures"
                        subtitle="UFR et Écoles"
                        btnText="Configurer"
                        iconColor="#b91c1c"
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
                    />
                    <ActionCard
                        icon={Layers}
                        title="Maquettes"
                        subtitle="Validation des maquettes"
                        btnText="Valider"
                        iconColor="#1a5e34"
                    />
                    <ActionCard
                        icon={BookOpen}
                        title="Pédagogie"
                        subtitle="Modules, UE, EC"
                        btnText="Superviser"
                        iconColor="#1a5e34"
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
                    />
                    <ActionCard
                        icon={Users}
                        title="Classes"
                        subtitle="Répartition des classes"
                        btnText="Gérer"
                        iconColor="#ca8a04"
                    />
                    <ActionCard
                        icon={ClipboardList}
                        title="Cahier de Texte"
                        subtitle="Suivi des enseignements"
                        btnText="Auditer"
                        iconColor="#ca8a04"
                    />
                </div>
            </div>
        </div >
    );
};

export default AdminDashboard;
