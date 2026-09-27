const STORAGE_KEY = 'edutrack_auth';

// "Remember me" checked -> localStorage (survives browser restart).
// Unchecked -> sessionStorage (cleared when the tab/browser closes).
// Only one copy ever exists at a time - saving to one clears the other.
export function saveAuth(authData, remember) {
  const serialized = JSON.stringify(authData);
  if (remember) {
    localStorage.setItem(STORAGE_KEY, serialized);
    sessionStorage.removeItem(STORAGE_KEY);
  } else {
    sessionStorage.setItem(STORAGE_KEY, serialized);
    localStorage.removeItem(STORAGE_KEY);
  }
}

export function loadAuth() {
  const fromSession = sessionStorage.getItem(STORAGE_KEY);
  if (fromSession) return JSON.parse(fromSession);
  const fromLocal = localStorage.getItem(STORAGE_KEY);
  return fromLocal ? JSON.parse(fromLocal) : null;
}

export function clearAuth() {
  localStorage.removeItem(STORAGE_KEY);
  sessionStorage.removeItem(STORAGE_KEY);
}

export function getToken() {
  const auth = loadAuth();
  return auth ? auth.token : null;
}