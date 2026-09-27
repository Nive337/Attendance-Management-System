import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import CascadingCourseFilter from '../../components/CascadingCourseFilter';
import Modal from '../../components/Modal';
import { useToast } from '../../components/toast/ToastContext';
import { getErrorMessage } from '../../utils/apiErrors';
import { getAttendanceRoster, submitAttendance } from '../../api/attendanceApi';
import './AttendancePage.css';

function todayIso() {
  const now = new Date();
  const local = new Date(now.getTime() - now.getTimezoneOffset() * 60000);
  return local.toISOString().slice(0, 10);
}

function formatDisplayDate(iso) {
  return new Date(`${iso}T00:00:00`).toLocaleDateString(undefined, {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

const STATUS_OPTIONS = [
  { value: 'PRESENT', label: 'Present', symbol: '✓' },
  { value: 'ABSENT', label: 'Absent', symbol: '✕' },
  { value: 'LATE', label: 'Late', symbol: '⏱' },
];

export default function AttendancePage() {
  const { showToast } = useToast();

  const [selectedOffering, setSelectedOffering] = useState(null);
  const [date, setDate] = useState(todayIso());
  const [sessionNumber, setSessionNumber] = useState(1);

  const [roster, setRoster] = useState(null);
  const [records, setRecords] = useState([]);
  const [savedSnapshot, setSavedSnapshot] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [confirmation, setConfirmation] = useState(null);

  const isDirty = roster != null && JSON.stringify(records) !== savedSnapshot;
  const isDirtyRef = useRef(false);
  useEffect(() => {
    isDirtyRef.current = isDirty;
  }, [isDirty]);

  // Warn on tab close/refresh if there's unsaved work.
  useEffect(() => {
    function handleBeforeUnload(e) {
      if (isDirtyRef.current) {
        e.preventDefault();
        e.returnValue = '';
      }
    }
    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => window.removeEventListener('beforeunload', handleBeforeUnload);
  }, []);

  useEffect(() => {
    if (!selectedOffering) {
      setRoster(null);
      setRecords([]);
      setSavedSnapshot('');
      return;
    }

    setLoading(true);
    setError(null);
    getAttendanceRoster(selectedOffering.id, date, sessionNumber)
      .then((data) => {
        setRoster(data);
        const initialRecords = data.students.map((s) => ({
          enrollmentId: s.enrollmentId,
          rollNumber: s.rollNumber,
          name: s.name,
          status: s.status,
          note: s.note || '',
        }));
        setRecords(initialRecords);
        setSavedSnapshot(JSON.stringify(initialRecords));
      })
      .catch((err) => setError(getErrorMessage(err, 'Could not load the attendance roster.')))
      .finally(() => setLoading(false));
  }, [selectedOffering, date, sessionNumber]);

  const handleOfferingSelect = useCallback((offering) => {
  if (isDirtyRef.current) {
    showToast("Switched class before saving — the previous roster's unsaved changes were discarded.", 'error');
  }
  setSelectedOffering(offering);
}, [showToast]);

  function handleDateChange(newDate) {
    if (isDirty && !window.confirm('You have unsaved attendance changes. Discard them and switch date?')) {
      return;
    }
    setDate(newDate);
  }

  function handleSessionChange(newSession) {
    if (isDirty && !window.confirm('You have unsaved attendance changes. Discard them and switch session?')) {
      return;
    }
    setSessionNumber(newSession);
  }

  const filteredRecords = useMemo(() => {
    const q = searchTerm.trim().toLowerCase();
    if (!q) return records;
    return records.filter((r) => r.name.toLowerCase().includes(q) || r.rollNumber.toLowerCase().includes(q));
  }, [records, searchTerm]);

  const summary = useMemo(() => {
    const total = records.length;
    const present = records.filter((r) => r.status === 'PRESENT').length;
    const absent = records.filter((r) => r.status === 'ABSENT').length;
    const late = records.filter((r) => r.status === 'LATE').length;
    const rate = total === 0 ? 0 : Math.round((present / total) * 10000) / 100;
    return { total, present, absent, late, rate };
  }, [records]);

  function setRowStatus(enrollmentId, status) {
    setRecords((prev) => prev.map((r) => (r.enrollmentId === enrollmentId ? { ...r, status } : r)));
  }

  function setRowNote(enrollmentId, note) {
    setRecords((prev) => prev.map((r) => (r.enrollmentId === enrollmentId ? { ...r, note } : r)));
  }

  function markAllVisible(status) {
    const visibleIds = new Set(filteredRecords.map((r) => r.enrollmentId));
    setRecords((prev) => prev.map((r) => (visibleIds.has(r.enrollmentId) ? { ...r, status } : r)));
  }

  async function handleSubmit() {
    setSubmitting(true);
    try {
      const payload = {
        courseOfferingId: selectedOffering.id,
        attendanceDate: date,
        sessionNumber: Number(sessionNumber),
        records: records.map((r) => ({ enrollmentId: r.enrollmentId, status: r.status, note: r.note })),
      };
      const result = await submitAttendance(payload);
      setSavedSnapshot(JSON.stringify(records));
      setConfirmation(result);
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="attendance-page">
      <div className="attendance-toolbar">
        <CascadingCourseFilter onSelect={handleOfferingSelect} />

        <label className="attendance-date-field">
          <span>Date</span>
          <input type="date" value={date} onChange={(e) => handleDateChange(e.target.value)} />
        </label>

        <label className="attendance-session-field">
          <span>Session / Period</span>
          <input
            type="number"
            min="1"
            max="20"
            value={sessionNumber}
            onChange={(e) => handleSessionChange(e.target.value)}
          />
        </label>
      </div>

      {!selectedOffering && (
        <div className="attendance-empty">
          Select an academic year, degree, semester, section, and subject/lecturer above to take attendance.
        </div>
      )}

      {selectedOffering && loading && <div className="attendance-empty">Loading roster…</div>}

      {selectedOffering && !loading && error && <div className="attendance-error-banner">{error}</div>}

      {selectedOffering && !loading && !error && roster && (
        <>
          <div className="attendance-class-header">
            <div>
              <h2>
                {roster.subjectName} <span className="attendance-class-code">({roster.subjectCode})</span>
              </h2>
              <p>
                {roster.degreeName} · Semester {roster.semester} · Section {roster.section} · {roster.lecturerName}
              </p>
            </div>
            <div className="attendance-class-date">{formatDisplayDate(date)}</div>
          </div>

          {roster.alreadySubmitted && (
            <div className="attendance-info-banner">
              Attendance for this date and session was already recorded — you're viewing it below and can correct
              it before resubmitting.
            </div>
          )}

          {isDirty && <div className="attendance-dirty-banner">You have unsaved changes.</div>}

          {records.length === 0 ? (
            <div className="attendance-empty">No students are enrolled in this class yet.</div>
          ) : (
            <>
              <div className="attendance-row-toolbar">
                <input
                  className="attendance-search"
                  type="text"
                  placeholder="Search student by name or roll number…"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
                <button className="attendance-mark-all attendance-mark-present" onClick={() => markAllVisible('PRESENT')}>
                  Mark All Present
                </button>
                <button className="attendance-mark-all attendance-mark-absent" onClick={() => markAllVisible('ABSENT')}>
                  Mark All Absent
                </button>
              </div>

              <div className="attendance-table-wrap">
                <table className="attendance-table">
                  <thead>
                    <tr>
                      <th>Roll No.</th>
                      <th>Student Name</th>
                      <th>Status</th>
                      <th>Notes</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredRecords.length === 0 && (
                      <tr>
                        <td colSpan={4} className="attendance-state-row">
                          No students match "{searchTerm}".
                        </td>
                      </tr>
                    )}
                    {filteredRecords.map((r) => (
                      <tr key={r.enrollmentId}>
                        <td>{r.rollNumber}</td>
                        <td>{r.name}</td>
                        <td>
                          <div className="status-group">
                            {STATUS_OPTIONS.map((opt) => (
                              <button
                                key={opt.value}
                                type="button"
                                className={`status-btn status-btn-${opt.value.toLowerCase()} ${
                                  r.status === opt.value ? 'status-btn-on' : ''
                                }`}
                                title={opt.label}
                                aria-label={opt.label}
                                onClick={() => setRowStatus(r.enrollmentId, opt.value)}
                              >
                                {opt.symbol}
                              </button>
                            ))}
                          </div>
                        </td>
                        <td>
                          <input
                            className="attendance-note-input"
                            type="text"
                            placeholder="Add note…"
                            value={r.note}
                            maxLength={255}
                            onChange={(e) => setRowNote(r.enrollmentId, e.target.value)}
                          />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div className="attendance-summary-bar">
                <div>
                  <span>Total Students</span>
                  <strong>{summary.total}</strong>
                </div>
                <div>
                  <span>Present</span>
                  <strong className="attendance-summary-present">{summary.present}</strong>
                </div>
                <div>
                  <span>Absent</span>
                  <strong className="attendance-summary-absent">{summary.absent}</strong>
                </div>
                <div>
                  <span>Late</span>
                  <strong className="attendance-summary-late">{summary.late}</strong>
                </div>
                <div>
                  <span>Attendance Rate</span>
                  <strong>{summary.rate}%</strong>
                </div>

                <button className="attendance-submit-btn" onClick={handleSubmit} disabled={submitting}>
                  {submitting ? 'Submitting…' : 'Submit Attendance'}
                </button>
              </div>
            </>
          )}
        </>
      )}

      {confirmation && (
        <Modal title="Attendance Submitted" onClose={() => setConfirmation(null)} width={420}>
          <div className="attendance-confirm">
            <div className="attendance-confirm-icon">✅</div>
            <p>
              Attendance for <strong>{roster?.subjectName}</strong> on {formatDisplayDate(confirmation.attendanceDate)}{' '}
              (Session {confirmation.sessionNumber}) has been recorded.
            </p>
            <div className="attendance-confirm-stats">
              <div>
                <span>Total</span>
                <strong>{confirmation.totalStudents}</strong>
              </div>
              <div>
                <span>Present</span>
                <strong className="attendance-summary-present">{confirmation.presentCount}</strong>
              </div>
              <div>
                <span>Absent</span>
                <strong className="attendance-summary-absent">{confirmation.absentCount}</strong>
              </div>
              <div>
                <span>Late</span>
                <strong className="attendance-summary-late">{confirmation.lateCount}</strong>
              </div>
            </div>
            <p className="attendance-confirm-rate">Attendance Rate: {confirmation.attendancePercentage}%</p>
            <button className="attendance-confirm-done" onClick={() => setConfirmation(null)}>
              Done
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
}