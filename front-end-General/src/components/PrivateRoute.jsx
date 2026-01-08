import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const PrivateRoute = ({ allowedRoles, role, children }) => {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    // Normalize roles to an array
    const roles = allowedRoles || (role ? [role] : []);

    // If no roles specified, assume open to authenticated users (or restrict? safe default is restrict)
    if (roles.length === 0) {
        // If no specific roles required, allow access (or maybe check logic)
        return children ? children : <Outlet />;
    }

    // Check if user has permission
    // Admin always has access
    if (user.role === 'ADMIN') {
        return children ? children : <Outlet />;
    }

    // Check if user's role is in the allowed list
    if (roles.includes(user.role)) {
        return children ? children : <Outlet />;
    }

    return <Navigate to="/unauthorized" replace />;
};

export default PrivateRoute;
