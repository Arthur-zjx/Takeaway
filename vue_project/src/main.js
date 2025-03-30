import { createApp } from 'vue'
import App from './App.vue'
import axios from 'axios'
import router from "@/router/index.js";
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

// ✅ 添加这行：允许 axios 发送 cookie
axios.defaults.withCredentials = true

// ✅ 设置后端地址
axios.defaults.baseURL = 'http://localhost:8080'

app.config.globalProperties.$axios = axios
app.use(router)
app.use(ElementPlus)
app.mount('#app')
