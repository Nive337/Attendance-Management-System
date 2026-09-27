import axiosClient from './axiosClient';

export function getLecturers() {
  return axiosClient.get('/api/lecturers').then((res) => res.data);
}