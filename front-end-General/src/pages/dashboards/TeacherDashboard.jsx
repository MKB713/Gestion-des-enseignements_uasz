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
    Eye,
    GitBranch
} from 'lucide-react';
import './../dashboards/MasterDashboard.css'; // Reusing Master styles for consistency

const ActionCard = ({ icon: Icon, title, subtitle, btnText, btnClass = '', iconColor = '#15803d' }) => (
    <div className="action-card">
        <div className="card-icon" style={{ color: iconColor }}>
            <Icon size={24} />
        </div>
        <h3>{title}</h3>
        <p>{subtitle}</p>
        <button className={`card-btn ${btnClass}`}>{btnText}</button>
    </div>
);

const TeacherDashboard = () => {
    return (
        <div className="master-dashboard"> {/* Reusing class for styles */}
            <div className="dashboard-title-section">
                <h1 className="dashboard-title">
                    <Users size={32} className="mr-2" />
                    Espace Enseignant
                </h1>
            </div>

            <div className="welcome-banner" style={{ backgroundColor: '#f0fdf4', borderLeftColor: '#15803d' }}>
                <h3 style={{ color: '#15803d', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <Eye size={20} />
                    Mode Consultation
                </h3>
                <p>Vous pouvez consulter toutes les ressources pédagogiques. Seul le <strong>Cahier de Texte</strong> est modifiable pour documenter vos cours.</p>
            </div>

            {/* Grid of 3 columns */}
            <div className="cards-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', marginTop: '2rem' }}>

                {/* Planning */}
                <ActionCard
                    icon={Calendar}
                    title="Emploi du Temps"
                    subtitle="Consultez vos emplois du temps"
                    btnText="Consultation"
                    btnClass="consult"
                    iconColor="#15803d"
                />

                {/* Cahier de Texte */}
                <ActionCard
                    icon={ClipboardList}
                    title="Cahier de Texte"
                    subtitle="Gérez vos notes pédagogiques"
                    btnText="Gestion autorisée"
                    btnClass="" // Default green
                    iconColor="#1a5e34"
                />

                {/* Formations */}
                <ActionCard
                    icon={GraduationCap}
                    title="Formations"
                    subtitle="Toutes les filières, niveaux, cycles"
                    btnText="Consultation"
                    btnClass="consult"
                    iconColor="#15803d"
                />

                {/* Filières */}
                <ActionCard
                    icon={GitBranch}
                    title="Filières"
                    subtitle="Liste des filières"
                    btnText="Consultation"
                    btnClass="consult"
                    iconColor="#ca8a04"
                />

                {/* Classes */}
                <ActionCard
                    icon={Users}
                    title="Classes"
                    subtitle="Organisation des classes"
                    btnText="Consultation"
                    btnClass="consult"
                    iconColor="#15803d"
                />

                {/* Maquettes */}
                <ActionCard
                    icon={Layers} // Or FileText
                    title="Maquettes"
                    subtitle="Structure pédagogique"
                    btnText="Consultation"
                    btnClass="consult"
                    iconColor="#ca8a04"
                />

                {/* Modules */}
                <ActionCard
                    icon={BookOpen}
                    title="Modules"
                    subtitle="Modules d'enseignement"
                    btnText="Consultation"
                    btnClass="consult"
                    iconColor="#15803d"
                />

                {/* Unités (UE) */}
                <ActionCard
                    icon={Layers}
                    title="Unités (UE)"
                    subtitle="Unités d'enseignement"
                    btnText="Consultation"
                    btnClass="consult"
                    iconColor="#ca8a04"
                />

                {/* Éléments (EC) */}
                <ActionCard
                    icon={Layers}
                    title="Éléments (EC)"
                    subtitle="Éléments constitutifs"
                    btnText="Consultation"
                    btnClass="consult"
                    iconColor="#15803d"
                />

            </div>
        </div>
    );
};

export default TeacherDashboard;
