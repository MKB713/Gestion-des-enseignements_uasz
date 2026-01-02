import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Mail, Lock, LogIn, ArrowLeft } from 'lucide-react';
import './Login.css';

const Login = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log('Login attempt:', formData);
        // Add authentication logic here
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
                                />
                            </div>
                        </div>

                        <div className="form-options">
                            <div className="remember-me">
                                <input type="checkbox" id="remember" />
                                <label htmlFor="remember">Se souvenir de moi</label>
                            </div>
                            <a href="#" className="forgot-password">Mot de passe oublié ?</a>
                        </div>

                        <button type="submit" className="btn-login">
                            <LogIn size={20} />
                            Se connecter
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
