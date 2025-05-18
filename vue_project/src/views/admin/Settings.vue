<template>
  <el-container style="height: 100vh; padding: 20px;">
    <el-row>
      <el-col :span="24">
        <el-card>
          <h2 class="page-title">Update Admin Account</h2>
          <el-form :model="form" ref="formRef" label-width="120px">
            <el-form-item label="Username">
              <el-input v-model="form.username" placeholder="Enter new username" />
            </el-form-item>
            <el-form-item label="New Password">
              <el-input type="password" v-model="form.password" placeholder="Enter new password" />
            </el-form-item>
            <el-form-item label="Confirm Password">
              <el-input type="password" v-model="form.confirmPassword" placeholder="Confirm new password" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateAccount">Update</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()

const form = ref({
  id: '',
  username: '',
  password: '',
  confirmPassword: ''
})

onMounted(async () => {
  const token = sessionStorage.getItem('ADMIN_TOKEN')
  if (!token) {
    ElMessage.error('未检测到登录信息，请重新登录')
    return router.push({ name: 'Login' })
  }
  try {
    const res = await axios.get('/auth/me', {
      headers: { Authorization: `Bearer ${token}` }
    })
    form.value.id = res.data.id
    form.value.username = res.data.username
  } catch (err) {
    console.error('获取当前用户失败：', err)
    ElMessage.error('获取用户信息失败，请重新登录')
    router.push({ name: 'Login' })
  }
})

const updateAccount = async () => {
  if (!form.value.id) {
    return ElMessage.error('无法获取用户ID，请重新登录后再试')
  }
  if (form.value.password !== form.value.confirmPassword) {
    return ElMessage.error('两次输入的密码不一致！')
  }
  try {
    const token = sessionStorage.getItem('ADMIN_TOKEN')
    const res = await axios.put(
        `/auth/update-admin/${form.value.id}`,
        {
          username: form.value.username,
          password: form.value.password
        },
        {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          }
        }
    )
    ElMessage.success(res.data.message || '账户更新成功！')
    // 如果想同步更新本地存储用户名：
    localStorage.setItem('username', form.value.username)
  } catch (err) {
    console.error('更新失败：', err)
    ElMessage.error(err.response?.data?.error || '更新出错')
  }
}
</script>

<style scoped>
.page-title { font-size: 24px; font-weight: bold; }
.el-card { padding: 20px; background: #fff; }
.el-row { height: 100%; }
</style>
