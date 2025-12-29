import React, { useState } from 'react';
import AuthService from '../services/AuthService';
import './Login.css'; // Import du CSS que nous avons créé

const Login = () => {
    // --- ÉTATS (STATES) ---
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    // --- LOGIQUE ---
    const handleLogin = async (e) => {
        e.preventDefault(); // Empêche le rechargement de la page
        setLoading(true);
        setError('');

        try {
            // Appel au backend via Axios
            const response = await AuthService.login(email, password);

            // Si succès (à adapter selon le retour JSON de votre AuthController)
            // Supposons que le backend renvoie { token: "...", user: {...} }
            AuthService.saveUser(response.data.token, response.data.user);

            // Redirection (on verra le Router à l'étape suivante)
            window.location.href = "/dashboard";

        } catch (err) {
            console.error(err);
            setError("Email ou mot de passe incorrect !");
            setLoading(false);
        }
    };

    // --- RENDER (AFFICHAGE) ---
    return (
        <div className="login-page-body">
            <div className="login-container">
                <div className="login-row">

                    {/* SECTION GAUCHE (IMAGE) */}
                    <div className="left-section">
                        {/* Assurez-vous que l'image est bien dans public/img/ */}
                        <img src="/images/Mobile login.gif" alt="Login Illustration" className="login-image" />
                    </div>

                    {/* SECTION DROITE (FORMULAIRE) */}
                    <div className="right-section">
                        <div className="login-header">
                            <h3>Connexion</h3>
                            <p className="text-muted">Connectez-vous à votre compte</p>
                        </div>

                        <form onSubmit={handleLogin}>
                            {/* Message d'erreur */}
                            {error && (
                                <div className="alert alert-danger border-0 shadow-sm mb-4" role="alert">
                                    <i className="bi bi-exclamation-triangle-fill me-2"></i>
                                    {error}
                                </div>
                            )}

                            {/* Email */}
                            <div className="form-group">
                                <label className="form-label">Adresse Email</label>
                                <div className="input-wrapper">
                                    <input
                                        type="email"
                                        className="form-control-custom"
                                        placeholder="votre.email@uasz.sn"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                    />
                                    <i className="bi bi-envelope-fill input-icon"></i>
                                </div>
                            </div>

                            {/* Mot de passe */}
                            <div className="form-group">
                                <label className="form-label">Mot de Passe</label>
                                <div className="input-wrapper">
                                    <input
                                        type={showPassword ? "text" : "password"}
                                        className="form-control-custom"
                                        placeholder="Entrez votre mot de passe"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        required
                                    />
                                    <i className="bi bi-lock-fill input-icon"></i>

                                    {/* Oeil pour voir le mot de passe */}
                                    <i
                                        className={`bi ${showPassword ? 'bi-eye' : 'bi-eye-slash'} password-toggle`}
                                        onClick={() => setShowPassword(!showPassword)}
                                    ></i>
                                </div>
                            </div>

                            {/* Bouton */}
                            <button type="submit" className="btn-login mt-3" disabled={loading}>
                                {loading ? (
                                    <span><span className="spinner-border spinner-border-sm me-2"></span>Connexion...</span>
                                ) : (
                                    <span><i className="bi bi-box-arrow-in-right me-2"></i>Se connecter</span>
                                )}
                            </button>
                        </form>

                        <div className="text-center mt-4">
                            <a href="/" className="text-decoration-none text-success">
                                <i className="bi bi-arrow-left me-1"></i> Retour à l'accueil
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;