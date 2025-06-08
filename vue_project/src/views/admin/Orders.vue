<template>
  <div class="orders-page" style="padding: 1rem;">
    <el-card>
      <h2>Orders</h2>
      <el-table
          v-if="orders.length"
          :data="orders"
          stripe
          style="width: 100%; margin-top: 20px"
          v-loading="loading"
          element-loading-text="Loading orders..."
      >
        <el-table-column prop="id" label="Order ID" width="180" />
        <el-table-column prop="recipientName" label="Name" width="120" />
        <el-table-column prop="phone" label="Phone" width="120" />
        <el-table-column prop="address" label="Address" />
        <el-table-column prop="status" label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="total" label="Total" width="100" />
        <el-table-column label="Actions" width="120">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="viewOrder(row.id)">
              View
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <p v-else style="margin-top: 20px;">No orders yet.</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const orders = ref([])
const loading = ref(false)

const fetchOrders = async () => {
  loading.value = true
  try {
    const { data } = await axios.get('/api/admin/orders')
    orders.value = data.sort((a, b) => b.id - a.id)
    // Place the first incoming orders at the top in a more prominent position by identify ID sequence
  } catch (err) {
    console.error(err)
    ElMessage.error('Failed to fetch order list')
  } finally {
    loading.value = false
  }
}

const viewOrder = (id) => {
  // Navigate via router (unmount this component)
  router.push(`/main/orders/${id}`)
}

function getStatusText(status) {
  switch (status) {
    case 'pending':   return 'Pending'
    case 'confirmed': return 'Confirmed'
    case 'cooking':   return 'Cooking'
    case 'delivered': return 'Delivered'
    case 'canceled':  return 'Canceled'
    default:          return status
  }
}

function getStatusType(status) {
  switch (status) {
    case 'pending':   return 'warning'
    case 'confirmed': return 'success'
    case 'cooking':   return ''
    case 'delivered': return 'info'
    case 'canceled':  return 'danger'
    default:          return ''
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.orders-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}
</style>
