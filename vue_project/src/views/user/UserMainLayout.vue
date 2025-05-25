<template>
  <div class="user-layout">
    <!-- 顶部栏：渐变绿色背景 -->
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

    <!-- 主体布局：左侧菜单 + 右侧内容区 -->
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
        <!-- 展示子页面 -->
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

// 跟踪路由变化更新选中项
watch(() => route.path, newPath => {
  activeMenu.value = newPath
})

// 菜单选择跳转
const handleSelect = index => {
  router.push(index)
}

// 退出登录
const logout = async () => {
  // 1. 弹出确认框
  try {
    await ElMessageBox.confirm(
        'Are you sure you want to logout?',
        'Confirm',
        { confirmButtonText: 'OK', cancelButtonText: 'Cancel', type: 'warning' }
    )
  } catch {
    // 用户取消操作
    return
  }

  // 2. 调用后端登出接口（清 Session/清服务器端 Cookie）
  try {
    await axios.post('/logout')
  } catch (e) {
    console.warn('Logout request failed:', e)
  }

  // 3. 本地清除登录状态和 JWT
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userToken')  // 如果你把 JWT 存在这里的话

  // 4. 跳回登录页
  router.replace({ name: 'Login' })
}

onMounted(async () => {
  // 获取当前用户信息
  let currentUser = null
  try {
    const { data: me } = await axios.get('/auth/me', { withCredentials: true })
    currentUser = me.username
  } catch (err) {
    console.error('Failed to fetch current user', err)
  }

  // 预加载当前用户所有订单状态
  const orderStatusMap = new Map()
  try {
    const { data: myOrders } = await axios.get('/api/orders', { withCredentials: true })
    myOrders.forEach(o => orderStatusMap.set(o.id, o.status))
  } catch (err) {
    console.error('Failed to preload orders', err)
  }

  // 建立 STOMP over SockJS 连接并订阅
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
        // 初次见：用户自己下单通知
        orderStatusMap.set(order.id, order.status)
        ElNotification({
          title: 'Order Placed',
          message: `Your order #${order.id} has been placed`,
          duration: 3000,
          onClick: () => router.push(`/user/orders/${order.id}`)
        })
      } else if (prevStatus !== order.status) {
        // 状态更新通知
        orderStatusMap.set(order.id, order.status)
        ElNotification({
          title: 'Order Update',
          message: `Order #${order.id} is now ${order.status}`,
          duration: 3000,
          onClick: () => router.push(`/user/orders/${order.id}`)
        })
      }
      // 相同状态不再通知
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
