import React, { useState, useEffect } from 'react';
import { Calendar as CalendarIcon } from 'lucide-react';
import Timetable from '../../components/Timetable';
import { apiRequest, API_ENDPOINTS } from '../../config/api';
import '../admin/AdminDepartments.css'; // Import Admin styles

const TimetablePage = ({ roleTitle = 'Emploi du Temps' }) => {
    const [events, setEvents] = useState([]);
    const [currentDate, setCurrentDate] = useState(new Date());
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchTimetable();
    }, [currentDate]);

    const fetchTimetable = async () => {
        try {
            setLoading(true);
            const formattedDate = currentDate.toISOString().split('T')[0];
            const data = await apiRequest(`${API_ENDPOINTS.EMPLOI_TEMPS.WEEKLY}?date=${formattedDate}`);

            if (data && data.seancesParJour) {
                const allSeances = Object.values(data.seancesParJour).flat();
                setEvents(allSeances);
            } else {
                setEvents([]);
            }
            setError(null);
        } catch (err) {
            console.error("Erreur chargement emploi du temps:", err);
            setEvents([]);
        } finally {
            setLoading(false);
        }
    };

    const handleWeekChange = (newDate) => {
        setCurrentDate(newDate);
    };

    return (
        <div className="admin-departments-container">
            <div className="header-actions">
                <h2>
                    <CalendarIcon size={32} style={{ verticalAlign: 'middle', marginRight: '10px', color: '#006633' }} />
                    {roleTitle}
                </h2>
            </div>

            <div className="welcome-banner" style={{
                backgroundColor: '#f0fdf4', // Light green
                borderLeft: '4px solid #16a34a', // Green-600
                padding: '1.5rem',
                marginBottom: '2rem',
                borderRadius: '0.5rem',
                boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)'
            }}>
                <h3 style={{ color: '#166534', marginTop: 0, marginBottom: '0.5rem', fontSize: '1.25rem', fontWeight: 600 }}>Planning Hebdomadaire</h3>
                <p style={{ color: '#15803d', margin: 0 }}>Consultez et gérez l'emploi du temps des cours, examens et activités pédagogiques.</p>
            </div>

            {loading && <div className="loading" style={{ textAlign: 'center', padding: '20px' }}>Chargement...</div>}

            <Timetable
                events={events}
                weekStart={currentDate}
                onWeekChange={handleWeekChange}
            />
        </div>
    );
};

export default TimetablePage;
