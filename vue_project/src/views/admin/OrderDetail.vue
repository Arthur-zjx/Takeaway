<template>
  <div class="order-detail-container">
    <el-card v-if="order" class="order-detail-card">
      <h2>Order #{{ order.id }}</h2>
      <p><strong>Customer:</strong> {{ order.username }}</p>
      <p><strong>Phone:</strong> {{ order.phone }}</p>
      <p><strong>Address:</strong> {{ order.address }}</p>
      <p><strong>Status:</strong>
        <el-tag :type="getStatusType(order.status)">
          {{ getStatusText(order.status) }}
        </el-tag>
      </p>

      <el-table :data="order.dishes" stripe style="margin: 1em 0;">
        <el-table-column prop="name" label="Dish" />
        <el-table-column prop="quantity" label="Quantity" width="80" />
        <el-table-column prop="price" label="Price" width="100" />
        <el-table-column
            label="Subtotal"
            :formatter="({ row }) => (row.quantity * row.price).toFixed(2)"
        />
      </el-table>

      <p class="total-price"><strong>Total:</strong> ¥{{ totalPrice }}</p>

      <div class="button-group">
        <el-button
            type="success"
            :disabled="order.status !== 'pending'"
            @click="handleChangeStatus('cooking')"
        >
          Accept
        </el-button>
        <el-button
            type="danger"
            :disabled="order.status !== 'pending'"
            @click="handleChangeStatus('canceled')"
        >
          Reject
        </el-button>
        <el-button
            type="warning"
            v-if="order.status === 'cooking'"
            @click="handleChangeStatus('delivered')"
        >
          Mark as Delivered
        </el-button>
        <!-- 用 goBack() 明确跳回列表 -->
        <el-button @click="goBack">Cancel</el-button>
      </div>
    </el-card>

    <div v-else class="loading-text">Loading order details...</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const orderId = Number(route.params.id)

const order = ref(null)

const fetchOrder = async () => {
  try {
    const { data } = await axios.get('/api/admin/orders')
    const found = data.find(o => o.id === orderId)
    if (!found) throw new Error('Order not found')
    order.value = found
  } catch (err) {
    console.error(err)
    ElMessage.error('Failed to load order. Returning to list.')
    goBack()
  }
}

const totalPrice = computed(() => {
  if (!order.value) return '0.00'
  return order.value.dishes
      .reduce((sum, d) => sum + d.quantity * d.price, 0)
      .toFixed(2)
})

function getStatusText(status) {
  switch (status) {
    case 'pending':   return 'Pending'
    case 'cooking':   return 'Cooking'
    case 'delivered': return 'Delivered'
    case 'canceled':  return 'Canceled'
    default:          return status
  }
}

function getStatusType(status) {
  switch (status) {
    case 'pending':   return 'warning'
    case 'cooking':   return ''
    case 'delivered': return 'success'
    case 'canceled':  return 'danger'
    default:          return ''
  }
}

const handleChangeStatus = async (newStatus) => {
  try {
    await axios.patch(`/api/admin/orders/${orderId}/status`, { status: newStatus })
    order.value.status = newStatus
    ElMessage.success('Order status updated.')
  } catch (err) {
    console.error(err)
    ElMessage.error('Failed to update status.')
  }
}

// 明确跳回订单列表
function goBack() {
  router.push('/main/orders')
}

onMounted(fetchOrder)
</script>

<style scoped>
.order-detail-container {
  max-width: 800px;
  margin: 20px auto;
}
.order-detail-card {
  padding: 20px;
}
.total-price {
  font-size: 1.1em;
  margin-top: 10px;
}
.button-group {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
.loading-text {
  text-align: center;
  margin-top: 40px;
  color: #888;
}
</style>
