import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import './Login.css';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await AuthService.login(username, password);
            const { token, user } = response.data;

            AuthService.setToken(token);
            AuthService.setUser(user);

            navigate('/dashboard');
        } catch (err) {
            setError('Identifiants invalides. Veuillez réessayer.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="login-header">
                    <h1>UASZ</h1>
                    <p>Gestion du Déroulement des Enseignements</p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    {error && <div className="alert alert-danger">{error}</div>}

                    <div className="form-group mb-3">
                        <label htmlFor="username" className="form-label">Nom d'utilisateur</label>
                        <input
                            type="text"
                            className="form-control"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>

                    <div className="form-group mb-3">
                        <label htmlFor="password" className="form-label">Mot de passe</label>
                        <input
                            type="password"
                            className="form-control"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary-custom w-100"
                        disabled={loading}
                    >
                        {loading ? 'Connexion en cours...' : 'Se connecter'}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default Login;
