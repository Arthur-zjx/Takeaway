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
import { ElMessage } from 'element-plus'

const form = ref({
  id: '',
  username: '',
  password: '',
  confirmPassword: ''
})

onMounted(() => {
  const savedUsername = localStorage.getItem('username')
  const savedUserId = localStorage.getItem('userId')
  if (savedUsername) {
    form.value.username = savedUsername
  }
  if (savedUserId) {
    form.value.id = savedUserId
  }
})

const updateAccount = async () => {
  // 确保有用户ID
  if (!form.value.id) {
    return ElMessage.error('无法获取用户ID，请重新登录后再试')
  }
  // 前端校验：确认密码一致
  if (form.value.password !== form.value.confirmPassword) {
    return ElMessage.error('两次输入的密码不一致！')
  }
  try {
    const response = await fetch(`http://localhost:8080/users/${form.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify({
        username: form.value.username,
        password: form.value.password
      })
    })
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || '更新失败')
    }
    const result = await response.json()
    ElMessage.success(result.message || '账户更新成功！')
    // 可选：更新本地存储
    localStorage.setItem('username', form.value.username)
  } catch (error) {
    ElMessage.error(error.message || '更新出错')
  }
}
</script>

<style scoped>
.page-title { font-size: 24px; font-weight: bold; }
.el-card { padding: 20px; background: #fff; }
.el-row { height: 100%; }
</style>
