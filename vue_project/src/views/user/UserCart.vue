<template>
  <div class="user-cart">
    <h2>Your Cart</h2>

    <!-- Inline Profile Form -->
    <div class="profile-form">
      <el-form :model="profileForm" label-width="80px">
        <el-form-item label="Name">
          <el-input v-model="profileForm.username" placeholder="Enter your name" />
        </el-form-item>
        <el-form-item label="Phone">
          <el-input v-model="profileForm.phone" placeholder="Enter your phone" />
        </el-form-item>
        <el-form-item label="Address">
          <el-input v-model="profileForm.address" placeholder="Enter your address" />
        </el-form-item>
      </el-form>
    </div>

    <div v-if="items.length > 0">
      <div
          v-for="item in items"
          :key="item.id"
          class="cart-item"
      >
        <img :src="item.imageUrl" alt="" class="item-image"/>

        <div class="item-info">
          <h3>{{ item.name }}</h3>
          <p>Category: {{ item.category }}</p>
          <p class="description">{{ item.description }}</p>
        </div>

        <div class="qty-controls">
          <el-button size="small" @click="decrement(item)">-</el-button>
          <span>{{ item.quantity }}</span>
          <el-button size="small" @click="increment(item)">+</el-button>
        </div>

        <div class="item-actions">
          <span class="subtotal">${{ (item.price * item.quantity).toFixed(2) }}</span>
          <el-button type="danger" @click="remove(item)">Remove</el-button>
        </div>

        <div class="status-info">
          <span>Status: {{ item.status }}</span>
          <span v-if="item.updateTime">Updated at: {{ formatDate(item.updateTime) }}</span>
        </div>
      </div>

      <div class="cart-footer">
        <p class="total">Total: ${{ totalPrice.toFixed(2) }}</p>
        <el-button type="success" @click="checkout">Checkout</el-button>
      </div>
    </div>

    <div v-else>
      <p>Your cart is empty.</p>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'

// Inline profile form data
const profileForm = reactive({
  username: localStorage.getItem('username') || '',
  phone:    localStorage.getItem('phone')    || '',
  address:  localStorage.getItem('address')  || ''
})

const cartStore = useCartStore()
const items = computed(() => cartStore.items)
const totalPrice = computed(() => cartStore.totalPrice)
const router = useRouter()

function increment(item) {
  cartStore.updateQuantity(item.id, item.quantity + 1)
}

function decrement(item) {
  if (item.quantity > 1) cartStore.updateQuantity(item.id, item.quantity - 1)
}

function remove(item) {
  cartStore.removeItem(item.id)
}

async function checkout() {
  // Validate profile
  if (!profileForm.username || !profileForm.phone || !profileForm.address) {
    ElMessage.warning('Please fill in username, phone, and address before checkout.')
    return
  }

  const orderReq = {
    recipientName: profileForm.username,
    phone:    profileForm.phone,
    address:  profileForm.address,
    dishes:   items.value.map(i => ({ name: i.name, quantity: i.quantity, price: i.price }))
  }

  try {
    const { data } = await axios.post('/api/orders', orderReq)
    ElMessage.success('Order placed successfully!')
    // Persist profile
    localStorage.setItem('username', profileForm.username)
    localStorage.setItem('phone', profileForm.phone)
    localStorage.setItem('address', profileForm.address)
    cartStore.clearCart()
    // After successful checkout in UserCart.vue
    router.push({ name: 'UserOrders' })

  } catch (err) {
    console.error(err)
    ElMessage.error('Checkout failed: ' + (err.response?.data?.message || err.message))
  }
}

// Helper
function formatDate(dt) {
  const d = new Date(dt)
  return d.toLocaleString()
}
</script>

<style scoped>
.user-cart { padding: 20px; }
.profile-form { margin-bottom: 20px; }
.cart-item { display: grid; grid-template-columns: 80px 1fr auto auto; gap: 12px; align-items: start;
  border-bottom: 1px solid #eee; padding-bottom: 12px; margin-bottom: 12px; }
.item-image { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; }
.item-info h3 { margin: 0; font-size: 1.1em; }
.description { font-size: 0.9em; color: #666; }
.qty-controls { display: flex; align-items: center; }
.item-actions { display: flex; flex-direction: column; align-items: flex-end; }
.subtotal { font-weight: bold; margin-bottom: 6px; }
.status-info { grid-column: 1 / -1; font-size: 0.85em; color: #999; margin-top: 4px; }
.cart-footer { display: flex; justify-content: space-between; align-items: center;
  padding-top: 16px; border-top: 2px solid #ddd; }
.total { font-size: 1.2em; font-weight: bold; }
</style>
