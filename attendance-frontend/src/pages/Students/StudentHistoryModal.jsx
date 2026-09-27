import { useEffect, useState } from 'react';
import Modal from '../../components/Modal';
import StatusBadge from '../../components/StatusBadge';
import { getStudentHistory } from '../../api/studentsApi';
import './StudentHistoryModal.css';

export default function StudentHistoryModal({ studentId, onClose }) {
  const [history, setHistory] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getStudentHistory(studentId)
      .then(setHistory)
      .catch(() => setError('Could not load academic history.'))
      .finally(() => setLoading(false));
  }, [studentId]);

  return (
    <Modal title={history ? `Academic History — ${history.name}` : 'Academic History'} onClose={onClose} width={680}>
      {loading && <p>Loading…</p>}
      {error && <p className="history-error">{error}</p>}

      {history && (
        <>
          <p className="history-parent">
            Parent: {history.parentName} · {history.parentPhone}
          </p>

          {history.enrollments.length === 0 ? (
            <p className="history-empty">No enrollment history found.</p>
          ) : (
            <table className="history-table">
              <thead>
                <tr>
                  <th>Year</th>
                  <th>Degree</th>
                  <th>Sem</th>
                  <th>Section</th>
                  <th>Roll No.</th>
                  <th>Status</th>
                  <th>Classes</th>
                  <th>Attendance</th>
                </tr>
              </thead>
              <tbody>
                {history.enrollments.map((e) => (
                  <tr key={e.enrollmentId}>
                    <td>{e.academicYearLabel}</td>
                    <td>{e.degreeName}</td>
                    <td>{e.semester}</td>
                    <td>{e.section}</td>
                    <td>{e.rollNumber}</td>
                    <td>
                      <StatusBadge status={e.status} />
                    </td>
                    <td>{e.totalClasses}</td>
                    <td>{e.totalClasses === 0 ? '—' : `${e.attendancePercentage}%`}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </>
      )}
    </Modal>
  );
}