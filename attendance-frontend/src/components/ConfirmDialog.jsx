import Modal from './Modal';
import './ConfirmDialog.css';

export default function ConfirmDialog({
  title,
  message,
  confirmLabel = 'Confirm',
  cancelLabel = 'Cancel',
  danger = false,
  onConfirm,
  onCancel,
  submitting = false,
}) {
  return (
    <Modal title={title} onClose={onCancel} width={400}>
      <p className="confirm-message">{message}</p>
      <div className="confirm-actions">
        <button className="confirm-btn confirm-btn-cancel" onClick={onCancel} disabled={submitting}>
          {cancelLabel}
        </button>
        <button
          className={`confirm-btn ${danger ? 'confirm-btn-danger' : 'confirm-btn-primary'}`}
          onClick={onConfirm}
          disabled={submitting}
        >
          {submitting ? 'Please wait…' : confirmLabel}
        </button>
      </div>
    </Modal>
  );
}