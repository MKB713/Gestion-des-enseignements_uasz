import React, { useState, useEffect, useMemo } from 'react';
import { ChevronLeft, ChevronRight, Filter, Download, MapPin, User, Clock } from 'lucide-react';
import './Timetable.css';

const TIMES = Array.from({ length: 13 }, (_, i) => i + 8); // 8h to 20h

const DAYS_MAP = {
    "MONDAY": 0,
    "TUESDAY": 1,
    "WEDNESDAY": 2,
    "THURSDAY": 3,
    "FRIDAY": 4,
    "SATURDAY": 5,
    "SUNDAY": 6
};

const Timetable = ({ events = [], weekStart, onWeekChange, onFilter, onExport }) => {
    // Current date state for navigation
    const [currentDate, setCurrentDate] = useState(new Date());

    useEffect(() => {
        if (weekStart) {
            setCurrentDate(new Date(weekStart));
        }
    }, [weekStart]);

    // Generate days for the current week header
    const daysOfWeek = useMemo(() => {
        const start = new Date(currentDate);
        const day = start.getDay();
        const diff = start.getDate() - day + (day === 0 ? -6 : 1); // Adjust when day is sunday
        const startOfWeek = new Date(start.setDate(diff));

        const days = [];
        for (let i = 0; i < 6; i++) { // Mon-Sat
            const d = new Date(startOfWeek);
            d.setDate(startOfWeek.getDate() + i);
            days.push(d);
        }
        return days;
    }, [currentDate]);

    const handlePrevWeek = () => {
        const newDate = new Date(currentDate);
        newDate.setDate(newDate.getDate() - 7);
        setCurrentDate(newDate);
        if (onWeekChange) onWeekChange(newDate);
    };

    const handleNextWeek = () => {
        const newDate = new Date(currentDate);
        newDate.setDate(newDate.getDate() + 7);
        setCurrentDate(newDate);
        if (onWeekChange) onWeekChange(newDate);
    };

    // Calculate grid position
    // event: { dateSeance: "2024-01-01", heureDebut: "08:00", heureFin: "10:00", ... }
    const getPosition = (event) => {
        if ((!event.dateSeance && !event.jour) || !event.heureDebut || !event.heureFin) return null;

        let dayIndex = -1;

        if (event.dateSeance) {
            const eventDate = new Date(event.dateSeance);
            dayIndex = daysOfWeek.findIndex(d => d.toDateString() === eventDate.toDateString());
        } else if (event.jour) {
            // If the day is within the view (Mon-Sat usually)
            if (DAYS_MAP[event.jour] !== undefined && DAYS_MAP[event.jour] < daysOfWeek.length) {
                dayIndex = DAYS_MAP[event.jour];
            }
        }

        if (dayIndex === -1) return null; // Event not in this week

        const [startH, startM] = event.heureDebut.split(':').map(Number);
        const [endH, endM] = event.heureFin.split(':').map(Number);

        const startHour = startH + (startM / 60);
        const endHour = endH + (endM / 60);
        const duration = endHour - startHour;

        // VERTICAL LAYOUT: Days are columns, hours are rows
        // Day index determines column (column 2+ for days)
        // Hour determines row (row 2+ for hours starting at 8:00)
        const colStart = dayIndex + 2;
        const rowStart = Math.floor(startHour - 8) + 2;

        // duration corresponds to height (row span)
        const rowSpan = Math.max(1, Math.ceil(duration));

        return {
            gridColumn: `${colStart} / span 1`,
            gridRow: `${rowStart} / span ${rowSpan}`,
        };
    };

    return (
        <div className="timetable-container">
            <div className="timetable-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <h2 style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'var(--text-main)', margin: 0 }}>
                        Semaine du {daysOfWeek[0].toLocaleDateString('fr-FR')}
                    </h2>
                    <div className="flex gap-2">
                        <button className="btn btn-icon" onClick={handlePrevWeek} aria-label="Semaine passée"><ChevronLeft size={20} /></button>
                        <button className="btn btn-icon" onClick={handleNextWeek} aria-label="Semaine suivante"><ChevronRight size={20} /></button>
                    </div>
                </div>
                <div className="timetable-controls">
                    <button className="btn btn-secondary" onClick={onFilter}>
                        <Filter size={18} className="mr-2" />
                        Filtrer
                    </button>
                    <button className="btn btn-primary" onClick={onExport}>
                        <Download size={18} className="mr-2" />
                        Exporter
                    </button>
                    <div className="hidden-controls" style={{ display: 'none' }}></div>
                </div>
            </div>

            <div className="timetable-grid">
                {/* Header Row: Empty Cell + Days */}
                <div className="header-cell"></div>
                {daysOfWeek.map((day, index) => (
                    <div key={index} className={`header-cell ${day.toDateString() === new Date().toDateString() ? 'today' : ''}`}>
                        <div style={{ textTransform: 'capitalize' }}>
                            {day.toLocaleDateString('fr-FR', { weekday: 'long' })}
                            <span style={{ marginLeft: '8px', opacity: 0.7, fontSize: '0.9em' }}>{day.getDate()}</span>
                        </div>
                    </div>
                ))}

                {/* Time Cells (First Column) */}
                {TIMES.map((time, timeIndex) => (
                    <div
                        key={timeIndex}
                        className="time-cell"
                        style={{ gridRow: `${timeIndex + 2} / span 1`, gridColumn: '1 / span 1' }}
                    >
                        {time}:00
                    </div>
                ))}

                {/* Grid Background Cells */}
                {TIMES.map((_, timeIndex) => (
                    daysOfWeek.map((_, dayIndex) => (
                        <div
                            key={`${timeIndex}-${dayIndex}`}
                            className="grid-cell"
                            style={{
                                gridRow: `${timeIndex + 2} / span 1`,
                                gridColumn: `${dayIndex + 2} / span 1`
                            }}
                        />
                    ))
                ))}

                {/* Events */}
                {events.map((event, index) => {
                    const style = getPosition(event);
                    if (!style) return null;

                    // Determine class based on type
                    let typeClass = 'event-cm';
                    if (event.typeSeance === 'TD') typeClass = 'event-td';
                    if (event.typeSeance === 'TP') typeClass = 'event-tp';

                    return (
                        <div
                            key={event.id || index}
                            className={`event-card ${typeClass}`}
                            style={style}
                            title={`${event.libelle || event.module || 'Cours'} - ${event.salleNom}`}
                        >
                            <div className="event-title">{event.ecNom || event.module || 'Cours'}</div>
                            <div className="event-info">
                                <div className="event-location">
                                    <MapPin size={12} /> {event.salleNom || 'Salle inconnue'}
                                </div>
                                <div className="event-prof">
                                    <User size={12} /> {event.enseignantNom || 'Enseignant'}
                                </div>
                                <div className="event-time flex items-center gap-1 mt-1 font-mono text-xs opacity-75">
                                    <Clock size={12} /> {event.heureDebut} - {event.heureFin}
                                </div>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default Timetable;
