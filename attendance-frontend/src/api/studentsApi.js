import axiosClient from './axiosClient';

export function getStudentHistory(studentId) {
  return axiosClient.get(`/api/students/${studentId}/history`).then((res) => res.data);
}