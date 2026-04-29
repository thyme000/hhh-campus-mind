<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore, type Role } from '@/store/user'
import { loginApi } from '@/api/auth'
import type { LoginPayload } from '@/types/api'

const router = useRouter()
const userStore = useUserStore()

const roles = [
  { value: 'STUDENT', label: '学生' },
  { value: 'TEACHER', label: '教师' },
  { value: 'ADMIN', label: '管理员' }
] as const

const selectedRole = ref<(typeof roles)[number]['value']>('STUDENT')
const userId = ref('')
const password = ref('')
const collegeName = ref('')
const isLoading = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  if (!userId.value.trim() || !password.value.trim() || !collegeName.value.trim()) {
    errorMessage.value = '请完整填写登录信息'
    return
  }

  const normalizedUserId = userId.value.trim()
  if (!/^\d+$/.test(normalizedUserId)) {
    errorMessage.value = '学号或工号格式不正确'
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    const payload: LoginPayload = {
      userId: normalizedUserId,
      password: password.value,
      collegeName: collegeName.value,
      userType: selectedRole.value
    }
    const token = await loginApi(payload)

    userStore.login(selectedRole.value.toLowerCase() as Role, userId.value, token)
    router.push('/')
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '登录失败，请稍后重试'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-800 flex items-center justify-center p-4">
    <div class="w-full max-w-md bg-white border border-slate-200 rounded-lg shadow-sm p-8">
      <div class="mb-8">
        <h1 class="text-2xl font-semibold text-slate-900">校园思维</h1>
        <p class="mt-2 text-sm text-slate-500">登录后继续使用智能问答</p>
      </div>

      <div class="grid grid-cols-3 gap-2 mb-6">
        <button
          v-for="role in roles"
          :key="role.value"
          type="button"
          class="h-10 rounded-md border text-sm font-medium transition"
          :class="
            selectedRole === role.value
              ? 'border-slate-900 bg-slate-900 text-white'
              : 'border-slate-200 bg-white text-slate-600 hover:border-slate-300'
          "
          @click="selectedRole = role.value"
        >
          {{ role.label }}
        </button>
      </div>

      <div class="space-y-4">
        <div>
          <label class="block text-sm text-slate-600 mb-1">学院</label>
          <input
            v-model="collegeName"
            type="text"
            placeholder="例如：计算机学院"
            class="w-full h-11 px-3 rounded-md border border-slate-300 bg-white text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-500"
          />
        </div>

        <div>
          <label class="block text-sm text-slate-600 mb-1">学号 / 工号</label>
          <input
            v-model="userId"
            type="text"
            placeholder="请输入账号"
            class="w-full h-11 px-3 rounded-md border border-slate-300 bg-white text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-500"
          />
        </div>

        <div>
          <label class="block text-sm text-slate-600 mb-1">密码</label>
          <input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            class="w-full h-11 px-3 rounded-md border border-slate-300 bg-white text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-500"
            @keyup.enter="handleLogin"
          />
        </div>
      </div>

      <p v-if="errorMessage" class="mt-4 text-sm text-rose-600">{{ errorMessage }}</p>

      <button
        type="button"
        class="mt-6 w-full h-11 rounded-md bg-slate-900 text-white text-sm font-medium hover:bg-slate-800 transition disabled:opacity-60"
        :disabled="isLoading"
        @click="handleLogin"
      >
        {{ isLoading ? '登录中...' : '登录' }}
      </button>
    </div>
  </div>
</template>
