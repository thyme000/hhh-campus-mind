declare module 'ali-oss' {
  export interface OSSOptions {
    region?: string
    bucket?: string
    endpoint?: string
    secure?: boolean
    accessKeyId?: string
    accessKeySecret?: string
    stsToken?: string
  }

  export interface PutResult {
    name?: string
    url?: string
    res?: unknown
  }

  export default class OSS {
    constructor(options?: OSSOptions)
    put(name: string, file: Blob | File | Buffer | ArrayBuffer): Promise<PutResult>
  }
}

