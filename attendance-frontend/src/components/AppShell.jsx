import { useEffect, useState } from 'react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router';
import { useAuth } from '../auth/AuthContext';
import {
  IconDashboard,
  IconCourses,
  IconStudents,
  IconAttendance,
  IconReports,
  IconLogout,
  IconMenu,
  IconChevronLeft,
  IconChevronRight,
  IconBell,
} from './icons';
import './AppShell.css';

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: IconDashboard },
  { to: '/courses', label: 'Courses', icon: IconCourses },
  { to: '/students', label: 'Students', icon: IconStudents },
  { to: '/attendance', label: 'Attendance', icon: IconAttendance },
  { to: '/reports', label: 'View Reports', icon: IconReports },
];

const PAGE_META = {
  '/dashboard': { title: 'Dashboard', subtitle: 'Overview of your classes and attendance' },
  '/courses': { title: 'Courses', subtitle: 'Manage your course offerings' },
  '/students': { title: 'Students', subtitle: 'Manage enrolled students' },
  '/attendance': { title: 'Attendance', subtitle: "Mark today's attendance" },
  '/reports': { title: 'View Reports', subtitle: 'Attendance reports and trends' },
};

const COLLAPSE_KEY = 'edutrack_sidebar_collapsed';

function initials(name) {
  if (!name) return '';
  return name.split(' ').map((part) => part[0]).join('').slice(0, 2).toUpperCase();
}

function currentPageMeta(pathname) {
  const match = Object.keys(PAGE_META).find((key) => pathname.startsWith(key));
  return match ? PAGE_META[match] : { title: 'EDU-TRACK', subtitle: '' };
}

export default function AppShell() {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [collapsed, setCollapsed] = useState(() => localStorage.getItem(COLLAPSE_KEY) === 'true');
  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    localStorage.setItem(COLLAPSE_KEY, String(collapsed));
  }, [collapsed]);

  useEffect(() => {
    setMobileOpen(false);
  }, [location.pathname]);

  function handleLogout() {
    if (window.confirm('Are you sure you want to logout?')) {
      logout();
      navigate('/login', { replace: true });
    }
  }

  const meta = currentPageMeta(location.pathname);

  return (
    <div className={`shell ${collapsed ? 'shell-collapsed' : ''}`}>
      {mobileOpen && <div className="shell-overlay" onClick={() => setMobileOpen(false)} />}

      <aside className={`shell-sidebar ${mobileOpen ? 'shell-sidebar-open' : ''}`}>
        <div className="shell-brand">
          <div className="shell-brand-mark">🎓</div>
          {!collapsed && <div className="shell-brand-name">EDU-TRACK</div>}
        </div>

        <nav className="shell-nav">
          {NAV_ITEMS.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => `shell-nav-item ${isActive ? 'shell-nav-item-active' : ''}`}
              title={collapsed ? label : undefined}
            >
              <Icon />
              {!collapsed && <span>{label}</span>}
            </NavLink>
          ))}
        </nav>

        <div className="shell-spacer" />

        <button className="shell-nav-item shell-logout" onClick={handleLogout} title={collapsed ? 'Logout' : undefined}>
          <IconLogout />
          {!collapsed && <span>Logout</span>}
        </button>

        <button
          className="shell-collapse-toggle"
          onClick={() => setCollapsed((prev) => !prev)}
          aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        >
          {collapsed ? <IconChevronRight /> : <IconChevronLeft />}
        </button>

        <div className="shell-profile">
          <div className="shell-avatar">{initials(auth?.name)}</div>
          {!collapsed && (
            <div>
              <div className="shell-profile-name">{auth?.name}</div>
              <div className="shell-profile-role">{auth?.department || auth?.role}</div>
            </div>
          )}
        </div>
      </aside>

      <div className="shell-main">
        <header className="shell-topbar">
          <button className="shell-mobile-toggle" onClick={() => setMobileOpen(true)} aria-label="Open menu">
            <IconMenu />
          </button>

          <div>
            <h1>{meta.title}</h1>
            {meta.subtitle && <p>{meta.subtitle}</p>}
          </div>

          <div className="shell-topbar-right">
            {/* Visual only for now - not wired to Phase 11's SMS notifications yet */}
            <button className="shell-bell" aria-label="Notifications">
              <IconBell />
            </button>
          </div>
        </header>

        <main className="shell-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}