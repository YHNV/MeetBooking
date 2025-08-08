import axios from 'axios'

const instance = axios.create({
    baseURL: 'http://localhost:8080',
    // baseURL: '/api',
    withCredentials: true,
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json', // 默认所有请求都是 json 格式
    },
})

// 响应拦截器
instance.interceptors.response.use(
    (response) => response.data, // 直接返回 data
    (error) => Promise.reject(error),
)

export function useApi() {
    return {
        get: (url, config = {}) => instance.get(url, config),
        post: (url, data, config = {}) => instance.post(url, data, config),
    }
}