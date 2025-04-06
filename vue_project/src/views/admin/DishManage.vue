<template>
  <div class="dish-container">
    <h2 class="page-title">Dish Management</h2>

    <div class="filter-bar">
      <el-input v-model="filters.name" placeholder="Dish Name" class="filter-item" />
      <el-select v-model="filters.category" placeholder="Category" class="filter-item">
        <el-option label="All" value="" />
        <el-option label="Soup" value="soup" />
        <el-option label="Fish" value="fish" />
      </el-select>
      <el-select v-model="filters.status" placeholder="Status" class="filter-item">
        <el-option label="All" value="" />
        <el-option label="Available" value="available" />
        <el-option label="Unavailable" value="unavailable" />
      </el-select>
      <el-button type="primary" @click="handleSearch">Search</el-button>
      <el-button @click="handleBatchDelete" type="danger">Delete Selected</el-button>
      <el-button type="success" @click="handleAddDish">+ Add Dish</el-button>
    </div>

    <el-table :data="filteredDishes" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" />
      <el-table-column prop="name" label="Dish Name" />
      <el-table-column prop="imageUrl" label="Image">
        <template #default="scope">
          <img :src="scope.row.imageUrl" class="dish-img" />
        </template>
      </el-table-column>
      <el-table-column prop="category" label="Category" />
      <el-table-column prop="price" label="Price" />
      <el-table-column prop="status" label="Status">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'available' ? 'success' : 'info'">
            {{ scope.row.status === 'available' ? 'Available' : 'Unavailable' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="Last Updated">
        <template #default="scope">
          <span>{{ scope.row.updateTime ? formatDate(scope.row.updateTime) : 'empty' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Actions">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" @click="handleEdit(scope.row)">Edit</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">Delete</el-button>
            <el-button size="small" type="primary" @click="toggleStatus(scope.row)">
              {{ scope.row.status === 'available' ? 'Disable' : 'Enable' }}
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 筛选条件
const filters = ref({
  name: '',
  category: '',
  status: ''
})

// 所有菜品列表（从后端获取）
const dishList = ref([])

// 当前选中的菜品（用于批量删除）
const selectedDishes = ref([])

// 计算属性：根据筛选条件过滤菜品列表
const filteredDishes = computed(() => {
  return dishList.value.filter(dish => {
    const matchName = filters.value.name ? dish.name.toLowerCase().includes(filters.value.name.toLowerCase()) : true
    const matchCategory = filters.value.category ? dish.category === filters.value.category : true
    const matchStatus = filters.value.status ? dish.status === filters.value.status : true
    return matchName && matchCategory && matchStatus
  })
})

// 格式化日期函数：将 ISO 字符串转换为本地时间格式
const formatDate = (timestamp) => {
  if (!timestamp) return 'empty'
  const date = new Date(timestamp)
  return date.toLocaleString()
}

// 获取所有菜品数据
const fetchDishes = async () => {
  try {
    const response = await axios.get('/api/dish/')
    dishList.value = response.data
  } catch (err) {
    console.error('Error fetching dishes:', err)
  }
}

// 搜索按钮操作（目前使用本地过滤）
const handleSearch = () => {
  console.log('Search filters applied:', filters.value)
}

// 批量删除选中的菜品
const handleBatchDelete = async () => {
  if (selectedDishes.value.length === 0) {
    alert("Please select at least one dish to delete.")
    return
  }
  if (!confirm("Are you sure you want to delete the selected dishes?")) {
    return
  }
  try {
    await Promise.all(selectedDishes.value.map(dish => axios.delete(`/api/dish/${dish.id}`)))
    alert("Selected dishes deleted successfully!")
    fetchDishes()
  } catch (err) {
    console.error("Error deleting selected dishes:", err)
  }
}

// 监听表格多选变化
const handleSelectionChange = (selection) => {
  selectedDishes.value = selection
}

// 编辑单个菜品，跳转到 DishEdit 页面
const handleEdit = (dish) => {
  router.push(`/main/dish/edit/${dish.id}`)
}

// 删除单个菜品
const handleDelete = async (dish) => {
  if (!confirm(`Are you sure you want to delete ${dish.name}?`)) {
    return
  }
  try {
    await axios.delete(`/api/dish/${dish.id}`)
    alert(`${dish.name} deleted successfully!`)
    fetchDishes()
  } catch (err) {
    console.error("Error deleting dish:", err)
  }
}

// 切换菜品状态（available/unavailable），调用 PUT 更新菜品状态
const toggleStatus = async (dish) => {
  const newStatus = dish.status === 'available' ? 'unavailable' : 'available'
  const updatedDish = {...dish, status: newStatus}
  try {
    await axios.put(`/api/dish/${dish.id}`, updatedDish)
    dish.status = newStatus
    alert(`Dish status updated to ${newStatus}`)
  } catch (err) {
    console.error("Error updating dish status:", err)
  }
}

// 添加新菜品，跳转到 DishEdit 页面（不传 id 表示新增）
const handleAddDish = () => {
  router.push(`/main/dish/edit`)
}

// 页面加载时获取菜品数据
onMounted(() => {
  fetchDishes()
})
</script>

<style scoped>
.dish-container {
  padding: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.filter-item {
  width: 180px;
}

.dish-img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 6px;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
