import { useEffect, useState } from 'react';
import CascadingCourseFilter from '../../components/CascadingCourseFilter';
import StatusBadge from '../../components/StatusBadge';
import ConfirmDialog from '../../components/ConfirmDialog';
import { useToast } from '../../components/toast/ToastContext';
import { getErrorMessage } from '../../utils/apiErrors';
import { getRoster, bulkCreateEnrollments, updateEnrollment, updateEnrollmentStatus } from '../../api/enrollmentsApi';
import BulkAddStudentsModal from './BulkAddStudentsModal';
import EditStudentModal from './EditStudentModal';
import StudentHistoryModal from './StudentHistoryModal';
import './StudentsPage.css';

export default function StudentsPage() {
  const { showToast } = useToast();

  const [selectedOffering, setSelectedOffering] = useState(null);
  const [roster, setRoster] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [bulkModalOpen, setBulkModalOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [removeTarget, setRemoveTarget] = useState(null);
  const [removeSubmitting, setRemoveSubmitting] = useState(false);
  const [historyStudentId, setHistoryStudentId] = useState(null);

  function fetchRoster() {
    if (!selectedOffering) {
      setRoster([]);
      return;
    }
    setLoading(true);
    setError(null);
    getRoster(selectedOffering.id)
      .then(setRoster)
      .catch(() => setError('Could not load students for this class.'))
      .finally(() => setLoading(false));
  }

  useEffect(fetchRoster, [selectedOffering]);

  async function handleBulkSubmit(payload) {
    await bulkCreateEnrollments(payload);
    showToast(`${payload.students.length} student(s) added successfully.`);
    setBulkModalOpen(false);
    fetchRoster();
  }

  async function handleEditSubmit(form) {
    await updateEnrollment(editTarget.enrollmentId, form);
    showToast('Student updated successfully.');
    setEditTarget(null);
    fetchRoster();
  }

  async function handleConfirmRemove() {
    setRemoveSubmitting(true);
    try {
      await updateEnrollmentStatus(removeTarget.enrollmentId, 'WITHDRAWN');
      showToast('Student removed from this class.');
      setRemoveTarget(null);
      fetchRoster();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setRemoveSubmitting(false);
    }
  }

  return (
    <div className="students-page">
      <div className="students-toolbar">
        <CascadingCourseFilter onSelect={setSelectedOffering} />
        <button className="students-add-btn" onClick={() => setBulkModalOpen(true)}>
          + Add Students
        </button>
      </div>

      {!selectedOffering && (
        <div className="students-empty">
          Select an academic year, degree, semester, section, and subject/lecturer above to view its student
          roster — or click "Add Students" to enroll students without selecting a class first.
        </div>
      )}

      {selectedOffering && (
        <div className="students-table-wrap">
          <table className="students-table">
            <thead>
              <tr>
                <th>Roll No.</th>
                <th>Name</th>
                <th>Parent Name</th>
                <th>Parent Phone</th>
                <th>Year</th>
                <th>Degree</th>
                <th>Sem</th>
                <th>Section</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading && (
                <tr>
                  <td colSpan={10} className="students-state-row">
                    Loading students…
                  </td>
                </tr>
              )}
              {!loading && error && (
                <tr>
                  <td colSpan={10} className="students-state-row students-state-error">
                    {error}
                  </td>
                </tr>
              )}
              {!loading && !error && roster.length === 0 && (
                <tr>
                  <td colSpan={10} className="students-state-row">
                    No students found for this class yet.
                  </td>
                </tr>
              )}
              {!loading &&
                !error &&
                roster.map((s) => (
                  <tr key={s.enrollmentId}>
                    <td>{s.rollNumber}</td>
                    <td>{s.name}</td>
                    <td>{s.parentName}</td>
                    <td>{s.parentPhone}</td>
                    <td>{s.academicYearLabel}</td>
                    <td>{s.degreeName}</td>
                    <td>{s.semester}</td>
                    <td>{s.section}</td>
                    <td>
                      <StatusBadge status={s.status} />
                    </td>
                    <td className="students-actions">
                      <button onClick={() => setEditTarget(s)}>Modify</button>
                      <button onClick={() => setHistoryStudentId(s.studentId)}>History</button>
                      <button className="students-remove" onClick={() => setRemoveTarget(s)}>
                        Remove
                      </button>
                    </td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
      )}

      {bulkModalOpen && (
        <BulkAddStudentsModal
          prefill={
            selectedOffering
              ? {
                  academicYearId: selectedOffering.academicYearId,
                  degreeId: selectedOffering.degreeId,
                  semester: selectedOffering.semester,
                  section: selectedOffering.section,
                }
              : null
          }
          onSubmit={handleBulkSubmit}
          onClose={() => setBulkModalOpen(false)}
        />
      )}

      {editTarget && (
        <EditStudentModal enrollment={editTarget} onSubmit={handleEditSubmit} onClose={() => setEditTarget(null)} />
      )}

      {removeTarget && (
        <ConfirmDialog
          title="Remove Student"
          message={`Remove ${removeTarget.name} (Roll ${removeTarget.rollNumber}) from this class? Their enrollment record and attendance history are kept, but they'll no longer appear on the roster.`}
          confirmLabel="Remove"
          danger
          submitting={removeSubmitting}
          onConfirm={handleConfirmRemove}
          onCancel={() => setRemoveTarget(null)}
        />
      )}

      {historyStudentId && (
        <StudentHistoryModal studentId={historyStudentId} onClose={() => setHistoryStudentId(null)} />
      )}
    </div>
  );
}