// src/main.js

// Polyfill global object for environments without 'global'
if (typeof global === 'undefined') {
    // @ts-ignore
    window.global = window
}
// End of global polyfill

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import axios from 'axios'
import router from '@/router/index.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(createPinia())

// Allow axios to send cookies with requests
axios.defaults.withCredentials = true
// Set axios base URL for backend API
axios.defaults.baseURL = 'http://localhost:8080'

// Intercept requests and inject appropriate JWT based on URL prefix
axios.interceptors.request.use(config => {
    // Admin endpoints: use ADMIN_TOKEN from sessionStorage
    if (config.url.startsWith('/api/admin')) {
        const adminToken = sessionStorage.getItem('ADMIN_TOKEN')
        if (adminToken) {
            config.headers.Authorization = `Bearer ${adminToken}`
        }
    }
    // User endpoints: use userToken from localStorage
    else if (config.url.startsWith('/api')) {
        const userToken = localStorage.getItem('userToken')
        if (userToken) {
            config.headers.Authorization = `Bearer ${userToken}`
        }
    }
    return config
}, error => Promise.reject(error))

// Global response interceptor: on 401 unauthorized, redirect to Login page
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
