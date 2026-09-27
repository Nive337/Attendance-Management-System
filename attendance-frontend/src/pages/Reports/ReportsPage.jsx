import { useEffect, useMemo, useState } from 'react';
import CascadingCourseFilter from '../../components/CascadingCourseFilter';
import StatusBadge from '../../components/StatusBadge';
import { useToast } from '../../components/toast/ToastContext';
import { getErrorMessage } from '../../utils/apiErrors';
import { getAttendanceReport } from '../../api/reportsApi';
import { generateMonthlyNotifications, getNotificationsForOffering, dispatchNotifications } from '../../api/notificationsApi';
import './ReportsPage.css';

function currentYearMonth() {
  const now = new Date();
  return { year: now.getFullYear(), month: now.getMonth() + 1 };
}

function monthInputValue(year, month) {
  return `${year}-${String(month).padStart(2, '0')}`;
}

export default function ReportsPage() {
  const { showToast } = useToast();

  const [selectedOffering, setSelectedOffering] = useState(null);
  const [filterMode, setFilterMode] = useState('month'); // 'month' | 'range'
  const initial = currentYearMonth();
  const [year, setYear] = useState(initial.year);
  const [month, setMonth] = useState(initial.month);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [notifications, setNotifications] = useState([]);
  const [notifLoading, setNotifLoading] = useState(false);
  const [generating, setGenerating] = useState(false);
  const [dispatching, setDispatching] = useState(false);

  function fetchReport() {
    if (!selectedOffering) {
      setReport(null);
      return;
    }
    if (filterMode === 'range' && (!startDate || !endDate)) {
      setReport(null);
      return;
    }

    setLoading(true);
    setError(null);
    const params =
      filterMode === 'month'
        ? { courseOfferingId: selectedOffering.id, year, month }
        : { courseOfferingId: selectedOffering.id, startDate, endDate };

    getAttendanceReport(params)
      .then(setReport)
      .catch((err) => setError(getErrorMessage(err, 'Could not load the attendance report.')))
      .finally(() => setLoading(false));
  }

  useEffect(fetchReport, [selectedOffering, filterMode, year, month, startDate, endDate]);

  function fetchNotifications() {
    if (!selectedOffering || filterMode !== 'month') {
      setNotifications([]);
      return;
    }
    setNotifLoading(true);
    getNotificationsForOffering(selectedOffering.id)
      .then(setNotifications)
      .catch(() => showToast('Could not load SMS notification status.', 'error'))
      .finally(() => setNotifLoading(false));
  }

  useEffect(fetchNotifications, [selectedOffering, filterMode]);

  const notificationsForMonth = useMemo(() => {
    const map = new Map();
    notifications
      .filter((n) => n.notifYear === Number(year) && n.notifMonth === Number(month))
      .forEach((n) => map.set(n.studentEnrollmentId, n));
    return map;
  }, [notifications, year, month]);

  const hasPendingOrFailed = notifications.some((n) => n.status === 'PENDING' || n.status === 'FAILED');

  async function handleGenerate() {
    setGenerating(true);
    try {
      const result = await generateMonthlyNotifications(selectedOffering.id, year, month);
      showToast(
        result.length === 0
          ? 'No new or updated notifications needed for this month.'
          : `${result.length} notification(s) created or updated for this month.`,
      );
      fetchNotifications();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setGenerating(false);
    }
  }

  async function handleDispatch() {
    setDispatching(true);
    try {
      const result = await dispatchNotifications(selectedOffering.id);
      const sent = result.filter((n) => n.status === 'SENT').length;
      const failed = result.filter((n) => n.status === 'FAILED').length;
      if (result.length === 0) {
        showToast('No pending notifications to send for this class.');
      } else {
        showToast(`${sent} sent, ${failed} failed.`, failed > 0 && sent === 0 ? 'error' : 'success');
      }
      fetchNotifications();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setDispatching(false);
    }
  }

  function exportCsv() {
    if (!report) return;
    const headers = ['Roll Number', 'Student Name', 'Total Classes', 'Present', 'Absent', 'Late', 'Attendance %', 'Status'];
    const rows = report.students.map((s) => [
      s.rollNumber, s.name, s.totalClasses, s.presentCount, s.absentCount, s.lateCount, s.attendancePercentage, s.status,
    ]);
    const csvContent = [headers, ...rows]
      .map((row) => row.map((cell) => `"${String(cell).replace(/"/g, '""')}"`).join(','))
      .join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    const rangeLabel = filterMode === 'month' ? `${year}-${String(month).padStart(2, '0')}` : `${startDate}_to_${endDate}`;
    link.download = `attendance-${report.subjectCode}-${rangeLabel}.csv`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
  }

  return (
    <div className="reports-page">
      <div className="reports-toolbar no-print">
        <CascadingCourseFilter onSelect={setSelectedOffering} />

        <div className="reports-mode-toggle">
          <button className={filterMode === 'month' ? 'reports-mode-active' : ''} onClick={() => setFilterMode('month')}>
            Month
          </button>
          <button className={filterMode === 'range' ? 'reports-mode-active' : ''} onClick={() => setFilterMode('range')}>
            Date Range
          </button>
        </div>

        {filterMode === 'month' ? (
          <input
            type="month"
            value={monthInputValue(year, month)}
            onChange={(e) => {
              const [y, m] = e.target.value.split('-');
              setYear(Number(y));
              setMonth(Number(m));
            }}
          />
        ) : (
          <div className="reports-range-inputs">
            <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
            <span>to</span>
            <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
          </div>
        )}
      </div>

      {!selectedOffering && (
        <div className="reports-empty no-print">
          Select an academic year, degree, semester, section, and subject/lecturer above to view its attendance report.
        </div>
      )}

      {selectedOffering && filterMode === 'range' && (!startDate || !endDate) && (
        <div className="reports-empty no-print">Select both a start and end date.</div>
      )}

      {selectedOffering && loading && <div className="reports-empty no-print">Loading report…</div>}
      {selectedOffering && !loading && error && <div className="reports-error-banner no-print">{error}</div>}

      {selectedOffering && !loading && !error && report && (
        <>
          <div className="reports-class-header">
            <div>
              <h2>
                {report.subjectName} <span className="reports-class-code">({report.subjectCode})</span>
              </h2>
              <p>
                {report.degreeName} · Semester {report.semester} · Section {report.section} · {report.lecturerName}
              </p>
            </div>
            <div className="reports-period">
              {report.startDate ? `${report.startDate} to ${report.endDate}` : 'All time'}
            </div>
          </div>

          <div className="reports-summary-cards">
            <div className="reports-summary-card">
              <span>Average Attendance</span>
              <strong>{report.summary.averageAttendancePercentage}%</strong>
            </div>
            <div className="reports-summary-card reports-summary-good">
              <span>Above 75%</span>
              <strong>{report.summary.studentsAboveThreshold}</strong>
            </div>
            <div className="reports-summary-card reports-summary-warning">
              <span>Below 75%</span>
              <strong>{report.summary.studentsBelowThreshold}</strong>
            </div>
            <div className="reports-summary-card">
              <span>No Data Yet</span>
              <strong>{report.summary.studentsWithNoData}</strong>
            </div>
            <div className="reports-summary-card">
              <span>Classes Conducted</span>
              <strong>{report.summary.totalSessionsConducted}</strong>
            </div>
            <div className="reports-summary-card">
              <span>Attendance Records</span>
              <strong>{report.summary.totalAttendanceRecords}</strong>
            </div>
          </div>

          <div className="reports-actions no-print">
            <button className="reports-action-btn" onClick={exportCsv} disabled={report.students.length === 0}>
              Export CSV
            </button>
            <button className="reports-action-btn" onClick={() => window.print()}>
              Print Report
            </button>

            {filterMode === 'month' && (
              <>
                <button className="reports-action-btn reports-notify-btn" onClick={handleGenerate} disabled={generating}>
                  {generating ? 'Checking…' : 'Generate Notifications'}
                </button>
                <button
                  className="reports-action-btn reports-dispatch-btn"
                  onClick={handleDispatch}
                  disabled={dispatching || !hasPendingOrFailed}
                >
                  {dispatching ? 'Sending…' : 'Send Pending SMS (this class)'}
                </button>
              </>
            )}
          </div>

          {filterMode === 'range' && (
            <p className="reports-sms-note no-print">
              SMS notifications are calculated per calendar month — switch to "Month" above to generate or send them.
            </p>
          )}

          <div className="reports-table-wrap">
            <table className="reports-table">
              <thead>
                <tr>
                  <th>Roll No.</th>
                  <th>Student Name</th>
                  <th>Total Classes</th>
                  <th>Present</th>
                  <th>Absent</th>
                  <th>Late</th>
                  <th>Attendance %</th>
                  <th>Status</th>
                  {filterMode === 'month' && <th className="no-print">SMS Status</th>}
                </tr>
              </thead>
              <tbody>
                {report.students.length === 0 && (
                  <tr>
                    <td colSpan={filterMode === 'month' ? 9 : 8} className="reports-state-row">
                      No students found for this class.
                    </td>
                  </tr>
                )}
                {report.students.map((s) => {
                  const notif = notificationsForMonth.get(s.enrollmentId);
                  return (
                    <tr key={s.enrollmentId} className={s.status === 'WARNING' ? 'reports-row-warning' : ''}>
                      <td>{s.rollNumber}</td>
                      <td>{s.name}</td>
                      <td>{s.totalClasses}</td>
                      <td>{s.presentCount}</td>
                      <td>{s.absentCount}</td>
                      <td>{s.lateCount}</td>
                      <td className="reports-percentage-cell">
                        <div className="reports-progress-track">
                          <div
                            className={`reports-progress-fill ${s.status === 'WARNING' ? 'reports-progress-warning' : 'reports-progress-good'}`}
                            style={{ width: `${Math.min(s.attendancePercentage, 100)}%` }}
                          />
                        </div>
                        <span>{s.totalClasses === 0 ? '—' : `${s.attendancePercentage}%`}</span>
                      </td>
                      <td>
                        <StatusBadge status={s.status} />
                      </td>
                      {filterMode === 'month' && (
                        <td className="no-print">
                          {notifLoading ? '…' : notif ? <StatusBadge status={notif.status} /> : '—'}
                        </td>
                      )}
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}