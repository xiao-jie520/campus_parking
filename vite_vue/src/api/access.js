import request from './request.js'

export function recordAccess(data) {
  return request.post('/access/record', data)
}

export function getLatestRecord(plateNumber) {
  return request.get(`/access/latest/${plateNumber}`)
}

export function getAccessRecordList(params) {
  return request.get('/access/record/list', { params })
}
