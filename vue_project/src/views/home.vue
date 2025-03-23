<!-- src/views/Home.vue -->
<template>
  <div class="home-container">
    <h1>🎉 Google 登录成功！</h1>
    <p>欢迎回来，亲爱的用户！</p>
    <button @click="fetchUserInfo">获取用户信息</button>

    <div v-if="user.name">
      <h3>姓名：{{ user.name }}</h3>
      <h3>邮箱：{{ user.email }}</h3>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const user = ref({})

const fetchUserInfo = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/userinfo', {
      withCredentials: true
    })
    user.value = res.data
  } catch (e) {
    alert('无法获取用户信息')
    console.error(e)
  }
}
</script>

<style scoped>
.home-container {
  padding: 40px;
  text-align: center;
}
</style>
