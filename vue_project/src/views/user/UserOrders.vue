<template>
  <div class="user-orders">
    <h2>My Orders</h2>

    <el-table
        v-if="orders.length"
        :data="orders"
        stripe
        style="width: 100%; margin-top: 20px"
        :fit="true"
    >
      <!-- No fixed width for each column -->
      <el-table-column prop="id" label="Order ID" min-width="80" />
      <el-table-column prop="status" label="Status" min-width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="total" label="Total ($)" min-width="100" />
      <el-table-column prop="createdAt" label="Created At" min-width="160" />
      <el-table-column label="Actions" min-width="120">
        <template #default="{ row }">
          <el-button size="small" @click="handleView(row)">View</el-button>
        </template>
      </el-table-column>
    </el-table>


    <p v-else style="margin-top: 20px;">No orders yet.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()

// Order list
const orders = ref([])

function statusText(s) {
  switch (s) {
    case 'pending':   return 'Pending'
    case 'confirmed': return 'Confirmed'
    case 'delivered': return 'Delivered'
    case 'canceled':  return 'Canceled'
    default:          return s
  }
}

function statusTagType(s) {
  switch (s) {
    case 'pending':   return 'warning'
    case 'confirmed': return 'success'
    case 'delivered': return 'info'
    case 'canceled':  return 'danger'
    default:          return null
  }
}


// Click View to navigate to order detail page
function handleView(order) {
  router.push({ name: 'UserOrderDetail', params: { id: order.id } })
}

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/orders')
    orders.value = data.sort((a, b) => b.id - a.id)
  } catch (err) {
    console.error(err)
    ElMessage.error('Failed to fetch orders.')
  }
})
</script>

<style scoped>
.user-orders {
  padding: 20px;
}
</style>
