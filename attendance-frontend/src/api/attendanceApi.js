import axiosClient from './axiosClient';

export function getAttendanceRoster(courseOfferingId, date, sessionNumber) {
  return axiosClient
    .get('/api/attendance/roster', { params: { courseOfferingId, date, session: sessionNumber } })
    .then((res) => res.data);
}

export function submitAttendance(payload) {
  return axiosClient.post('/api/attendance/submit', payload).then((res) => res.data);
}