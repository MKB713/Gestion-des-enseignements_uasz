import React from 'react';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    AreaChart,
    Area
} from 'recharts';

const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
        return (
            <div style={{
                backgroundColor: 'white',
                padding: '1rem',
                border: '1px solid var(--border-color)',
                borderRadius: '8px',
                boxShadow: 'var(--shadow-lg)'
            }}>
                <p style={{ margin: 0, fontWeight: 'bold', color: 'var(--text-main)' }}>{label}</p>
                <p style={{ margin: 0, color: payload[0].color }}>
                    {payload[0].name}: {payload[0].value}
                </p>
            </div>
        );
    }
    return null;
};

const EvolutionChart = ({ data, title, color = "#15803d", subtitle }) => {
    return (
        <div style={{
            backgroundColor: 'var(--bg-surface)',
            padding: '1.5rem',
            borderRadius: 'var(--radius-lg)',
            boxShadow: 'var(--shadow-sm)',
            border: '1px solid var(--border-color)',
            marginBottom: '2rem'
        }}>
            <div style={{ marginBottom: '1.5rem' }}>
                <h3 style={{ margin: 0, fontSize: '1.1rem', color: 'var(--text-main)' }}>{title}</h3>
                {subtitle && <p style={{ margin: '0.25rem 0 0 0', fontSize: '0.85rem', color: 'var(--text-muted)' }}>{subtitle}</p>}
            </div>

            <div style={{ width: '100%', height: 300 }}>
                <ResponsiveContainer>
                    <AreaChart data={data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                        <defs>
                            <linearGradient id={`colorGradient-${title}`} x1="0" y1="0" x2="0" y2="1">
                                <stop offset="5%" stopColor={color} stopOpacity={0.1} />
                                <stop offset="95%" stopColor={color} stopOpacity={0} />
                            </linearGradient>
                        </defs>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border-color)" />
                        <XAxis
                            dataKey="name"
                            axisLine={false}
                            tickLine={false}
                            tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
                            dy={10}
                        />
                        <YAxis
                            axisLine={false}
                            tickLine={false}
                            tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
                        />
                        <Tooltip content={<CustomTooltip />} />
                        <Area
                            type="monotone"
                            dataKey="value"
                            name="Valeur"
                            stroke={color}
                            strokeWidth={3}
                            fill={`url(#colorGradient-${title})`}
                            activeDot={{ r: 6, strokeWidth: 0 }}
                        />
                    </AreaChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default EvolutionChart;
