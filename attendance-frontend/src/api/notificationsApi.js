import axiosClient from './axiosClient';

export function getNotificationsForOffering(courseOfferingId) {
  return axiosClient
    .get('/api/reports/attendance/notifications', { params: { courseOfferingId } })
    .then((res) => res.data);
}

export function generateMonthlyNotifications(courseOfferingId, year, month) {
  return axiosClient
    .post('/api/reports/attendance/notifications', null, { params: { courseOfferingId, year, month } })
    .then((res) => res.data);
}

export function dispatchNotifications(courseOfferingId) {
  return axiosClient
    .post('/api/reports/attendance/notifications/dispatch', null, { params: { courseOfferingId } })
    .then((res) => res.data);
}