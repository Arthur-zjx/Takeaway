import { createRouter, createWebHistory } from 'vue-router'

// 公共页面
import LoginRegister    from '@/views/loginRegister.vue'

// 管理员页面（admin）
import MainLayout       from '@/views/admin/MainLayout.vue'
import Dashboard        from '@/views/admin/Dashboard.vue'
import Orders           from '@/views/admin/Orders.vue'
import DishManage       from '@/views/admin/DishManage.vue'

// 用户页面（user）
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

    // 管理员路由配置
    {
        path: '/main',
        component: MainLayout,
        children: [
            { path: 'dashboard', name: 'Dashboard',  component: Dashboard },
            { path: 'orders',    name: 'Orders',     component: Orders },
            { path: 'dish',      name: 'DishManage', component: DishManage },
            { path: 'dish/edit/:id?', name: 'DishEdit', component: () => import('@/views/admin/DishEdit.vue') },
            { path: 'settings',  name: 'Settings',   component: () => import('@/views/admin/Settings.vue') },
            { path: '', redirect: '/main/dashboard' }
        ]
    },

    // 用户路由配置
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

// 登录权限拦截守卫
router.beforeEach((to, from, next) => {
    const adminToken = sessionStorage.getItem('ADMIN_TOKEN')
    const userToken  = localStorage.getItem('userToken')

    if (to.path.startsWith('/main')) {
        // 管理端路由：必须有 adminToken
        if (!adminToken) {
            return next({ name: 'Login' })
        }
        return next()
    }

    if (to.path.startsWith('/user')) {
        // 用户端路由：必须有 userToken
        if (!userToken) {
            return next({ name: 'Login' })
        }
        return next()
    }

    // 其它（/, /oauth-success 等）直接放行
    next()
})

export default router
