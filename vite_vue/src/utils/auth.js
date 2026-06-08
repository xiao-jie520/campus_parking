const TOKEN_KEY = 'jwt_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function isLoggedIn() {
  return !!getToken()
}

export function getUserRole() {
  try {
    const token = getToken()
    if (!token) return null
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.role || null
  } catch {
    return null
  }
}

export function getUsername() {
  try {
    const token = getToken()
    if (!token) return ''
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.username || ''
  } catch {
    return ''
  }
}
