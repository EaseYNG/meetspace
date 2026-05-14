import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { guest: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { guest: true },
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/HomeView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'explore',
        name: 'Explore',
        component: () => import('@/views/ExploreView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'activity/create',
        name: 'ActivityCreate',
        component: () => import('@/views/ActivityFormView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'activity/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/ActivityDetailView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'activity/:id/edit',
        name: 'ActivityEdit',
        component: () => import('@/views/ActivityFormView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/ProfileView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'my-activities',
        name: 'MyActivities',
        component: () => import('@/views/MyActivitiesView.vue'),
        meta: { requiresAuth: true },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to, _from) => {
  const auth = useAuthStore()
  if (!auth.loaded) {
    await auth.checkSession()
  }
  if (to.meta.requiresAuth && !auth.isLoggedIn()) {
    return '/login'
  }
  if (to.meta.guest && auth.isLoggedIn()) {
    return '/'
  }
})

export default router
