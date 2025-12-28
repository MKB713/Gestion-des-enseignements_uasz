import axios from 'axios';

const API_URL = 'http://localhost:8090/api/deroulement-enseignements';

const AuthService = {
    login: (username, password) => {
        return axios.post('http://localhost:8089/api/auth/login', {
            username,
            password
        });
    },

    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    isAuthenticated: () => {
        return !!localStorage.getItem('token');
    },

    getToken: () => {
        return localStorage.getItem('token');
    },

    setToken: (token) => {
        localStorage.setItem('token', token);
    },

    getUser: () => {
        return JSON.parse(localStorage.getItem('user') || '{}');
    },

    setUser: (user) => {
        localStorage.setItem('user', JSON.stringify(user));
    }
};

export default AuthService;
