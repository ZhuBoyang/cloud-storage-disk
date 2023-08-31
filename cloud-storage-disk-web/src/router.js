import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('./App.vue'),
    redirect: '/home'
  },
  {
    // 引导页
    path: '/home',
    component: () => import('./view/HomeView.vue')
  },
  {
    // 账户初始化
    path: '/initial',
    component: () => import('./view/InitialView.vue')
  },
  {
    // 登录
    path: '/login',
    component: () => import('./view/LoginView.vue')
  },
  {
    path: '/player',
    component: () => import('./components/VideoPlayer.vue')
  },
  {
    path: '/reset',
    component: () => import('./view/ResetPwdView.vue')
  },
  {
    path: '/dashboard',
    component: () => import('./view/DashboardView.vue'),
    children: [
      {
        path: '/dashboard/files',
        component: () => import('./view/FileView.vue')
      },
      {
        path: '/dashboard/trash',
        component: () => import('./view/TrashView.vue')
      },
      {
        path: '/dashboard/transformer',
        component: () => import('./view/TransformerView.vue')
      },
      {
        path: '/dashboard/application',
        component: () => import('./view/ApplicationView.vue')
      }
    ]
  },
  {
    path: '/center',
    component: () => import('./view/UserCenterView.vue'),
    redirect: '/center/user',
    children: [
      {
        path: '/center/user',
        component: () => import('./view/UserSelfView.vue')
      },
      {
        path: '/center/password',
        component: () => import('./view/UserPasswordView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
