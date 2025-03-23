import { createApp } from 'vue'
import App from './App.vue'
import axios from 'axios'
import router from "@/router/index.js";

const app = createApp(App)

// **确保 axios 访问 Spring Boot**
axios.defaults.baseURL = 'http://localhost:8080'

app.config.globalProperties.$axios = axios
app.use(router)
app.mount('#app')
