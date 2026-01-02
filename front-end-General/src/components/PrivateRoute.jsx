import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const PrivateRoute = ({ allowedRoles }) => {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    // Check if user has one of the allowed roles
    // Logic: if allowedRoles includes the user's role OR if user is ADMIN (admins can usually access everything, but here we enforce strict dashboards unless specified)
    // For this strict dashboard requirement:
    if (!allowedRoles.includes(user.role) && user.role !== 'ADMIN') {
        // Redirect to their own dashboard or 403
        // For simplicity, redirect to home or show unauthorized
        return <Navigate to="/" replace />;
    }

    // Special case: If user IS admin but tries to access a restricted view that might confuse them (e.g. student view), we might allow it or not.
    // For this requirement, Admin has "full access", so we simply allow if user is admin.
    if (user.role === 'ADMIN' || user.role === 'CHEF_DE_DEPARTEMENT') {
        return <Outlet />;
    }

    if (allowedRoles.includes(user.role)) {
        return <Outlet />;
    }

    return <Navigate to="/unauthorized" replace />;
};

export default PrivateRoute;
