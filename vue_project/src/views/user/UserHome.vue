<template>
  <div class="user-home">
    <h2 class="section-title">Menu</h2>
    <div class="dish-list">
      <div
          class="dish-card"
          v-for="(dish, index) in dishList"
          :key="dish.id"
      >
        <img :src="dish.imageUrl" alt="Dish Image" class="card-image" />
        <div class="card-body">
          <p class="card-name">{{ dish.name }}</p>
          <p class="card-price">¥{{ dish.price }}</p>
          <el-button type="primary" @click="addToCart(dish)">
            Add to Cart
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'

const cartStore = useCartStore()
const dishList = ref([])

// Fetch dishes when component mounts
onMounted(async () => {
  try {
    const response = await axios.get('/api/dish/')
    // Expect response.data to be an array of dish objects
    dishList.value = response.data
  } catch (error) {
    console.error('Error fetching dish list:', error)
  }
})

// Add the selected dish to the cart
function addToCart(dish) {
  cartStore.addItem(dish)
  // Log current cart items for debugging
  console.log(
      '[Home] after add, cartStore.items =',
      JSON.parse(JSON.stringify(cartStore.items))
  )
  ElMessage.success(`${dish.name} has been added to your cart`)
}
</script>

<style scoped>
.user-home {
  padding: 10px;
}

.section-title {
  margin: 20px 0 10px;
  font-size: 20px;
  font-weight: bold;
}

/* Container for dish cards */
.dish-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

/* Individual dish card */
.dish-card {
  width: 220px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background-color: #fff;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* Image area */
.card-image {
  width: 100%;
  height: 130px;
  object-fit: cover;
}

/* Card body section */
.card-body {
  padding: 10px;
  flex: 1;
}

.card-name {
  font-weight: bold;
  margin: 0 0 4px 0;
}

.card-price {
  margin: 0 0 8px 0;
  color: #666;
}
</style>
