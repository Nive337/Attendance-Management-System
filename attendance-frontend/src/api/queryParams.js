// Strips null/undefined/empty-string filter values before axios builds the
// query string - otherwise an unselected dropdown (value "") would send
// e.g. "academicYearId=" and Spring would fail to bind it to a Long.
export function cleanParams(params) {
  const result = {};
  Object.entries(params || {}).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== '') {
      result[key] = value;
    }
  });
  return result;
}