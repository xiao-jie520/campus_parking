import request from './request.js'

export function login(username, password) {
  return request.post('/login', { username, password })
}

export function getVehicleList(params) {
  return request.get('/vehicle/list', { params })
}

export function addVehicle(data) {
  return request.post('/vehicle', data)
}

export function updateVehicle(data) {
  return request.put('/vehicle', data)
}

export function deleteVehicle(id) {
  return request.delete(`/vehicle/${id}`)
}

export function getParkedVehicles() {
  return request.get('/vehicle/parked')
}
