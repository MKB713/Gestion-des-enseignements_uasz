import React from 'react';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const COLORS = ['#15803d', '#ca8a04', '#1e293b', '#b91c1c', '#0ea5e9'];

const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
        return (
            <div style={{
                backgroundColor: 'white',
                padding: '0.5rem',
                border: '1px solid var(--border-color)',
                borderRadius: '8px',
                boxShadow: 'var(--shadow-md)'
            }}>
                <p style={{ margin: 0, fontWeight: 'bold' }}>{payload[0].name}</p>
                <p style={{ margin: 0 }}>{payload[0].value}</p>
            </div>
        );
    }
    return null;
};

const DistributionChart = ({ data, title }) => {
    return (
        <div style={{
            backgroundColor: 'var(--bg-surface)',
            padding: '1.5rem',
            borderRadius: 'var(--radius-lg)',
            boxShadow: 'var(--shadow-sm)',
            border: '1px solid var(--border-color)',
            height: '100%',
            display: 'flex',
            flexDirection: 'column'
        }}>
            <h3 style={{ margin: '0 0 1rem 0', fontSize: '1.1rem', color: 'var(--text-main)' }}>{title}</h3>
            <div style={{ flex: 1, minHeight: '250px' }}>
                <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                        <Pie
                            data={data}
                            cx="50%"
                            cy="50%"
                            innerRadius={60}
                            outerRadius={80}
                            fill="#8884d8"
                            paddingAngle={5}
                            dataKey="value"
                        >
                            {data.map((entry, index) => (
                                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                            ))}
                        </Pie>
                        <Tooltip content={<CustomTooltip />} />
                        <Legend verticalAlign="bottom" height={36} iconType="circle" />
                    </PieChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default DistributionChart;
