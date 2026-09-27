import { useEffect, useState } from 'react';
import { searchCourses, createCourse, updateCourse, updateCourseStatus } from '../../api/coursesApi';
import { getAcademicYears } from '../../api/academicYearsApi';
import { getDegrees } from '../../api/degreesApi';
import { getLecturers } from '../../api/lecturersApi';
import { useDebounce } from '../../hooks/useDebounce';
import { useToast } from '../../components/toast/ToastContext';
import { getErrorMessage } from '../../utils/apiErrors';
import StatusBadge from '../../components/StatusBadge';
import ConfirmDialog from '../../components/ConfirmDialog';
import CourseFormModal from './CourseFormModal';
import './CoursesPage.css';

const EMPTY_FILTERS = { academicYearId: '', degreeId: '', semester: '', status: '' };

export default function CoursesPage() {
  const { showToast } = useToast();

  const [academicYears, setAcademicYears] = useState([]);
  const [degrees, setDegrees] = useState([]);
  const [lecturers, setLecturers] = useState([]);
  const [referenceDataError, setReferenceDataError] = useState(null);

  const [filters, setFilters] = useState(EMPTY_FILTERS);
  const [searchInput, setSearchInput] = useState('');
  const debouncedSearch = useDebounce(searchInput, 300);

  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [formModal, setFormModal] = useState(null); // { mode: 'create' | 'edit', course? }
  const [statusTarget, setStatusTarget] = useState(null);
  const [statusSubmitting, setStatusSubmitting] = useState(false);

  useEffect(() => {
    Promise.all([getAcademicYears(), getDegrees(), getLecturers()])
      .then(([years, degreeList, lecturerList]) => {
        setAcademicYears(years);
        setDegrees(degreeList);
        setLecturers(lecturerList);
      })
      .catch(() => setReferenceDataError('Could not load academic years, degrees, or lecturers.'));
  }, []);

  function fetchCourses() {
    setLoading(true);
    setError(null);
    searchCourses({ ...filters, search: debouncedSearch })
      .then(setCourses)
      .catch(() => setError('Could not load courses.'))
      .finally(() => setLoading(false));
  }

  useEffect(fetchCourses, [filters, debouncedSearch]);

  const hasActiveFilters =
    debouncedSearch || filters.academicYearId || filters.degreeId || filters.semester || filters.status;

  const canAddCourse = academicYears.length > 0 && degrees.length > 0 && lecturers.length > 0;

  async function handleFormSubmit(payload) {
    if (formModal.mode === 'create') {
      await createCourse(payload);
      showToast('Course created successfully.');
    } else {
      await updateCourse(formModal.course.id, payload);
      showToast('Course updated successfully.');
    }
    setFormModal(null);
    fetchCourses();
  }

  async function handleConfirmStatusChange() {
    const nextStatus = statusTarget.status === 'ACTIVE' ? 'ARCHIVED' : 'ACTIVE';
    setStatusSubmitting(true);
    try {
      await updateCourseStatus(statusTarget.id, nextStatus);
      showToast(nextStatus === 'ARCHIVED' ? 'Course deactivated.' : 'Course reactivated.');
      setStatusTarget(null);
      fetchCourses();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setStatusSubmitting(false);
    }
  }

  return (
    <div className="courses-page">
      {referenceDataError && <div className="courses-banner courses-banner-error">{referenceDataError}</div>}

      <div className="courses-toolbar">
        <input
          className="courses-search"
          type="text"
          placeholder="Search by subject or lecturer…"
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
        />

        <select
          value={filters.academicYearId}
          onChange={(e) => setFilters((f) => ({ ...f, academicYearId: e.target.value }))}
        >
          <option value="">All Academic Years</option>
          {academicYears.map((y) => (
            <option key={y.id} value={y.id}>
              {y.yearLabel}
            </option>
          ))}
        </select>

        <select value={filters.degreeId} onChange={(e) => setFilters((f) => ({ ...f, degreeId: e.target.value }))}>
          <option value="">All Degrees</option>
          {degrees.map((d) => (
            <option key={d.id} value={d.id}>
              {d.name}
            </option>
          ))}
        </select>

        <select value={filters.semester} onChange={(e) => setFilters((f) => ({ ...f, semester: e.target.value }))}>
          <option value="">All Semesters</option>
          {Array.from({ length: 12 }, (_, i) => i + 1).map((s) => (
            <option key={s} value={s}>
              Semester {s}
            </option>
          ))}
        </select>

        <select value={filters.status} onChange={(e) => setFilters((f) => ({ ...f, status: e.target.value }))}>
          <option value="">All Statuses</option>
          <option value="ACTIVE">Active</option>
          <option value="ARCHIVED">Archived</option>
        </select>

        <button
          className="courses-add-btn"
          onClick={() => setFormModal({ mode: 'create' })}
          disabled={!canAddCourse}
          title={canAddCourse ? undefined : 'Add at least one academic year, degree, and lecturer first'}
        >
          + Add Course
        </button>
      </div>

      <div className="courses-table-wrap">
        <table className="courses-table">
          <thead>
            <tr>
              <th>Academic Year</th>
              <th>Degree</th>
              <th>Semester</th>
              <th>Section</th>
              <th>Subject</th>
              <th>Subject Code</th>
              <th>Lecturer</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={9} className="courses-state-row">
                  Loading courses…
                </td>
              </tr>
            )}

            {!loading && error && (
              <tr>
                <td colSpan={9} className="courses-state-row courses-state-error">
                  {error}
                </td>
              </tr>
            )}

            {!loading && !error && courses.length === 0 && (
              <tr>
                <td colSpan={9} className="courses-state-row">
                  {hasActiveFilters
                    ? 'No courses match your search or filters.'
                    : 'No courses yet. Click "Add Course" to create the first one.'}
                </td>
              </tr>
            )}

            {!loading &&
              !error &&
              courses.map((course) => (
                <tr key={course.id}>
                  <td>{course.academicYearLabel}</td>
                  <td>{course.degreeName}</td>
                  <td>{course.semester}</td>
                  <td>{course.section}</td>
                  <td>{course.subjectName}</td>
                  <td>{course.subjectCode}</td>
                  <td>{course.lecturerName}</td>
                  <td>
                    <StatusBadge status={course.status} />
                  </td>
                  <td className="courses-actions">
                    <button onClick={() => setFormModal({ mode: 'edit', course })}>Edit</button>
                    <button
                      className={course.status === 'ACTIVE' ? 'courses-deactivate' : 'courses-activate'}
                      onClick={() => setStatusTarget(course)}
                    >
                      {course.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                    </button>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      {formModal && (
        <CourseFormModal
          mode={formModal.mode}
          initialCourse={formModal.course}
          academicYears={academicYears.filter((y) => y.status === 'ACTIVE')}
          degrees={degrees.filter((d) => d.status === 'ACTIVE')}
          lecturers={lecturers}
          onSubmit={handleFormSubmit}
          onClose={() => setFormModal(null)}
        />
      )}

      {statusTarget && (
        <ConfirmDialog
          title={statusTarget.status === 'ACTIVE' ? 'Deactivate Course' : 'Reactivate Course'}
          message={
            statusTarget.status === 'ACTIVE'
              ? `Deactivate ${statusTarget.subjectName} (${statusTarget.section})? Lecturers won't be able to take attendance for it, but all existing history is kept.`
              : `Reactivate ${statusTarget.subjectName} (${statusTarget.section})?`
          }
          confirmLabel={statusTarget.status === 'ACTIVE' ? 'Deactivate' : 'Reactivate'}
          danger={statusTarget.status === 'ACTIVE'}
          submitting={statusSubmitting}
          onConfirm={handleConfirmStatusChange}
          onCancel={() => setStatusTarget(null)}
        />
      )}
    </div>
  );
}