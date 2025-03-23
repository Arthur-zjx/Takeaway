<template>
  <el-container style="height: 100vh;">
    <!-- Top Bar -->
    <el-header style="background: linear-gradient(135deg, rgb(57, 167, 176), rgb(56, 183, 145)); color: white; display: flex; justify-content: space-between; align-items: center;">
      <div style="font-size: 20px; font-weight: bold;">🍽️ Takeaway Admin Panel</div>
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
import { ArrowDown, House, Document, ForkSpoon } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = ref(route.path)

watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})

const handleSelect = (index) => {
  router.push(index)
}

const logout = () => {
  localStorage.removeItem('isLoggedIn')
  router.push('/')
}

onMounted(() => {
  const oauthSuccess = route.query.oauthSuccess
  if (oauthSuccess === 'true') {
    localStorage.setItem('isLoggedIn', 'true')
    router.replace('/main/dashboard')
  }
})
</script>

<style scoped>
.el-menu-item {
  font-size: 16px;
}
</style>
