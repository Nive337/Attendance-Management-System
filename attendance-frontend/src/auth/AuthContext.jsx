import { createContext, useContext, useState, useCallback } from 'react';
import { login as loginRequest } from '../api/authApi';
import { saveAuth, loadAuth, clearAuth } from './authStorage';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => loadAuth());

  const login = useCallback(async (email, password, remember) => {
    const data = await loginRequest(email, password);
    saveAuth(data, remember);
    setAuth(data);
    return data;
  }, []);

  const logout = useCallback(() => {
    clearAuth();
    setAuth(null);
  }, []);

  const value = { auth, isAuthenticated: !!auth, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}