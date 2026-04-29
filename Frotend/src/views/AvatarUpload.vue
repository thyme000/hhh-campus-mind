<template>
  <div class="box">
    <h2>修改我的头像</h2>
    <img :src="avatarUrl" class="avatar-preview" alt="头像预览">

    <div class="file-input">
      <input type="file" accept="image/*" @change="previewImage">
    </div>

    <button @click="uploadAvatar">✅ 上传头像</button>
    <div id="result" :class="resultClass">{{ resultText }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const avatarUrl = ref('/avatars/default.png')
const resultText = ref('')
const resultClass = ref('')
let selectedFile = null

// 页面加载时获取用户头像
onMounted(async () => {
  try {
    const res = await fetch('/api/get-avatar?user_id=1')
    const data = await res.json()
    avatarUrl.value = data.avatar_url
  } catch (e) {
    console.error('获取头像失败', e)
  }
})

// 预览图片
function previewImage(event) {
  const file = event.target.files[0]
  if (file) {
    selectedFile = file
    const reader = new FileReader()
    reader.onload = function(e) {
      avatarUrl.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// 上传头像
async function uploadAvatar() {
  if (!selectedFile) {
    resultText.value = '请先选择一张图片'
    resultClass.value = 'text-red-500'
    return
  }

  const formData = new FormData()
  formData.append('user_id', '1')
  formData.append('file', selectedFile)

  try {
    const res = await fetch('/api/upload-avatar', {
      method: 'POST',
      body: formData
    })
    const data = await res.json()

    if (data.code === 0) {
      resultText.value = '✅ 上传成功！头像已更新'
      resultClass.value = 'text-green-500'
      avatarUrl.value = data.avatar_url
    } else {
      resultText.value = '❌ 上传失败：' + data.msg
      resultClass.value = 'text-red-500'
    }
  } catch (e) {
    resultText.value = '❌ 网络错误：' + e.message
    resultClass.value = 'text-red-500'
  }
}
</script>

<style scoped>
.box {
  max-width: 400px;
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  margin: 0 auto;
  text-align: center;
}
.avatar-preview {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 3px solid #007bff;
  object-fit: cover;
  margin: 0 auto 20px;
}
button {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.text-green-500 { color: #28a745; }
.text-red-500 { color: #dc3545; }
</style>