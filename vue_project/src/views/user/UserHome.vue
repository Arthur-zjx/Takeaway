<template>
  <div class="user-home">
    <h2 class="section-title">Menu</h2>
    <div class="dish-list">
      <div
          class="dish-card"
          v-for="(dish, index) in dishList"
          :key="dish.id"
      >
        <img :src="dish.imageUrl" alt="dish" class="card-image" />
        <div class="card-body">
          <p class="card-name">{{ dish.name }}</p>
          <p class="card-price">¥{{ dish.price }}</p>
          <el-button type="success" size="small" @click="addToCart(dish)">
            Add
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const dishList = ref([])

// 组件加载时调用接口获取真实数据
onMounted(async () => {
  try {
    const response = await axios.get('/api/dish/')
    // 假设接口返回的数据格式为一个数组，每个元素是一个菜品对象
    dishList.value = response.data
  } catch (error) {
    console.error('Error fetching dish list:', error)
  }
})

// 添加到购物车（示例）
function addToCart(dish) {
  console.log('Add to cart:', dish)
  // 这里可以调用全局状态管理（如 Vuex 或 Pinia）更新购物车数据
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

/* 卡片容器 */
.dish-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

/* 单个菜品卡片 */
.dish-card {
  width: 220px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background-color: #fff;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 图片区域 */
.card-image {
  width: 100%;
  height: 130px;
  object-fit: cover;
}

/* 卡片主体 */
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
