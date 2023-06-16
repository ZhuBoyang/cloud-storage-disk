import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('./App.vue'),
    redirect: '/login'
  },
  {
    path: '/login',
    component: () => import('./view/LoginView.vue')
  },
  {
    path: '/register',
    component: () => import('./view/RegisterView.vue')
  },
  {
    path: '/reset',
    component: () => import('./view/ResetPwdView.vue')
  },
  {
    path: '/index/:id',
    component: () => import('./view/HomeView.vue'),
    children: [
      {
        path: '/dashboard/:id',
        component: () => import('./view/DashboardView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
