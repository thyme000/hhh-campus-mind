import { apiPost } from '@/api/client'

export interface UploadDocumentPayload {
  objectKey: string
  fileName: string
  fileSize: number
  fileType: string
  courseId: number
  fileSha256: string
  traceId?: string
}

export const uploadDocumentApi = (payload: UploadDocumentPayload) =>
  apiPost<void>('/document/upload', payload)

export const deleteDocumentApi = (documentId: string) =>
  apiPost<void>(`/document/delete/${documentId}`)

export const restoreDocumentApi = (documentId: string) =>
  apiPost<void>(`/document/restore/${documentId}`)

