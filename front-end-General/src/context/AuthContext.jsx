import React, { createContext, useState, useContext, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check for stored user and validate token on load
        const initAuth = async () => {
            const storedUser = authService.getUser();
            const token = authService.getAccessToken();

            if (storedUser && token) {
                // Optionally validate token with backend
                const isValid = await authService.validateToken();
                if (isValid) {
                    setUser(storedUser);
                } else {
                    // Token expired, try to refresh
                    try {
                        await authService.refreshToken();
                        setUser(storedUser);
                    } catch (error) {
                        authService.clearAuth();
                    }
                }
            }
            setLoading(false);
        };

        initAuth();
    }, []);

    const login = async (credentials) => {
        try {
            const data = await authService.login(credentials);
            setUser(data.user);
            return data;
        } catch (error) {
            throw error;
        }
    };

    const logout = async () => {
        try {
            await authService.logout();
            setUser(null);
        } catch (error) {
            console.error('Logout error:', error);
            // Clear local data even if API call fails
            authService.clearAuth();
            setUser(null);
        }
    };

    const refreshToken = async () => {
        try {
            const newToken = await authService.refreshToken();
            return newToken;
        } catch (error) {
            setUser(null);
            throw error;
        }
    };

    return (
        <AuthContext.Provider value={{
            user,
            login,
            logout,
            refreshToken,
            loading,
            isAuthenticated: !!user
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
