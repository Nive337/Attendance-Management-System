import axiosClient from './axiosClient';
import { cleanParams } from './queryParams';

export function getAttendanceReport(params) {
  return axiosClient.get('/api/reports/attendance', { params: cleanParams(params) }).then((res) => res.data);
}