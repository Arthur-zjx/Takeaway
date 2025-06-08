import { createRouter, createWebHistory } from 'vue-router'

// Public pages
import LoginRegister    from '@/views/loginRegister.vue'

// Admin pages
import MainLayout       from '@/views/admin/MainLayout.vue'
import Dashboard        from '@/views/admin/Dashboard.vue'
import Orders           from '@/views/admin/Orders.vue'
import DishManage       from '@/views/admin/DishManage.vue'
import OrderDetail      from '@/views/admin/OrderDetail.vue'

// User pages
import UserMainLayout   from '@/views/user/UserMainLayout.vue'
import UserHome         from '@/views/user/UserHome.vue'
import UserCart         from '@/views/user/UserCart.vue'

const routes = [
    {
        path: '/',
        name: 'Login',
        component: LoginRegister
    },
    {
        path: '/oauth-success',
        name: 'OAuthSuccess',
        beforeEnter: (to, from, next) => {
            localStorage.setItem('isLoggedIn', 'true')
            next('/user')
        }
    },

    // Admin routes
    {
        path: '/main',
        component: MainLayout,
        children: [
            { path: 'dashboard', name: 'Dashboard',  component: Dashboard },
            { path: 'orders',    name: 'Orders',     component: Orders },
            { path: 'orders/:id', name: 'OrderDetail', component: () => import('@/views/admin/OrderDetail.vue'), props: true },
            { path: 'dish',      name: 'DishManage', component: DishManage },
            { path: 'dish/edit/:id?', name: 'DishEdit', component: () => import('@/views/admin/DishEdit.vue') },
            { path: 'settings',  name: 'Settings',   component: () => import('@/views/admin/Settings.vue') },
            { path: '', redirect: '/main/dashboard' }
        ]
    },

    // User routes
    {
        path: '/user',
        component: UserMainLayout,
        children: [
            { path: 'home',   name: 'UserHome',   component: UserHome,   meta: { keepAlive: true } },
            { path: 'cart',   name: 'UserCart',   component: UserCart,   meta: { keepAlive: true } },
            { path: 'orders', name: 'UserOrders', component: () => import('@/views/user/UserOrders.vue') },
            { path: 'orders/:id', name: 'UserOrderDetail', component: () => import('@/views/user/UserOrderDetail.vue') },
            { path: '', redirect: '/user/home' }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// Route guard for authentication
router.beforeEach((to, from, next) => {
    const adminToken = sessionStorage.getItem('ADMIN_TOKEN')
    const userToken  = localStorage.getItem('userToken')

    if (to.path.startsWith('/main')) {
        // Admin routes require ADMIN_TOKEN
        if (!adminToken) {
            return next({ name: 'Login' })
        }
        return next()
    }

    if (to.path.startsWith('/user')) {
        // User routes require userToken
        if (!userToken) {
            return next({ name: 'Login' })
        }
        return next()
    }

    // Other routes (/, /oauth-success, etc.) are allowed
    next()
})

export default router
