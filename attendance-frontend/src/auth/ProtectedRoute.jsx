import { Navigate, Outlet, useLocation } from 'react-router';
import { useAuth } from './AuthContext';

export default function ProtectedRoute() {
  const { isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    // Remembers where the lecturer was headed, so LoginPage can send them
    // back there instead of always landing on /dashboard.
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
}
