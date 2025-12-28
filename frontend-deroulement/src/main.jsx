import React from 'react';
import ReactDOM from 'react-dom/client'
import App from './App.jsx'

// 1. Styles par défaut de Vite
import './index.css'

// 2. Bootstrap Framework (CSS)
import 'bootstrap/dist/css/bootstrap.min.css';

// 3. Bootstrap Icons
import 'bootstrap-icons/font/bootstrap-icons.css';

// 4. Bootstrap JS
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

// 5. Styles académiques personnalisés
import './assets/css/academic.css';

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <App />
    </React.StrictMode>,
)
