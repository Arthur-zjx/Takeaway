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

      <el-table
          v-if="order.dishes && order.dishes.length"
          :data="order.dishes"
          stripe
          style="margin: 1em 0;"
      >
        <el-table-column prop="name" label="Dish" />
        <el-table-column prop="quantity" label="Quantity" width="80" />
        <el-table-column prop="price" label="Price" width="100" />
        <el-table-column
            label="Subtotal"
            :formatter="({ row }) => {
            if (!row || typeof row.quantity !== 'number' || typeof row.price !== 'number') {
              return ''
            }
            return (row.quantity * row.price).toFixed(2)
          }"
        />
      </el-table>

      <p class="total-price"><strong>Total:</strong> ${{ totalPrice }}</p>

      <div class="button-group">
        <!-- Pending: Accept or Reject -->
        <el-button
            v-if="order.status === 'pending'"
            type="success"
            @click="handleChangeStatus('cooking')"
        >
          Accept
        </el-button>
        <el-button
            v-if="order.status === 'pending'"
            type="danger"
            @click="handleChangeStatus('canceled')"
        >
          Reject
        </el-button>

        <!-- Cooking: Start Delivery -->
        <el-button
            v-if="order.status === 'cooking'"
            type="warning"
            @click="handleChangeStatus('delivering')"
        >
          Start Delivery
        </el-button>

        <!-- Delivering: Complete Order -->
        <el-button
            v-if="order.status === 'delivering'"
            type="primary"
            @click="handleChangeStatus('completed')"
        >
          Complete Order
        </el-button>

        <!-- Always available -->
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

async function fetchOrder() {
  try {
    const { data } = await axios.get('/api/admin/orders', { withCredentials: true })
    const found = data.find(o => o.id === orderId)
    console.log('🏷️ [OrderDetail] raw order data:', found)
    if (!found) throw new Error('Order not found')
    order.value = found
  } catch (err) {
    console.error(err)
    ElMessage.error('Failed to load order. Returning to list.')
    goBack()
  }
}

const totalPrice = computed(() => {
  if (!order.value || !order.value.dishes) return '0.00'
  return order.value.dishes
      .reduce((sum, d) => sum + (typeof d.quantity === 'number' ? d.quantity : 0) * (typeof d.price === 'number' ? d.price : 0), 0)
      .toFixed(2)
})

function getStatusText(status) {
  switch (status) {
    case 'pending':   return 'Pending'
    case 'cooking':   return 'Cooking'
    case 'delivering': return 'Delivering'
    case 'completed': return 'Completed'
    case 'canceled':  return 'Canceled'
    default:          return status
  }
}

function getStatusType(status) {
  switch (status) {
    case 'pending':   return 'warning'
    case 'cooking':   return ''
    case 'delivering': return 'info'
    case 'completed': return 'success'
    case 'canceled':  return 'danger'
    default:          return ''
  }
}

async function handleChangeStatus(newStatus) {
  try {
    await axios.patch(`/api/admin/orders/${orderId}/status`, { status: newStatus }, { withCredentials: true })
    order.value.status = newStatus
    ElMessage.success('Order status updated.')
  } catch (err) {
    console.error(err)
    ElMessage.error('Failed to update status.')
  }
}

function goBack() {
  router.push({ name: 'Orders' })
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
