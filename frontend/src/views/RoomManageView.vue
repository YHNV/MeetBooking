<template>
  <div class="meeting-room-page">
    <el-card>
      <div class="filter-container">
        <el-row :gutter="20">
          <!-- 会议室名称筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">会议室名称</label>
              <el-input
                v-model="filters.roomName"
                placeholder="请输入会议室名称"
                clearable
                @keyup.enter="handleQuery"
              />
            </div>
          </el-col>

          <!-- 会议室类型筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">会议室类型</label>
              <el-select v-model="filters.roomType" placeholder="请选择类型" clearable>
                <el-option label="小型" value="SMALL" />
                <el-option label="大型" value="LARGE" />
              </el-select>
            </div>
          </el-col>

          <!-- 容量范围筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">容量范围</label>
              <el-row :gutter="5">
                <el-col :span="11">
                  <el-input v-model="filters.minCapacity" type="number" placeholder="最小" />
                </el-col>
                <el-col :span="2" class="flex items-center justify-center">-</el-col>
                <el-col :span="11">
                  <el-input v-model="filters.maxCapacity" type="number" placeholder="最大" />
                </el-col>
              </el-row>
            </div>
          </el-col>

          <!-- 会议室状态筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">状态</label>
              <el-select v-model="filters.roomStatus" placeholder="请选择状态" clearable>
                <el-option label="可用" value="AVAILABLE" />
                <el-option label="禁用" value="DISABLED" />
                <el-option label="维护中" value="MAINTENANCE" />
              </el-select>
            </div>
          </el-col>
        </el-row>

        <!-- 设备筛选 -->
        <el-row :gutter="20" class="mt-3">
          <el-col :span="18">
            <div class="filter-item">
              <label class="filter-label">设备筛选</label>
              <el-select
                v-model="filters.equipmentIdList"
                placeholder="请选择设备"
                multiple
                collapse-tags
                collapse-tags-tooltip
                :max-collapse-tags="7"
              >
                <!-- <el-select v-model="filters.equipmentIdList" placeholder="请选择设备" multiple> -->
                <el-option
                  v-for="eq in equipmentList"
                  :key="eq.equipmentId"
                  :label="eq.equipmentName"
                  :value="eq.equipmentId"
                />
              </el-select>
            </div>
          </el-col>

          <el-col :span="3">
            <label class="filter-label">&nbsp;</label>
            <el-button type="success" @click="handleAdd"> 新增会议室 </el-button>
          </el-col>

          <!-- 操作按钮 -->
          <el-col :span="3">
            <div class="filter-item">
              <label class="filter-label">&nbsp;</label>
              <div class="button-group">
                <el-button type="primary" @click="handleQuery"> 查询 </el-button>
                <el-button @click="handleReset"> 重置 </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 会议室表格 -->
      <el-table :data="meetingRoomList" border stripe v-loading="loading" element-loading-text="加载中...">
        <el-table-column prop="roomId" label="会议室ID" width="120" />
        <el-table-column prop="roomName" label="名称" width="160" />
        <el-table-column prop="roomType" label="类型" width="100" :formatter="formatRoomType" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="roomStatus" label="状态" width="100" :formatter="formatRoomStatus" />
        <el-table-column label="图片" width="120">
          <template #default="scope">
            <el-image
              v-if="scope.row.imageUrl"
              :src="scope.row.imageUrl"
              :preview-src-list="[scope.row.imageUrl]"
              style="width: 60px; height: 40px; object-fit: cover"
              fit="cover"
              :preview-teleported="true"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="200"
          :formatter="(row) => formatDate(row.createTime)"
        />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="handleViewEquipment(scope.row)"> 查看设备 </el-button>
            <!-- <el-button size="small" type="primary" @click="handleUploadImage(scope.row)"> 上传图片 </el-button> -->
            <el-button size="small" type="success" @click="handleEdit(scope.row)"> 编辑 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 编辑会议室对话框 -->
      <el-dialog title="编辑会议室" v-model="editDialogVisible" width="600px" :before-close="handleDialogClose">
        <el-form :model="editForm" ref="editFormRef" :rules="editRules" label-width="120px">
          <el-form-item label="会议室ID" prop="roomId" v-if="editForm.roomId">
            <el-input v-model="editForm.roomId" disabled />
          </el-form-item>
          <el-form-item label="会议室名称" prop="roomName">
            <el-input v-model="editForm.roomName" max-length="31" />
          </el-form-item>
          <el-form-item label="会议室类型" prop="roomType">
            <el-select v-model="editForm.roomType" placeholder="请选择类型">
              <el-option label="小型" value="SMALL" />
              <el-option label="大型" value="LARGE" />
            </el-select>
          </el-form-item>
          <el-form-item label="容纳人数" prop="capacity">
            <el-input v-model.number="editForm.capacity" type="number" min="1" />
          </el-form-item>
          <el-form-item label="位置" prop="location">
            <el-input v-model="editForm.location" max-length="255" />
          </el-form-item>
          <el-form-item label="状态" prop="roomStatus">
            <el-select v-model="editForm.roomStatus" placeholder="请选择状态">
              <el-option label="可用" value="AVAILABLE" />
              <el-option label="禁用" value="DISABLED" />
              <el-option label="维护中" value="MAINTENANCE" />
            </el-select>
          </el-form-item>
          <el-form-item label="设备" prop="equipmentIds">
            <el-select
              v-model="editForm.equipmentIds"
              placeholder="请选择设备"
              multiple
              collapse-tags
              collapse-tags-tooltip
              :max-collapse-tags="3"
            >
              <!-- <el-select v-model="editForm.equipmentIds" placeholder="请选择设备" multiple> -->
              <el-option
                v-for="eq in equipmentList"
                :key="eq.equipmentId"
                :label="eq.equipmentName"
                :value="eq.equipmentId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="上传图片">
            <el-upload
              class="upload-demo"
              drag
              action=""
              :http-request="handleAddImageUpload"
              :on-success="handleAddUploadSuccess"
              :before-upload="beforeUpload"
              list-type="picture"
              :limit="1"
              :on-exceed="handleExceed"
              :file-list="fileList"
            >
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="64" height="64">
                <path
                  fill="currentColor"
                  d="M544 864V672h128L512 480 352 672h128v192H320v-1.6c-5.376.32-10.496 1.6-16 1.6A240 240 0 0 1 64 624c0-123.136 93.12-223.488 212.608-237.248A239.808 239.808 0 0 1 512 192a239.872 239.872 0 0 1 235.456 194.752c119.488 13.76 212.48 114.112 212.48 237.248a240 240 0 0 1-240 240c-5.376 0-10.56-1.28-16-1.6v1.6z"
                ></path>
              </svg>
              <div class="el-upload__text">点击或拖拽文件到此处上传</div>
              <template #tip>
                <div class="el-upload__tip text-muted">支持jpg、jpeg、png格式，文件大小不超过5MB</div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item label="当前图片">
            <el-image
              v-if="editForm.imageUrl"
              :src="editForm.imageUrl"
              :preview-src-list="[editForm.imageUrl]"
              style="width: 200px; height: 150px; object-fit: cover"
              fit="cover"
            />
            <span v-else>无图片</span>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="success" @click="handleSave">保存</el-button>
        </template>
      </el-dialog>

      <!-- 设备列表对话框 -->
      <el-dialog title="会议室设备" v-model="equipmentDialogVisible" width="500px">
        <el-table :data="currentRoomEquipment" border>
          <el-table-column prop="equipmentId" label="设备ID" width="100" />
          <el-table-column prop="equipmentName" label="设备名称" />
          <el-table-column prop="description" label="描述" />
        </el-table>
        <template #footer>
          <el-button @click="equipmentDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <!-- 上传图片对话框 -->
      <el-dialog title="上传会议室图片" v-model="uploadImageDialogVisible" width="500px">
        <el-upload
          class="upload-demo"
          drag
          action=""
          :http-request="handleImageUpload"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
          list-type="picture"
          limit="1"
          :on-exceed="handleExceed"
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="64" height="64">
            <path
              fill="currentColor"
              d="M544 864V672h128L512 480 352 672h128v192H320v-1.6c-5.376.32-10.496 1.6-16 1.6A240 240 0 0 1 64 624c0-123.136 93.12-223.488 212.608-237.248A239.808 239.808 0 0 1 512 192a239.872 239.872 0 0 1 235.456 194.752c119.488 13.76 212.48 114.112 212.48 237.248a240 240 0 0 1-240 240c-5.376 0-10.56-1.28-16-1.6v1.6z"
            ></path>
          </svg>
          <div class="el-upload__text">点击或拖拽文件到此处上传</div>
          <template #tip>
            <div class="el-upload__tip text-muted">支持jpg、jpeg、png格式，文件大小不超过5MB</div>
          </template>
        </el-upload>
        <template #footer>
          <el-button @click="uploadImageDialogVisible = false">取消</el-button>
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
const meetingRoomList = ref([])
const equipmentList = ref([])
const currentRoomEquipment = ref([])
const fileList = ref([])

// 对话框状态
const editDialogVisible = ref(false)
const equipmentDialogVisible = ref(false)
const uploadImageDialogVisible = ref(false)
const editFormRef = ref(null)
const currentRoomId = ref(null)

// 编辑表单数据
const editForm = reactive({
  roomId: '',
  roomName: '',
  roomType: '',
  capacity: 1,
  location: '',
  roomStatus: '',
  imageUrl: '',
  equipmentIds: [],
})

// 编辑表单验证规则
const editRules = reactive({
  roomName: [{ required: true, message: '请输入会议室名称', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择会议室类型', trigger: 'change' }],
  capacity: [
    { required: true, message: '请输入容纳人数', trigger: 'blur' },
    { type: 'number', min: 1, message: '容纳人数不能小于1', trigger: 'blur' },
  ],
  location: [{ required: true, message: '请输入位置信息', trigger: 'blur' }],
  roomStatus: [{ required: true, message: '请选择状态', trigger: 'change' }],
})

// 筛选条件
const filters = reactive({
  roomName: '',
  roomType: '',
  minCapacity: '',
  maxCapacity: '',
  location: '',
  roomStatus: '',
  equipmentIdList: [],
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  pages: 0,
})

// 判断是否为管理员
const isAdmin = computed(() => accountStore.accountInfo?.isAdmin || false)

// 页面加载时查询数据
onMounted(() => {
  if (!isAdmin.value) {
    ElMessage.warning('您没有权限访问此页面')
    router.replace('/')
  } else {
    fetchMeetingRoomList()
    fetchEquipmentList()
  }
})

// 查询会议室列表
const fetchMeetingRoomList = async () => {
  try {
    loading.value = true

    // 构建请求体数据
    const requestData = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...(filters.roomName && { roomName: filters.roomName }),
      ...(filters.roomType && { roomType: filters.roomType }),
      ...(filters.minCapacity && { minCapacity: Number(filters.minCapacity) }),
      ...(filters.maxCapacity && { maxCapacity: Number(filters.maxCapacity) }),
      ...(filters.location && { location: filters.location }),
      ...(filters.roomStatus && { roomStatus: filters.roomStatus }),
      ...(filters.equipmentIdList.length > 0 && { equipmentIdList: filters.equipmentIdList }),
    }

    const response = await http.post('/meetingRoom/queryMeetingRooms', requestData)

    if (response.code === 2001) {
      meetingRoomList.value = response.data.list || []
      pagination.total = response.data.total || 0
      pagination.pages = response.data.pages || 0
    } else {
      ElMessage.error(response.msg || '会议室信息查询失败')
    }
  } catch (error) {
    console.error('查询会议室列表失败:', error)
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取设备列表
const fetchEquipmentList = async () => {
  try {
    const response = await http.post('/equip/getSimpleEquipmentList')
    if (response.code === 2001) {
      equipmentList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '设备列表查询失败')
    }
  } catch (error) {
    console.error('获取设备列表失败:', error)
    ElMessage.error('查询失败，请稍后重试')
  }
}

// 获取指定会议室的设备
const fetchRoomEquipment = async (roomId) => {
  try {
    const response = await http.post('/meetingRoom/getRoomEquipmentList', roomId)
    if (response.code === 2001) {
      currentRoomEquipment.value = response.data || []
      return true
    } else {
      ElMessage.error(response.msg || '获取会议室设备失败')
      return false
    }
  } catch (error) {
    console.error('获取会议室设备失败:', error)
    ElMessage.error('操作失败，请稍后重试')
    return false
  }
}

// 处理查询
const handleQuery = () => {
  pagination.pageNum = 1
  fetchMeetingRoomList()
}

// 处理重置
const handleReset = () => {
  Object.keys(filters).forEach((key) => {
    if (key === 'equipmentIdList') {
      filters[key] = []
    } else {
      filters[key] = ''
    }
  })
  pagination.pageNum = 1
  pagination.pageSize = 10
  fetchMeetingRoomList()
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchMeetingRoomList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchMeetingRoomList()
}

// 格式化会议室类型
const formatRoomType = (row) => {
  return row.roomType === 'SMALL' ? '小型' : '大型'
}

// 格式化会议室状态
const formatRoomStatus = (row) => {
  switch (row.roomStatus) {
    case 'AVAILABLE':
      return '可用'
    case 'DISABLED':
      return '禁用'
    case 'MAINTENANCE':
      return '维护中'
    default:
      return row.roomStatus
  }
}

// 查看设备
const handleViewEquipment = async (row) => {
  const success = await fetchRoomEquipment(row.roomId)
  if (success) {
    equipmentDialogVisible.value = true
  }
}

// 上传图片
const handleUploadImage = (row) => {
  currentRoomId.value = row.roomId
  // fileList.value = []
  uploadImageDialogVisible.value = true
}

const handleExceed = (files) => {
  ElMessage.warning(`只能上传1张图片`)
}

// 图片上传前校验
const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isJpgOrPng) {
    ElMessage.error('只能上传JPG/PNG格式的图片')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

// 处理图片上传
const handleImageUpload = async (params) => {
  try {
    const formData = new FormData()
    formData.append('imageFile', params.file)
    formData.append('roomId', currentRoomId.value)

    const response = await http.post('/meetingRoom/uploadImage', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    if (response.code === 2001) {
      params.onSuccess(response)
    } else {
      params.onError(response)
    }
  } catch (error) {
    params.onError(error)
  }
}

// 上传成功处理
const handleUploadSuccess = () => {
  ElMessage.success('图片上传成功')
  uploadImageDialogVisible.value = false
  fetchMeetingRoomList()
}

// 图片上传相关变量
const tempImageUrl = ref('') // 临时存储新增时上传的图片URL

// 新增时处理图片上传
const handleAddImageUpload = async (params) => {
  try {
    const formData = new FormData()
    formData.append('imageFile', params.file)
    // 新增时还没有roomId，这里只上传图片获取URL

    const response = await http.post('/meetingRoom/uploadImage', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    if (response.code === 2001) {
      tempImageUrl.value = response.data // 保存返回的图片URL
      params.onSuccess(response)
    } else {
      params.onError(response)
    }
  } catch (error) {
    params.onError(error)
  }
}

// 新增时上传成功处理
const handleAddUploadSuccess = (response) => {
  // 避免2次调用
  if (!response || !response.data) return

  ElMessage.success('图片上传成功')
  // console.log(response)
  editForm.imageUrl = response.data // 将图片URL赋值给表单

  // fileList.value = [{url: response.data}]
}

// 新增会议室
const handleAdd = () => {
  // 重置表单
  Object.assign(editForm, {
    roomId: '',
    roomName: '',
    roomType: '',
    capacity: 1,
    location: '',
    roomStatus: 'AVAILABLE',
    imageUrl: '',
    equipmentIds: [],
  })
  tempImageUrl.value = ''
  editDialogVisible.value = true
  fileList.value = []
}

// 编辑会议室
const handleEdit = (row) => {
  Object.assign(editForm, {
    roomId: row.roomId,
    roomName: row.roomName,
    roomType: row.roomType,
    capacity: row.capacity,
    location: row.location,
    roomStatus: row.roomStatus,
    imageUrl: row.imageUrl,
    equipmentIds: [], // 这里需要获取当前会议室的设备列表
  })

  // 获取设备列表
  fetchRoomEquipment(row.roomId).then((success) => {
    if (success) {
      editForm.equipmentIds = currentRoomEquipment.value.map((item) => item.equipmentId)
    }
    editDialogVisible.value = true
  })

  // 重置图片列表
  fileList.value = []
}

// 关闭对话框
const handleDialogClose = () => {
  editDialogVisible.value = false
  editFormRef.value?.resetFields()

  // 重置图片列表
  fileList.value = []
}

// 保存会议室信息
const handleSave = async () => {
  try {
    await editFormRef.value.validate()

    let response
    if (editForm.roomId) {
      // 更新会议室
      response = await http.post('/meetingRoom/updateMeetingRoom', editForm)
    } else {
      // 新增会议室
      response = await http.post('/meetingRoom/addMeetingRoom', editForm)
    }

    if (response.code === 2001) {
      ElMessage.success(editForm.roomId ? '会议室更新成功' : '会议室新增成功')
      editDialogVisible.value = false
      fetchMeetingRoomList()
    } else {
      ElMessage.error(response.msg || (editForm.roomId ? '更新失败' : '新增失败'))
    }
  } catch (error) {
    console.error('操作会议室失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}
</script>

<style scoped>
.meeting-room-page {
  padding: 24px; /* 稍微增加页面内边距 */
  max-width: 1500px; /* 限制最大宽度，避免在宽屏上过度拉伸 */
  margin: 0 auto; /* 居中显示 */
}

.filter-container {
  margin-bottom: 20px;
}

.filter-item {
  display: flex;
  align-items: center;
}

.button-group {
  display: flex;
  gap: 10px; /* 按钮之间的间距 */
  align-items: center; /* 垂直居中对齐 */
}

.filter-label {
  width: 80px;
  text-align: right;
  margin-right: 10px;
  font-size: small;
  white-space: nowrap; /* 防止标签换行 */
  line-height: 32px; /* 与输入框高度匹配 */
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.mt-3 {
  margin-top: 15px;
}
</style>
