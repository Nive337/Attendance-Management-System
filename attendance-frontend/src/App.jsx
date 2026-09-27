import { AuthProvider } from './auth/AuthContext';
import AppRoutes from './routes';
import './styles/global.css';
import { ToastProvider } from './components/toast/ToastContext';

export default function App() {
  return (
    <AuthProvider>
     <ToastProvider>
      <AppRoutes />
     </ToastProvider>
    </AuthProvider>
  );
}