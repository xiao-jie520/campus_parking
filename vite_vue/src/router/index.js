import { createRouter, createWebHashHistory } from 'vue-router'
import { isLoggedIn } from '../utils/auth.js'

const routes = [
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresGuest: true },
  },
  {
    path: '/',
    component: () => import('../views/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
      },
      {
        path: 'vehicles',
        name: 'VehicleList',
        component: () => import('../views/VehicleList.vue'),
      },
      {
        path: 'access',
        name: 'AccessRecord',
        component: () => import('../views/AccessRecord.vue'),
      },
      {
        path: 'access-history',
        name: 'AccessHistory',
        component: () => import('../views/AccessHistory.vue'),
      },
      {
        path: 'parking',
        name: 'ParkingGuide',
        component: () => import('../views/ParkingGuide.vue'),
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('../views/UserList.vue'),
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const loggedIn = isLoggedIn()
  if (to.meta.requiresAuth && !loggedIn) {
    next('/login')
  } else if (to.meta.requiresGuest && loggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
