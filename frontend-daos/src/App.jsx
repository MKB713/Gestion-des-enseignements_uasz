import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import MainLayout from './components/layout/MainLayout';

// --- IMPORTS ORGANISÉS ---
// Assure-toi que les chemins correspondent à tes dossiers réels
import FormationList from './components/maquette/FormationList';
import FormationForm from './components/maquette/FormationForm';

import FiliereList from './components/maquette/FiliereList';
import FiliereForm from './components/maquette/FiliereForm';

import NiveauList from './components/maquette/NiveauList';
import NiveauForm from './components/maquette/NiveauForm';

// Composants Choix Enseignement
import ChoixList from './components/choix-enseignement/ChoixList';
import ChoixForm from './components/choix-enseignement/ChoixForm';
import ChoixDetail from './components/choix-enseignement/ChoixDetail';

// NOUVEAUX COMPOSANTS (Avec Modales intégrées)
import ModuleList from './components/module/ModuleList'; // ou './components/maquette/ModuleList' selon ton choix
import UEList from './components/ue/UEList';
import ECList from './components/ec/ECList';

// ==================== ENSEIGNANT ====================
import EnseignantList from './components/EnseignantList';
import EnseignantForm from './components/enseignant/EnseignantForm';
import ResponsableList from './components/enseignant/ResponsableList';
import ResponsableForm from './components/enseignant/ResponsableForm';
import CoordinateurList from './components/enseignant/CoordinateurList';
import CoordinateurForm from './components/enseignant/CoordinateurForm';

// --- DEROULEMENT ENSEIGNEMENT ---
import SeanceList from './components/deroulement-enseignement/seance/SeanceList';
import SeanceForm from './components/deroulement-enseignement/seance/SeanceForm';
import ProgressionBoard from './components/deroulement-enseignement/progression/ProgressionBoard';
import StatistiquesBoard from './components/deroulement-enseignement/progression/StatistiquesBoard';

function App() {
    return (
        <Router>
            <Routes>
                {/* LOGIN (Hors Layout) */}
                <Route path="/login" element={<Login />} />
                <Route path="/" element={<Navigate to="/login" />} />

                {/* APPLICATION (Avec Sidebar) */}
                <Route element={<MainLayout />}>
                    <Route path="/dashboard" element={<div className="text-center mt-5"><h2>Bienvenue sur le Portail UASZ</h2></div>} />

                    {/* --- FORMATIONS --- */}
                    <Route path="/lst-formations" element={<FormationList />} />
                    <Route path="/ajouter-formation" element={<FormationForm />} />
                    <Route path="/modifier-formation/:id" element={<FormationForm />} />

                    {/* --- FILIERES --- */}
                    <Route path="/lst-filieres" element={<FiliereList />} />
                    <Route path="/ajouter-filiere" element={<FiliereForm />} />
                    <Route path="/modifier-filiere/:id" element={<FiliereForm />} />

                    {/* --- NIVEAUX --- */}
                    <Route path="/niveaux" element={<NiveauList />} />
                    <Route path="/ajouter-niveau" element={<NiveauForm />} />
                    <Route path="/modifier-niveau/:id" element={<NiveauForm />} />

                    {/* --- MAQUETTE (UE, EC, MODULE) --- */}
                    {/* Note: Pas besoin de routes 'ajouter' ou 'modifier' car on utilise des MODALES ici */}
                    <Route path="/lst-modules" element={<ModuleList />} />
                    <Route path="/lst-ues" element={<UEList />} />
                    <Route path="/lst-ecs" element={<ECList />} />
                    {/* ==================== ENSEIGNANTS ==================== */}
                    <Route path="/lst-enseignants" element={<EnseignantList />} />
                    <Route path="/ajouter-enseignant" element={<EnseignantForm />} />
                    <Route path="/modifier-enseignant/:id" element={<EnseignantForm />} />

                    {/* ==================== RESPONSABLES ==================== */}
                    <Route path="/lst-responsables" element={<ResponsableList />} />
                    <Route path="/ajouter-responsable" element={<ResponsableForm />} />
                    <Route path="/modifier-responsable/:id" element={<ResponsableForm />} />

                    {/* ==================== COORDINATEURS ==================== */}
                    <Route path="/lst-coordinateurs" element={<CoordinateurList />} />
                    <Route path="/ajouter-coordinateur" element={<CoordinateurForm />} />
                    <Route path="/modifier-coordinateur/:id" element={<CoordinateurForm />} />
                    {/* --- DEROULEMENT ENSEIGNEMENT --- */}
                    <Route path="/lst-seances" element={<SeanceList />} />
                    <Route path="/ajouter-seance" element={<SeanceForm />} />
                    <Route path="/modifier-seance/:id" element={<SeanceForm />} />
                    <Route path="/progression" element={<ProgressionBoard />} />
                    <Route path="/statistiques" element={<StatistiquesBoard />} />

                    {/* Routes Choix Enseignement */}
                    <Route path="/choix-enseignement" element={<ChoixList />} />
                    <Route path="/choix-enseignement/ajouter" element={<ChoixForm />} />
                    <Route path="/choix-enseignement/modifier/:id" element={<ChoixForm />} />
                    <Route path="/choix-enseignement/detail/:id" element={<ChoixDetail />} />
                    <Route path="/choix-enseignement/enseignant/:id" element={<ChoixList />} />

                    {/* Placeholder Enseignants */}
                    <Route path="/lst-enseignants" element={<div className="p-4">Module Enseignants (À venir)</div>} />
                 
                </Route>

                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}
export default App;
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Layouts
import Sidebar from './components/layout/Sidebar';

// Composants existants
import Dashboard from './components/Dashboard';
// ... vos autres composants

// Composants Choix Enseignement
import ChoixList from './components/choix-enseignement/ChoixList';
import ChoixForm from './components/choix-enseignement/ChoixForm';
import ChoixDetail from './components/choix-enseignement/ChoixDetail';

function App() {
    return (
        <Router>
            <div className="d-flex">
                <Sidebar />
                <main className="flex-grow-1" style={{ marginLeft: '250px' }}>
                    <Routes>
                        {/* Routes existantes */}
                        <Route path="/dashboard" element={<Dashboard />} />

                        {/* Routes Choix Enseignement */}
                        <Route path="/choix-enseignement" element={<ChoixList />} />
                        <Route path="/choix-enseignement/ajouter" element={<ChoixForm />} />
                        <Route path="/choix-enseignement/modifier/:id" element={<ChoixForm />} />
                        <Route path="/choix-enseignement/detail/:id" element={<ChoixDetail />} />
                        <Route path="/choix-enseignement/enseignant/:id" element={<ChoixList />} />

                        {/* Autres routes... */}
                    </Routes>
                </main>
                <ToastContainer position="top-right" autoClose={3000} />
            </div>
        </Router>
    );
}

export default App;