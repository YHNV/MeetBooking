<template>
  <div class="announcement-management-page">
    <el-card>
      <div class="filter-container">
        <el-row :gutter="20">
          <!-- 公告标题筛选 -->
          <el-col :span="8">
            <div class="filter-item">
              <label class="filter-label">公告标题</label>
              <el-input v-model="filters.title" placeholder="请输入公告标题" clearable @keyup.enter="handleQuery" />
            </div>
          </el-col>

          <!-- 状态筛选 -->
          <el-col :span="4">
            <div class="filter-item">
              <label class="filter-label">公告状态</label>
              <el-select v-model="filters.isActive" placeholder="请选择公告状态" clearable @change="handleQuery">
                <el-option label="启用" value="true" />
                <el-option label="禁用" value="false" />
              </el-select>
            </div>
          </el-col>

          <!-- 发布人筛选 -->
          <el-col :span="4">
            <div class="filter-item">
              <label class="filter-label">发布人ID</label>
              <el-input v-model="filters.senderId" placeholder="请输入发布人ID" clearable @keyup.enter="handleQuery" />
            </div>
          </el-col>

          <!-- 操作按钮 -->
          <el-col :span="8">
            <div class="filter-item">
              <label class="filter-label">&nbsp;</label>
              <div>
                <el-button type="primary" @click="handleQuery"> 查询 </el-button>
                <el-button @click="handleReset"> 重置 </el-button>
                <el-button type="success" @click="handleAdd"> 发布公告 </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 公告表格 -->
      <el-table :data="filteredAnnouncementList" border stripe v-loading="loading" element-loading-text="加载中...">
        <el-table-column prop="announcementId" label="公告ID" width="100" />
        <el-table-column prop="title" label="标题" width="100" />
        <!-- 内容列 -->
        <el-table-column prop="content" label="内容" min-width="200">
          <template #default="scope">
            <div class="content-cell">
              {{ scope.row.content.length > 20 ? scope.row.content.slice(0, 40) + '...' : scope.row.content }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="senderId" label="发布人ID" width="120" />
        <el-table-column
          prop="createTime"
          label="发布时间"
          width="180"
          :formatter="(row) => formatDate(row.createTime)"
        />
        <el-table-column
          prop="updateTime"
          label="更新时间"
          width="180"
          :formatter="(row) => formatDate(row.updateTime)"
        />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'" size="small">
              {{ scope.row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button
              size="small"
              :type="scope.row.isActive ? 'warning' : 'success'"
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.isActive ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)"> 编辑 </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 编辑公告对话框 -->
      <el-dialog title="编辑公告" v-model="editDialogVisible" width="600px" :before-close="handleDialogClose">
        <el-form :model="editForm" ref="editFormRef" :rules="formRules" label-width="100px">
          <el-form-item label="公告ID" prop="announcementId">
            <el-input v-model="editForm.announcementId" disabled />
          </el-form-item>
          <el-form-item label="发布人ID" prop="senderId">
            <el-input v-model="editForm.senderId" disabled />
          </el-form-item>
          <el-form-item label="公告标题" prop="title">
            <el-input v-model="editForm.title" />
          </el-form-item>
          <el-form-item label="公告内容" prop="content">
            <el-input v-model="editForm.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
          </el-form-item>
          <el-form-item label="状态" prop="isActive">
            <el-switch v-model="editForm.isActive" active-text="启用" inactive-text="禁用" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="success" @click="handleSave">保存</el-button>
        </template>
      </el-dialog>

      <!-- 添加公告对话框 -->
      <el-dialog title="发布新公告" v-model="addDialogVisible" width="600px" :before-close="handleAddDialogClose">
        <el-form :model="addForm" ref="addFormRef" :rules="formRules" label-width="100px">
          <el-form-item label="发布人ID" prop="senderId">
            <el-input v-model="addForm.senderId" placeholder="请输入发布人ID" disabled />
          </el-form-item>
          <el-form-item label="公告标题" prop="title">
            <el-input v-model="addForm.title" placeholder="请输入公告标题" />
          </el-form-item>
          <el-form-item label="公告内容" prop="content">
            <el-input v-model="addForm.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="handleAddDialogClose">取消</el-button>
          <el-button type="success" @click="handleAddSave">发布</el-button>
        </template>
      </el-dialog>

      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="totalFiltered"
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
import { ElMessage, ElMessageBox } from 'element-plus'

const http = useApi()
const router = useRouter()
const accountStore = useAccountStore()

// 状态管理
const loading = ref(false)
const announcementList = ref([]) // 存储所有公告数据

// 编辑对话框状态
const editDialogVisible = ref(false)
const editFormRef = ref(null)

// 编辑表单数据
const editForm = reactive({
  announcementId: '',
  senderId: '',
  title: '',
  content: '',
  isActive: true,
})

// 添加对话框状态
const addDialogVisible = ref(false)
const addFormRef = ref(null)

// 添加表单数据
const addForm = reactive({
  senderId: '',
  title: '',
  content: '',
})

// 表单验证规则
const formRules = reactive({
  senderId: [{ required: true, message: '请输入发布人ID', trigger: 'blur' }],
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
})

// 筛选条件
const filters = reactive({
  title: '',
  isActive: null,
  senderId: '',
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  pages: 0,
})

// 检查是否有权限访问
const isAdmin = computed(() => accountStore.accountInfo.isAdmin)
const adminId = computed(() => accountStore.accountInfo.accountId)

// 过滤后的公告列表（前端筛选）
const filteredAnnouncementList = computed(() => {
  // 应用筛选条件
  let filtered = announcementList.value.filter((item) => {
    // 标题筛选
    if (filters.title && !item.title.includes(filters.title)) {
      return false
    }
    // 状态筛选
    if (filters.isActive !== null && item.isActive !== (filters.isActive === 'true')) {
      return false
    }
    // 发布人筛选
    if (filters.senderId && !String(item.senderId).includes(filters.senderId)) {
      return false
    }
    return true
  })

  // 应用分页
  const startIndex = (pagination.pageNum - 1) * pagination.pageSize
  const endIndex = startIndex + pagination.pageSize
  return filtered.slice(startIndex, endIndex)
})

// 过滤后的总条数
const totalFiltered = computed(() => {
  return announcementList.value.filter((item) => {
    if (filters.title && !item.title.includes(filters.title)) {
      return false
    }
    if (filters.isActive !== null && item.isActive !== (filters.isActive === 'true')) {
      return false
    }
    if (filters.senderId && item.senderId !== filters.senderId) return false
    return true
  }).length
})

// 页面加载时查询数据
onMounted(() => {
  if (!isAdmin.value) {
    ElMessage.warning('您没有权限访问此页面')
    router.replace('/')
  } else {
    fetchAnnouncementList()
  }
})

// 查询公告列表（仅带分页参数）
const fetchAnnouncementList = async () => {
  try {
    loading.value = true

    // 构建请求体数据（仅包含分页参数）
    const requestData = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    }

    // 调用API查询
    const response = await http.post('/ann/getAnnouncement', requestData)

    if (response.code === 2001) {
      announcementList.value = response.data.list || []
      pagination.total = response.data.total || 0
      pagination.pages = response.data.pages || 0
    } else {
      ElMessage.error(response.msg || '公告查询失败')
    }
  } catch (error) {
    console.error('查询公告列表失败:', error)
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理查询（前端筛选）
const handleQuery = () => {
  pagination.pageNum = 1 // 重置到第一页
  // 不需要重新请求后端，计算属性会自动处理筛选
  fetchAnnouncementList()
}

// 处理重置
const handleReset = () => {
  Object.keys(filters).forEach((key) => {
    filters[key] = null
  })
  pagination.pageNum = 1
  pagination.pageSize = 10
  // 不需要重新请求后端，计算属性会自动处理筛选
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  // 不需要重新请求后端，计算属性会自动处理分页
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  // 不需要重新请求后端，计算属性会自动处理分页
}

// 切换公告状态
const handleToggleStatus = async (row) => {
  const actionText = row.isActive ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}公告【${row.title}】吗？`, '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    console.log(row)

    // 调用修改接口更新状态
    const response = await http.post('/ann/updateAnnouncement', {
      ...row,
      isActive: !row.isActive,
    })

    if (response.code === 2001 && response.data) {
      ElMessage.success(response.msg)
      fetchAnnouncementList() // 重新获取列表
    } else {
      ElMessage.error(response.msg || `${actionText}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`切换公告状态失败:`, error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 打开编辑对话框
const handleEdit = (row) => {
  Object.assign(editForm, {
    announcementId: row.announcementId,
    senderId: row.senderId,
    title: row.title,
    content: row.content,
    isActive: row.isActive,
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
    await editFormRef.value.validate()

    const response = await http.post('/ann/updateAnnouncement', editForm)

    if (response.code === 2001 && response.data) {
      ElMessage.success(response.msg)
      editDialogVisible.value = false
      fetchAnnouncementList() // 重新获取列表
    } else {
      ElMessage.error(response.msg || '更新失败')
    }
  } catch (error) {
    if (error.name === 'Error') {
      console.error('更新公告失败:', error)
      ElMessage.error('更新失败，请稍后重试')
    }
  }
}

// 打开添加对话框
const handleAdd = () => {
  addFormRef.value?.resetFields()
  Object.keys(addForm).forEach((key) => {
    addForm[key] = ''
  })
  addForm.senderId = adminId.value
  addDialogVisible.value = true
}

// 关闭添加对话框
const handleAddDialogClose = () => {
  addDialogVisible.value = false
  addFormRef.value?.resetFields()
}

// 保存新增公告
const handleAddSave = async () => {
  try {
    await addFormRef.value.validate()

    const response = await http.post('/ann/addAnnouncement', addForm)

    if (response.code === 2001 && response.data) {
      ElMessage.success(response.msg)
      addDialogVisible.value = false
      fetchAnnouncementList() // 重新获取列表
    } else {
      ElMessage.error(response.msg || '发布失败')
    }
  } catch (error) {
    if (error.name === 'Error') {
      console.error('发布公告失败:', error)
      ElMessage.error('发布失败，请稍后重试')
    }
  }
}

// 删除公告
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除公告【${row.title}】吗？此操作不可恢复！`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error',
    })

    const response = await http.post('/ann/deleteAnnouncement', row.announcementId)

    if (response.code === 2001 && response.data) {
      ElMessage.success(response.msg)
      fetchAnnouncementList() // 重新获取列表
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除公告失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}
</script>

<style scoped>
.announcement-management-page {
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

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  line-height: 1;
  padding-left: 2px;
}

.el-input__wrapper,
.el-select__wrapper {
  height: 36px !important;
}

.el-button {
  height: 36px;
  padding: 0 16px;
  margin-right: 8px;
}

.pagination-container {
  margin-top: 20px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.el-table {
  border-radius: 6px;
  overflow: hidden;
}

.el-table-column {
  min-width: 80px;
}

/* 内容列样式 */
.content-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 调整textarea高度 */
.el-textarea__wrapper {
  min-height: 120px !important;
}
</style>
