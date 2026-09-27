import { useEffect, useState } from 'react';
import axiosClient from '../../api/axiosClient';
import { useAuth } from '../../auth/AuthContext';
import './DashboardPage.css';

export default function DashboardPage() {
  const { auth } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    axiosClient
      .get('/api/dashboard')
      .then((res) => {
        if (!cancelled) setDashboard(res.data);
      })
      .catch(() => {
        if (!cancelled) setError('Could not load dashboard data.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="dashboard-page">
      <p className="dashboard-welcome">Welcome back, {auth?.name?.split(' ')[0]}.</p>

      {loading && <p>Loading dashboard…</p>}
      {error && <p className="dashboard-error">{error}</p>}

      {dashboard && (
        <div className="dashboard-cards">
          <div className="dashboard-card">
            <span>Total Students</span>
            <strong>{dashboard.totalStudents}</strong>
          </div>
          <div className="dashboard-card">
            <span>Today's Attendance</span>
            <strong>{dashboard.todayAttendance}%</strong>
          </div>
          <div className="dashboard-card">
            <span>Below 75%</span>
            <strong>{dashboard.below75}</strong>
          </div>
        </div>
      )}

      <p className="dashboard-note">
        This confirms the shell, navigation, and authenticated API calls all work together.
        Charts, today's classes, and recent activity are a later Phase 12 slice.
      </p>
    </div>
  );
}