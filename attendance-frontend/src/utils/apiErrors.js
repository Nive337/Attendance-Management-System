// Matches the ApiError/fieldErrors shape from GlobalExceptionHandler (Phase 3).
export function getFieldError(error, fieldName) {
  const fieldErrors = error?.response?.data?.fieldErrors;
  if (!Array.isArray(fieldErrors)) return null;
  const match = fieldErrors.find((fe) => fe.field === fieldName);
  return match ? match.message : null;
}

export function getErrorMessage(error, fallback = 'Something went wrong. Please try again.') {
  return error?.response?.data?.message || fallback;
}