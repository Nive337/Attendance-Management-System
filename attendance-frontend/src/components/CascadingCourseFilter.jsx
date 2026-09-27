import { useEffect, useState } from 'react';
import { getAcademicYears } from '../api/academicYearsApi';
import { getDegrees } from '../api/degreesApi';
import { searchCourses } from '../api/coursesApi';
import './CascadingCourseFilter.css';

// Reusable Year -> Degree -> Semester -> Section -> Subject/Lecturer chain.
// Sections and the final offering list are both derived client-side from one
// fetch per year+degree+semester change - there's no separate "sections" endpoint,
// since Section is just a string field on CourseOffering, not its own entity.
export default function CascadingCourseFilter({ onSelect }) {
  const [academicYears, setAcademicYears] = useState([]);
  const [degrees, setDegrees] = useState([]);
  const [referenceError, setReferenceError] = useState(null);

  const [academicYearId, setAcademicYearId] = useState('');
  const [degreeId, setDegreeId] = useState('');
  const [semester, setSemester] = useState('');
  const [section, setSection] = useState('');
  const [courseOfferingId, setCourseOfferingId] = useState('');

  const [offeringsForSemester, setOfferingsForSemester] = useState([]);
  const [loadingOfferings, setLoadingOfferings] = useState(false);
  const [offeringsError, setOfferingsError] = useState(null);

  useEffect(() => {
    Promise.all([getAcademicYears(), getDegrees()])
      .then(([years, degreeList]) => {
        setAcademicYears(years.filter((y) => y.status === 'ACTIVE'));
        setDegrees(degreeList.filter((d) => d.status === 'ACTIVE'));
      })
      .catch(() => setReferenceError('Could not load academic years or degrees.'));
  }, []);

  useEffect(() => {
    setSection('');
    setCourseOfferingId('');
    onSelect(null);

    if (!academicYearId || !degreeId || !semester) {
      setOfferingsForSemester([]);
      return;
    }

    setLoadingOfferings(true);
    setOfferingsError(null);
    searchCourses({ academicYearId, degreeId, semester, status: 'ACTIVE' })
      .then(setOfferingsForSemester)
      .catch(() => setOfferingsError('Could not load classes for this selection.'))
      .finally(() => setLoadingOfferings(false));
  }, [academicYearId, degreeId, semester, onSelect]);

  useEffect(() => {
    setCourseOfferingId('');
    onSelect(null);
  }, [section, onSelect]);

  useEffect(() => {
    if (!courseOfferingId) return;
    const offering = offeringsForSemester.find((o) => String(o.id) === String(courseOfferingId));
    onSelect(offering || null);
  }, [courseOfferingId, offeringsForSemester, onSelect]);

  const availableSections = [...new Set(offeringsForSemester.map((o) => o.section))].sort();
  const offeringsForSection = offeringsForSemester.filter((o) => o.section === section);

  return (
    <div className="class-filter">
      {referenceError && <div className="class-filter-error">{referenceError}</div>}

      <select value={academicYearId} onChange={(e) => setAcademicYearId(e.target.value)}>
        <option value="">Academic Year</option>
        {academicYears.map((y) => (
          <option key={y.id} value={y.id}>
            {y.yearLabel}
          </option>
        ))}
      </select>

      <select value={degreeId} onChange={(e) => setDegreeId(e.target.value)} disabled={!academicYearId}>
        <option value="">Degree</option>
        {degrees.map((d) => (
          <option key={d.id} value={d.id}>
            {d.name}
          </option>
        ))}
      </select>

      <select value={semester} onChange={(e) => setSemester(e.target.value)} disabled={!degreeId}>
        <option value="">Semester</option>
        {Array.from({ length: 12 }, (_, i) => i + 1).map((s) => (
          <option key={s} value={s}>
            Semester {s}
          </option>
        ))}
      </select>

      <select
        value={section}
        onChange={(e) => setSection(e.target.value)}
        disabled={!semester || loadingOfferings || availableSections.length === 0}
      >
        <option value="">Section</option>
        {availableSections.map((s) => (
          <option key={s} value={s}>
            Section {s}
          </option>
        ))}
      </select>

      <select
        value={courseOfferingId}
        onChange={(e) => setCourseOfferingId(e.target.value)}
        disabled={!section || offeringsForSection.length === 0}
      >
        <option value="">Subject / Lecturer</option>
        {offeringsForSection.map((o) => (
          <option key={o.id} value={o.id}>
            {o.subjectName} — {o.lecturerName}
          </option>
        ))}
      </select>

      {loadingOfferings && <span className="class-filter-hint">Loading classes…</span>}
      {offeringsError && <span className="class-filter-hint class-filter-hint-error">{offeringsError}</span>}
      {!loadingOfferings && semester && offeringsForSemester.length === 0 && (
        <span className="class-filter-hint">No course offerings found for this year/degree/semester.</span>
      )}
    </div>
  );
}