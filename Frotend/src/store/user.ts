import { defineStore } from 'pinia'
import { ref } from 'vue'

export type Role = 'student' | 'teacher' | 'admin' | null

export const useUserStore = defineStore('user', () => {
  const role = ref<Role>((localStorage.getItem('role') as Role) || null)
  const username = ref(localStorage.getItem('username') || '')
  const token = ref(localStorage.getItem('token') || '')
  
  const login = (r: Role, name: string, t: string) => {
    role.value = r
    username.value = name
    token.value = t
    
    localStorage.setItem('role', r || '')
    localStorage.setItem('username', name)
    localStorage.setItem('token', t)
  }
  
  const logout = () => {
    role.value = null
    username.value = ''
    token.value = ''
    
    localStorage.removeItem('role')
    localStorage.removeItem('username')
    localStorage.removeItem('token')
  }

  return { role, username, token, login, logout }
})
