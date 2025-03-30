<template>
  <div class="dashboard-container">
    <h2 class="dashboard-title">Today’s Overview</h2>
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="(item, index) in overviewStats" :key="index">
        <el-card class="stat-card">
          <p class="label">{{ item.label }}</p>
          <p class="value">{{ item.value }}</p>
        </el-card>
      </el-col>
    </el-row>

    <h2 class="dashboard-title">Dish Overview</h2>
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="(item, index) in dishStats" :key="index">
        <el-card class="stat-card clickable" @click="goToDishes">
          <p class="label">{{ item.label }}</p>
          <p class="value">{{ item.value }}</p>
        </el-card>
      </el-col>
    </el-row>

    <h2 class="dashboard-title">Order Management</h2>
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="(item, index) in orderStats" :key="index">
        <el-card class="stat-card clickable" @click="goToOrders(item.status)">
          <p class="label">{{ item.label }}</p>
          <p class="value">{{ item.value }}</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const overviewStats = [
  { label: 'Turnover', value: '¥198' },
  { label: 'Valid Orders', value: 3 },
  { label: 'Completion Rate', value: '100%' },
  { label: 'Avg Order Price', value: '¥66' }
]

const dishStats = [
  { label: 'Active Dishes', value: 24 },
  { label: 'Inactive Dishes', value: 3 }
]

const orderStats = [
  { label: 'Pending', value: 2, status: 2 },
  { label: 'To be Delivered', value: 1, status: 3 },
  { label: 'Completed', value: 5, status: 5 },
  { label: 'Cancelled', value: 1, status: 6 },
  { label: 'All Orders', value: 9, status: 0 }
]

function goToOrders(status) {
  router.push(`/main/orders?status=${status}`)
}

function goToDishes() {
  router.push('/main/dish')
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.dashboard-title {
  font-size: 20px;
  margin: 20px 0 10px;
}
.stat-row {
  margin-bottom: 20px;
}
.stat-card {
  text-align: center;
  border-radius: 10px;
}
.label {
  font-size: 16px;
  color: #888;
}
.value {
  font-size: 24px;
  font-weight: bold;
  margin-top: 10px;
}
.clickable {
  cursor: pointer;
  transition: 0.2s;
}
.clickable:hover {
  background-color: #f0f9ff;
}
</style>
