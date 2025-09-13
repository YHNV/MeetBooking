<template>
  <div class="reservation-management-page">
    <el-card>
      <div class="filter-container">
        <el-row :gutter="20">
          <!-- 预约人ID筛选 -->
          <el-col :span="5">
            <div class="filter-item">
              <label class="filter-label">预约人ID</label>
              <el-input v-model="filters.accountId" placeholder="请输入预约人ID" clearable @keyup.enter="handleQuery" />
            </div>
          </el-col>

          <!-- 预约日期筛选 -->
          <el-col :span="5">
            <div class="filter-item">
              <label class="filter-label">预约日期</label>
              <el-date-picker
                v-model="filters.reservationDate"
                type="date"
                placeholder="选择预约日期"
                clearable
                @change="handleQuery"
              />
            </div>
          </el-col>

          <!-- 预约状态筛选 -->
          <el-col :span="5">
            <div class="filter-item">
              <label class="filter-label">预约状态</label>
              <el-select
                v-model="filters.reservationStatus"
                placeholder="请选择预约状态"
                clearable
                @change="handleQuery"
              >
                <el-option label="待处理" value="PENDING" />
                <el-option label="已批准" value="APPROVED" />
                <el-option label="已拒绝" value="REJECTED" />
                <el-option label="已取消" value="CANCELLED" />
                <el-option label="已过期" value="EXPIRED" />
              </el-select>
            </div>
          </el-col>

          <!-- 操作按钮 -->
          <el-col :span="9">
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

      <!-- 预约表格 -->
      <el-table :data="reservationList" border stripe v-loading="loading" element-loading-text="加载中...">
        <el-table-column prop="reservationId" label="预约ID" width="100" />
        <el-table-column prop="roomId" label="会议室ID" width="100" />
        <el-table-column prop="accountId" label="预约人ID" width="100" />
        <el-table-column prop="empName" label="预约人姓名" width="120" />
        <el-table-column prop="meetingTopic" label="会议主题" />
        <el-table-column prop="reservationDate" label="预约日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="endTime" label="结束时间" width="120" />
        <el-table-column prop="reservationStatus" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.reservationStatus)">
              {{ formatStatus(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          :formatter="(row) => formatDate(row.createTime)"
        />
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button
              size="small"
              type="success"
              @click="handleApprove(scope.row)"
              :disabled="!canModify(scope.row.reservationStatus)"
            >
              同意
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleReject(scope.row)"
              :disabled="!canModify(scope.row.reservationStatus)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 拒绝原因对话框 -->
      <el-dialog title="拒绝预约" v-model="rejectDialogVisible" width="400px">
        <el-form :model="rejectForm" ref="rejectFormRef" :rules="rejectRules" label-width="80px">
          <el-form-item label="拒绝原因" prop="rejectReason">
            <el-input v-model="rejectForm.rejectReason" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReject">确认拒绝</el-button>
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
const reservationList = ref([])

// 筛选条件
const filters = reactive({
  accountId: null,
  reservationDate: null,
  reservationStatus: '',
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  pages: 0,
})

// 拒绝原因表单
const rejectDialogVisible = ref(false)
const rejectFormRef = ref(null)
const rejectForm = reactive({
  reservationId: null,
  rejectReason: '',
})

// 拒绝原因验证规则
const rejectRules = reactive({
  rejectReason: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }],
})

// 判断是否为管理员
const isAdmin = computed(() => accountStore.accountInfo.isAdmin)

// 页面加载时查询数据
onMounted(() => {
  if (!isAdmin.value) {
    // 不是管理员，进行拦截
    ElMessage.warning('您没有权限访问此页面')
    router.replace('/') // 重定向到首页
  } else {
    fetchReservationList()
  }
})

// 格式化日期为YYYY-MM-DD，解决时区问题
const formatDateForBackend = (date) => {
  if (!date) return null
  // 创建一个新的Date对象，避免修改原始日期
  const d = new Date(date)
  // 手动获取年、月、日，确保是本地日期
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 查询预约列表
const fetchReservationList = async () => {
  try {
    loading.value = true

    // 构建请求体数据（合并分页参数和筛选条件）
    const requestData = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      // 仅包含有值的筛选条件
      ...(filters.accountId && { accountId: filters.accountId }),
      ...(filters.reservationDate && { reservationDate: formatDateForBackend(filters.reservationDate) }),
      ...(filters.reservationStatus && { reservationStatus: filters.reservationStatus }),
    }

    // 调用API查询
    const response = await http.post('/res/queryReservations', requestData)

    // 如果 code 正常，则显示数据
    if (response.code === 2001) {
      reservationList.value = response.data.list || []
      pagination.total = response.data.total || 0
      pagination.pages = response.data.pages || 0
    } else {
      ElMessage.error(response.msg || '预约信息查询失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '查询失败，请稍后重试')
    } else {
      console.error('查询预约列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 修改预约状态
const updateReservationStatus = async (reservationId, status, reason = '') => {
  try {
    loading.value = true

    // 获取当前登录管理员ID
    const approvalAccountId = accountStore.accountInfo.accountId

    // 构建请求数据
    const requestData = {
      reservationId,
      reservationStatus: status,
      ...(reason && { rejectReason: reason }),
      approvalAccountId,
    }

    // 调用API更新状态
    const response = await http.post('/res/updateReservation', requestData)

    if (response.code === 2001 && response.data) {
      // ElMessage.success(`${status === 'APPROVAL' ? '同意' : '拒绝'}预约成功`)
      ElMessage.success(response.msg)
      rejectDialogVisible.value = false
      fetchReservationList() // 刷新列表
    } else {
      ElMessage.error(response.msg || `操作失败`)
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '操作失败，请稍后重试')
    } else {
      console.error('更新预约状态失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 处理查询
const handleQuery = () => {
  pagination.pageNum = 1 // 重置到第一页
  fetchReservationList()
}

// 处理重置
const handleReset = () => {
  // 重置筛选条件
  filters.accountId = null
  filters.reservationDate = null
  filters.reservationStatus = ''

  // 重置分页
  pagination.pageNum = 1
  pagination.pageSize = 10

  // 重新查询
  fetchReservationList()
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchReservationList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchReservationList()
}

// 格式化状态显示
const formatStatus = (row) => {
  const statusMap = {
    PENDING: '待处理',
    APPROVED: '已批准',
    REJECTED: '已拒绝',
    CANCELLED: '已取消',
    EXPIRED: '已过期',
  }
  return statusMap[row.reservationStatus] || row.reservationStatus
}

// 获取状态标签的类型
const getStatusTagType = (status) => {
  const typeMap = {
    PENDING: 'info', // 待处理 - 蓝色
    APPROVED: 'success', // 已批准 - 绿色
    REJECTED: 'danger', // 已拒绝 - 红色
    CANCELLED: 'warning', // 已取消 - 橙色
    EXPIRED: 'gray', // 已过期 - 灰色
  }
  return typeMap[status] || 'default'
}

// 判断是否可以修改状态
const canModify = (status) => {
  // 根据业务需求调整可修改的状态
  return ['PENDING'].includes(status)
}

// 处理同意操作
const handleApprove = (row) => {
  ElMessageBox.confirm(`确定要同意预约【${row.reservationId}】吗？`, '确认操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success',
  })
    .then(() => {
      updateReservationStatus(row.reservationId, 'APPROVED')
    })
    .catch(() => {
      ElMessage.info('已取消操作')
    })
}

// 处理拒绝操作
const handleReject = (row) => {
  // 重置表单
  rejectForm.reservationId = row.reservationId
  rejectForm.rejectReason = ''
  rejectFormRef.value?.resetFields()

  // 显示拒绝原因对话框
  rejectDialogVisible.value = true
}

// 确认拒绝
const confirmReject = async () => {
  try {
    // 表单验证
    await rejectFormRef.value.validate()

    // 调用更新状态方法
    updateReservationStatus(rejectForm.reservationId, 'REJECTED', rejectForm.rejectReason)
  } catch (error) {
    // 表单验证失败不做处理
    if (error.name !== 'Error') return

    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '操作失败，请稍后重试')
    } else {
      console.error('确认拒绝失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}
</script>

<style scoped>
.reservation-management-page {
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
  width: auto;
  text-align: left;
  margin-right: 0;
}

.el-input__wrapper,
.el-select__wrapper,
.el-date-picker__wrapper {
  height: 36px !important;
}

.el-button {
  height: 36px;
  padding: 0 16px;
}

.pagination-container {
  margin-top: 20px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  text-align: right;
}

.el-table {
  border-radius: 6px;
  overflow: hidden;
}

.el-table-column {
  min-width: 80px;
}
</style>
