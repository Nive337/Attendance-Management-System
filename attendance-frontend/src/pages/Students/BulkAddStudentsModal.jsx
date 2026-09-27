import { useEffect, useState } from 'react';
import Modal from '../../components/Modal';
import { getAcademicYears } from '../../api/academicYearsApi';
import { getDegrees } from '../../api/degreesApi';
import { getErrorMessage } from '../../utils/apiErrors';
import './BulkAddStudentsModal.css';

let rowIdCounter = 0;
function emptyRow() {
  return { key: ++rowIdCounter, rollNumber: '', name: '', parentName: '', parentPhone: '' };
}

export default function BulkAddStudentsModal({ prefill, onSubmit, onClose }) {
  const [academicYears, setAcademicYears] = useState([]);
  const [degrees, setDegrees] = useState([]);

  const [academicYearId, setAcademicYearId] = useState(prefill?.academicYearId ?? '');
  const [degreeId, setDegreeId] = useState(prefill?.degreeId ?? '');
  const [semester, setSemester] = useState(prefill?.semester ?? '');
  const [section, setSection] = useState(prefill?.section ?? '');

  const [rows, setRows] = useState([emptyRow(), emptyRow(), emptyRow()]);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState(null);

  useEffect(() => {
    Promise.all([getAcademicYears(), getDegrees()]).then(([years, degreeList]) => {
      setAcademicYears(years.filter((y) => y.status === 'ACTIVE'));
      setDegrees(degreeList.filter((d) => d.status === 'ACTIVE'));
    });
  }, []);

  function updateRow(key, field, value) {
    setRows((prev) => prev.map((r) => (r.key === key ? { ...r, [field]: value } : r)));
  }

  function addRow() {
    setRows((prev) => [...prev, emptyRow()]);
  }

  function removeRow(key) {
    setRows((prev) => (prev.length > 1 ? prev.filter((r) => r.key !== key) : prev));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setFormError(null);

    const nonEmptyRows = rows.filter(
      (r) => r.rollNumber.trim() || r.name.trim() || r.parentName.trim() || r.parentPhone.trim(),
    );
    if (nonEmptyRows.length === 0) {
      setFormError('Add at least one student row.');
      return;
    }

    setSubmitting(true);
    try {
      await onSubmit({
        academicYearId: Number(academicYearId),
        degreeId: Number(degreeId),
        semester: Number(semester),
        section: section.trim().toUpperCase(),
        students: nonEmptyRows.map((r) => ({
          rollNumber: r.rollNumber.trim(),
          name: r.name.trim(),
          parentName: r.parentName.trim(),
          parentPhone: r.parentPhone.trim(),
        })),
      });
    } catch (err) {
      setFormError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  const filledCount = rows.filter((r) => r.rollNumber.trim() || r.name.trim()).length;

  return (
    <Modal title="Add Students" onClose={onClose} width={760}>
      <form onSubmit={handleSubmit} className="bulk-form">
        {formError && <div className="bulk-form-error">{formError}</div>}

        <div className="bulk-form-header">
          <label>
            <span>Academic Year</span>
            <select value={academicYearId} onChange={(e) => setAcademicYearId(e.target.value)} required>
              <option value="">Select year</option>
              {academicYears.map((y) => (
                <option key={y.id} value={y.id}>
                  {y.yearLabel}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>Degree</span>
            <select value={degreeId} onChange={(e) => setDegreeId(e.target.value)} required>
              <option value="">Select degree</option>
              {degrees.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.name}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>Semester</span>
            <select value={semester} onChange={(e) => setSemester(e.target.value)} required>
              <option value="">Select semester</option>
              {Array.from({ length: 12 }, (_, i) => i + 1).map((s) => (
                <option key={s} value={s}>
                  Semester {s}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>Section</span>
            <input
              type="text"
              value={section}
              onChange={(e) => setSection(e.target.value)}
              placeholder="A"
              maxLength={5}
              required
            />
          </label>
        </div>

        <div className="bulk-table-wrap">
          <table className="bulk-table">
            <thead>
              <tr>
                <th>Roll Number</th>
                <th>Student Name</th>
                <th>Parent Name</th>
                <th>Parent Phone</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row.key}>
                  <td>
                    <input value={row.rollNumber} onChange={(e) => updateRow(row.key, 'rollNumber', e.target.value)} />
                  </td>
                  <td>
                    <input value={row.name} onChange={(e) => updateRow(row.key, 'name', e.target.value)} />
                  </td>
                  <td>
                    <input
                      value={row.parentName}
                      onChange={(e) => updateRow(row.key, 'parentName', e.target.value)}
                    />
                  </td>
                  <td>
                    <input
                      value={row.parentPhone}
                      onChange={(e) => updateRow(row.key, 'parentPhone', e.target.value)}
                    />
                  </td>
                  <td>
                    <button
                      type="button"
                      className="bulk-remove-row"
                      onClick={() => removeRow(row.key)}
                      disabled={rows.length === 1}
                      aria-label="Remove row"
                    >
                      ✕
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <button type="button" className="bulk-add-row" onClick={addRow}>
          + Add Row
        </button>

        <div className="bulk-form-actions">
          <button type="button" className="bulk-cancel" onClick={onClose} disabled={submitting}>
            Cancel
          </button>
          <button type="submit" className="bulk-save" disabled={submitting}>
            {submitting ? 'Saving…' : `Save All Students (${filledCount})`}
          </button>
        </div>
      </form>
    </Modal>
  );
}