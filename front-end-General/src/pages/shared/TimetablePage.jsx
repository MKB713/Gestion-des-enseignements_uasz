import React, { useState, useEffect } from 'react';
import { Calendar as CalendarIcon } from 'lucide-react';
import Timetable from '../../components/Timetable';
import { apiRequest, API_ENDPOINTS } from '../../config/api';

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
            // Format date as YYYY-MM-DD
            const formattedDate = currentDate.toISOString().split('T')[0];

            // Build query params
            // For now, fetching global or user-specific if logic was here.
            // The endpoint `/semaine` returns data.
            // If we are "student", maybe we need to filter? 
            // For now, let's just fetch the generic weekly planning or filtered by user context if we had it.
            // The API supports ?date=...

            const data = await apiRequest(`${API_ENDPOINTS.EMPLOI_TEMPS.WEEKLY}?date=${formattedDate}`);

            if (data && data.seancesParJour) {
                // Flatten the map of lists into a single array
                const allSeances = Object.values(data.seancesParJour).flat();
                setEvents(allSeances);
            } else {
                setEvents([]);
            }
            setError(null);

        } catch (err) {
            console.error("Erreur chargement emploi du temps:", err);
            // Don't show error to user immediately on first load if it's empty, but logging is good
            // setError("Impossible de charger l'emploi du temps."); 
            setEvents([]);
        } finally {
            setLoading(false);
        }
    };

    const handleWeekChange = (newDate) => {
        setCurrentDate(newDate);
    };

    return (
        <div className="master-dashboard">
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
