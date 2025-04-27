import { createRouter, createWebHistory } from 'vue-router'

// 公共页面
import LoginRegister from '@/views/loginRegister.vue'

// 管理员页面（admin）
import MainLayout from '@/views/admin/MainLayout.vue'
import Dashboard from '@/views/admin/Dashboard.vue'
import Orders from '@/views/admin/Orders.vue'
import DishManage from '@/views/admin/DishManage.vue'

// 用户页面（user）
// 请确保在 src/views/user/ 文件夹中创建对应的组件
import UserMainLayout from '@/views/user/UserMainLayout.vue'
import UserHome from '@/views/user/UserHome.vue'
import UserCart from '@/views/user/UserCart.vue'

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
            // 默认登录成功后跳转到管理员首页
            next('/user')
        }
    },
    // 管理员路由配置
    {
        path: '/main',
        component: MainLayout,
        meta: { requiresAuth: true },
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: Dashboard
            },
            {
                path: 'orders',
                name: 'Orders',
                component: Orders
            },
            {
                path: 'dish',
                name: 'DishManage',
                component: DishManage
            },
            {
                path: 'dish/edit/:id?',
                name: 'DishEdit',
                component: () => import('@/views/admin/DishEdit.vue')
            },
            {
                path: 'settings',
                name: 'Settings',
                component: () => import('@/views/admin/Settings.vue')  // 引入 Settings 页面
            },
            {
                path: '',
                redirect: '/main/dashboard'
            }
        ]
    },
    // 用户路由配置
    {
        path: '/user',
        component: UserMainLayout,
        meta: { requiresAuth: true },
        children: [
            {
                path: 'home',
                name: 'UserHome',
                component: UserHome,
                meta: { keepAlive: true }

            },
            {
                path: 'cart',
                name: 'UserCart',
                component: UserCart,
                meta: { keepAlive: true }

            },
            {
                path: '',
                redirect: '/user/home'
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 登录权限拦截守卫
router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        const isAuthenticated = localStorage.getItem('isLoggedIn') === 'true'
        if (!isAuthenticated) {
            next('/') // 未登录跳转登录页
        } else {
            next() // 已登录放行
        }
    } else {
        next()
    }
})

export default router
