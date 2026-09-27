import { useState } from 'react';
import Modal from '../../components/Modal';
import { getErrorMessage, getFieldError } from '../../utils/apiErrors';
import './CourseFormModal.css';

const SEMESTERS = Array.from({ length: 12 }, (_, i) => i + 1);

export default function CourseFormModal({ mode, initialCourse, academicYears, degrees, lecturers, onSubmit, onClose }) {
  const [form, setForm] = useState({
    academicYearId: initialCourse?.academicYearId ?? '',
    degreeId: initialCourse?.degreeId ?? '',
    semester: initialCourse?.semester ?? '',
    section: initialCourse?.section ?? '',
    subjectCode: initialCourse?.subjectCode ?? '',
    subjectName: initialCourse?.subjectName ?? '',
    lecturerId: initialCourse?.lecturerId ?? '',
  });
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState(null);
  const [apiError, setApiError] = useState(null);

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setFormError(null);
    setApiError(null);
    setSubmitting(true);
    try {
      await onSubmit({
        academicYearId: Number(form.academicYearId),
        degreeId: Number(form.degreeId),
        semester: Number(form.semester),
        section: form.section.trim().toUpperCase(),
        subjectCode: form.subjectCode.trim(),
        subjectName: form.subjectName.trim(),
        lecturerId: Number(form.lecturerId),
      });
    } catch (err) {
      setApiError(err);
      setFormError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Modal title={mode === 'create' ? 'Add Course' : 'Modify Course'} onClose={onClose} width={520}>
      <form onSubmit={handleSubmit} className="course-form">
        {formError && <div className="course-form-error">{formError}</div>}

        <div className="course-form-grid">
          <label>
            <span>Academic Year</span>
            <select value={form.academicYearId} onChange={(e) => update('academicYearId', e.target.value)} required>
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
            <select value={form.degreeId} onChange={(e) => update('degreeId', e.target.value)} required>
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
            <select value={form.semester} onChange={(e) => update('semester', e.target.value)} required>
              <option value="">Select semester</option>
              {SEMESTERS.map((s) => (
                <option key={s} value={s}>
                  Semester {s}
                </option>
              ))}
            </select>
            {getFieldError(apiError, 'semester') && (
              <small className="course-field-error">{getFieldError(apiError, 'semester')}</small>
            )}
          </label>

          <label>
            <span>Section</span>
            <input
              type="text"
              value={form.section}
              onChange={(e) => update('section', e.target.value)}
              placeholder="A"
              maxLength={5}
              required
            />
            {getFieldError(apiError, 'section') && (
              <small className="course-field-error">{getFieldError(apiError, 'section')}</small>
            )}
          </label>

          <label>
            <span>Subject Code</span>
            <input
              type="text"
              value={form.subjectCode}
              onChange={(e) => update('subjectCode', e.target.value)}
              placeholder="BCOM-DBMS"
              required
            />
            {getFieldError(apiError, 'subjectCode') && (
              <small className="course-field-error">{getFieldError(apiError, 'subjectCode')}</small>
            )}
          </label>

          <label>
            <span>Subject Name</span>
            <input
              type="text"
              value={form.subjectName}
              onChange={(e) => update('subjectName', e.target.value)}
              placeholder="Database Management Systems"
              required
            />
            {getFieldError(apiError, 'subjectName') && (
              <small className="course-field-error">{getFieldError(apiError, 'subjectName')}</small>
            )}
          </label>

          <label className="course-form-span">
            <span>Lecturer</span>
            <select value={form.lecturerId} onChange={(e) => update('lecturerId', e.target.value)} required>
              <option value="">Select lecturer</option>
              {lecturers.map((l) => (
                <option key={l.id} value={l.id}>
                  {l.name}
                  {l.department ? ` — ${l.department}` : ''}
                </option>
              ))}
            </select>
          </label>
        </div>

        <div className="course-form-actions">
          <button type="button" className="course-form-cancel" onClick={onClose} disabled={submitting}>
            Cancel
          </button>
          <button type="submit" className="course-form-save" disabled={submitting}>
            {submitting ? 'Saving…' : 'Save Course'}
          </button>
        </div>
      </form>
    </Modal>
  );
}