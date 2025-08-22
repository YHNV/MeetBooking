<template>
  <div class="employee-query-page">
    <el-card>
      <div class="filter-container">
        <el-row :gutter="20">
          <!-- 员工姓名筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">员工姓名</label>
              <el-input v-model="filters.empName" placeholder="请输入员工姓名" clearable @keyup.enter="handleQuery" />
            </div>
          </el-col>

          <!-- 职位筛选 -->
          <!-- <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">职位</label>
              <el-input v-model="filters.position" placeholder="请输入职位" clearable @keyup.enter="handleQuery" />
            </div>
          </el-col> -->

          <!-- 部门筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">部门名称</label>
              <el-input v-model="filters.deptId" placeholder="请输入部门名称" clearable @keyup.enter="handleQuery" />
            </div>
          </el-col>

          <!-- 手机号筛选 -->
          <!-- <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">手机号</label>
              <el-input v-model="filters.phone" placeholder="请输入手机号" clearable @keyup.enter="handleQuery" />
            </div>
          </el-col> -->
        </el-row>

        <el-row :gutter="20" class="mt-3">
          <!-- 是否在职 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">是否在职</label>
              <el-select v-model="filters.isActive" placeholder="请选择在职状态" clearable>
                <el-option label="在职" value="true" />
                <el-option label="离职" value="false" />
              </el-select>
            </div>
          </el-col>

          <!-- 是否为管理者 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">是否为管理者</label>
              <el-select v-model="filters.isManager" placeholder="请选择是否管理" clearable>
                <el-option label="是" value="true" />
                <el-option label="否" value="false" />
              </el-select>
            </div>
          </el-col>

          <!-- 操作按钮 -->
          <el-col :span="12" class="filter-item">
            <label class="filter-label">&nbsp;</label>
            <div>
              <el-button type="primary" @click="handleQuery"> 查询 </el-button>
              <el-button @click="handleReset"> 重置 </el-button>
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
        <el-table-column prop="createTime" label="入职时间" width="180" :formatter="formatDate" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleView(scope.row)"> 查看 </el-button>
            <el-button size="small" type="success" @click="handleEdit(scope.row)"> 编辑 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页控件 -->
      <div class="pagination-container mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
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
import { ref, reactive, onMounted } from 'vue'
import { useApi } from '@/composables/useApi.js'

const http = useApi()

// 状态管理
const loading = ref(false)
const employeeList = ref([])

// 筛选条件
const filters = reactive({
  empName: '',
  //   position: '',
  //   deptName: '',
  //   phone: '',
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
  fetchEmployeeList()
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
      //   ...(filters.position && { position: filters.position }),
      //   ...(filters.deptName && { deptName: filters.deptName }),
      //   ...(filters.phone && { phone: filters.phone }),
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
      ElMessage.error(response.msg || '查询失败')
    }
  } catch (error) {
    console.error('查询员工列表失败:', error)
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
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
    filters[key] = key === 'deptId' ? 0 : null
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

// 格式化日期
const formatDate = (row) => {
  if (!row.createTime) return ''
  const date = new Date(row.createTime)
  return date.toLocaleString()
}

// 查看员工详情
const handleView = (row) => {
  // 可以跳转到详情页或弹出详情对话框
  console.log('查看员工:', row)
  ElMessage.info(`查看员工 ${row.empName} 的详情`)
}

// 编辑员工信息
const handleEdit = (row) => {
  // 可以跳转到编辑页或弹出编辑对话框
  console.log('编辑员工:', row)
  ElMessage.info(`编辑员工 ${row.empName} 的信息`)
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
