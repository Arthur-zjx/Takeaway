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
    ElMessage.error('No login information detected, please log in again')
    return router.push({ name: 'Login' })
  }
  try {
    const res = await axios.get('/auth/me', {
      headers: { Authorization: `Bearer ${token}` }
    })
    form.value.id = res.data.id
    form.value.username = res.data.username
  } catch (err) {
    console.error('Failed to fetch current user:', err)
    ElMessage.error('Failed to fetch user info, please log in again')
    router.push({ name: 'Login' })
  }
})

const updateAccount = async () => {
  if (!form.value.id) {
    return ElMessage.error('Unable to get user ID, please log in again and retry')
  }
  if (form.value.password !== form.value.confirmPassword) {
    return ElMessage.error('The two passwords do not match!')
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
    ElMessage.success(res.data.message || 'Account updated successfully!')
    // Optionally update local storage username:
    localStorage.setItem('username', form.value.username)
  } catch (err) {
    console.error('Update failed:', err)
    ElMessage.error(err.response?.data?.error || 'update failed')
  }
}
</script>

<style scoped>
.page-title { font-size: 24px; font-weight: bold; }
.el-card { padding: 20px; background: #fff; }
.el-row { height: 100%; }
</style>
