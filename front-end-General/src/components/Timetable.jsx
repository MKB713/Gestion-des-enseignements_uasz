import React, { useState } from 'react';
import { ChevronLeft, ChevronRight, Filter, Download, MapPin, User, Clock } from 'lucide-react';
import './Timetable.css';

const DAYS = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
const TIMES = Array.from({ length: 14 }, (_, i) => i + 8); // 8h to 21h

const Timetable = ({ events = [] }) => {
    const [currentWeek, setCurrentWeek] = useState(new Date());

    // Helper to calculate grid position
    // In a real app, this would be more complex to handle exact minutes
    const getPosition = (dayIndex, startHour, duration) => {
        // Grid columns: Time + 6 Days. So Monday is col 2.
        const colStart = dayIndex + 2;
        // Grid rows: Header + 14 hours. 8am is row 2.
        const rowStart = (startHour - 8) + 2;
        const rowSpan = duration;

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
                        Semaine du 01 Janvier 2026
                    </h2>
                    <div className="flex gap-2">
                        <button className="btn btn-icon"><ChevronLeft size={20} /></button>
                        <button className="btn btn-icon"><ChevronRight size={20} /></button>
                    </div>
                </div>
                <div className="timetable-controls">
                    <button className="btn btn-secondary">
                        <Filter size={18} className="mr-2" />
                        Filtrer
                    </button>
                    <button className="btn btn-primary">
                        <Download size={18} className="mr-2" />
                        Exporter
                    </button>
                </div>
            </div>

            <div className="timetable-grid">
                {/* Header Row: Empty Cell + Days */}
                <div className="header-cell"></div>
                {DAYS.map((day, index) => (
                    <div key={day} className={`header-cell ${index === 0 ? 'today' : ''}`}>
                        {day}
                    </div>
                ))}

                {/* Time Cells */}
                {TIMES.map((time) => (
                    <div key={time} className="time-cell" style={{ gridRow: `${time - 8 + 2} / span 1`, gridColumn: '1 / span 1' }}>
                        {time}:00
                    </div>
                ))}

                {/* Grid Background Cells (Lines) */}
                {TIMES.map((time) => (
                    DAYS.map((_, dayIndex) => (
                        <div
                            key={`${time}-${dayIndex}`}
                            className="grid-cell"
                            style={{
                                gridRow: `${time - 8 + 2} / span 1`,
                                gridColumn: `${dayIndex + 2} / span 1`
                            }}
                        />
                    ))
                ))}

                {/* Events */}
                {events.map((event, index) => {
                    const dayIndex = DAYS.indexOf(event.day);
                    if (dayIndex === -1) return null;

                    const style = getPosition(dayIndex, event.startHour, event.duration);

                    // Determine class based on type
                    let typeClass = 'event-cm';
                    if (event.type === 'TD') typeClass = 'event-td';
                    if (event.type === 'TP') typeClass = 'event-tp';
                    if (event.type === 'EXAM') typeClass = 'event-exam';

                    return (
                        <div
                            key={index}
                            className={`event-card ${typeClass}`}
                            style={style}
                        >
                            <div className="event-title">{event.module}</div>
                            <div className="event-info">
                                <div className="event-location">
                                    <MapPin size={12} /> {event.room}
                                </div>
                                <div className="event-prof">
                                    <User size={12} /> {event.professor}
                                </div>
                                <div className="event-time flex items-center gap-1 mt-1 font-mono text-xs opacity-75">
                                    <Clock size={12} /> {event.startHour}h - {event.startHour + event.duration}h
                                </div>
                            </div>
                            <div style={{
                                position: 'absolute',
                                top: '4px',
                                right: '4px',
                                background: 'rgba(0,0,0,0.2)',
                                padding: '2px 6px',
                                borderRadius: '4px',
                                fontWeight: 'bold'
                            }}>
                                {event.type}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default Timetable;
