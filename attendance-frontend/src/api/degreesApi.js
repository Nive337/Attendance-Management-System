import axiosClient from './axiosClient';

export function getDegrees() {
  return axiosClient.get('/api/degrees').then((res) => res.data);
}