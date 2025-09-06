<template>
  <div class="employee-query-page">
    <el-card>
      <div class="filter-container">
        <el-row :gutter="20">
          <!-- 员工姓名筛选 -->
          <el-col :span="5">
            <div class="filter-item">
              <label class="filter-label">员工姓名</label>
              <el-input v-model="filters.empName" placeholder="请输入员工姓名" clearable @keyup.enter="handleQuery" />
            </div>
          </el-col>

          <!-- 部门筛选 -->
          <el-col :span="5">
            <div class="filter-item">
              <label class="filter-label">部门</label>
              <el-select v-model="filters.deptId" placeholder="请选择部门" clearable @change="handleQuery">
                <el-option
                  v-for="dept in departmentMap"
                  :key="dept.deptId"
                  :label="dept.deptName"
                  :value="dept.deptId"
                />
              </el-select>
            </div>
          </el-col>

          <!-- 是否在职 -->
          <el-col :span="5">
            <div class="filter-item">
              <label class="filter-label">是否在职</label>
              <el-select v-model="filters.isActive" placeholder="请选择在职状态" clearable @change="handleQuery">
                <el-option label="在职" value="true" />
                <el-option label="离职" value="false" />
              </el-select>
            </div>
          </el-col>

          <!-- 是否为管理者 -->
          <el-col :span="5">
            <div class="filter-item">
              <label class="filter-label">是否为管理者</label>
              <el-select v-model="filters.isManager" placeholder="请选择是否管理" clearable @change="handleQuery">
                <el-option label="是" value="true" />
                <el-option label="否" value="false" />
              </el-select>
            </div>
          </el-col>

          <!-- 操作按钮 -->
          <el-col :span="4">
            <div class="filter-item">
              <label class="filter-label">&nbsp;</label>
              <div>
                <el-button type="primary" @click="handleQuery"> 查询 </el-button>
                <el-button @click="handleReset"> 重置 </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 员工表格 -->
      <el-table :data="employeeList" border stripe v-loading="loading" element-loading-text="加载中...">
        <el-table-column prop="empId" label="员工ID" width="100" />
        <el-table-column prop="empName" label="姓名" width="120" />
        <el-table-column prop="position" label="职位" width="150" />
        <el-table-column prop="deptName" label="部门" width="150" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="isActive" label="是否在职" width="120" :formatter="formatIsActive" />
        <el-table-column prop="isManager" label="是否管理" width="120" :formatter="formatIsManager" />
        <el-table-column
          prop="createTime"
          label="入职时间"
          width="180"
          :formatter="(row) => formatDate(row.createTime)"
        />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" :type="scope.row.isActive ? 'danger' : 'info'" @click="handleToggle(scope.row)">
              {{ scope.row.isActive ? '冻结' : '解冻' }}
            </el-button>
            <el-button size="small" type="success" @click="handleEdit(scope.row)"> 编辑 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 编辑员工信息 -->
      <el-dialog title="编辑员工信息" v-model="editDialogVisible" width="500px" :before-close="handleDialogClose">
        <el-form :model="editForm" ref="editFormRef" :rules="editRules" label-width="100px">
          <el-form-item label="员工ID" prop="empId">
            <el-input v-model="editForm.empId" disabled />
          </el-form-item>
          <el-form-item label="员工姓名" prop="empName">
            <el-input v-model="editForm.empName" />
          </el-form-item>
          <el-form-item label="部门" prop="deptId">
            <el-select v-model="editForm.deptId" placeholder="请选择部门">
              <el-option v-for="dept in departmentMap" :key="dept.deptId" :label="dept.deptName" :value="dept.deptId" />
            </el-select>
          </el-form-item>
          <el-form-item label="职位" prop="position">
            <el-input v-model="editForm.position" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="editForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="editForm.email" />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="editForm.idCard" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleResetPassword" v-if="editForm.empId"> 重置密码 </el-button>
          <el-button type="success" @click="handleSave">保存</el-button>
        </template>
      </el-dialog>

      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from '@/stores/account.js'
import { useApi } from '@/composables/useApi.js'
import { formatDate } from '@/utils/date.js'

const http = useApi()
const router = useRouter()
const accountStore = useAccountStore()

// 状态管理
const loading = ref(false)
const employeeList = ref([])
const departmentMap = ref([])

// 编辑对话框状态
const editDialogVisible = ref(false)
const editFormRef = ref(null)

// 编辑表单数据
const editForm = reactive({
  empId: '',
  empName: '',
  deptId: '',
  position: '',
  phone: '',
  email: '',
  idCard: '',
})

// 编辑表单验证规则
const editRules = reactive({
  empName: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }],
  idCard: [{ pattern: /(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }],
})

const isAdmin = computed(() => accountStore.accountInfo.isAdmin)

// 筛选条件
const filters = reactive({
  empName: '',
  isActive: null, // 使用null作为初始值，避免传递空字符串
  isManager: null,
  deptId: null,
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  pages: 0,
})

// 页面加载时查询数据
onMounted(() => {
  if (!isAdmin.value) {
    // 不是管理员，进行拦截
    ElMessage.warning('您没有权限访问此页面')
    router.replace('/') // 重定向到首页
  } else {
    fetchEmployeeList()
    fetchDepartmentMap()
  }
})

// 查询员工列表
const fetchEmployeeList = async () => {
  try {
    loading.value = true

    // 构建请求体数据（合并分页参数和筛选条件）
    const requestData = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      // 仅包含有值的筛选条件，避免传递null或空字符串
      // 当 filters.empName 有值的时候，empName 才等于 filters.empName
      ...(filters.empName && { empName: filters.empName }),
      ...(filters.isActive !== null && { isActive: filters.isActive === 'true' }), // 转换为布尔值
      ...(filters.isManager !== null && { isManager: filters.isManager === 'true' }), // 转换为布尔值
      ...(filters.deptId && { deptId: filters.deptId }),
    }

    // 调用API查询
    const response = await http.post('/emp/queryEmployees', requestData)

    // 如果 code 正常，则显示数据
    if (response.code === 2001) {
      employeeList.value = response.data.list || []
      pagination.total = response.data.total || 0
      pagination.pages = response.data.pages || 0
    } else {
      ElMessage.error(response.msg || '员工信息查询失败')
    }
  } catch (error) {
    console.error('查询员工列表失败:', error)
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取部门信息
const fetchDepartmentMap = async () => {
  try {
    // 获取部门映射信息
    const response = await http.post('/dept/getSimpleDepartmentList')

    if (response.code === 2001) {
      departmentMap.value = response.data || []
    } else {
      ElMessage.error(response.msg || '部门映射查询失败')
    }
  } catch (error) {
    console.error('获取部门映射信息失败:', error)
    ElMessage.error('查询失败，请稍后重试')
  }
}

// 切换员工状态（冻结/解冻）
const toggleAccountStatus = async (empId, currentStatus) => {
  try {
    // 切换用户状态
    const response = await http.post('/account/toggleAccountStatus', empId)

    if (response.code === 2001) {
      ElMessage.success(`操作成功：${currentStatus ? '已冻结' : '已解冻'}`)
      fetchEmployeeList() // 刷新列表
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('切换用户状态失败：', error)
    ElMessage.error('切换失败，请稍后重试')
  }
}

// 处理查询
const handleQuery = () => {
  pagination.pageNum = 1 // 重置到第一页
  fetchEmployeeList()
}

// 处理重置
const handleReset = () => {
  // 重置筛选条件
  Object.keys(filters).forEach((key) => {
    filters[key] = null
  })
  // 重置分页
  pagination.pageNum = 1
  pagination.pageSize = 10
  // 重新查询
  fetchEmployeeList()
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchEmployeeList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchEmployeeList()
}

// 格式化是否在职
const formatIsActive = (row) => {
  return row.isActive ? '在职' : '离职'
}

// 格式化是否为管理者
const formatIsManager = (row) => {
  return row.isManager ? '是' : '否'
}

// 查看员工详情
const handleToggle = (row) => {
  const actionText = row.isActive ? '冻结' : '解冻'
  ElMessageBox.confirm(`确定要${actionText}员工【${row.empName}】吗？`, '确认操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      toggleAccountStatus(row.empId, row.isActive)
    })
    .catch(() => {
      ElMessage.info('已取消操作')
    })
}

// 打开编辑对话框
const handleEdit = (row) => {
  // 复制行数据到编辑表单
  Object.assign(editForm, {
    empId: row.empId,
    empName: row.empName,
    deptId: row.deptId,
    position: row.position,
    phone: row.phone,
    email: row.email,
    idCard: row.idCard || '',
  })
  editDialogVisible.value = true
}

// 关闭对话框
const handleDialogClose = () => {
  editDialogVisible.value = false
  editFormRef.value?.resetFields()
}

// 保存编辑内容
const handleSave = async () => {
  try {
    // 表单验证
    await editFormRef.value.validate()

    // 调用API更新员工信息
    const response = await http.post('/emp/updateEmployeeInfo', editForm)

    if (response.code === 2001) {
      ElMessage.success('员工信息更新成功')
      editDialogVisible.value = false
      fetchEmployeeList() // 刷新列表
    } else {
      ElMessage.error(response.msg || '更新失败')
    }
  } catch (error) {
    if (error.name === 'Error') {
      console.error('更新员工信息失败:', error)
      ElMessage.error('更新失败，请稍后重试')
    }
  }
}

// 重置密码
const handleResetPassword = async () => {
  try {
    ElMessageBox.confirm('确定要重置该员工的密码吗？重置后密码将变为初始密码', '确认重置', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      const response = await http.post('/account/resetPassword', { empId: editForm.empId })

      if (response.code === 2001) {
        ElMessage.success('密码重置成功')
      } else {
        ElMessage.error(response.msg || '密码重置失败')
      }
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}
</script>

<style scoped>
.employee-query-page {
  padding: 24px; /* 稍微增加页面内边距 */
  max-width: 1500px; /* 限制最大宽度，避免在宽屏上过度拉伸 */
  margin: 0 auto; /* 居中显示 */
}

.filter-container {
  background-color: #f5f7fa;
  padding: 20px; /* 增加内边距，让内容不那么拥挤 */
  border-radius: 6px; /* 稍微增大圆角 */
  margin-bottom: 24px; /* 增加与表格的距离 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); /* 增加轻微阴影，提升层次感 */
}

/* 筛选项样式 */
.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px; /* 标签与输入框之间的距离 */
}

/* 筛选标签样式 */
.filter-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  line-height: 1;
  padding-left: 2px;
}

/* 调整筛选行之间的间距 */
.el-row.mt-3 {
  margin-top: 16px !important;
}

/* 调整输入框高度，使其更协调 */
.el-input__wrapper,
.el-select__wrapper {
  height: 36px !important;
}

/* 调整按钮高度与输入框一致 */
.el-button {
  height: 36px;
  padding: 0 16px;
}

.pagination-container {
  margin-top: 20px; /* 增加分页与表格的距离 */
  padding-top: 10px;
  border-top: 1px solid #f0f0f0; /* 增加分隔线 */
}

/* 让表格内容区域更宽敞 */
.el-table {
  border-radius: 6px;
  overflow: hidden; /* 配合圆角使用 */
}

/* 调整表格列的最小宽度，避免内容挤压 */
.el-table-column {
  min-width: 80px;
}
</style>
