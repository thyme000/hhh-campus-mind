import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
// 新增：导入头像上传组件
import AvatarUpload from '../views/AvatarUpload.vue'

import { useUserStore } from '../store/user'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: Login
        },
        {
            path: '/',
            name: 'home',
            component: Home,
            meta: { requiresAuth: true }
        },
        // 新增：头像上传页面路由（加在原有路由后面）
        {
            path: '/avatar',
            name: 'AvatarUpload',
            component: AvatarUpload,
            meta: { requiresAuth: true } // 和首页一样需要登录才能访问
        }
    ]
})

router.beforeEach((to, _from, next) => {
    const userStore = useUserStore()
    if (to.meta.requiresAuth && !userStore.role) {
        next('/login')
    } else if (to.path === '/login' && userStore.role) {
        next('/')
    } else {
        next()
    }
})

export default router