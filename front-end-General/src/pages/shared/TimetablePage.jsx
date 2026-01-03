import React from 'react';
import { Calendar as CalendarIcon } from 'lucide-react';
import Timetable from '../../components/Timetable';

// Mock Data - In a real app, this would be fetched based on role/user
const mockEvents = [
    { day: 'Lundi', startHour: 8, duration: 4, module: 'Algorithmique Avancée', type: 'CM', room: 'Amphi A', professor: 'Dr. Diop' },
    { day: 'Lundi', startHour: 14, duration: 2, module: 'Algorithmique Avancée', type: 'TD', room: 'Salle 12', professor: 'M. Ndiaye' },
    { day: 'Mardi', startHour: 10, duration: 2, module: 'Architecture Web', type: 'CM', room: 'Amphi B', professor: 'Dr. Fall' },
    { day: 'Mercredi', startHour: 8, duration: 4, module: 'Bases de Données', type: 'CM', room: 'Amphi C', professor: 'Dr. Sow' },
    { day: 'Jeudi', startHour: 14, duration: 3, module: 'Développement Mobile', type: 'TP', room: 'Labo Info 1', professor: 'Mme. Faye' },
    { day: 'Vendredi', startHour: 9, duration: 3, module: 'Anglais Technique', type: 'TD', room: 'Salle 24', professor: 'Mr. Smith' },
    { day: 'Samedi', startHour: 10, duration: 2, module: 'Conférence Tech', type: 'CM', room: 'Auditorium', professor: 'Invité' },
];

const TimetablePage = ({ roleTitle = 'Emploi du Temps' }) => {
    return (
        <div className="master-dashboard"> {/* Reusing generic dashboard wrapper for padding */}
            <div className="dashboard-title-section">
                <h1 className="dashboard-title">
                    <CalendarIcon size={32} className="mr-2" />
                    {roleTitle}
                </h1>
            </div>

            <div className="welcome-banner" style={{ backgroundColor: '#f0f9ff', borderLeftColor: '#0ea5e9' }}>
                <h3 style={{ color: '#0369a1' }}>Planning Hebdomadaire</h3>
                <p>Consultez et gérez l'emploi du temps des cours, examens et activités pédagogiques.</p>
            </div>

            <Timetable events={mockEvents} />
        </div>
    );
};

export default TimetablePage;
