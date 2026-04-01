import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '@/utils/auth'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { guest: true }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/activities',
    name: 'ActivityList',
    component: () => import('@/views/ActivityList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/activities/create',
    name: 'ActivityCreate',
    component: () => import('@/views/ActivityCreate.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/activities/:id',
    name: 'ActivityDetail',
    component: () => import('@/views/ActivityDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/activities/:id/edit',
    name: 'ActivityEdit',
    component: () => import('@/views/ActivityEdit.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authenticated = isAuthenticated()

  // 需要认证的路由
  if (to.meta.requiresAuth && !authenticated) {
    next('/login')
    return
  }

  // 访客路由（登录、注册）- 如果已登录则跳转到主页
  if (to.meta.guest && authenticated) {
    next('/home')
    return
  }

  next()
})

export default router
