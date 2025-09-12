<template>
  <div class="equipment-management-page">
    <el-card>
      <div class="filter-container">
        <el-row :gutter="20">
          <!-- 设备名称筛选 -->
          <el-col :span="12">
            <div class="filter-item">
              <label class="filter-label">设备名称</label>
              <el-input
                v-model="filters.equipmentName"
                placeholder="请输入设备名称"
                clearable
                @keyup.enter="handleQuery"
              />
            </div>
          </el-col>

          <!-- 操作按钮 -->
          <el-col :span="12">
            <div class="filter-item">
              <label class="filter-label">&nbsp;</label>
              <div>
                <el-button type="primary" @click="handleQuery"> 查询 </el-button>
                <el-button @click="handleReset"> 重置 </el-button>
                <el-button type="success" @click="handleAdd"> 新增设备 </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 设备表格 -->
      <el-table :data="equipmentList" border stripe v-loading="loading" element-loading-text="加载中...">
        <el-table-column prop="equipmentId" label="设备ID" width="100" />
        <el-table-column prop="equipmentName" label="设备名称" width="180" />
        <el-table-column prop="description" label="设备描述" />
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
          :formatter="(row) => formatDate(row.updateTime)"
        />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleEdit(scope.row)"> 编辑 </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 设备表单对话框（新增/编辑） -->
      <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
        <el-form :model="equipmentForm" ref="equipmentFormRef" :rules="equipmentRules" label-width="100px">
          <el-form-item label="设备名称" prop="equipmentName">
            <el-input v-model="equipmentForm.equipmentName" placeholder="请输入设备名称" />
          </el-form-item>
          <el-form-item label="设备描述" prop="description">
            <el-input v-model="equipmentForm.description" type="textarea" :rows="4" placeholder="请输入设备描述" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSave">确认</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'

const http = useApi()
const router = useRouter()
const accountStore = useAccountStore()

// 状态管理
const loading = ref(false)
const equipmentList = ref([])

// 筛选条件
const filters = reactive({
  equipmentName: '',
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  pages: 0,
})

// 设备表单对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增设备')
const equipmentFormRef = ref(null)
const equipmentForm = reactive({
  equipmentId: null,
  equipmentName: '',
  description: '',
})

// 表单验证规则
const equipmentRules = reactive({
  equipmentName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  description: [{ max: 200, message: '设备描述不能超过200个字符', trigger: 'blur' }],
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
    fetchEquipmentList()
  }
})

const fetchEquipmentList = async () => {
  try {
    loading.value = true

    // 调用API查询，不传递任何参数
    const response = await http.post('/equip/getSimpleEquipmentList')

    // 如果 code 正常，则显示数据
    if (response.code === 2001) {
      let allEquipment = response.data || []

      // 前端根据设备名称筛选
      if (filters.equipmentName) {
        allEquipment = allEquipment.filter((item) => item.equipmentName.includes(filters.equipmentName))
      }

      // 处理分页逻辑
      const startIndex = (pagination.pageNum - 1) * pagination.pageSize
      const endIndex = startIndex + pagination.pageSize
      equipmentList.value = allEquipment.slice(startIndex, endIndex)
      pagination.total = allEquipment.length
    } else {
      ElMessage.error(response.msg || '设备信息查询失败')
    }
  } catch (error) {
    console.error('查询设备列表失败:', error)
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 新增设备
const addEquipment = async () => {
  try {
    loading.value = true

    // 构建请求数据
    const requestData = {
      equipmentName: equipmentForm.equipmentName,
      description: equipmentForm.description,
    }

    // 调用API新增
    const response = await http.post('/equip/addEquipment', requestData)

    if (response.code === 2001 && response.data) {
      ElMessage.success(response.msg || '新增设备成功')
      dialogVisible.value = false
      fetchEquipmentList() // 刷新列表
    } else {
      ElMessage.error(response.msg || '新增设备失败')
    }
  } catch (error) {
    console.error('新增设备失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 更新设备
const updateEquipment = async () => {
  try {
    loading.value = true

    // 构建请求数据
    const requestData = {
      equipmentId: equipmentForm.equipmentId,
      equipmentName: equipmentForm.equipmentName,
      description: equipmentForm.description,
    }

    // 调用API更新
    const response = await http.post('/equip/updateEquipment', requestData)

    if (response.code === 2001 && response.data) {
      ElMessage.success(response.msg || '更新设备成功')
      dialogVisible.value = false
      fetchEquipmentList() // 刷新列表
    } else {
      ElMessage.error(response.msg || '更新设备失败')
    }
  } catch (error) {
    console.error('更新设备失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 删除设备
const deleteEquipment = async (equipmentId) => {
  try {
    loading.value = true

    // 调用API删除
    const response = await http.post('/equip/deleteEquipment', equipmentId)

    if (response.code === 2001 && response.data) {
      ElMessage.success(response.msg || '删除设备成功')
      fetchEquipmentList() // 刷新列表
    } else {
      ElMessage.error(response.msg || '删除设备失败')
    }
  } catch (error) {
    console.error('删除设备失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理查询
const handleQuery = () => {
  pagination.pageNum = 1 // 重置到第一页
  fetchEquipmentList()
}

// 处理重置
const handleReset = () => {
  // 重置筛选条件
  filters.equipmentName = ''

  // 重置分页
  pagination.pageNum = 1
  pagination.pageSize = 10

  // 重新查询
  fetchEquipmentList()
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchEquipmentList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchEquipmentList()
}

// 处理新增设备
const handleAdd = () => {
  // 重置表单
  equipmentForm.equipmentId = null
  equipmentForm.equipmentName = ''
  equipmentForm.description = ''
  equipmentFormRef.value?.resetFields()

  // 设置对话框标题并显示
  dialogTitle.value = '新增设备'
  dialogVisible.value = true
}

// 处理编辑设备
const handleEdit = (row) => {
  // 填充表单数据
  equipmentForm.equipmentId = row.equipmentId
  equipmentForm.equipmentName = row.equipmentName
  equipmentForm.description = row.description

  // 设置对话框标题并显示
  dialogTitle.value = '编辑设备'
  dialogVisible.value = true
}

// 处理删除设备
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除设备【${row.equipmentName}】吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      deleteEquipment(row.equipmentId)
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

// 确认保存（新增或更新）
const confirmSave = async () => {
  try {
    // 表单验证
    await equipmentFormRef.value.validate()

    // 根据是否有equipmentId判断是新增还是更新
    if (equipmentForm.equipmentId) {
      await updateEquipment()
    } else {
      await addEquipment()
    }
  } catch (error) {
    // 表单验证失败不做处理
    if (error.name !== 'Error') return

    console.error('保存设备失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}
</script>

<style scoped>
.equipment-management-page {
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
  margin-right: 8px;
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
