import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'reception',
        name: 'Reception',
        component: () => import('@/views/reception/Index.vue'),
        meta: { title: '前台工作台', roles: ['reception', 'manager'] }
      },
      {
        path: 'cleaner',
        name: 'Cleaner',
        component: () => import('@/views/cleaner/Index.vue'),
        meta: { title: '保洁工作台', roles: ['cleaner', 'manager'] }
      },
      {
        path: 'driver',
        name: 'Driver',
        component: () => import('@/views/driver/Index.vue'),
        meta: { title: '司机/取件工作台', roles: ['driver', 'manager'] }
      },
      {
        path: 'factory',
        name: 'Factory',
        component: () => import('@/views/factory/Index.vue'),
        meta: { title: '洗涤厂工作台', roles: ['factory', 'manager'] }
      },
      {
        path: 'warehouse',
        name: 'Warehouse',
        component: () => import('@/views/warehouse/Index.vue'),
        meta: { title: '库管工作台', roles: ['warehouse', 'manager'] }
      },
      {
        path: 'manager',
        name: 'Manager',
        component: () => import('@/views/manager/Index.vue'),
        meta: { title: '店长工作台', roles: ['manager'] }
      },
      {
        path: 'track',
        name: 'Track',
        component: () => import('@/views/Track.vue'),
        meta: { title: '布草轨迹' }
      },
      {
        path: 'rooms',
        name: 'Rooms',
        component: () => import('@/views/Rooms.vue'),
        meta: { title: '房间管理', roles: ['reception', 'manager'] }
      },
      {
        path: 'linen-types',
        name: 'LinenTypes',
        component: () => import('@/views/LinenTypes.vue'),
        meta: { title: '布草类型', roles: ['manager'] }
      },
      {
        path: 'stocks',
        name: 'Stocks',
        component: () => import('@/views/Stocks.vue'),
        meta: { title: '库存管理', roles: ['warehouse', 'manager'] }
      },
      {
        path: 'damage',
        name: 'Damage',
        component: () => import('@/views/DamageReports.vue'),
        meta: { title: '报损管理', roles: ['manager', 'warehouse', 'factory'] }
      },
      {
        path: 'cost',
        name: 'Cost',
        component: () => import('@/views/CostAllocation.vue'),
        meta: { title: '成本分摊', roles: ['manager'] }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (!userStore.isLoggedIn) {
    userStore.loadFromStorage()
  }

  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/dashboard')
  } else if (to.meta.roles && userStore.userRole && !to.meta.roles.includes(userStore.userRole)) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
