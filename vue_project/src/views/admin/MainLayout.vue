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
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import axios from 'axios'

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
        'Are you sure you want to log out?',
        'Warning',
        { confirmButtonText: 'Yes', cancelButtonText: 'No', type: 'warning' }
    )
    const response = await fetch('http://localhost:8080/logout', {
      method: 'POST',
      credentials: 'include'
    })
    if (!response.ok) throw new Error('Logout failed')
    localStorage.removeItem('isLoggedIn')
    router.push('/')
  } catch (err) {
    console.error('Logout error:', err)
  }
}

onMounted(async () => {
  // OAuth callback
  if (route.query.oauthSuccess === 'true') {
    localStorage.setItem('isLoggedIn', 'true')
    router.replace('/main/dashboard')
  }

  // 1. 预加载现有订单状态到 Map，防止状态更新被当做新订单
  const orderStatusMap = new Map()
  try {
    const { data: orders } = await axios.get('/api/admin/orders', { withCredentials: true })
    orders.forEach(o => orderStatusMap.set(o.id, o.status))
  } catch (err) {
    console.error('Failed to preload orders for status map', err)
  }

  // 2. 建立 STOMP over SockJS 连接并订阅（确保只订阅一次）
  let subscription = null
  const stompClient = new Client({
    webSocketFactory: () => new SockJS('/ws/orders'),
    reconnectDelay: 5000,
    debug: msg => console.log('[STOMP]', msg)
  })

  stompClient.onConnect = () => {
    if (subscription) return
    subscription = stompClient.subscribe('/topic/orders', msg => {
      // 先关闭所有旧通知，避免重复
      ElNotification.closeAll()

      const order = JSON.parse(msg.body)
      const prevStatus = orderStatusMap.get(order.id)

      if (prevStatus == null) {
        // 新订单：先记录，再通知
        orderStatusMap.set(order.id, order.status)

        const newNoti = ElNotification({
          title: 'New Order',
          message: `Order #${order.id}, Customer: ${order.username}`,
          duration: 0,
          onClick: () => {
            newNoti.close()
            router.push(`/main/orders/${order.id}`)
          }
        })
      } else if (prevStatus !== order.status) {
        // 状态变更：更新 Map 并通知（3s 自动关闭）
        orderStatusMap.set(order.id, order.status)

        ElNotification({
          title: 'Order Updated',
          message: `Order #${order.id} is now ${order.status}`,
          duration: 3000,
          onClick: () => {
            router.push(`/main/orders/${order.id}`)
          }
        })
      }
      // 相同状态不再通知
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
