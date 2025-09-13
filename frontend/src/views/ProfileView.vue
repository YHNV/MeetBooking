<template>
  <div class="personal-info-container">
    <!-- 个人信息卡片 -->
    <el-card class="info-card" shadow="hover">
      <div class="card-header">
        <h2 class="card-title">个人信息</h2>
      </div>

      <div class="info-content">
        <!-- 左侧信息 -->
        <div class="info-left">
          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="姓名">{{ formData.empName || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="工号">{{ formData.empId || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="部门">{{ formData.deptName || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="职位">{{ formData.position || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="手机号码">{{ formData.phone || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="电子邮箱">{{ formData.email || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 右侧信息 -->
        <div class="info-right">
          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="性别">{{ formData.gender || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="年龄">{{ formData.age || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="出生日期">{{ formData.formattedBirthday || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="身份证号">{{ formData.idCard || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="部门地址">{{ formData.deptAddress || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="desc-item" label-width="120px">
            <el-descriptions-item label="部门描述">{{ formData.deptDesc || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <!-- 账户状态信息 -->
      <div class="account-status">
        <h3 class="status-title">账户状态</h3>
        <div class="status-content">
          <el-descriptions column="1" border class="status-item" label-width="120px">
            <el-descriptions-item label="账户状态">
              <el-tag type="success" v-if="formData.isActive">已激活</el-tag>
              <el-tag type="danger" v-else>未激活</el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="1" border class="status-item" label-width="120px">
            <el-descriptions-item label="是否管理员">
              <el-tag type="primary" v-if="formData.isAdmin">是</el-tag>
              <el-tag type="info" v-else>否</el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <el-descriptions column="2" border class="status-item" label-width="120px">
            <el-descriptions-item label="最后登录时间">
              <span class="login-time">{{ formatDate(formData.lastLoginTime) }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <!-- 修改密码按钮 -->
      <div class="btn-container">
        <el-button type="primary" @click="passwordDialogVisible = true" class="modify-btn"> 修改密码 </el-button>
      </div>
    </el-card>

    <!-- 修改密码弹窗布局 -->
    <el-dialog
      title="修改密码"
      v-model="passwordDialogVisible"
      width="500px"
      :close-on-click-modal="false"
      @keyup.enter="changePassword"
    >
      <el-form
        :model="passwordForm"
        class="password-form"
        label-width="100px"
        :rules="passwordRules"
        ref="passwordFormRef"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useApi } from '@/composables/useApi.js'
import { formatDate } from '@/utils/date.js'

const http = useApi()

// 表单数据
const formData = reactive({
  accountId: '',
  isAdmin: false,
  isActive: true,
  firstLogin: true,
  lastLoginTime: '',
  createTime: '',
  updateTime: '',
  empId: '',
  empName: '',
  position: '',
  phone: '',
  email: '',
  idCard: '',
  isManager: false,
  deptId: '',
  deptName: '',
  managerId: '',
  deptAddress: '',
  deptDesc: '',
  age: null,
  gender: '',
  formattedBirthday: '',
})

// 密码修改相关
const passwordDialogVisible = ref(false)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const passwordFormRef = ref(null)

// 密码校验规则
const passwordRules = reactive({
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])[^\s]{6,20}$/,
      message: '密码需包含大小写字母、数字和特殊字符',
      trigger: 'blur',
    },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
})

// 获取个人信息
const getAccountInfo = async () => {
  try {
    const response = await http.post('/account/getAccountDetail')
    if (response.code === 2001) {
      // console.log('个人信息：', response.data)
      Object.assign(formData, response.data)
    } else {
      ElMessage.error(response.msg || '个人信息查询失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '查询失败，请稍后重试')
    } else {
      console.error('获取个人信息失败：', error)
      ElMessage.error('查询失败，请稍后重试')
    }
  }
}

// 修改密码
const changePassword = async () => {
  // 先进行表单验证
  passwordFormRef.value.validate(async (valid) => {
    if (!valid) {
      return false
    }

    try {
      const response = await http.post('/account/changePassword', passwordForm)

      if (response.data) {
        console.log('修改密码成功')
        ElMessage.success(response.msg)
        // 重置表单
        passwordFormRef.value.resetFields()
      } else {
        ElMessage.error(response.msg || '修改密码失败')
      }

      // 关闭弹窗
      passwordDialogVisible.value = false
    } catch (error) {
      if (error.response && error.response.data) {
        ElMessage.error(error.response.data.msg || '修改失败，请稍后重试')
      } else {
        console.error('修改密码失败：', error)
        ElMessage.error('修改失败，请稍后重试')
      }
    }
  })
}
onMounted(() => {
  console.log('获取个人信息')
  getAccountInfo()
})
</script>

<style scoped>
/* 容器基础样式 */
.personal-info-container {
  width: 1200px;
  margin: 0 auto;
  padding: 10px;
}

/* 页面标题样式 */
.page-header {
  margin-bottom: 30px;
}
.page-title {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  margin: 0;
}
.page-desc {
  font-size: 1rem;
  color: #666;
  margin: 8px 0 0;
}

/* 卡片样式 */
.info-card {
  border-radius: 12px;
  overflow: hidden;
  margin-top: 20px;
  border: 1px solid #ebeef5;
}

/* 卡片头部样式 */
.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}
.card-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 信息内容布局（左右分栏） */
.info-content {
  display: flex;
  padding: 30px 20px 0;
  gap: 40px;
}
.info-left,
.info-right {
  flex: 1;
}

/* 描述项统一样式（核心对齐样式） */
.desc-item {
  width: 100%;
  margin-bottom: 20px;
  box-sizing: border-box;
}

/* 账户状态区域样式 */
.account-status {
  margin: 30px 20px 0;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
.status-title {
  font-size: 1.1rem;
  font-weight: 500;
  color: #333;
  margin: 0 0 16px;
}
.status-content {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.status-item {
  flex: 1;
  min-width: 200px;
  box-sizing: border-box;
}

/* 最后登录时间文本样式 */
.login-time {
  color: #666;
}

/* 按钮容器样式 */
.btn-container {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
}
.modify-btn {
  transition: all 0.3s ease;
}
.modify-btn:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 密码表单样式 */
.password-form {
  margin-top: 10px;
}
/* 弹窗表单项间距 */
.el-form-item {
  margin-bottom: 20px;
}
</style>
