import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import MainLayout from './components/layout/MainLayout';

import SeanceList from './components/seance/SeanceList';
import SeanceForm from './components/seance/SeanceForm';
import ProgressionBoard from './components/progression/ProgressionBoard';
import StatistiquesBoard from './components/progression/StatistiquesBoard';

function App() {
    return (
        <Router>
            <Routes>
                {/* LOGIN (Hors Layout) */}
                <Route path="/login" element={<Login />} />
                <Route path="/" element={<Navigate to="/login" />} />

                {/* APPLICATION (Avec Sidebar) */}
                <Route element={<MainLayout />}>
                    <Route path="/dashboard" element={<div className="text-center mt-5"><h2>Tableau de Bord - Déroulement des Enseignements</h2></div>} />

                    {/* --- SEANCES --- */}
                    <Route path="/lst-seances" element={<SeanceList />} />
                    <Route path="/ajouter-seance" element={<SeanceForm />} />
                    <Route path="/modifier-seance/:id" element={<SeanceForm />} />

                    {/* --- PROGRESSION --- */}
                    <Route path="/progression" element={<ProgressionBoard />} />

                    {/* --- STATISTIQUES --- */}
                    <Route path="/statistiques" element={<StatistiquesBoard />} />
                </Route>
            </Routes>
        </Router>
    );
}

export default App;
