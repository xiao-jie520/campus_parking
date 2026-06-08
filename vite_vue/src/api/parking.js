import request from './request.js'

// 区域
export function getAreaList() {
  return request.get('/parking/area/list')
}

export function addArea(data) {
  return request.post('/parking/area', data)
}

export function updateArea(data) {
  return request.put('/parking/area', data)
}

export function deleteArea(id) {
  return request.delete(`/parking/area/${id}`)
}

// 车位
export function getSpotList(areaId) {
  return request.get('/parking/spot/list', { params: { areaId } })
}

export function addSpot(data) {
  return request.post('/parking/spot', data)
}

export function deleteSpot(id) {
  return request.delete(`/parking/spot/${id}`)
}

export function assignSpot(areaId, vehicleId, plateNumber) {
  return request.post('/parking/spot/assign', null, { params: { areaId, vehicleId, plateNumber } })
}

export function releaseSpot(spotId) {
  return request.post('/parking/spot/release', null, { params: { spotId } })
}

export function releaseSpotByPlate(plateNumber) {
  return request.post('/parking/spot/release-by-plate', null, { params: { plateNumber } })
}
