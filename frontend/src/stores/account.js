import { defineStore } from 'pinia'
import { ref } from 'vue'
import { formatDate } from '@/utils/date.js'

export const useAccountStore = defineStore(
  'account',
  () => {
    // 用户是否登录
    const isLoggedIn = ref(false)
    // 用户数据，为了方便显示，有默认值
    const accountInfo = ref({
        accountId:0,
      empId: 0,
      empName: '未登录用户',
      isAdmin: false,
      isManager: false,
      firstLogin: false,
      lastLoginTime: new Date(),
      deptName: '未获取',
      position: '未获取',
      token: '未获取',
    })

    // 初始化数据：从本地存储加载数据，页面刷新后恢复状态
    const init = () => {
      const saved = localStorage.getItem('accountStore')
      if (saved) {
        try {
          const parsed = JSON.parse(saved)
          // 恢复登录状态
          isLoggedIn.value = parsed.isLoggedIn
          // 恢复用户信息
          accountInfo.value = { ...accountInfo.value, ...parsed.accountInfo }
          // 处理日期类型（localStorage存储的是字符串，需要转回Date）
          if (parsed.accountInfo.lastLoginTime) {
            accountInfo.value.lastLoginTime = new Date(parsed.accountInfo.lastLoginTime)
          }
        } catch (e) {
          console.error('加载用户信息失败', e)
          /// 加载失败时重置
          reset()
        }
      }
    }

    // 登录成功后更新账号信息
    const setAccountInfo = (loginResponse) => {
      // 更新登录状态
      isLoggedIn.value = true
      // 更新用户信息，只覆盖后端返回的字段，保留默认结构
      accountInfo.value = {
        ...accountInfo.value,
        ...loginResponse,
        // 确保lastLoginTime是Date类型
        lastLoginTime: loginResponse.lastLoginTime ? new Date(loginResponse.lastLoginTime) : new Date(),
      }
      // accountInfo.value.lastLoginTime = formatDate(loginResponse.lastLoginTime)
      // 持久化到本地存储
      saveToLocalStorage()
    }

    //退出登录：重置状态
    const logout = () => {
      // 调用后端退出登录接口
      reset()
      // 清除本地存储
      localStorage.removeItem('accountStore')
    }

    // 私有方法：保存到本地存储
    const saveToLocalStorage = () => {
      localStorage.setItem(
        'accountStore',
        JSON.stringify({
          isLoggedIn: isLoggedIn.value,
          accountInfo: {
            ...accountInfo.value,
            // 日期对象转为字符串存储
            lastLoginTime: accountInfo.value.lastLoginTime.toISOString(),
          },
        }),
      )
    }

    // 重置状态
    const reset = () => {
      isLoggedIn.value = false
      accountInfo.value = {
          accountId:0,
        empId: 0,
        empName: '未登录用户',
        isAdmin: false,
        isManager: false,
        firstLogin: false,
        lastLoginTime: new Date(),
        deptName: '未获取',
        position: '未获取',
        token: '未获取',
      }
    }

    return {
      isLoggedIn,
      accountInfo,
      init,
      setAccountInfo,
      logout,
    }
  },
  // 设置持久化开启，这样跨页面也能有效
  {
    persist: true,
  },
)
