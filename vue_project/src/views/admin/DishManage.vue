<template>
  <div class="dish-container">
    <h2 class="page-title">Dish Management</h2>

    <div class="filter-bar">
      <el-input v-model="filters.name" placeholder="Dish Name" class="filter-item" />
      <el-select v-model="filters.category" placeholder="Category" class="filter-item">
        <el-option label="All" value="" />
        <el-option label="Appetizers" value="appetizers" />
        <el-option label="Soups" value="soups" />
        <el-option label="Seafood" value="seafood" />
        <el-option label="Meat" value="meat" />
        <el-option label="Vegetables" value="vegetables" />
        <el-option label="Rice & Noodles" value="rice_noodles" />
        <el-option label="Chef's Specials" value="chefs-specials" />
        <el-option label="Desserts" value="desserts" />
        <el-option label="Beverages" value="beverages" />
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElNotification, ElMessageBox } from 'element-plus'

const router = useRouter()

// Filter conditions
const filters = ref({ name: '', category: '', status: '' })

// Full dish list
const dishList = ref([])

// Currently selected dishes (for batch delete)
const selectedDishes = ref([])

// Computed: filter dish list by conditions
const filteredDishes = computed(() => dishList.value.filter(dish => {
  const matchName = filters.value.name ? dish.name.toLowerCase().includes(filters.value.name.toLowerCase()) : true
  const matchCategory = filters.value.category ? dish.category === filters.value.category : true
  const matchStatus = filters.value.status ? dish.status === filters.value.status : true
  return matchName && matchCategory && matchStatus
}))

// Date formatting helper
const formatDate = timestamp => timestamp ? new Date(timestamp).toLocaleString() : 'empty'

// Fetch all dishes
const fetchDishes = async () => {
  try {
    // Admin should see all dishes, add ?showAll=true
    const { data } = await axios.get('/api/dish/?showAll=true', { withCredentials: true })
    dishList.value = data
  } catch (err) {
    console.error('Error fetching dishes:', err)
  }
}

// Search button (local filter)
const handleSearch = () => console.log('Search filters applied:', filters.value)

// Batch delete selected dishes
const handleBatchDelete = async () => {
  if (!selectedDishes.value.length) {
    return ElNotification({ type: 'warning', title: 'No Selection', message: 'Please select at least one dish to delete.', duration: 3000 })
  }
  const confirm = await ElMessageBox.confirm('Are you sure you want to delete the selected dishes?', 'Confirm', { type: 'warning' })
      .catch(() => false)
  if (!confirm) return
  try {
    await Promise.all(selectedDishes.value.map(d => axios.delete(`/api/dish/${d.id}`)))
    ElNotification({ type: 'success', title: 'Deleted', message: 'Selected dishes deleted.', duration: 3000 })
    fetchDishes()
  } catch (err) {
    console.error('Error deleting selected dishes:', err)
    ElNotification({type: 'error', title: 'Error', message: 'Failed to delete selected dishes.', duration: 3000})
  }
}

// Handle table selection change
const handleSelectionChange = selection => selectedDishes.value = selection

// Edit a single dish
const handleEdit = dish => router.push(`/main/dish/edit/${dish.id}`)

// Delete a single dish
const handleDelete = async dish => {
  const confirm = await ElMessageBox.confirm(`Delete ${dish.name}?`, 'Confirm', {type: 'warning'})
      .catch(() => false)
  if (!confirm) return
  try {
    await axios.delete(`/api/dish/${dish.id}`)
    ElNotification({type: 'success', title: 'Deleted', message: `${dish.name} deleted.`, duration: 3000})
    fetchDishes()
  } catch (err) {
    console.error('Error deleting dish:', err)
    ElNotification({type: 'error', title: 'Error', message: 'Failed to delete dish.', duration: 3000})
  }
}

// Toggle dish status
const toggleStatus = async dish => {
  const newStatus = dish.status === 'available' ? 'unavailable' : 'available'
  try {
    await axios.put(`/api/dish/${dish.id}`, {...dish, status: newStatus})
    dish.status = newStatus
    ElNotification({type: 'success', title: 'Updated', message: `Dish status set to ${newStatus}.`, duration: 3000})
  } catch (err) {
    console.error('Error updating dish status:', err)
    ElNotification({type: 'error', title: 'Error', message: 'Failed to update dish status.', duration: 3000})
  }
}

// Add new dish
const handleAddDish = () => router.push(`/main/dish/edit`)

onMounted(fetchDishes)
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
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
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
  gap: 8px;
  flex-wrap: wrap;
}
</style>
