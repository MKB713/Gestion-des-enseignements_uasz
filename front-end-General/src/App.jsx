import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
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
              <Route path="/teacher/*" element={<TeacherDashboard />} />
            </Route>

            <Route element={<PrivateRoute allowedRoles={['RESPONSABLE_MASTER']} />}>
              <Route path="/master/*" element={<MasterDashboard />} />
            </Route>

            <Route element={<PrivateRoute allowedRoles={['COORDONATEUR_DES_LICENCES']} />}>
              <Route path="/coordinator/*" element={<CoordinatorDashboard />} />
            </Route>

            <Route element={<PrivateRoute allowedRoles={['ADMIN', 'CHEF_DE_DEPARTEMENT']} />}>
              <Route path="/admin/*" element={<AdminDashboard />} />
            </Route>

          </Route>

        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
