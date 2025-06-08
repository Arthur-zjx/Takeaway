<template>
  <div class="user-layout">
    <!-- Header bar with gradient green background -->
    <header class="header-bar">
      <div class="header-left">
        <h2>Takeaway User Panel</h2>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="el-dropdown-link">
            User <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="logout">Logout</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- Main layout: sidebar + content area -->
    <div class="body-wrapper">
      <aside class="sidebar">
        <el-menu
            class="menu"
            :default-active="activeMenu"
            background-color="#2d3a4b"
            text-color="#f0f0f0"
            active-text-color="#52c41a"
            @select="handleSelect"
        >
          <el-menu-item index="/user/home">
            <el-icon><House /></el-icon>
            <span slot="title">Home</span>
          </el-menu-item>
          <el-menu-item index="/user/cart">
            <el-icon><ShoppingCart /></el-icon>
            <span slot="title">Cart</span>
          </el-menu-item>
          <el-menu-item index="/user/orders">
            <el-icon><Document /></el-icon>
            <span slot="title">My Orders</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <main class="content">
        <!-- Render child route view -->
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { House, ShoppingCart, Document, ArrowDown } from '@element-plus/icons-vue'
import { ElMessageBox, ElNotification } from 'element-plus'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const activeMenu = ref(route.path)

// Track route changes and update active menu
watch(() => route.path, newPath => {
  activeMenu.value = newPath
})

// Navigate when a menu item is selected
const handleSelect = index => {
  router.push(index)
}

// Logout
const logout = async () => {
  // 1. Show confirmation dialog
  try {
    await ElMessageBox.confirm(
        'Are you sure you want to logout?',
        'Confirm',
        { confirmButtonText: 'OK', cancelButtonText: 'Cancel', type: 'warning' }
    )
  } catch {
    // User canceled the operation
    return
  }

  // 2. Call backend logout API (clear session/cookie on server)
  try {
    await axios.post('/logout')
  } catch (e) {
    console.warn('Logout request failed:', e)
  }

  // 3. Clear local login state and JWT
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userToken')  // If you stored JWT here

  // 4. Redirect to login page
  router.replace({ name: 'Login' })
}

onMounted(async () => {
  // Fetch current user information
  let currentUser = null
  try {
    const { data: me } = await axios.get('/auth/me', { withCredentials: true })
    currentUser = me.username
  } catch (err) {
    console.error('Failed to fetch current user', err)
  }

  // Preload all order statuses for the current user
  const orderStatusMap = new Map()
  try {
    const { data: myOrders } = await axios.get('/api/orders', { withCredentials: true })
    myOrders.forEach(o => orderStatusMap.set(o.id, o.status))
  } catch (err) {
    console.error('Failed to preload orders', err)
  }

  // Establish STOMP over SockJS connection and subscribe
  const stompClient = new Client({
    webSocketFactory: () => new SockJS('/ws/orders'),
    reconnectDelay: 5000,
    debug: msg => console.log('[STOMP]', msg)
  })

  stompClient.onConnect = () => {
    stompClient.subscribe('/topic/orders', msg => {
      const order = JSON.parse(msg.body)
      if (order.username !== currentUser) return

      const prevStatus = orderStatusMap.get(order.id)
      if (prevStatus == null) {
        // First time: user placed order notification
        orderStatusMap.set(order.id, order.status)
        ElNotification({
          title: 'Order Placed',
          message: `Your order #${order.id} has been placed`,
          duration: 3000,
          onClick: () => router.push(`/user/orders/${order.id}`)
        })
      } else if (prevStatus !== order.status) {
        // Order status update notification
        orderStatusMap.set(order.id, order.status)
        ElNotification({
          title: 'Order Update',
          message: `Order #${order.id} is now ${order.status}`,
          duration: 3000,
          onClick: () => router.push(`/user/orders/${order.id}`)
        })
      }
      // Ignore duplicate status updates
    })
  }

  stompClient.activate()
})
</script>


<style scoped>
.user-layout { display: flex; flex-direction: column; height: 100vh; }

.header-bar {
  height: 60px;
  background: linear-gradient(to right, #2dbf8e, #4ae0c3);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.header-left h2 { margin: 0; }
.header-right { display: flex; align-items: center; }

.body-wrapper { display: flex; flex: 1; overflow: hidden; }
.sidebar {
  width: 220px;
  background-color: #2d3a4b;
  padding-top: 20px;
}
.menu { border-right: none; }
.content {
  flex: 1;
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
