import { Routes, Route, Navigate } from 'react-router';
import ProtectedRoute from './auth/ProtectedRoute';
import AppShell from './components/AppShell';
import ComingSoonPage from './components/ComingSoonPage';
import LoginPage from './pages/Login/LoginPage';
import DashboardPage from './pages/Dashboard/DashboardPage';
import CoursesPage from './pages/Courses/CoursesPage';
import StudentsPage from './pages/Students/StudentsPage';
import AttendancePage from './pages/Attendance/AttendancePage';
import ReportsPage from './pages/Reports/ReportsPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppShell />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/courses" element={<CoursesPage />} />
          <Route path="/students" element={<StudentsPage />} />
          <Route path="/attendance" element={<AttendancePage />} />
          <Route path="/reports" element={<ReportsPage />} />
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}