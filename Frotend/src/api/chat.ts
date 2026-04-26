import { apiGet, apiPost, apiPut } from '@/api/client'
import type { ChatMessageItem, PageResult, SessionItem } from '@/types/api'

export const createSessionApi = () => apiPut<void>('/ai/chat/session/create')

export const listSessionApi = (pageNo = 1, pageSize = 20) =>
  apiGet<PageResult<SessionItem>>(`/ai/chat/session/list?pageNo=${pageNo}&pageSize=${pageSize}`)

export const deleteSessionApi = (sessionId: string) => apiPut<void>(`/ai/chat/session/delete/${sessionId}`)

export const renameSessionApi = (sessionId: string, title: string) =>
  apiPost<void>(`/ai/chat/session/rename/${sessionId}`, title)

export const listMessagesApi = (sessionId: string, pageNo = 1, pageSize = 100) =>
  apiGet<PageResult<ChatMessageItem>>(
    `/ai/chat/message/list/${sessionId}?pageNo=${pageNo}&pageSize=${pageSize}`
  )

interface StreamOptions {
  sessionId: string
  content: string
  traceId: string
  onMessage: (chunk: string) => void
  onDone?: (data?: string) => void
  signal?: AbortSignal
}

export async function streamChatApi(options: StreamOptions) {
  const token = localStorage.getItem('token') || ''
  if (!token) {
    throw new Error('未登录，请重新登录')
  }

  const search = new URLSearchParams({
    sessionId: options.sessionId,
    content: options.content,
    traceId: options.traceId
  })

  const response = await fetch(`/api/ai/chat/message/stream?${search.toString()}`, {
    method: 'GET',
    headers: {
      Authorization: token,
      Accept: 'text/event-stream'
    },
    signal: options.signal
  })

  if (!response.ok || !response.body) {
    throw new Error(`流式请求失败(${response.status})`)
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''
  let eventName = 'message'
  let dataLines: string[] = []

  const flushEvent = () => {
    if (dataLines.length === 0) return
    const data = dataLines.join('\n')
    if (eventName === 'error') {
      throw new Error(data || '消息流出错')
    }
    if (eventName === 'done') {
      options.onDone?.(data)
      return
    }
    if (eventName === 'message') {
      options.onMessage(data)
    }
  }

  while (true) {
    const { value, done } = await reader.read()
    if (done) {
      if (dataLines.length) flushEvent()
      break
    }

    buffer += decoder.decode(value, { stream: true })
    const lines = buffer.split(/\r?\n/)
    buffer = lines.pop() ?? ''

    for (const line of lines) {
      if (!line) {
        flushEvent()
        eventName = 'message'
        dataLines = []
        continue
      }
      if (line.startsWith('event:')) {
        eventName = line.slice(6).trim() || 'message'
      } else if (line.startsWith('data:')) {
        let dataPart = line.slice(5)
        // SSE 规范中 data: 后允许一个空格，这里只去掉一个，保留正文原始空白
        if (dataPart.startsWith(' ')) {
          dataPart = dataPart.slice(1)
        }
        dataLines.push(dataPart)
      }
    }
  }

  options.onDone?.()
}
