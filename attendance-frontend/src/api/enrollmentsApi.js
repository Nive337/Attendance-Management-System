import axiosClient from './axiosClient';

export function getRoster(courseOfferingId) {
  return axiosClient.get('/api/enrollments', { params: { courseOfferingId } }).then((res) => res.data);
}

export function bulkCreateEnrollments(payload) {
  return axiosClient.post('/api/enrollments/bulk', payload).then((res) => res.data);
}

export function updateEnrollment(enrollmentId, payload) {
  return axiosClient.put(`/api/enrollments/${enrollmentId}`, payload).then((res) => res.data);
}

export function updateEnrollmentStatus(enrollmentId, status) {
  return axiosClient.patch(`/api/enrollments/${enrollmentId}/status`, { status }).then((res) => res.data);
}