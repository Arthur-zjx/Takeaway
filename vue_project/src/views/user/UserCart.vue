<template>
  <div class="user-cart">
    <h2>Your Cart</h2>

    <div v-if="items.length > 0">
      <div
          v-for="item in items"
          :key="item.id"
          class="cart-item"
      >
        <!-- Display dish image -->
        <img :src="item.imageUrl" alt="" class="item-image"/>

        <!-- Dish details -->
        <div class="item-info">
          <h3>{{ item.name }}</h3>
          <p>Category: {{ item.category }}</p>
          <p class="description">{{ item.description }}</p>
        </div>

        <!-- Quantity controls -->
        <div class="qty-controls">
          <el-button size="small" @click="decrement(item)">-</el-button>
          <span>{{ item.quantity }}</span>
          <el-button size="small" @click="increment(item)">+</el-button>
        </div>

        <!-- Subtotal & actions -->
        <div class="item-actions">
          <span class="subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          <el-button type="danger" @click="remove(item)">
            Remove
          </el-button>
        </div>

        <!-- Optional: show status and updated time -->
        <div class="status-info">
          <span>Status: {{ item.status }}</span>
          <span v-if="item.updateTime">
            Updated at: {{ formatDate(item.updateTime) }}
          </span>
        </div>
      </div>

      <!-- Footer: total price & checkout -->
      <div class="cart-footer">
        <p class="total">Total: ¥{{ totalPrice.toFixed(2) }}</p>
        <el-button type="success" @click="checkout">
          Checkout
        </el-button>
      </div>
    </div>

    <div v-else>
      <p>Your cart is empty.</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useCartStore } from '@/stores/cart'

// According to Dish.java model, fields include: id, name, category, description, imageUrl, price, status, updateTime
const cartStore = useCartStore()
console.log('[Cart] initial cartStore.items:', JSON.parse(JSON.stringify(cartStore.items)))

const items = computed(() => cartStore.items)
const totalPrice = computed(() => cartStore.totalPrice)
const router = useRouter()

function increment(item) {
  cartStore.updateQuantity(item.id, item.quantity + 1)
}

function decrement(item) {
  if (item.quantity > 1) {
    cartStore.updateQuantity(item.id, item.quantity - 1)
  }
}

function remove(item) {
  cartStore.removeItem(item.id)
}

async function checkout() {
  try {
    await axios.post('/api/orders', { items: items.value })
    cartStore.clearCart()
    router.push('/order-status')
  } catch (err) {
    console.error(err)
  }
}

// Helper: format date/time
function formatDate(dt) {
  const d = new Date(dt)
  return d.toLocaleString()
}
</script>

<style scoped>
.user-cart {
  padding: 20px;
}

.cart-item {
  display: grid;
  grid-template-columns: 80px 1fr auto auto;
  gap: 12px;
  align-items: start;
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
  margin-bottom: 12px;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info h3 {
  margin: 0;
  font-size: 1.1em;
}

.description {
  font-size: 0.9em;
  color: #666;
}

.qty-controls {
  display: flex;
  align-items: center;
}

.item-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.subtotal {
  font-weight: bold;
  margin-bottom: 6px;
}

.status-info {
  grid-column: 1 / -1;
  font-size: 0.85em;
  color: #999;
  margin-top: 4px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 2px solid #ddd;
}

.total {
  font-size: 1.2em;
  font-weight: bold;
}
</style>
