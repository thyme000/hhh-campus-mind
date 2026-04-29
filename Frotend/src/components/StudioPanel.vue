<script setup lang="ts">
import { 
  Headphones, Video, Map, FileText, LayoutList, CheckSquare, Table,
  PlayCircle, FileQuestion, Network, PlusCircle, MoreVertical
} from 'lucide-vue-next'

const tools = [
  { icon: Headphones, label: '音频概览', color: 'text-blue-400', bg: 'bg-blue-500/10' },
  { icon: Video, label: '视频概览', color: 'text-green-400', bg: 'bg-green-500/10' },
  { icon: Map, label: '思维导图', color: 'text-purple-400', bg: 'bg-purple-500/10' },
  { icon: FileText, label: '报告', color: 'text-yellow-400', bg: 'bg-yellow-500/10' },
  { icon: LayoutList, label: '闪卡', color: 'text-pink-400', bg: 'bg-pink-500/10' },
  { icon: CheckSquare, label: '测验', color: 'text-teal-400', bg: 'bg-teal-500/10' },
  { icon: Table, label: '数据表格', color: 'text-indigo-400', bg: 'bg-indigo-500/10', fullWidth: true },
]

const recentSources = [
  {
    icon: PlayCircle,
    title: '现代网络安全：从城堡到代码',
    meta: '10个来源 · 4分钟前',
    color: 'text-emerald-400'
  },
  {
    icon: FileQuestion,
    title: '网络安全测验',
    meta: '10个来源 · 18分钟前',
    color: 'text-sky-400'
  },
  {
    icon: Network,
    title: '现代网络技术与生态演进全景图谱',
    meta: '7个来源 · 22分钟前',
    color: 'text-fuchsia-400'
  }
]
</script>

<template>
  <div class="h-full flex flex-col bg-[#161616] border-l border-gray-800/50 relative">
    <div class="p-6 border-b border-gray-800/50 h-16 flex items-center justify-between flex-shrink-0 bg-panel/50 backdrop-blur-md sticky top-0 z-10">
      <h2 class="text-lg font-medium text-gray-100 tracking-wide">Studio</h2>
      <button class="p-2 text-gray-400 hover:text-white rounded-lg hover:bg-gray-800 transition-colors">
        <LayoutList class="w-5 h-5" />
      </button>
    </div>

    <div class="flex-1 overflow-y-auto p-6 custom-scrollbar">
      
      <!-- Tools Grid -->
      <div class="grid grid-cols-2 gap-3 mb-10">
        <button 
          v-for="tool in tools" 
          :key="tool.label"
          class="flex items-center p-3.5 rounded-2xl bg-gray-800/40 hover:bg-gray-700/60 border border-gray-700/50 transition-all duration-200 group relative overflow-hidden text-left"
          :class="{ 'col-span-2': tool.fullWidth }"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-white/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
          
          <div :class="[tool.bg]" class="p-2.5 rounded-xl shadow-inner mr-3 flex-shrink-0">
            <component :is="tool.icon" :class="tool.color" class="w-5 h-5" />
          </div>
          
          <span class="text-[15px] font-medium text-gray-300 group-hover:text-white transition-colors flex-1 truncate">
            {{ tool.label }}
          </span>
          
          <div class="w-6 h-6 rounded-full bg-gray-700/50 flex items-center justify-center text-gray-400 group-hover:text-white opacity-0 group-hover:opacity-100 transition-opacity">
            <PlusCircle class="w-4 h-4" />
          </div>
        </button>
      </div>

      <!-- Recent Sources -->
      <div>
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-4 px-1">最近动态</h3>
        <div class="space-y-2">
          <div 
            v-for="(source, index) in recentSources" 
            :key="index"
            class="flex items-start p-3 rounded-xl hover:bg-gray-800/60 cursor-pointer transition-colors group border border-transparent hover:border-gray-700/50"
          >
            <div class="mt-1 flex-shrink-0 bg-gray-800/50 p-2 rounded-lg">
              <component :is="source.icon" :class="source.color" class="w-5 h-5" />
            </div>
            <div class="ml-3 flex-1 overflow-hidden">
              <div class="text-[15px] font-medium text-gray-200 truncate group-hover:text-white transition-colors">{{ source.title }}</div>
              <div class="text-xs text-gray-500 mt-1">{{ source.meta }}</div>
            </div>
            <button class="opacity-0 group-hover:opacity-100 p-1.5 text-gray-400 hover:text-white rounded-lg hover:bg-gray-700 transition-all ml-2">
              <MoreVertical class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
      
    </div>

    <!-- Floating Add Note Button -->
    <div class="absolute bottom-6 right-6">
      <button class="flex items-center space-x-2 bg-white text-gray-900 px-5 py-3 rounded-2xl shadow-[0_8px_30px_rgb(0,0,0,0.4)] hover:bg-gray-100 transition-all duration-200 font-semibold transform hover:scale-105 border border-gray-200">
        <FileText class="w-5 h-5 text-gray-700" />
        <span>添加笔记</span>
      </button>
    </div>
  </div>
</template>
