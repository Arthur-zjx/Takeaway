<template>
  <div class="edit-dish-container">
    <h2>Edit Dish</h2>
    <el-form :model="dish" label-width="120px">
      <el-form-item label="Dish Name">
        <el-input v-model="dish.name" />
      </el-form-item>
      <el-form-item label="Price">
        <el-input v-model="dish.price" />
      </el-form-item>
      <el-form-item label="Category">
        <el-select v-model="dish.category">
          <el-option label="Appetizers" value="appetizers" />
          <el-option label="Soups" value="soups" />
          <el-option label="Seafood" value="seafood" />
          <el-option label="Meat" value="meat" />
          <el-option label="Vegetables" value="vegetables" />
          <el-option label="Rice & Noodles" value="rice_noodles" />
          <el-option label="Chef's Specials" value="chefs_specials" />
          <el-option label="Desserts" value="desserts" />
          <el-option label="Beverages" value="beverages" />
        </el-select>
      </el-form-item>
      <el-form-item label="Description">
        <el-input v-model="dish.description" />
      </el-form-item>
      <el-form-item label="Status">
        <el-switch
            v-model="dish.status"
            active-value="available"
            inactive-value="unavailable"
        />
      </el-form-item>
      <el-form-item label="Image">
        <div class="image-upload-container">
          <div class="current-image" v-if="dish.imageUrl">

            <img :src="dish.imageUrl" class="preview-img" />
          </div>
          <div class="upload-button-container">
            <el-upload
                :auto-upload="false"
                :limit="1"
                :show-file-list="false"
                accept="image/*"
                :on-change="handleFileChange"
            >
              <el-button type="primary">Select New Image</el-button>
            </el-upload>
          </div>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSave">Save</el-button>
        <el-button @click="router.back()">Cancel</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const dishId = route.params.id

// Determine whether it is edit mode
const isEditMode = computed(() => !!dishId)

// Initialize the dish object
// imageUrl is used to preview the image
// imageFile is used to store the newly selected file
const dish = ref({
  name: '',
  price: '',
  category: '',
  description: '',
  status: 'available',
  imageUrl: '',
  imageFile: null
})

// If in edit mode, load the existing dish data
const fetchDish = async () => {
  try {
    const response = await axios.get(`/api/dish/${dishId}`)
    // Assign fetched data and clear imageFile
    dish.value = { ...response.data, imageFile: null }
  } catch (err) {
    console.error('Error fetching dish data:', err)
    ElMessage.error('Failed to load dish data.')
  }
}

onMounted(() => {
  if (isEditMode.value) {
    fetchDish()
  }
})

// Handle image selection and generate local preview
const handleFileChange = (file) => {
  dish.value.imageFile = file.raw
  const reader = new FileReader()
  reader.onload = (e) => {
    dish.value.imageUrl = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// Save dish data:
// In edit mode, if a new image is selected, upload it first, then update the dish
// In create mode, upload everything in one go
const handleSave = async () => {
  try {
    if (isEditMode.value) {
      let updatedImageUrl = dish.value.imageUrl
      // If a new image is selected, upload it to get the updated URL
      if (dish.value.imageFile) {
        const formData = new FormData()
        formData.append('file', dish.value.imageFile)
        // Call image upload API
        const res = await axios.post('/api/dish/uploadImage', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        updatedImageUrl = res.data // the returned S3 URL
      }
      // Construct the update payload
      const payload = {
        name: dish.value.name,
        price: dish.value.price,
        category: dish.value.category,
        description: dish.value.description,
        status: dish.value.status,
        imageUrl: updatedImageUrl
      }
      console.log('Saving dish to backend:', payload)
      await axios.put(`/api/dish/${dishId}`, payload, {
        headers: { 'Content-Type': 'application/json' }
      })
      ElMessage.success('Dish updated successfully!')
    } else {
      // In create mode, directly call the upload API
      const formData = new FormData()
      if (dish.value.imageFile) {
        formData.append('file', dish.value.imageFile)
      }
      formData.append('name', dish.value.name)
      formData.append('price', dish.value.price)
      formData.append('category', dish.value.category)
      formData.append('description', dish.value.description)
      formData.append('status', dish.value.status)
      await axios.post('/api/dish/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      ElMessage.success('Dish added successfully!')
    }
    router.push('/main/dish')
  } catch (err) {
    console.error('Error saving dish:', err)
    ElMessage.error('Failed to save dish.')
  }
}
</script>




<style scoped>
.edit-dish-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.image-upload-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.current-image {
  text-align: center;
}

.preview-img {
  width: 120px;
  border-radius: 6px;
  margin-top: 10px;
  border: 1px solid #ccc;
}

.upload-button-container {
  /* 可根据需要调整上传按钮区域的样式 */
}
</style>
