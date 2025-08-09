<template>
  <!-- 登录页面 -->
  <div class="login-page">
    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 头部标题 -->
      <div class="login-header">
        <h2>企业会议室预约系统</h2>
      </div>
      <!-- 登录表单 -->
      <div class="login-form">
        <el-form :model="loginRequest">
          <el-form-item>
            <el-input placeholder="请输入账号" v-model="loginRequest.accountId"> </el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              type="password"
              placeholder="请输入密码"
              show-password
              v-model="loginRequest.password"
            >
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button class="login-button" type="primary" @click="login">登录</el-button>
          </el-form-item>
        </el-form>
      </div>
      <!-- 底部信息 -->
      <div class="login-footer">
        <p>登录账号：员工工号</p>
        <p>登录密码：yh@6位数生日</p>
        <p class="footer-msg">© 企业会议室预约系统</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useApi } from '@/composables/useApi.js'
import { useAccountStore } from '@/stores/account.js'

const http = useApi()
const accountStore = useAccountStore()
const router = useRouter()

// axios.defaults.baseURL = 'http://localhost:8080'

// 登录的请求体
const loginRequest = ref({
  accountId: '',
  password: '',
})

// 登录按钮点击事件
const login = async () => {
  // 前端验证输入框是否为空
  if (!loginRequest.value.accountId) {
    ElMessage.warning('请输入账号')
    return
  }
  if (!loginRequest.value.password) {
    ElMessage.warning('请输入密码')
    return
  }

  // 调用登录接口，发送登录请求
  console.log('账号：', loginRequest.value.username, '密码：', loginRequest.value.password)
  // ElMessage.info('登录请求已发送，等待响应...')

  // 调用登录接口
  try {
    const response = await http.post('/auth/login', loginRequest.value)
    console.log(response)
    // 如果登录失败，提示msg，清空登陆框
    if (response.code !== 2001) {
      ElMessage.error(response.msg)
      loginRequest.value = {
        accountId: '',
        password: '',
      }
      return
    }

    // 如果登录成功，将用户信息存储到小菠萝，并跳转到首页，提示登录成功
    ElMessage.success(response.msg)
    accountStore.setAccountInfo(response.data)
    return router.push('/test')
  } catch (error) {
    console.log('服务器异常' + error)
    ElMessage.error('服务器异常')
    return
  }
}
</script>

<style scoped>
/* 设置全局背景色 */
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #ecf5ff; /* 页面背景色，可根据喜好调整 */
}

.login-card {
  width: 400px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.login-header {
  text-align: center;
  background: #409eff;
  color: #fff;
  padding: 20px 15px;
}

.login-header h2 {
  font-size: 18px;
  margin: 0;
}

.login-form {
  padding: 30px 30px 20px 30px;
}

/* 输入框样式调整 */
::v-deep .el-input__inner {
  /* 圆角设置 */
  border-radius: 6px; /* 圆角大小，数值越大弧度越明显 */
  /* 高度设置 */
  height: 40px; /* 输入框高度 */
  line-height: 40px; /* 文字垂直居中 */
  padding: 0 5px; /* 内部边距，避免文字贴边 */
}

.login-button {
  /* 圆角设置（与输入框保持一致） */
  border-radius: 6px;
  /* 高度设置 */
  height: 40px;
  line-height: 40px;
  margin-top: 25px;
  font-size: 16px; /* 文字大小（可选） */
  width: 100%; /* 按钮占满宽度 */
  padding: 0; /* 清除默认内边距，避免高度异常 */
}

.login-footer {
  padding: 0 30px 30px;
  text-align: center;
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.footer-msg {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}
</style>
