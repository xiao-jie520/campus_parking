import request from './request.js'

export function getTodayStatistics() {
  return request.get('/statistics/today')
}

export function getAreaStatistics() {
  return request.get('/statistics/area')
}
