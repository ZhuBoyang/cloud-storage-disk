import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('./App.vue'),
    redirect: '/login'
  },
  {
    path: '/login',
    component: () => import('./pages/LoginPage.vue')
  },
  {
    path: '/register',
    component: () => import('./pages/RegisterPage.vue')
  },
  {
    path: '/reset',
    component: () => import('./pages/ResetPwd.vue')
  },
  {
    path: '/index',
    component: () => import('./pages/HomePage.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        component: () => import('./pages/DashboardPage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
