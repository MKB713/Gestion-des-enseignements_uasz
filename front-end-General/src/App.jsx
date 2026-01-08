import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Accueil from './pages/Accueil';
import Login from './pages/Login';
import DashboardLayout from './layouts/DashboardLayout';
import PrivateRoute from './components/PrivateRoute';

// Dashboard Components
import StudentDashboard from './pages/dashboards/StudentDashboard';
import TeacherDashboard from './pages/dashboards/TeacherDashboard';
import MasterDashboard from './pages/dashboards/MasterDashboard';
import CoordinatorDashboard from './pages/dashboards/CoordinatorDashboard';
import AdminDashboard from './pages/dashboards/AdminDashboard';
import AdminUsers from './pages/admin/Users';
import AdminDepartments from './pages/admin/Departments';
import AdminFilieres from './pages/admin/Filieres';
import AdminNiveaux from './pages/admin/Niveaux';
import AdminClasses from "./pages/admin/Classes";
import AdminMaquettes from "./pages/admin/Maquettes";
import MaquetteDetails from "./pages/admin/MaquetteDetails";
import AdminUEs from "./pages/admin/UEs";
import AdminModules from "./pages/admin/Modules";
import AdminECs from "./pages/admin/ECs";
import AdminEnseignants from "./pages/admin/Enseignants";
import AdminFormations from './pages/admin/Formations';
import AdminCahierTexte from "./pages/admin/CahierTexte";
import AdminEtudiants from "./pages/admin/Etudiants";
import AdminDeroulementClasses from "./pages/admin/DeroulementClasses";
import AdminPlannings from "./pages/admin/Plannings";
import RepartitionEnseignements from "./pages/admin/RepartitionEnseignements";
import AdminSalles from "./pages/admin/Salles";
import AdminBatiments from "./pages/admin/Batiments";

// Master Pages
import MasterFormations from './pages/master/Formations';
import MasterFilieres from './pages/master/Filieres';
import MasterClasses from './pages/master/Classes';
import MasterStructures from './pages/master/Structures';
import MasterMaquettes from './pages/master/Maquettes';
import MasterModules from './pages/master/Modules';
import MasterUE from './pages/master/UE';
import MasterEC from './pages/master/EC';
import MasterEnseignants from './pages/master/Enseignants';
import MasterEmploiTemps from './pages/master/EmploiTemps';
import MasterCahierTexte from './pages/master/CahierTexte';

// Coordinator Pages
import CoordFormations from './pages/coordinator/Formations';
import CoordFilieres from './pages/coordinator/Filieres';
import CoordClasses from './pages/coordinator/Classes';
import CoordStructures from './pages/coordinator/Structures';
import CoordMaquettes from './pages/coordinator/Maquettes';
import CoordModules from './pages/coordinator/Modules';
import CoordUE from './pages/coordinator/UE';
import CoordEC from './pages/coordinator/EC';
import CoordEnseignants from './pages/coordinator/Enseignants';
import CoordEmploiTemps from './pages/coordinator/EmploiTemps';
import CoordCahierTexte from './pages/coordinator/CahierTexte';
import TimetablePage from './pages/shared/TimetablePage';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Accueil />} />
          <Route path="/login" element={<Login />} />

          {/* Protected Routes Wrapper */}
          <Route element={<DashboardLayout />}>

            <Route element={<PrivateRoute allowedRoles={['ETUDIANT']} />}>
              <Route path="/student/dashboard" element={<StudentDashboard />} />
            </Route>

            <Route element={<PrivateRoute allowedRoles={['ENSEIGNANT']} />}>
              <Route path="/teacher/dashboard" element={<TeacherDashboard />} />
              <Route path="/teacher/emploi-temps" element={<TimetablePage roleTitle="Mon Emploi du Temps" />} />
              <Route path="/teacher/*" element={<TeacherDashboard />} />
            </Route>

            <Route element={<PrivateRoute allowedRoles={['RESPONSABLE_MASTER']} />}>
              <Route path="/master/dashboard" element={<MasterDashboard />} />
              <Route path="/master/formations" element={<MasterFormations />} />
              <Route path="/master/filieres" element={<MasterFilieres />} />
              <Route path="/master/classes" element={<MasterClasses />} />
              <Route path="/master/structures" element={<MasterStructures />} />
              <Route path="/master/maquettes" element={<MasterMaquettes />} />
              <Route path="/master/modules" element={<MasterModules />} />
              <Route path="/master/ues" element={<MasterUE />} />
              <Route path="/master/ecs" element={<MasterEC />} />
              <Route path="/master/enseignants" element={<MasterEnseignants />} />
              <Route path="/master/emploi-temps" element={<TimetablePage roleTitle="Planning Master" />} />
              <Route path="/master/cahier-texte" element={<MasterCahierTexte />} />
            </Route>

            <Route element={<PrivateRoute allowedRoles={['COORDONATEUR_DES_LICENCES']} />}>
              <Route path="/coordinator/dashboard" element={<CoordinatorDashboard />} />
              <Route path="/coordinator/formations" element={<CoordFormations />} />
              <Route path="/coordinator/filieres" element={<CoordFilieres />} />
              <Route path="/coordinator/classes" element={<CoordClasses />} />
              <Route path="/coordinator/structures" element={<CoordStructures />} />
              <Route path="/coordinator/maquettes" element={<CoordMaquettes />} />
              <Route path="/coordinator/modules" element={<CoordModules />} />
              <Route path="/coordinator/ues" element={<CoordUE />} />
              <Route path="/coordinator/ecs" element={<CoordEC />} />
              <Route path="/coordinator/enseignants" element={<CoordEnseignants />} />
              <Route path="/coordinator/emploi-temps" element={<TimetablePage roleTitle="Planning Licence" />} />
              <Route path="/coordinator/cahier-texte" element={<CoordCahierTexte />} />
            </Route>

            <Route element={<PrivateRoute allowedRoles={['ADMIN', 'CHEF_DE_DEPARTEMENT']} />}>
              <Route path="/admin/dashboard" element={<AdminDashboard />} />
              <Route path="/admin/users" element={<AdminUsers />} />
              <Route path="/admin/departments" element={<AdminDepartments />} />
              <Route path="/admin/filieres" element={<AdminFilieres />} />
              <Route path="/admin/niveaux" element={<AdminNiveaux />} />
              <Route path="/admin/classes" element={<PrivateRoute role="ADMIN"><AdminClasses /></PrivateRoute>} />
              <Route path="/admin/maquettes" element={<PrivateRoute role="ADMIN"><AdminMaquettes /></PrivateRoute>} />
              <Route path="/admin/maquette-details" element={<PrivateRoute role="ADMIN"><MaquetteDetails /></PrivateRoute>} />
              <Route path="/admin/maquettes/:id" element={<PrivateRoute role="ADMIN"><MaquetteDetails /></PrivateRoute>} />
              <Route path="/admin/ues" element={<PrivateRoute role="ADMIN"><AdminUEs /></PrivateRoute>} />
              <Route path="/admin/modules" element={<PrivateRoute role="ADMIN"><AdminModules /></PrivateRoute>} />
              <Route path="/admin/ecs" element={<PrivateRoute role="ADMIN"><AdminECs /></PrivateRoute>} />
              <Route path="/admin/enseignants" element={<PrivateRoute role="ADMIN"><AdminEnseignants /></PrivateRoute>} />
              <Route path="/admin/structures" element={<Navigate to="/admin/departments" replace />} />
              <Route path="/admin/formations" element={<AdminFormations />} />
              <Route path="/admin/plannings" element={<AdminPlannings />} />
              <Route path="/admin/salles" element={<AdminSalles />} />
              <Route path="/admin/batiments" element={<AdminBatiments />} />
              <Route path="/admin/cahier-texte" element={<AdminCahierTexte />} />
              <Route path="/admin/etudiants" element={<AdminEtudiants />} />
              <Route path="/admin/deroulement-classes" element={<AdminDeroulementClasses />} />
              <Route path="/admin/repartition" element={<PrivateRoute role="ADMIN"><RepartitionEnseignements /></PrivateRoute>} />
              <Route path="/admin/*" element={<AdminDashboard />} />
            </Route>

          </Route>

        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
