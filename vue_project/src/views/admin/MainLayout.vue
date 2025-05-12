<template>
  <el-container style="height: 100vh;">
    <!-- Top Bar -->
    <el-header
        style="background: linear-gradient(135deg, rgb(57, 167, 176), rgb(56, 183, 145)); color: white; display: flex; justify-content: space-between; align-items: center;"
    >
      <div style="font-size: 20px; font-weight: bold;">Takeaway Admin Panel</div>
      <div>
        <el-dropdown>
          <span class="el-dropdown-link">
            Admin <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="logout">Logout</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <!-- Side Menu -->
      <el-aside width="200px" style="background: #1f2d3d; color: white;">
        <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical-demo"
            background-color="#1f2d3d"
            text-color="#fff"
            active-text-color="rgb(56, 183, 145)"
            @select="handleSelect"
        >
          <el-menu-item index="/main/dashboard">
            <el-icon><House /></el-icon>
            <span>Dashboard</span>
          </el-menu-item>
          <el-menu-item index="/main/orders">
            <el-icon><Document /></el-icon>
            <span>Orders</span>
          </el-menu-item>
          <el-menu-item index="/main/dish">
            <el-icon><ForkSpoon /></el-icon>
            <span>Dish Management</span>
          </el-menu-item>
          <el-menu-item index="/main/settings">
            <el-icon><Setting /></el-icon>
            <span>Settings</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- Main Content -->
      <el-main style="background: #f5f5f5;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowDown, House, Document, ForkSpoon, Setting } from '@element-plus/icons-vue'
import { ElMessageBox, ElNotification } from 'element-plus'
import { Client } from '@stomp/stompjs'

const router = useRouter()
const route = useRoute()

// 当前激活的侧边栏菜单
const activeMenu = ref(route.path)
watch(() => route.path, newPath => {
  activeMenu.value = newPath
})

// 菜单选择跳转
const handleSelect = index => {
  router.push(index)
}

// 退出登录
const logout = async () => {
  try {
    await ElMessageBox.confirm(
        '你确定要退出登录吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
    const response = await fetch('http://localhost:8080/logout', {
      method: 'POST',
      credentials: 'include'
    })
    if (!response.ok) throw new Error('Logout failed')
    localStorage.removeItem('isLoggedIn')
    router.push('/')
  } catch (error) {
    console.error('退出操作出错：', error)
  }
}

onMounted(() => {
  // Google OAuth 成功后跳转
  if (route.query.oauthSuccess === 'true') {
    localStorage.setItem('isLoggedIn', 'true')
    router.replace('/main/dashboard')
  }

  // —— 使用原生 WebSocket （方案 B） ——
  const stompClient = new Client({
    // 对应 Spring Boot 原生 WS 端点
    brokerURL: 'ws://localhost:8080/ws',
    reconnectDelay: 5000,
  })

  stompClient.onConnect = () => {
    stompClient.subscribe('/topic/orders', msg => {
      const order = JSON.parse(msg.body)
      ElNotification({
        title: 'New Order',
        message: `订单 #${order.id}，用户：${order.customerName}`,
        duration: 0, // 持续显示，直到用户点击或手动关闭
        onClick: () => {
          router.push(`/main/orders/${order.id}`)
        }
      })
    })
  }

  stompClient.activate()
})
</script>

<style scoped>
.el-menu-item {
  font-size: 16px;
}
</style>
