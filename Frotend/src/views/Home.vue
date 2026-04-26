<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { MessageSquarePlus, Send, Trash2, LogOut, Pencil } from 'lucide-vue-next'
import MarkdownIt from 'markdown-it'
import markdownItKatex from 'markdown-it-katex'
import DOMPurify from 'dompurify'
import 'katex/dist/katex.min.css'
import { useUserStore } from '@/store/user'
import {
  createSessionApi,
  deleteSessionApi,
  listMessagesApi,
  listSessionApi,
  renameSessionApi,
  streamChatApi
} from '@/api/chat'
import type { ChatMessageItem, SessionItem } from '@/types/api'

interface UiMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
}

const router = useRouter()
const userStore = useUserStore()

const sessions = ref<SessionItem[]>([])
const activeSessionId = ref('')
const messages = ref<UiMessage[]>([])
const inputText = ref('')
const isLoadingSessions = ref(false)
const isSending = ref(false)
const errorMessage = ref('')
const listLoading = ref(false)
const messageContainer = ref<HTMLElement | null>(null)

const activeSession = computed(() =>
  sessions.value.find((item) => item.sessionId === activeSessionId.value) ?? null
)

const roleText = computed(() => {
  if (userStore.role === 'admin') return '管理员'
  if (userStore.role === 'teacher') return '教师'
  return '学生'
})

const markdown = new MarkdownIt({
  html: false,
  breaks: true,
  linkify: true
}).use(markdownItKatex)

const renderedCache = new Map<string, string>()

const formatAssistantContent = (raw: string) => {
  let text = raw.replace(/\r\n/g, '\n').trim()
  if (!text) return text

  // Normalize LaTeX delimiters to markdown-it-katex friendly forms.
  text = text.replace(/\\\[((?:.|\n)*?)\\\]/g, (_, formula: string) => `\n\n$$\n${formula.trim()}\n$$\n\n`)
  text = text.replace(/\\\(((?:.|\n)*?)\\\)/g, (_: string, formula: string) => `$${formula.trim()}$`)

  // Fix common malformed emphasis like "步骤**：" -> "**步骤**："
  text = text.replace(/(^|\n)\s*(题目|步骤|答案)\*\*[:：]/g, '$1**$2**：')

  // Add visual paragraph breaks for Chinese solution sections.
  text = text.replace(/\s*(\*\*)?(题目|步骤|答案)(\*\*)?[:：]\s*/g, '\n\n**$2**：\n')

  // Collapse overly large blank regions.
  text = text.replace(/\n{3,}/g, '\n\n').trim()
  return text
}

const renderAssistantMessage = (content: string) => {
  if (!content) return ''
  const normalizedContent = formatAssistantContent(content)
  const cached = renderedCache.get(normalizedContent)
  if (cached) return cached
  const html = DOMPurify.sanitize(markdown.render(normalizedContent))
  renderedCache.set(normalizedContent, html)
  return html
}

const scrollToBottom = async () => {
  await nextTick()
  if (!messageContainer.value) return
  messageContainer.value.scrollTop = messageContainer.value.scrollHeight
}

const mapMessage = (item: ChatMessageItem): UiMessage => ({
  id: item.messageId,
  role: item.role === 'AI' ? 'assistant' : 'user',
  content: item.content
})

const loadSessions = async (autoPick = true) => {
  isLoadingSessions.value = true
  errorMessage.value = ''
  try {
    const page = await listSessionApi(1, 50)
    sessions.value = page.list.map((item) => ({
      ...item,
      sessionId: String(item.sessionId)
    }))

    if (autoPick && sessions.value.length > 0 && !activeSessionId.value) {
      activeSessionId.value = sessions.value[0].sessionId
    }
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '会话加载失败'
  } finally {
    isLoadingSessions.value = false
  }
}

const createSession = async () => {
  listLoading.value = true
  errorMessage.value = ''
  try {
    await createSessionApi()
    await loadSessions(false)
    if (sessions.value.length > 0) {
      activeSessionId.value = sessions.value[0].sessionId
    }
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '创建会话失败'
  } finally {
    listLoading.value = false
  }
}

const loadMessages = async (sessionId: string) => {
  if (!sessionId) return
  messages.value = []
  errorMessage.value = ''
  try {
    const page = await listMessagesApi(sessionId, 1, 200)
    messages.value = [...page.list]
      .map((item) => ({
        ...item,
        sessionId: String(item.sessionId),
        messageId: String(item.messageId)
      }))
      .reverse()
      .map(mapMessage)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '消息加载失败'
  } finally {
    await scrollToBottom()
  }
}

const removeSession = async (sessionId: string) => {
  errorMessage.value = ''
  try {
    await deleteSessionApi(sessionId)
    if (activeSessionId.value === sessionId) {
      activeSessionId.value = ''
      messages.value = []
    }
    await loadSessions(false)
    if (!activeSessionId.value && sessions.value.length > 0) {
      activeSessionId.value = sessions.value[0].sessionId
    }
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '删除会话失败'
  }
}

const renameSession = async (session: SessionItem) => {
  const currentTitle = session.title?.trim() || ''
  const nextTitle = window.prompt('请输入新的会话名称', currentTitle)?.trim()

  if (!nextTitle || nextTitle === currentTitle) return

  errorMessage.value = ''
  try {
    await renameSessionApi(session.sessionId, nextTitle)
    await loadSessions(false)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '重命名会话失败'
  }
}

const createTraceId = () => {
  if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
    return crypto.randomUUID()
  }
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

const sendMessage = async () => {
  if (!activeSessionId.value || !inputText.value.trim() || isSending.value) return

  errorMessage.value = ''
  isSending.value = true

  const userText = inputText.value.trim()
  inputText.value = ''

  const userMsgId = `local-user-${Date.now()}`
  const aiMsgId = `local-ai-${Date.now()}`

  messages.value.push({
    id: userMsgId,
    role: 'user',
    content: userText
  })

  messages.value.push({
    id: aiMsgId,
    role: 'assistant',
    content: ''
  })
  await scrollToBottom()

  try {
    const chunkQueue: string[] = []
    let isDrainingQueue = false

    const drainQueue = async () => {
      if (isDrainingQueue) return
      isDrainingQueue = true
      try {
        while (chunkQueue.length > 0) {
          const part = chunkQueue.shift() ?? ''
          if (!part) continue
          const target = messages.value.find((item) => item.id === aiMsgId)
          if (!target) break

          for (const char of part) {
            target.content += char
            void scrollToBottom()
            await new Promise((resolve) => setTimeout(resolve, 8))
          }
        }
      } finally {
        isDrainingQueue = false
      }
    }

    await streamChatApi({
      sessionId: activeSessionId.value,
      content: userText,
      traceId: createTraceId(),
      onMessage: (chunk) => {
        if (chunk) {
          chunkQueue.push(chunk)
          void drainQueue()
        }
      }
    })
    await new Promise((resolve) => {
      const check = () => {
        if (!isDrainingQueue && chunkQueue.length === 0) {
          resolve(true)
          return
        }
        setTimeout(check, 10)
      }
      check()
    })
    await loadSessions(false)
  } catch (error) {
    const target = messages.value.find((item) => item.id === aiMsgId)
    if (target && !target.content) {
      target.content = '抱歉，消息发送失败，请稍后重试。'
    }
    errorMessage.value = error instanceof Error ? error.message : '发送失败'
  } finally {
    isSending.value = false
  }
}

const logout = () => {
  userStore.logout()
  router.push('/login')
}

watch(activeSessionId, (newId) => {
  if (!newId) return
  void loadMessages(newId)
})

onMounted(async () => {
  await loadSessions()
  if (sessions.value.length === 0) {
    await createSession()
  }
})
</script>

<template>
  <div class="h-screen bg-slate-100 text-slate-900 flex flex-col">
    <header class="h-14 border-b border-slate-200 bg-white px-4 flex items-center justify-between">
      <div class="font-semibold">校园思维</div>
      <div class="flex items-center gap-3 text-sm">
        <span class="text-slate-500">{{ userStore.username }} · {{ roleText }}</span>
        <button
          type="button"
          class="h-8 px-3 rounded-md border border-slate-300 text-slate-700 hover:bg-slate-100 flex items-center gap-1"
          @click="logout"
        >
          <LogOut class="w-4 h-4" />
          退出
        </button>
      </div>
    </header>

    <main class="flex-1 min-h-0 flex">
      <aside class="w-72 border-r border-slate-200 bg-white p-3 flex flex-col">
        <button
          type="button"
          class="h-10 rounded-md bg-slate-900 text-white text-sm font-medium hover:bg-slate-800 disabled:opacity-60 flex items-center justify-center gap-2"
          :disabled="listLoading"
          @click="createSession"
        >
          <MessageSquarePlus class="w-4 h-4" />
          新建会话
        </button>

        <div class="mt-3 flex-1 min-h-0 overflow-y-auto space-y-2">
          <div v-if="isLoadingSessions" class="text-sm text-slate-500 p-2">加载中...</div>
          <button
            v-for="item in sessions"
            :key="item.sessionId"
            type="button"
            class="w-full text-left border rounded-md p-2 transition group"
            :class="
              activeSessionId === item.sessionId
                ? 'border-slate-900 bg-slate-900 text-white'
                : 'border-slate-200 bg-white hover:border-slate-300'
            "
            @click="activeSessionId = item.sessionId"
          >
            <div class="flex items-start justify-between gap-2">
              <div class="min-w-0">
                <div class="text-sm font-medium truncate">
                  {{ item.title || '未命名会话' }}
                </div>
                <div
                  class="text-xs mt-1"
                  :class="activeSessionId === item.sessionId ? 'text-slate-200' : 'text-slate-500'"
                >
                  {{ item.createAt }}
                </div>
              </div>
              <div class="flex items-center gap-1">
                <span
                  class="p-1 rounded hover:bg-black/10"
                  title="重命名会话"
                  @click.stop="renameSession(item)"
                >
                  <Pencil class="w-4 h-4" />
                </span>
                <span
                  class="p-1 rounded hover:bg-black/10"
                  title="删除会话"
                  @click.stop="removeSession(item.sessionId)"
                >
                  <Trash2 class="w-4 h-4" />
                </span>
              </div>
            </div>
          </button>
        </div>
      </aside>

      <section class="flex-1 min-w-0 flex flex-col">
        <div class="h-12 border-b border-slate-200 px-4 flex items-center bg-white text-sm text-slate-600">
          {{ activeSession?.title || '未命名会话' }}
        </div>

        <div ref="messageContainer" class="flex-1 overflow-y-auto p-4 space-y-4">
          <div
            v-for="msg in messages"
            :key="msg.id"
            class="w-full"
            :class="msg.role === 'user' ? 'flex justify-end' : 'flex justify-start'"
          >
            <div
              class="rounded-xl px-4 py-3 text-sm leading-7 shadow-sm"
              :class="
                msg.role === 'user'
                  ? 'max-w-3xl bg-slate-900 text-white whitespace-pre-wrap'
                  : 'max-w-4xl bg-white border border-slate-300 text-slate-800 chat-markdown'
              "
            >
              <template v-if="msg.role === 'assistant'">
                <div
                  v-if="msg.content"
                  v-html="renderAssistantMessage(msg.content)"
                />
                <template v-else-if="isSending">思考中...</template>
              </template>
              <template v-else>
                {{ msg.content }}
              </template>
            </div>
          </div>

          <div v-if="messages.length === 0" class="text-sm text-slate-500">开始提问吧。</div>
        </div>

        <div class="border-t border-slate-200 bg-white p-3">
          <div v-if="errorMessage" class="text-sm text-rose-600 mb-2">{{ errorMessage }}</div>
          <div class="flex items-end gap-2">
            <textarea
              v-model="inputText"
              rows="3"
              placeholder="请输入你的问题..."
              class="flex-1 resize-none rounded-md border border-slate-300 px-3 py-2 text-sm leading-6 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-500"
              @keydown.enter.exact.prevent="sendMessage"
            />
            <button
              type="button"
              class="h-10 px-4 rounded-md bg-slate-900 text-white text-sm font-medium hover:bg-slate-800 disabled:opacity-60 flex items-center gap-1"
              :disabled="!activeSessionId || !inputText.trim() || isSending"
              @click="sendMessage"
            >
              <Send class="w-4 h-4" />
              发送
            </button>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
:deep(.chat-markdown p) {
  margin: 0.45rem 0;
}

:deep(.chat-markdown) {
  line-height: 1.85;
  word-break: normal;
  overflow-wrap: anywhere;
}

:deep(.chat-markdown p:first-child) {
  margin-top: 0;
}

:deep(.chat-markdown p:last-child) {
  margin-bottom: 0;
}

:deep(.chat-markdown strong) {
  font-weight: 700;
}

:deep(.chat-markdown code) {
  background: rgb(241 245 249);
  border-radius: 0.25rem;
  padding: 0.1rem 0.35rem;
  font-size: 0.85em;
}

:deep(.chat-markdown pre) {
  background: rgb(15 23 42);
  color: rgb(226 232 240);
  border-radius: 0.5rem;
  padding: 0.75rem;
  overflow-x: auto;
  margin: 0.5rem 0;
}

:deep(.chat-markdown pre code) {
  background: transparent;
  color: inherit;
  padding: 0;
}

:deep(.chat-markdown ul),
:deep(.chat-markdown ol) {
  margin: 0.4rem 0;
  padding-left: 1.1rem;
}

:deep(.chat-markdown blockquote) {
  border-left: 3px solid rgb(148 163 184);
  margin: 0.4rem 0;
  padding: 0.2rem 0.7rem;
  color: rgb(71 85 105);
  background: rgb(248 250 252);
}

:deep(.chat-markdown .katex-display) {
  margin: 0.7rem 0 0.85rem;
  overflow-x: auto;
  overflow-y: hidden;
  text-align: left;
}

:deep(.chat-markdown .katex) {
  font-size: 1.02em;
  line-height: 1.2;
  vertical-align: -0.12em;
  white-space: nowrap;
}

:deep(.chat-markdown p .katex) {
  display: inline-block;
}

:deep(.chat-markdown .katex .base) {
  line-height: 1.2;
}

:deep(.chat-markdown .katex .mfrac .frac-line) {
  border-bottom-color: currentColor !important;
  border-bottom-width: 0.08em !important;
  min-height: 1px;
}

</style>
