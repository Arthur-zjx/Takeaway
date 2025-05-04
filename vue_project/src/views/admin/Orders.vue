<template>
  <div class="order-container">
    <h2 class="page-title">Order Management</h2>

    <!-- Table displaying orders -->
    <el-table :data="orderList" border style="width: 100%">
      <el-table-column prop="id" label="Order ID" width="180" />
      <el-table-column prop="username" label="Customer" width="120" />
      <el-table-column prop="phone" label="Phone" width="120" />
      <el-table-column prop="address" label="Address" />
      <el-table-column prop="status" label="Status" width="120">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="total" label="Total" width="100" />
      <el-table-column label="Actions" width="120">
        <template #default="scope">
          <el-button size="small" @click="viewOrder(scope.row)">View</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Order Detail Dialog -->
    <el-dialog v-model="dialogVisible" title="Order Detail" width="500px">
      <p><strong>Order ID:</strong> {{ selectedOrder.id }}</p>
      <p><strong>Customer:</strong> {{ selectedOrder.username }}</p>
      <p><strong>Phone:</strong> {{ selectedOrder.phone }}</p>
      <p><strong>Address:</strong> {{ selectedOrder.address }}</p>
      <p><strong>Status:</strong> {{ getStatusText(selectedOrder.status) }}</p>
      <p><strong>Total:</strong> {{ selectedOrder.total }}</p>
      <p><strong>Dishes:</strong></p>
      <ul>
        <li v-for="(dish, index) in selectedOrder.dishes" :key="index">
          {{ dish.name }} x {{ dish.quantity }} - {{ dish.price }}
        </li>
      </ul>
      <template #footer>
        <el-button @click="dialogVisible = false">Close</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElNotification } from 'element-plus'

const orderList = ref([
  {
    id: '1663672489663',
    username: 'John Doe',
    phone: '1234567890',
    address: '123 Apple Street',
    status: 'pending',
    total: '$154',
    dishes: [
      { name: 'Spicy Fish', quantity: 1, price: '$58.00' },
      { name: 'Beef Stew', quantity: 1, price: '$88.00' }
    ]
  }
])

const dialogVisible = ref(false)
const selectedOrder = ref({})

function viewOrder(order) {
  selectedOrder.value = order
  dialogVisible.value = true
}

function getStatusText(status) {
  switch (status) {
    case 'pending': return 'Pending'
    case 'confirmed': return 'Confirmed'
    case 'delivered': return 'Delivered'
    case 'canceled': return 'Canceled'
    default: return 'Unknown'
  }
}

function getStatusType(status) {
  switch (status) {
    case 'pending': return 'warning'
    case 'confirmed': return 'success'
    case 'delivered': return ''
    case 'canceled': return 'info'
    default: return ''
  }
}

// onMounted(() => {
//   // 模拟5秒后收到新订单
//   setTimeout(() => {
//     ElNotification({
//       title: 'New Order',
//       message: 'You have 1 new order pending. Click to view.',
//       type: 'warning',
//       duration: 0,
//       onClick: () => {
//         viewOrder(orderList.value[0])
//       }
//     })
//   }, 5000)
// })
</script>

<style scoped>
.order-container {
  padding: 20px;
}
.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
}
</style>