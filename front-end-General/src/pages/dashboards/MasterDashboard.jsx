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
    FileText
} from 'lucide-react';
import './MasterDashboard.css';
import EvolutionChart from '../../components/EvolutionChart';

// Mock Data for Master
const masterChartData = [
    { name: 'M1 S1', value: 40 },
    { name: 'M1 S2', value: 65 },
    { name: 'M2 S1', value: 50 },
    { name: 'M2 S2', value: 80 },
    { name: 'PFE', value: 20 },
    { name: 'PFE', value: 20 },
];

const masterDistributionData = [
    { name: 'Master 1', value: 45 },
    { name: 'Master 2', value: 35 },
];
import DistributionChart from '../../components/DistributionChart';

const StatCard = ({ value, label, color }) => (
    <div className="stat-card" style={{ borderTopColor: color }}>
        <div className="stat-value" style={{ color: color }}>{value}</div>
        <div className="stat-label">{label}</div>
    </div>
);

const ActionCard = ({ icon: Icon, title, subtitle, btnText, btnClass = '' }) => (
    <div className="action-card">
        <div className="card-icon">
            <Icon size={24} />
        </div>
        <h3>{title}</h3>
        <p>{subtitle}</p>
        <button className={`card-btn ${btnClass}`}>{btnText}</button>
    </div>
);

const MasterDashboard = () => {
    return (
        <div className="master-dashboard">
            <div className="dashboard-title-section">
                <h1 className="dashboard-title">
                    <Users size={32} className="mr-2" />
                    Espace Responsable Master
                </h1>
            </div>

            <div className="welcome-banner">
                <h3>Accès Complet - Niveau Master</h3>
                <p>Vous disposez des permissions complètes pour créer, modifier et supprimer tous les éléments liés aux formations de niveau Master.</p>
            </div>

            <div className="stats-grid">
                <StatCard value="0" label="FORMATIONS" color="#1a5e34" />
                <StatCard value="11" label="ENSEIGNANTS" color="#ca8a04" />
                <StatCard value="24" label="CLASSES" color="#ca8a04" />
                <StatCard value="140" label="UNITÉS (UE)" color="#dc2626" />
            </div>



            {/* Evolution Chart */}
            <EvolutionChart
                data={masterChartData}
                title="Suivi des Masters"
                subtitle="Avancement des promotions Master 1 & 2"
                color="#15803d"
            />

            <div style={{ marginBottom: '2rem' }}>
                <DistributionChart data={masterDistributionData} title="Répartition M1 / M2" />
            </div>

            <div className="dashboard-section">
                <div className="section-header">
                    <LayoutDashboard size={20} color="#1a5e34" />
                    <h2 className="section-title">Gestion Académique</h2>
                </div>
                <div className="cards-grid">
                    <ActionCard
                        icon={GraduationCap}
                        title="Formations"
                        subtitle="Gérer les Masters"
                        btnText="Gestion complète"
                    />
                    <ActionCard
                        icon={Layers}
                        title="Maquettes"
                        subtitle="Structure pédagogique"
                        btnText="Gestion complète"
                    />
                    <ActionCard
                        icon={BookOpen}
                        title="Modules"
                        subtitle="Gérer les modules"
                        btnText="Gestion complète"
                    />
                </div>
            </div>

            <div className="dashboard-section">
                <div className="section-header">
                    <Calendar size={20} color="#2563eb" /> {/* Using Blue for Organisation as per screenshot line color seems blue-ish or plain separator */}
                    <h2 className="section-title" style={{ color: '#2563eb' }}>Organisation</h2>
                </div>
                <div className="cards-grid">
                    <ActionCard
                        icon={Calendar}
                        title="Emploi du Temps"
                        subtitle="Planning des séances"
                        btnText="Gestion complète"
                    />
                    <ActionCard
                        icon={Users}
                        title="Classes"
                        subtitle="Gérer les classes"
                        btnText="Gestion complète"
                    />
                    <ActionCard
                        icon={ClipboardList}
                        title="Cahier de Texte"
                        subtitle="Suivi pédagogique"
                        btnText="Consultation"
                        btnClass="consult"
                    />
                </div>
            </div>
        </div >
    );
};

export default MasterDashboard;
