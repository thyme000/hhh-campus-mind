export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  total: number
  pages: number
  list: T[]
}

export interface LoginPayload {
  userId: string
  password: string
  collegeName: string
  userType: 'STUDENT' | 'TEACHER' | 'ADMIN'
}

export interface SessionItem {
  sessionId: string
  title: string | null
  createAt: string
}

export interface ChatMessageItem {
  sessionId: string
  messageId: string
  role: 'USER' | 'AI' | string
  content: string
  createAt: string
}
