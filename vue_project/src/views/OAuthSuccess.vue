<template>
  <div class="redirecting">
    <el-result
        icon="success"
        title="Login Successful"
        sub-title="Redirecting to dashboard..."
    >
      <template #extra>
        <el-button type="primary" @click="goHome">Go Now</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  // 设置本地登录状态
  localStorage.setItem('isLoggedIn', 'true')

  // 延迟 1 秒跳转主页面
  setTimeout(() => {
    router.push('/main')
  }, 1000)
})

const goHome = () => {
  router.push('/main')
}
</script>

<style scoped>
.redirecting {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
