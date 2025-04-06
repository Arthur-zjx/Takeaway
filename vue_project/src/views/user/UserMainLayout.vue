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
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { House, ShoppingCart, ArrowDown } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const activeMenu = ref(route.path)

// 跟踪路由变化更新选中项
watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})

// 菜单选择跳转
const handleSelect = (index) => {
  router.push(index)
}

// 退出登录
const logout = async () => {
  try {
    await ElMessageBox.confirm('Are you sure you want to logout?', 'Confirm', {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })
    localStorage.removeItem('isLoggedIn')
    router.push('/')
  } catch (error) {
    // 用户取消操作
  }
}
</script>

<style scoped>
.user-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* 顶部栏：渐变绿色背景 */
.header-bar {
  height: 60px;
  background: linear-gradient(to right, #2dbf8e, #4ae0c3);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left h2 {
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 主体布局：左侧菜单 + 右侧内容 */
.body-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 220px;
  background-color: #2d3a4b; /* 与 admin 相同的深色 */
  padding-top: 20px;
}

.menu {
  border-right: none;
}

.content {
  flex: 1;
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
