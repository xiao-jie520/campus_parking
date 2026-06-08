import request from './request.js'

export function getUserList(params) {
  return request.get('/user/list', { params })
}

export function addUser(data) {
  return request.post('/user', data)
}

export function updateUser(data) {
  return request.put('/user', data)
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

export function updatePassword(data) {
  return request.put('/user/password', data)
}

export function getCurrentUser() {
  return request.get('/user/me')
}

export function updateCurrentUser(data) {
  return request.put('/user/me', data)
}
