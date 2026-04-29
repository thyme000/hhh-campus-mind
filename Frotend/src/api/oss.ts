import OSS from 'ali-oss'
import { apiGet } from '@/api/client'

export interface OssStsInfo {
  accessKeyId: string
  accessKeySecret: string
  securityToken: string
  expiration: string
  region: string
  bucket: string
  endpoint: string
  dir: string
}

export const getOssStsApi = (bizType = 'file') =>
  apiGet<OssStsInfo>(`/oss/sts?bizType=${encodeURIComponent(bizType)}`)

const toRegion = (region: string) => (region.startsWith('oss-') ? region : `oss-${region}`)

const toEndpoint = (endpoint: string) =>
  endpoint.replace(/^https?:\/\//i, '').replace(/\/+$/, '')

const sanitizeFileName = (fileName: string) => fileName.replace(/[^\w.\-]/g, '_')

const buildObjectKey = (dir: string, fileName: string) => {
  const normalizedDir = dir.endsWith('/') ? dir : `${dir}/`
  const random = `${Date.now()}-${Math.random().toString(16).slice(2, 10)}`
  return `${normalizedDir}${random}_${sanitizeFileName(fileName)}`
}

export const uploadFileBySts = async (file: File, sts: OssStsInfo) => {
  const objectKey = buildObjectKey(sts.dir, file.name)
  const client = new OSS({
    region: toRegion(sts.region),
    bucket: sts.bucket,
    accessKeyId: sts.accessKeyId,
    accessKeySecret: sts.accessKeySecret,
    stsToken: sts.securityToken,
    endpoint: toEndpoint(sts.endpoint),
    secure: true
  })
  await client.put(objectKey, file)
  return objectKey
}

