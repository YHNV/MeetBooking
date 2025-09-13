<template>
  <div class="dept-management-page">
    <el-card>
      <div class="filter-container">
        <el-row :gutter="20">
          <!-- 部门筛选改为下拉选择 -->
          <el-col :span="8">
            <div class="filter-item">
              <label class="filter-label">部门名称</label>
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

          <!-- 操作按钮 -->
          <el-col :span="16">
            <div class="filter-item">
              <label class="filter-label">&nbsp;</label>
              <div class="button-group">
                <el-button type="primary" @click="handleQuery"> 查询 </el-button>
                <el-button @click="handleReset"> 重置 </el-button>
                <el-button type="success" @click="handleAdd"> 添加部门 </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 部门表格 -->
      <el-table :data="deptList" border stripe v-loading="loading" element-loading-text="加载中...">
        <el-table-column prop="deptId" label="部门ID" width="100" />
        <el-table-column prop="deptName" label="部门名称" width="180" />
        <el-table-column prop="empName" label="部门经理" width="150" />
        <el-table-column prop="deptAddress" label="部门地址" width="200" />
        <el-table-column prop="deptDesc" label="部门描述" />
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          :formatter="(row) => formatDate(row.createTime)"
        />
        <el-table-column
          prop="updateTime"
          label="更新时间"
          width="180"
          :formatter="(row) => formatDateForStr(row.updateTime)"
        />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleEdit(scope.row)"> 编辑 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 编辑部门信息 -->
      <el-dialog title="编辑部门信息" v-model="editDialogVisible" width="600px" :before-close="handleDialogClose">
        <el-form :model="editForm" ref="editFormRef" :rules="formRules" label-width="120px">
          <el-form-item label="部门ID" prop="deptId">
            <el-input v-model="editForm.deptId" disabled />
          </el-form-item>
          <el-form-item label="部门名称" prop="deptName">
            <el-input v-model="editForm.deptName" placeholder="请输入部门名称" />
          </el-form-item>
          <el-form-item label="部门经理ID" prop="managerId">
            <el-input v-model="editForm.managerId" placeholder="请输入部门经理ID" />
          </el-form-item>
          <el-form-item label="部门地址" prop="deptAddress">
            <el-input v-model="editForm.deptAddress" placeholder="请输入部门地址" />
          </el-form-item>
          <el-form-item label="部门描述" prop="deptDesc">
            <el-input v-model="editForm.deptDesc" placeholder="请输入部门描述" type="textarea" :rows="3" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="success" @click="handleSave">保存</el-button>
        </template>
      </el-dialog>

      <!-- 添加部门信息 -->
      <el-dialog title="添加部门信息" v-model="addDialogVisible" width="600px" :before-close="handleAddDialogClose">
        <el-form :model="addForm" ref="addFormRef" :rules="formRules" label-width="120px">
          <el-form-item label="部门名称" prop="deptName">
            <el-input v-model="addForm.deptName" placeholder="请输入部门名称" />
          </el-form-item>
          <el-form-item label="部门经理ID" prop="managerId">
            <el-input v-model="addForm.managerId" placeholder="请输入部门经理ID" />
          </el-form-item>
          <el-form-item label="部门地址" prop="deptAddress">
            <el-input v-model="addForm.deptAddress" placeholder="请输入部门地址" />
          </el-form-item>
          <el-form-item label="部门描述" prop="deptDesc">
            <el-input v-model="addForm.deptDesc" placeholder="请输入部门描述" type="textarea" :rows="3" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="handleAddDialogClose">取消</el-button>
          <el-button type="success" @click="handleAddSave">保存</el-button>
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
const deptList = ref([])
const departmentMap = ref([])

// 编辑对话框状态
const editDialogVisible = ref(false)
const editFormRef = ref(null)

// 编辑表单数据
const editForm = reactive({
  deptId: '',
  deptName: '',
  managerId: '',
  deptAddress: '',
  deptDesc: '',
})

// 添加部门对话框状态
const addDialogVisible = ref(false)
const addFormRef = ref(null)

// 添加部门表单数据 - managerId初始化为空字符串
const addForm = reactive({
  deptName: '',
  managerId: '',
  deptAddress: '',
  deptDesc: '',
})

// 表单验证规则 - 修改managerId验证方式
const formRules = reactive({
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 50, message: '部门名称长度在 2 到 50 个字符', trigger: 'blur' },
    { pattern: /^.+部$/, message: '部门名称必须以"部"结尾，且"部"前至少有一个字符', trigger: 'blur' },
  ],
  managerId: [
    { required: true, message: '请输入部门经理ID', trigger: 'blur' },
    { pattern: /^\d+$/, message: '部门经理ID必须是数字', trigger: 'blur' },
  ],
  deptAddress: [{ max: 200, message: '部门地址不能超过 200 个字符', trigger: 'blur' }],
  deptDesc: [{ max: 500, message: '部门描述不能超过 500 个字符', trigger: 'blur' }],
})

// 判断是否为管理员
const isAdmin = computed(() => accountStore.accountInfo.isAdmin)

// 筛选条件 - 修改为部门ID筛选
const filters = reactive({
  deptId: '',
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
    fetchDeptList()
    fetchDepartmentMap()
  }
})

// 查询部门列表 - 修改筛选条件为部门ID
const fetchDeptList = async () => {
  try {
    loading.value = true

    // 调用API查询所有部门详细信息
    const response = await http.post('/dept/getAllDeptList')

    // 如果 code 正常，则显示数据
    if (response.code === 2001) {
      let filteredData = response.data || []

      // 应用部门ID筛选
      if (filters.deptId) {
        filteredData = filteredData.filter((dept) => dept.deptId === filters.deptId)
      }

      // 处理分页
      pagination.total = filteredData.length
      const startIndex = (pagination.pageNum - 1) * pagination.pageSize
      const endIndex = startIndex + pagination.pageSize
      deptList.value = filteredData.slice(startIndex, endIndex)
    } else {
      ElMessage.error(response.msg || '部门信息查询失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '操作失败，请稍后重试')
    } else {
      console.error('查询部门列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 获取部门信息(简单列表)
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
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '查询失败，请稍后重试')
    } else {
      console.error('获取部门映射信息失败:', error)
      ElMessage.error('查询失败，请稍后重试')
    }
  }
}

// 处理查询
const handleQuery = () => {
  pagination.pageNum = 1 // 重置到第一页
  fetchDeptList()
}

// 处理重置 - 重置部门ID筛选条件
const handleReset = () => {
  // 重置筛选条件
  Object.keys(filters).forEach((key) => {
    filters[key] = ''
  })
  // 重置分页
  pagination.pageNum = 1
  pagination.pageSize = 10
  // 重新查询
  fetchDeptList()
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchDeptList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchDeptList()
}

// 格式化日期
const formatDateForStr = (dateString) => {
  if (!dateString) return ''
  return formatDate(dateString) // 假设使用与员工管理页面相同的日期格式化工具
}

// 打开编辑对话框
const handleEdit = (row) => {
  // 复制行数据到编辑表单
  Object.assign(editForm, {
    deptId: row.deptId,
    deptName: row.deptName,
    managerId: row.managerId?.toString() || '', // 确保为字符串类型
    deptAddress: row.deptAddress || '',
    deptDesc: row.deptDesc || '',
  })
  editDialogVisible.value = true
}

// 关闭编辑对话框
const handleDialogClose = () => {
  editDialogVisible.value = false
  editFormRef.value?.resetFields()
}

// 保存编辑内容
const handleSave = async () => {
  try {
    // 表单验证
    await editFormRef.value.validate()

    // 转换managerId为数字类型提交
    const formData = {
      ...editForm,
      managerId: Number(editForm.managerId),
    }

    // 调用API更新部门信息
    const response = await http.post('/dept/updateDept', formData)

    if (response.code === 2001 && response.data) {
      ElMessage.success(response.msg)
      editDialogVisible.value = false
      fetchDeptList() // 刷新列表
    } else {
      ElMessage.error(response.msg || '更新失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '更新失败，请稍后重试')
    } else {
      console.error('更新部门信息失败:', error)
      ElMessage.error('更新失败，请稍后重试')
    }
  }
}

// 打开添加部门对话框
const handleAdd = () => {
  // 重置表单
  addFormRef.value?.resetFields()
  // 重置表单数据
  Object.keys(addForm).forEach((key) => {
    addForm[key] = ''
  })
  addDialogVisible.value = true
}

// 关闭添加对话框
const handleAddDialogClose = () => {
  addDialogVisible.value = false
  addFormRef.value?.resetFields()
}

// 保存新增部门
const handleAddSave = async () => {
  try {
    // 表单验证
    await addFormRef.value.validate()

    // 转换managerId为数字类型提交
    const formData = {
      ...addForm,
      managerId: Number(addForm.managerId),
    }

    // 调用API添加部门
    const response = await http.post('/dept/addDept', formData)

    if (response.code === 2001 && response.data) {
      ElMessage.success(response.msg)
      addDialogVisible.value = false
      fetchDeptList() // 刷新列表
    } else {
      ElMessage.error(response.msg || '添加失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '添加失败，请稍后重试')
    } else {
      console.error('添加部门失败:', error)
      ElMessage.error('添加失败，请稍后重试')
    }
  }
}
</script>

<style scoped>
.dept-management-page {
  padding: 24px;
  max-width: 1500px;
  margin: 0 auto;
}

.filter-container {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 筛选项样式 */
.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* 筛选标签样式 */
.filter-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  line-height: 1;
  padding-left: 2px;
}

/* 按钮组样式 */
.button-group {
  display: flex;
  gap: 10px;
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
  margin-top: 20px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

/* 让表格内容区域更宽敞 */
.el-table {
  border-radius: 6px;
  overflow: hidden;
}

/* 调整表格列的最小宽度，避免内容挤压 */
.el-table-column {
  min-width: 80px;
}

/* 调整文本域高度 */
.el-textarea__wrapper {
  min-height: 100px !important;
}
</style>
