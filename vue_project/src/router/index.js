import { createRouter, createWebHistory } from 'vue-router'

// 页面组件
import LoginRegister from '@/views/loginRegister.vue'
import MainLayout from '@/views/MainLayout.vue'
import Dashboard from '@/views/Dashboard.vue'
import Orders from '@/views/Orders.vue'
import DishManage from '@/views/DishManage.vue'
import OAuthSuccess from '@/views/OAuthSuccess.vue'  // ✅ 新增：Google 登录成功跳转页

// 路由配置
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
            next('/main/dashboard')
        }
    },
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
                path: '',
                redirect: '/main/dashboard'
            }
        ]
    }
]


// 创建 router 实例
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
