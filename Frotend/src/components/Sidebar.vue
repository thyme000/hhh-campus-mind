<script setup lang="ts">
import { useUserStore } from '../store/user'
import { useRouter } from 'vue-router'
import { 
  Home, Map, Sparkles, Newspaper, Dices, Mail, Info, 
  UserCircle, LogOut, BookOpen, PlusSquare, Users, BarChart3, ShieldCheck 
} from 'lucide-vue-next'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const navItems = [
  { label: '首页', icon: Home },
  { label: '分类索引', icon: Map },
  { label: '特色内容', icon: Sparkles },
  { label: '新闻动态', icon: Newspaper },
  { label: '随机条目', icon: Dices },
  { label: '联络我们', icon: Mail },
  { label: '关于校园思维', icon: Info },
]

const teacherItems = [
  { label: '所教科目管理', icon: BookOpen },
  { label: '添加教学资源', icon: PlusSquare },
  { label: '学生画像/成绩', icon: Users },
]

const adminItems = [
  { label: '全局数据分析', icon: BarChart3 },
  { label: '添加/管理教师', icon: ShieldCheck },
]
</script>

<template>
  <div class="h-full flex flex-col bg-panel text-gray-300 overflow-y-auto custom-scrollbar">
    <div class="p-6">
      <div class="flex items-center space-x-3 mb-8 text-white font-bold text-lg tracking-wide">
        <div class="w-8 h-8 rounded-lg bg-gradient-to-tr from-indigo-500 to-purple-500 flex items-center justify-center shadow-lg">
          <Sparkles class="w-5 h-5 text-white" />
        </div>
        <span>校园思维助手</span>
      </div>

      <!-- 个人中心 -->
      <div class="mb-8">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3 px-2">用户中心</h3>
        <button class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl hover:bg-gray-800 transition-colors group">
          <UserCircle class="w-5 h-5 text-gray-400 group-hover:text-indigo-400" />
          <span class="text-sm font-medium">个人主页 ({{ userStore.role === 'admin' ? '管理员' : userStore.role === 'teacher' ? '教师' : '学生' }})</span>
        </button>
      </div>

      <!-- 教师专属 -->
      <div v-if="userStore.role === 'teacher'" class="mb-8">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3 px-2">教学管理</h3>
        <button v-for="item in teacherItems" :key="item.label" class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl hover:bg-gray-800 transition-colors group mb-1">
          <component :is="item.icon" class="w-5 h-5 text-gray-400 group-hover:text-indigo-400" />
          <span class="text-sm font-medium">{{ item.label }}</span>
        </button>
      </div>

      <!-- 管理员专属 -->
      <div v-if="userStore.role === 'admin'" class="mb-8">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3 px-2">系统管理</h3>
        <button v-for="item in adminItems" :key="item.label" class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl hover:bg-gray-800 transition-colors group mb-1">
          <component :is="item.icon" class="w-5 h-5 text-gray-400 group-hover:text-indigo-400" />
          <span class="text-sm font-medium">{{ item.label }}</span>
        </button>
      </div>

      <!-- 导航 -->
      <div class="mb-8">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3 px-2">导航</h3>
        <button v-for="item in navItems" :key="item.label" class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl hover:bg-gray-800 transition-colors group mb-1">
          <component :is="item.icon" class="w-5 h-5 text-gray-400 group-hover:text-gray-200" />
          <span class="text-sm font-medium">{{ item.label }}</span>
        </button>
      </div>
    </div>
    
    <div class="mt-auto p-6 border-t border-gray-800/50">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-3">
          <div class="w-8 h-8 rounded-full bg-gray-700 flex items-center justify-center text-sm font-bold text-white">
            {{ userStore.username.charAt(0).toUpperCase() }}
          </div>
          <div class="flex flex-col">
            <span class="text-sm font-medium text-gray-200 leading-tight">{{ userStore.username }}</span>
            <span class="text-xs text-gray-500">{{ userStore.role }}</span>
          </div>
        </div>
        <button @click="handleLogout" class="p-2 rounded-lg hover:bg-gray-800 text-gray-400 hover:text-red-400 transition-colors" title="退出登录">
          <LogOut class="w-4 h-4" />
        </button>
      </div>
    </div>
  </div>
</template>
