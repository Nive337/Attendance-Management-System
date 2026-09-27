import axiosClient from './axiosClient';
import { cleanParams } from './queryParams';

export function searchCourses(filters) {
  return axiosClient.get('/api/courses', { params: cleanParams(filters) }).then((res) => res.data);
}

export function createCourse(payload) {
  return axiosClient.post('/api/courses', payload).then((res) => res.data);
}

export function updateCourse(id, payload) {
  return axiosClient.put(`/api/courses/${id}`, payload).then((res) => res.data);
}

export function updateCourseStatus(id, status) {
  return axiosClient.patch(`/api/courses/${id}/status`, { status }).then((res) => res.data);
}