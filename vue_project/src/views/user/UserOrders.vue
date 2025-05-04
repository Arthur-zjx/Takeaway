<template>
  <div class="user-orders">
    <h2>My Orders</h2>

    <el-table
        v-if="orders.length"
        :data="orders"
        stripe
        style="width: 100%; margin-top: 20px"
    >
      <el-table-column prop="id" label="Order ID" width="120" />
      <el-table-column prop="status" label="Status" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="total" label="Total ($)" width="120" />
      <el-table-column prop="createdAt" label="Created At" width="180" />
      <el-table-column label="Actions" width="100">
        <template #default="{ row }">
          <el-button
              size="small"
              @click="() => handleView(row)"
          >
            View
          </el-button>
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

// 订单列表
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


// 点击 View 跳转到详情页
function handleView(order) {
  router.push({ name: 'UserOrderDetail', params: { id: order.id } })
}

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/orders')
    orders.value = data
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
