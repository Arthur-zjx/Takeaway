// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginRegister from '../views/loginRegister.vue'
import Home from '../views/home.vue'

const routes = [
    {
        path: '/',
        name: 'Login',
        component: LoginRegister
    },
    {
        path: '/home',
        name: 'Home',
        component: Home
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
