<template>
  <div class="order-detail">
    <el-card class="box-card" shadow="hover">
      <div slot="header" class="card-header">
        <span>Order Detail – #{{ order.id }}</span>
      </div>

      <div class="card-body">
        <p><strong>Status:</strong>
          <template v-if="tagType">
            <el-tag :type="tagType">{{ statusText(order.status) }}</el-tag>
          </template>
          <template v-else>
            {{ statusText(order.status) }}
          </template>
        </p>

        <p><strong>Total:</strong> ${{ order.total }}</p>
        <p><strong>Created At:</strong> {{ formatDate(order.createdAt) }}</p>

        <p><strong>Dishes:</strong></p>
        <el-table
            :data="order.dishes"
            border
            style="width: 100%; margin-top: 10px"
        >
          <el-table-column prop="name" label="Name" />
          <el-table-column prop="quantity" label="Qty" width="80" />
          <el-table-column prop="price" label="Price ($)" width="120" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// Read route parameters
const route = useRoute()
const orderId = route.params.id

// Order detail object
const order = ref({
  id: '',
  status: '',
  total: 0,
  createdAt: '',
  dishes: []
})

// Generate tag type based on status
function statusTagType(s) {
  switch (s) {
    case 'pending':   return 'warning'
    case 'confirmed': return 'success'
    case 'delivered': return 'info'
    case 'canceled':  return 'danger'
    default:          return null   // 不传空串
  }
}

// Get status text
function statusText(s) {
  switch (s) {
    case 'pending':   return 'Pending'
    case 'confirmed': return 'Confirmed'
    case 'delivered': return 'Delivered'
    case 'canceled':  return 'Canceled'
    default:          return s
  }
}

// Format datetime
function formatDate(dt) {
  return new Date(dt).toLocaleString()
}

// Compute tag type on mount
const tagType = ref(null)
onMounted(async () => {
  try {
    const { data } = await axios.get(`/api/orders/${orderId}`)
    order.value = data
    tagType.value = statusTagType(data.status)
  } catch (err) {
    console.error(err)
    ElMessage.error('Failed to load order.')
  }
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
}
.card-header {
  font-size: 1.2em;
  font-weight: bold;
}
.card-body p {
  margin: 8px 0;
}
</style>
