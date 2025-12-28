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

// NOUVEAUX COMPOSANTS (Avec Modales intégrées)
import ModuleList from './components/module/ModuleList'; // ou './components/maquette/ModuleList' selon ton choix
import UEList from './components/ue/UEList';
import ECList from './components/ec/ECList';

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

                    {/* Placeholder Enseignants */}
                    <Route path="/lst-enseignants" element={<div className="p-4">Module Enseignants (À venir)</div>} />
                </Route>

                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;