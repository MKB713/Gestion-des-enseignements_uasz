import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const API_GATEWAY_URL = process.env.REACT_APP_API_GATEWAY_URL || '/api';

  useEffect(() => {
    // Vérifier la santé de l'API Gateway
    const checkHealth = async () => {
      try {
        const response = await axios.get(`${API_GATEWAY_URL}/actuator/health`);
        console.log('API Gateway Health:', response.data);
        setLoading(false);
      } catch (err) {
        console.error('Erreur de connexion à l\'API Gateway:', err);
        setError(err.message);
        setLoading(false);
      }
    };

    checkHealth();

    // Configuration de la liste des services
    setServices([
      { name: 'Eureka Server', url: 'http://localhost:8761', description: 'Service Registry' },
      { name: 'Config Server', url: 'http://localhost:8888', description: 'Configuration Centralisée' },
      { name: 'API Gateway', url: 'http://localhost:8080', description: 'Point d\'entrée unique' },
      { name: 'Auth Service', url: 'http://localhost:8081', description: 'Authentification' },
      { name: 'Enseignant Service', url: 'http://localhost:8082', description: 'Gestion des Enseignants' },
      { name: 'Maquette Service', url: 'http://localhost:8083', description: 'Gestion des Maquettes' },
      { name: 'Choix Enseignement Service', url: 'http://localhost:8084', description: 'Choix d\'Enseignements' },
      { name: 'Emploi Temps Service', url: 'http://localhost:8085', description: 'Emploi du Temps' },
      { name: 'Déroulement Service', url: 'http://localhost:8086', description: 'Déroulement des Enseignements' },
    ]);
  }, [API_GATEWAY_URL]);

  return (
    <div className="App">
      <header className="App-header">
        <h1>DAOS - Gestion des Enseignements</h1>
        <p>Université Assane Seck de Ziguinchor</p>
      </header>

      <main className="App-main">
        {loading && <div className="loading">Chargement...</div>}

        {error && (
          <div className="error">
            <h3>Erreur de connexion</h3>
            <p>{error}</p>
            <p>Assurez-vous que l'API Gateway est démarré sur {API_GATEWAY_URL}</p>
          </div>
        )}

        {!loading && !error && (
          <div className="success">
            <h3>Connexion établie avec l'API Gateway</h3>
            <p>L'application est prête à être utilisée</p>
          </div>
        )}

        <section className="services-section">
          <h2>Services Disponibles</h2>
          <div className="services-grid">
            {services.map((service, index) => (
              <div key={index} className="service-card">
                <h3>{service.name}</h3>
                <p>{service.description}</p>
                <a href={service.url} target="_blank" rel="noopener noreferrer">
                  Accéder
                </a>
              </div>
            ))}
          </div>
        </section>

        <section className="documentation-section">
          <h2>Documentation</h2>
          <div className="doc-links">
            <a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noopener noreferrer">
              API Documentation (Swagger)
            </a>
            <a href="http://localhost:8761" target="_blank" rel="noopener noreferrer">
              Service Registry Dashboard
            </a>
          </div>
        </section>
      </main>

      <footer className="App-footer">
        <p>&copy; 2024 UASZ - Tous droits réservés</p>
      </footer>
    </div>
  );
}

export default App;
