import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, LogIn, ArrowLeft } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import './Login.css';

const Login = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            // Call the real authentication service
            const data = await login(formData);

            // Navigate to the appropriate dashboard based on the user's role
            navigateToRoleDashboard(data.user.role);
        } catch (err) {
            setError(err.message || 'Erreur de connexion. Veuillez réessayer.');
            console.error('Login error:', err);
        } finally {
            setLoading(false);
        }
    };

    const navigateToRoleDashboard = (role) => {
        switch (role) {
            case 'ETUDIANT':
                navigate('/student/dashboard');
                break;
            case 'ENSEIGNANT':
                navigate('/teacher/dashboard');
                break;
            case 'RESPONSABLE_MASTER':
                navigate('/master/dashboard');
                break;
            case 'COORDONATEUR_DES_LICENCES':
                navigate('/coordinator/dashboard');
                break;
            case 'ADMIN':
            case 'CHEF_DE_DEPARTEMENT':
                navigate('/admin/dashboard');
                break;
            default:
                navigate('/student/dashboard');
        }
    };

    return (
        <div className="login-container">
            <div className="login-wrapper">
                {/* Left Side - Image */}
                <div className="login-image-section">
                    <div className="image-overlay">
                        <div className="overlay-content">
                            <h2>Bienvenue à l'UASZ</h2>
                            <p>Connectez-vous pour accéder à votre espace numérique de travail.</p>
                        </div>
                    </div>
                    <img src="/images/Mobile login.gif" alt="Login Animation" className="login-image" />
                </div>

                {/* Right Side - Form */}
                <div className="login-form-section">
                    <div className="form-header">
                        <div className="brand-logo">
                            <img src="/images/logo.jpg" alt="UASZ Logo" />
                        </div>
                        <h1>Connexion</h1>
                        <p>Entrez vos identifiants pour continuer</p>
                    </div>

                    <form onSubmit={handleSubmit} className="login-form">
                        {error && (
                            <div className="error-message" style={{
                                backgroundColor: '#fee2e2',
                                color: '#dc2626',
                                padding: '12px',
                                borderRadius: '8px',
                                marginBottom: '16px',
                                fontSize: '14px'
                            }}>
                                {error}
                            </div>
                        )}

                        <div className="form-group">
                            <label htmlFor="email">Email / Identifiant</label>
                            <div className="input-icon-wrapper">
                                <Mail size={20} className="input-icon" />
                                <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    placeholder="exemple@uasz.sn"
                                    required
                                    disabled={loading}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">Mot de passe</label>
                            <div className="input-icon-wrapper">
                                <Lock size={20} className="input-icon" />
                                <input
                                    type="password"
                                    id="password"
                                    name="password"
                                    value={formData.password}
                                    onChange={handleChange}
                                    placeholder="••••••••"
                                    required
                                    disabled={loading}
                                />
                            </div>
                        </div>

                        <div className="form-options">
                            <div className="remember-me">
                                <input type="checkbox" id="remember" disabled={loading} />
                                <label htmlFor="remember">Se souvenir de moi</label>
                            </div>
                            <a href="#" className="forgot-password">Mot de passe oublié ?</a>
                        </div>

                        <button type="submit" className="btn-login" disabled={loading}>
                            <LogIn size={20} />
                            {loading ? 'Connexion en cours...' : 'Se connecter'}
                        </button>
                    </form>

                    <div className="form-footer">
                        <p>Pas encore de compte ? <a href="#">Contacter l'administrateur</a></p>
                        <Link to="/" className="back-link">
                            <ArrowLeft size={16} />
                            Retour à l'accueil
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
