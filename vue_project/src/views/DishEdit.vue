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
          <el-option label="Soup" value="soup" />
          <el-option label="Fish" value="fish" />
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
        <div v-if="dish.imageUrl">
          <p>Current Image:</p>
          <img :src="dish.imageUrl" class="preview-img" />
        </div>
        <el-upload
            :auto-upload="false"
            :limit="1"
            :show-file-list="false"
            accept="image/*"
            :on-change="handleFileChange"
        >
          <el-button type="primary">Select New Image</el-button>
        </el-upload>
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

// 判断是否处于编辑模式
const isEditMode = computed(() => !!dishId)

// 初始化 dish 对象，imageUrl 用于展示图片预览，imageFile 用于存储新选中的图片文件
const dish = ref({
  name: '',
  price: '',
  category: '',
  description: '',
  status: 'available',
  imageUrl: '',
  imageFile: null
})

// 若处于编辑模式，则加载已有菜品数据
const fetchDish = async () => {
  try {
    const response = await axios.get(`/api/dish/${dishId}`)
    // 将返回数据赋值，并清空 imageFile
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

// 处理选择图片，并生成本地预览
const handleFileChange = (file) => {
  dish.value.imageFile = file.raw
  const reader = new FileReader()
  reader.onload = (e) => {
    dish.value.imageUrl = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 保存：编辑模式下，如果选了新图片，则先调用上传图片接口，再更新菜品数据
const handleSave = async () => {
  try {
    if (isEditMode.value) {
      let updatedImageUrl = dish.value.imageUrl
      // 如果选择了新图片，则先上传图片获取新 URL
      if (dish.value.imageFile) {
        const formData = new FormData()
        formData.append('file', dish.value.imageFile)
        // 调用新上传图片接口
        const res = await axios.post('/api/dish/uploadImage', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        updatedImageUrl = res.data // 返回的是图片 URL
      }
      // 构造更新 payload
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
      // 新增模式直接调用新增接口（保持原有逻辑）
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
}

.preview-img {
  width: 100px;
  border-radius: 6px;
  margin-bottom: 10px;
}
</style>
