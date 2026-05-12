import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login.vue'),
    meta: { noAuth: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register.vue'),
    meta: { noAuth: true },
  },
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    redirect: '/explore',
    children: [
      { path: 'explore', name: 'Explore', component: () => import('@/views/explore.vue') },
      { path: 'activities', name: 'Activities', component: () => import('@/views/activities.vue') },
      { path: 'agent', name: 'Agent', component: () => import('@/views/agent.vue') },
      { path: 'profile', name: 'Profile', component: () => import('@/views/profile.vue') },
    ],
  },
  {
    path: '/activity/create',
    name: 'CreateActivity',
    component: () => import('@/views/createActivity.vue'),
  },
  {
    path: '/activity/:id',
    name: 'ActivityDetail',
    component: () => import('@/views/activityDetail.vue'),
  },
  {
    path: '/activity/:id/edit',
    name: 'EditActivity',
    component: () => import('@/views/createActivity.vue'),
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/settings.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (!to.meta.noAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
