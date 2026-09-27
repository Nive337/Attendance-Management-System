import { useState } from 'react';
import Modal from '../../components/Modal';
import { getErrorMessage, getFieldError } from '../../utils/apiErrors';
import './EditStudentModal.css';

export default function EditStudentModal({ enrollment, onSubmit, onClose }) {
  const [form, setForm] = useState({
    rollNumber: enrollment.rollNumber,
    name: enrollment.name,
    parentName: enrollment.parentName,
    parentPhone: enrollment.parentPhone,
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
      await onSubmit(form);
    } catch (err) {
      setApiError(err);
      setFormError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Modal title="Modify Student" onClose={onClose} width={420}>
      <form onSubmit={handleSubmit} className="edit-student-form">
        {formError && <div className="edit-student-error">{formError}</div>}

        <label>
          <span>Roll Number</span>
          <input value={form.rollNumber} onChange={(e) => update('rollNumber', e.target.value)} required />
          {getFieldError(apiError, 'rollNumber') && (
            <small className="edit-student-field-error">{getFieldError(apiError, 'rollNumber')}</small>
          )}
        </label>

        <label>
          <span>Student Name</span>
          <input value={form.name} onChange={(e) => update('name', e.target.value)} required />
        </label>

        <label>
          <span>Parent Name</span>
          <input value={form.parentName} onChange={(e) => update('parentName', e.target.value)} required />
        </label>

        <label>
          <span>Parent Phone</span>
          <input value={form.parentPhone} onChange={(e) => update('parentPhone', e.target.value)} required />
          {getFieldError(apiError, 'parentPhone') && (
            <small className="edit-student-field-error">{getFieldError(apiError, 'parentPhone')}</small>
          )}
        </label>

        <div className="edit-student-actions">
          <button type="button" onClick={onClose} disabled={submitting} className="edit-student-cancel">
            Cancel
          </button>
          <button type="submit" disabled={submitting} className="edit-student-save">
            {submitting ? 'Saving…' : 'Save Changes'}
          </button>
        </div>
      </form>
    </Modal>
  );
}