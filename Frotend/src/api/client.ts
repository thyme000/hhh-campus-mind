import type { ApiResult } from '@/types/api'

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

interface RequestOptions extends Omit<RequestInit, 'body' | 'method'> {
  body?: unknown
  withAuth?: boolean
}

const API_PREFIX = '/api'
const LARGE_INTEGER_ID_PATTERN = /"([A-Za-z_][A-Za-z0-9_]*[iI]d)"\s*:\s*(\d{16,})/g

const getToken = () => localStorage.getItem('token') || ''

const preserveLargeIntegerIds = (text: string) =>
  text.replace(LARGE_INTEGER_ID_PATTERN, (_, key: string, value: string) => `"${key}":"${value}"`)

export async function request<T>(
  path: string,
  method: HttpMethod,
  options: RequestOptions = {}
): Promise<T> {
  const { body, withAuth = true, headers, ...rest } = options
  const finalHeaders = new Headers(headers ?? {})

  if (!(body instanceof FormData)) {
    finalHeaders.set('Content-Type', 'application/json')
  }
  finalHeaders.set('Accept', 'application/json')

  if (withAuth) {
    const token = getToken()
    if (token) {
      finalHeaders.set('Authorization', token)
    }
  }

  const response = await fetch(`${API_PREFIX}${path}`, {
    method,
    headers: finalHeaders,
    body: body == null ? undefined : body instanceof FormData ? body : JSON.stringify(body),
    ...rest
  })

  let payload: ApiResult<T> | null = null
  try {
    const rawText = await response.text()
    if (!rawText) {
      if (!response.ok) {
        throw new Error(`请求失败(${response.status})`)
      }
      return undefined as T
    }
    payload = JSON.parse(preserveLargeIntegerIds(rawText)) as ApiResult<T>
  } catch {
    if (!response.ok) {
      throw new Error(`请求失败(${response.status})`)
    }
    throw new Error(`接口返回格式异常(${response.status})`)
  }

  if (!response.ok) {
    throw new Error(payload?.message || `请求失败(${response.status})`)
  }

  if (!payload) {
    throw new Error(`接口返回为空(${response.status})`)
  }

  if (payload.code !== 200) {
    throw new Error(payload.message || `请求失败(${payload.code})`)
  }

  return payload.data
}

export const apiGet = <T>(path: string, options?: RequestOptions) => request<T>(path, 'GET', options)
export const apiPost = <T>(path: string, body?: unknown, options?: RequestOptions) =>
  request<T>(path, 'POST', { ...options, body })
export const apiPut = <T>(path: string, body?: unknown, options?: RequestOptions) =>
  request<T>(path, 'PUT', { ...options, body })
export const apiDelete = <T>(path: string, options?: RequestOptions) => request<T>(path, 'DELETE', options)
