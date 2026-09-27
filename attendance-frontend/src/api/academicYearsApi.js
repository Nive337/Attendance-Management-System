import axiosClient from './axiosClient';

export function getAcademicYears() {
  return axiosClient.get('/api/academic-years').then((res) => res.data);
}