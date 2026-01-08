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
import './../dashboards/MasterDashboard.css'; // Reusing Master styles for consistency
import EvolutionChart from '../../components/EvolutionChart';

// Mock Data for Coordinator
const coordinatorChartData = [
    { name: 'Sem 1', value: 30 },
    { name: 'Sem 2', value: 45 },
    { name: 'Sem 3', value: 55 },
    { name: 'Sem 4', value: 70 },
    { name: 'Sem 5', value: 85 },
    { name: 'Sem 6', value: 95 },
    { name: 'Sem 6', value: 95 },
];

const coordinatorDistributionData = [
    { name: 'Licence 1', value: 450 },
    { name: 'Licence 2', value: 350 },
    { name: 'Licence 3', value: 200 },
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

const CoordinatorDashboard = () => {
    return (
        <div className="master-dashboard"> {/* Reusing class for styles */}
            <div className="dashboard-title-section">
                <h1 className="dashboard-title">
                    <Users size={32} className="mr-2" />
                    Espace Coordinateur Licence
                </h1>
            </div>

            <div className="welcome-banner">
                <h3>Accès Complet - Niveau Licence</h3>
                <p>Vous disposez des permissions complètes pour créer, modifier et supprimer tous les éléments liés aux formations de niveau Licence.</p>
            </div>

            <div className="stats-grid">
                <StatCard value="0" label="FORMATIONS" color="#1a5e34" />
                <StatCard value="11" label="ENSEIGNANTS" color="#ca8a04" />
                <StatCard value="24" label="CLASSES" color="#ca8a04" />
                <StatCard value="140" label="UNITÉS (UE)" color="#dc2626" />
            </div>



            {/* Evolution Chart */}
            <EvolutionChart
                data={coordinatorChartData}
                title="Progression des Cours (Licence)"
                subtitle="Taux d'avancement par semestre"
                color="#ca8a04"
            />

            <div style={{ marginBottom: '2rem' }}>
                <DistributionChart data={coordinatorDistributionData} title="Répartition par Niveau" />
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
                        subtitle="Gérer les Licences"
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
                    <Calendar size={20} color="#ca8a04" />
                    <h2 className="section-title" style={{ color: '#ca8a04' }}>Organisation</h2>
                </div>
                <div className="cards-grid">
                    <ActionCard
                        icon={Calendar}
                        title="Emploi du Temps"
                        subtitle="Planning des séances"
                        btnText="Gestion complète"
                        iconColor="#ca8a04"
                    />
                    <ActionCard
                        icon={Users}
                        title="Classes"
                        subtitle="Gérer les classes"
                        btnText="Gestion complète"
                        iconColor="#15803d"
                    />
                    <ActionCard
                        icon={ClipboardList}
                        title="Cahier de Texte"
                        subtitle="Suivi pédagogique"
                        btnText="Consultation"
                        btnClass="consult"
                        iconColor="#ca8a04"
                    />
                </div>
            </div>
        </div >
    );
};

export default CoordinatorDashboard;
