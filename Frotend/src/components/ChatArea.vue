<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { SendHorizontal, Pin, Copy, ThumbsUp, ThumbsDown, User, Bot, MoreVertical, Plus } from 'lucide-vue-next'
import { useUserStore } from '../store/user'

const userStore = useUserStore()

interface Message {
  id: number
  role: 'user' | 'assistant'
  content: string
}

const messages = ref<Message[]>([
  {
    id: 1,
    role: 'assistant',
    content: '你好，我是校园思维助手。内缓存以优化海量流量 29。\n\n- MobilityFirst: 将无线/移动终端作为主流接入设备进行架构设计 30。\n- SOFIA/Serval: 面向服务的体系结构，强调服务标识与寻址 31 32。\n\n6. 宏观发展趋势 (CNNIC 统计) 33\n- 基础设施: 中国已建成 5G 基站超 483 万座，行政村通 5G 比例达 95% 34。\n- AI 赋能: 生成式人工智能用户规模达 6.02 亿，成为推动数字化转型的核心引擎 33 34。\n\n如果您希望针对其中某个特定领域（例如“零信任的逻辑组件”）展开更详细的解析，请随时告诉我。'
  }
])

const inputMessage = ref('')
const messagesContainer = ref<HTMLElement | null>(null)

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const sendMessage = () => {
  if (!inputMessage.value.trim()) return
  
  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: inputMessage.value
  })
  
  const userMsg = inputMessage.value
  inputMessage.value = ''
  scrollToBottom()
  
  // Mock AI response
  setTimeout(() => {
    messages.value.push({
      id: Date.now(),
      role: 'assistant',
      content: `关于"${userMsg}"，这是一个很好的问题。现代网络安全架构中，SDN控制器主要负责集中式的网络策略下发与流量调度，而Web 3.0的去中心化身份(DID)则允许用户自主控制其数字身份数据。这对于校园网络安全管理有着重要的启示作用。`
    })
    scrollToBottom()
  }, 1000)
}

onMounted(() => {
  scrollToBottom()
})
</script>

<template>
  <div class="h-full flex flex-col bg-[#161616]">
    <!-- Header -->
    <div class="h-16 flex items-center justify-between px-6 border-b border-gray-800/50 flex-shrink-0 bg-panel/50 backdrop-blur-md sticky top-0 z-10">
      <div class="flex items-center space-x-3">
        <h2 class="text-lg font-medium text-gray-100 truncate">Modern Network Architectures: SDN and Zero Trust</h2>
      </div>
      <div class="flex items-center space-x-4">
        <button class="flex items-center space-x-2 text-sm font-medium text-gray-400 hover:text-white bg-gray-800/50 hover:bg-gray-700 px-3 py-1.5 rounded-lg transition-colors border border-gray-700/50">
          <Plus class="w-4 h-4" />
          <span>创建笔记本</span>
        </button>
        <button class="p-2 text-gray-400 hover:text-white rounded-lg hover:bg-gray-800 transition-colors">
          <MoreVertical class="w-5 h-5" />
        </button>
      </div>
    </div>

    <!-- Messages -->
    <div class="flex-1 overflow-y-auto p-6 space-y-8 custom-scrollbar" ref="messagesContainer">
      <div v-for="msg in messages" :key="msg.id" class="flex max-w-4xl mx-auto group">
        <!-- Avatar -->
        <div class="flex-shrink-0 mr-4">
          <div v-if="msg.role === 'assistant'" class="w-8 h-8 rounded-full bg-gradient-to-br from-indigo-500 to-purple-500 flex items-center justify-center shadow-lg shadow-indigo-500/20">
            <Bot class="w-5 h-5 text-white" />
          </div>
          <div v-else class="w-8 h-8 rounded-full bg-gray-700 flex items-center justify-center border border-gray-600">
            <User class="w-5 h-5 text-gray-300" />
          </div>
        </div>

        <!-- Content -->
        <div class="flex-1 overflow-hidden">
          <div class="flex items-center mb-1 space-x-2">
            <span class="font-medium text-sm text-gray-300">{{ msg.role === 'assistant' ? '校园思维助手' : userStore.username }}</span>
          </div>
          
          <!-- Message Body -->
          <div class="text-gray-300 leading-relaxed text-[15px] space-y-4" :class="{'bg-gray-800/40 p-4 rounded-2xl rounded-tl-none border border-gray-700/50': msg.role === 'assistant'}">
            <div class="whitespace-pre-wrap">{{ msg.content }}</div>
          </div>

          <!-- Actions (Only for AI) -->
          <div v-if="msg.role === 'assistant'" class="flex items-center space-x-2 mt-3 opacity-0 group-hover:opacity-100 transition-opacity">
            <button class="flex items-center space-x-1.5 text-xs text-gray-400 hover:text-indigo-400 bg-gray-800 hover:bg-gray-700 px-2.5 py-1.5 rounded-md transition-colors border border-gray-700">
              <Pin class="w-3.5 h-3.5" />
              <span>保存到笔记</span>
            </button>
            <button class="p-1.5 text-gray-400 hover:text-white rounded-md hover:bg-gray-700 transition-colors" title="复制">
              <Copy class="w-4 h-4" />
            </button>
            <button class="p-1.5 text-gray-400 hover:text-green-400 rounded-md hover:bg-gray-700 transition-colors" title="赞">
              <ThumbsUp class="w-4 h-4" />
            </button>
            <button class="p-1.5 text-gray-400 hover:text-red-400 rounded-md hover:bg-gray-700 transition-colors" title="踩">
              <ThumbsDown class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Input Area -->
    <div class="p-6 bg-gradient-to-t from-[#161616] to-transparent pt-2 border-t border-gray-800/30">
      <div class="max-w-4xl mx-auto relative group">
        <textarea
          v-model="inputMessage"
          @keydown.enter.prevent="sendMessage"
          placeholder="开始输入..."
          class="w-full bg-gray-800/80 border border-gray-700 text-gray-200 rounded-2xl pl-5 pr-14 py-4 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500/50 resize-none overflow-hidden placeholder-gray-500 shadow-lg shadow-black/20 transition-all"
          rows="1"
          style="min-height: 56px;"
        ></textarea>
        <button 
          @click="sendMessage"
          class="absolute right-3 bottom-3 p-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white transition-colors flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed shadow-md shadow-indigo-500/20"
          :disabled="!inputMessage.trim()"
        >
          <SendHorizontal class="w-4 h-4" />
        </button>
        <div class="absolute right-16 bottom-4 text-xs text-gray-500 font-medium">8 个来源</div>
      </div>
    </div>
  </div>
</template>
