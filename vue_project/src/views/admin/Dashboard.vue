<template>
  <div class="dashboard-container">
    <h2 class="dashboard-title">Overview</h2>
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
      <el-col :span="6">
        <el-card class="stat-card clickable" @click="goToDishes">
          <p class="label">Active Dishes</p>
          <p class="value">{{ dishStats.active !== null ? dishStats.active : '—' }}</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card clickable" @click="goToDishes">
          <p class="label">Inactive Dishes</p>
          <p class="value">{{ dishStats.inactive !== null ? dishStats.inactive : '—' }}</p>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// Reactive stats
const overviewStats = ref([])
const dishStats = ref({ active: null, inactive: null })
const orderStats = ref([])

function goToOrders(status) {
  router.push(`/main/orders?status=${status}`)
}

function goToDishes() {
  router.push('/main/dish')
}

async function fetchStats() {
  try {
    // 获取订单列表
    const { data: orders } = await axios.get('/api/admin/orders', { withCredentials: true })

    // 计算订单统计
    const totalOrders = orders.length
    const pendingCount = orders.filter(o => o.status === 'pending').length
    const cookingCount = orders.filter(o => o.status === 'cooking').length
    const deliveringCount = orders.filter(o => o.status === 'delivering').length
    const completedCount = orders.filter(o => o.status === 'completed').length
    const canceledCount = orders.filter(o => o.status === 'canceled').length

    // 计算业绩统计
    const turnover = orders.reduce((sum, o) => sum + (o.total || 0), 0)
    const avgPrice = totalOrders > 0 ? (turnover / totalOrders).toFixed(2) : '0.00'
    const completionRate = totalOrders > 0 ? ((completedCount / totalOrders) * 100).toFixed(0) + '%' : '0%'

    overviewStats.value = [
      { label: 'Turnover', value: `$${turnover.toFixed(2)}` },
      { label: 'Valid Orders', value: totalOrders },
      { label: 'Completion Rate', value: completionRate },
      { label: 'Avg Order Price', value: `$${avgPrice}` }
    ]

    orderStats.value = [
      { label: 'Pending', value: pendingCount, status: 'pending' },
      { label: 'Cooking', value: cookingCount, status: 'cooking' },
      { label: 'Delivering', value: deliveringCount, status: 'delivering' },
      { label: 'Completed', value: completedCount, status: 'completed' },
      { label: 'Cancelled', value: canceledCount, status: 'canceled' },
      { label: 'All Orders', value: totalOrders, status: '' }
    ]

    // 获取菜品统计
    const { data: ds } = await axios.get('/api/dish/stats', { withCredentials: true })
    dishStats.value = ds
  } catch (err) {
    console.error('Error fetching stats:', err)
  }
}

onMounted(fetchStats)
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
