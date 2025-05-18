// src/main.js

// —— Polyfill for global ——
if (typeof global === 'undefined') {
    // @ts-ignore
    window.global = window
}
// —— End Polyfill ——

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import axios from 'axios'
import router from '@/router/index.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(createPinia())

// 允许 axios 发送 cookie
axios.defaults.withCredentials = true
// 设置后端地址
axios.defaults.baseURL = 'http://localhost:8080'

// 拦截所有请求，根据 URL 前缀自动注入合适的 JWT
axios.interceptors.request.use(config => {
    // 管理员接口，使用 ADMIN_TOKEN（存在 sessionStorage）
    if (config.url.startsWith('/api/admin')) {
        const adminToken = sessionStorage.getItem('ADMIN_TOKEN')
        if (adminToken) {
            config.headers.Authorization = `Bearer ${adminToken}`
        }
    }
    // 普通用户接口，使用 userToken（存在 localStorage）
    else if (config.url.startsWith('/api')) {
        const userToken = localStorage.getItem('userToken')
        if (userToken) {
            config.headers.Authorization = `Bearer ${userToken}`
        }
    }
    return config
}, error => Promise.reject(error))

// 全局响应拦截器：遇到 401，跳转到登录页
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            router.push({ name: 'Login' })
        }
        return Promise.reject(error)
    }
)

app.config.globalProperties.$axios = axios
app.use(router)
app.use(ElementPlus)
app.mount('#app')
