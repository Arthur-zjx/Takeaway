<template>
  <div class="user-cart">
    <h2>Your Cart</h2>
    <div v-for="item in cartList" :key="item.id" class="cart-item">
      <p>{{ item.name }} x {{ item.quantity }}</p>
      <p>¥{{ item.price * item.quantity }}</p>
      <el-button type="danger" @click="removeItem(item)">Remove</el-button>
    </div>
    <p class="total">Total: ¥{{ total }}</p>
    <el-button type="success">Checkout</el-button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const cartList = ref([
  // 模拟购物车数据
  { id: 1, name: 'Pepperoni Pizza', price: 20, quantity: 2 },
  { id: 2, name: 'Cheese Pizza', price: 15, quantity: 1 },
])

function removeItem(item) {
  // 从 cartList 中移除或数量减1
  // 后面可对接后端
}

const total = computed(() => {
  return cartList.value.reduce((acc, item) => acc + item.price * item.quantity, 0)
})
</script>

<style scoped>
.user-cart {
  /* 样式随意 */
}
.cart-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}
.total {
  font-weight: bold;
}
</style>
