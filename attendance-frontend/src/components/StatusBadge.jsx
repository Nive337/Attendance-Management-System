import './StatusBadge.css';

const LABELS = {
  ACTIVE: 'Active',
  ARCHIVED: 'Archived',
  INACTIVE: 'Inactive',
  GOOD: 'Good',
  WARNING: 'Warning',
  NO_DATA: 'No Data',
  SENT: 'Sent',
  PENDING: 'Pending',
  FAILED: 'Failed',
};

export default function StatusBadge({ status }) {
  const normalized = (status || '').toUpperCase();
  const label = LABELS[normalized] || status;
  const cssKey = normalized.toLowerCase().replace(/_/g, '-');
  return <span className={`status-badge status-badge-${cssKey}`}>{label}</span>;
}