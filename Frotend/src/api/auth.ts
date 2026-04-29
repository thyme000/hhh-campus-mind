import { apiPost } from '@/api/client'
import type { LoginPayload } from '@/types/api'

export const loginApi = (payload: LoginPayload) =>
  apiPost<string>('/login', payload, { withAuth: false })

