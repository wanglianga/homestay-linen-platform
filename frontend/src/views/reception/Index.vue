<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2>前台工作台</h2>
        <p class="header-sub">当前日期：{{ currentDate }}</p>
      </div>
      <el-button type="danger" :icon="Warning" @click="goTo('/damage')">客赔登记</el-button>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <div class="stat-card success">
          <div class="stat-label">空闲</div>
          <div class="stat-value">{{ roomStats.free }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card danger">
          <div class="stat-label">占用</div>
          <div class="stat-value">{{ roomStats.occupied }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card warning">
          <div class="stat-label">清扫中</div>
          <div class="stat-value">{{ roomStats.cleaning }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-label">待清洁</div>
          <div class="stat-value">{{ roomStats.pending }}</div>
        </div>
      </el-col>
    </el-row>

    <div class="card">
      <div class="card-header">
        <h3>房间状态看板</h3>
      </div>
      <div class="room-grid">
        <div
          v-for="room in rooms"
          :key="room.id"
          class="room-card"
          :class="'status-' + room.status"
          @click="showRoomDetail(room)"
        >
          <div class="room-no">{{ room.roomNo }}</div>
          <div class="room-type">{{ room.roomType }}</div>
          <el-tag size="small" :type="statusTagType(room.status)" effect="dark">{{ room.status }}</el-tag>
        </div>
      </div>
      <el-empty v-if="!rooms || rooms.length === 0" description="暂无房间数据" />
    </div>

    <el-row :gutter="16">
      <el-col :span="10">
        <div class="card">
          <div class="card-header">
            <h3>退房登记</h3>
          </div>
          <el-form :model="checkoutForm" :rules="checkoutRules" ref="checkoutFormRef" label-width="80px">
            <el-form-item label="房间" prop="roomId">
              <el-select v-model="checkoutForm.roomId" placeholder="请选择房间" style="width: 100%">
                <el-option
                  v-for="r in occupiedRooms"
                  :key="r.id"
                  :label="r.roomNo + ' - ' + r.roomType"
                  :value="r.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="客人姓名" prop="guestName">
              <el-input v-model="checkoutForm.guestName" placeholder="请输入客人姓名" />
            </el-form-item>
            <el-form-item label="保洁员" prop="cleanerId">
              <el-select v-model="checkoutForm.cleanerId" placeholder="请选择保洁员" style="width: 100%">
                <el-option v-for="c in cleaners" :key="c.id" :label="c.realName" :value="c.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="checkoutForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleCheckout">提交退房</el-button>
              <el-button @click="resetCheckoutForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
      <el-col :span="14">
        <div class="card">
          <div class="card-header">
            <h3>退房记录</h3>
          </div>
          <el-table :data="checkoutRecords" stripe>
            <el-table-column prop="roomNo" label="房号" width="100" />
            <el-table-column prop="guestName" label="客人姓名" width="120" />
            <el-table-column prop="cleanerName" label="保洁员" width="100" />
            <el-table-column prop="checkoutTime" label="退房时间" width="170">
              <template #default="{ row }">{{ formatTime(row.checkoutTime) }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          </el-table>
          <el-empty v-if="!checkoutRecords || checkoutRecords.length === 0" description="暂无退房记录" />
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="detailVisible" title="房间详情" width="400px">
      <el-descriptions :column="1" border v-if="currentRoom">
        <el-descriptions-item label="房号">{{ currentRoom.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ currentRoom.floor }}楼</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentRoom.roomType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(currentRoom.status)">{{ currentRoom.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentRoom.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { listRooms, updateRoom, listCheckoutRecords, createCheckout, listUsersByRole } from '@/api'

const router = useRouter()

const rooms = ref([])
const cleaners = ref([])
const checkoutRecords = ref([])
const detailVisible = ref(false)
const currentRoom = ref(null)
const checkoutFormRef = ref(null)
const submitting = ref(false)

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日 dddd'))

const checkoutForm = reactive({
  roomId: null,
  guestName: '',
  cleanerId: null,
  remark: ''
})

const checkoutRules = {
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  guestName: [{ required: true, message: '请输入客人姓名', trigger: 'blur' }],
  cleanerId: [{ required: true, message: '请选择保洁员', trigger: 'change' }]
}

const roomStats = computed(() => {
  const list = rooms.value || []
  return {
    free: list.filter((r) => r.status === '空闲').length,
    occupied: list.filter((r) => r.status === '占用').length,
    cleaning: list.filter((r) => r.status === '清扫中').length,
    pending: list.filter((r) => r.status === '待清洁').length
  }
})

const occupiedRooms = computed(() => {
  return (rooms.value || []).filter((r) => r.status === '占用')
})

function statusTagType(status) {
  const map = {
    空闲: 'success',
    占用: 'danger',
    清扫中: 'warning',
    待清洁: 'info'
  }
  return map[status] || ''
}

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '-'
}

function goTo(path) {
  router.push(path)
}

function showRoomDetail(room) {
  currentRoom.value = room
  detailVisible.value = true
}

function resetCheckoutForm() {
  checkoutForm.roomId = null
  checkoutForm.guestName = ''
  checkoutForm.cleanerId = null
  checkoutForm.remark = ''
  checkoutFormRef.value?.resetFields()
}

async function loadRooms() {
  try {
    const data = await listRooms()
    rooms.value = Array.isArray(data) ? data : []
  } catch (e) {
    rooms.value = []
  }
}

async function loadCleaners() {
  try {
    const data = await listUsersByRole('cleaner')
    cleaners.value = Array.isArray(data) ? data : []
  } catch (e) {
    cleaners.value = []
  }
}

async function loadCheckoutRecords() {
  try {
    const data = await listCheckoutRecords()
    checkoutRecords.value = Array.isArray(data) ? data.slice(0, 10) : []
  } catch (e) {
    checkoutRecords.value = []
  }
}

async function handleCheckout() {
  if (!checkoutFormRef.value) return
  await checkoutFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const room = rooms.value.find((r) => r.id === checkoutForm.roomId)
      const cleaner = cleaners.value.find((c) => c.id === checkoutForm.cleanerId)
      await createCheckout({
        roomId: checkoutForm.roomId,
        roomNo: room?.roomNo,
        guestName: checkoutForm.guestName,
        cleanerId: checkoutForm.cleanerId,
        cleanerName: cleaner?.realName,
        remark: checkoutForm.remark
      })
      await updateRoom({
        id: checkoutForm.roomId,
        status: '待清洁'
      })
      ElMessage.success('退房登记成功')
      resetCheckoutForm()
      await loadRooms()
      await loadCheckoutRecords()
    } catch (e) {
      ElMessage.error('退房登记失败：' + (e.message || '未知错误'))
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadRooms()
  loadCleaners()
  loadCheckoutRecords()
})
</script>

<style scoped lang="scss">
.header-sub {
  color: #909399;
  margin-top: 6px;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  h3 {
    margin: 0;
    font-size: 16px;
    color: #303133;
  }
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}

.room-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  gap: 6px;
  border: 2px solid transparent;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .room-no {
    font-size: 20px;
    font-weight: 600;
  }

  .room-type {
    font-size: 12px;
    color: #606266;
  }

  &.status-空闲 {
    background: #f0f9eb;
    border-color: #c2e7b0;
    .room-no {
      color: #67c23a;
    }
  }

  &.status-占用 {
    background: #fef0f0;
    border-color: #fbc4c4;
    .room-no {
      color: #f56c6c;
    }
  }

  &.status-清扫中 {
    background: #fdf6ec;
    border-color: #f5dab1;
    .room-no {
      color: #e6a23c;
    }
  }

  &.status-待清洁 {
    background: #f4f4f5;
    border-color: #d3d4d6;
    .room-no {
      color: #909399;
    }
  }
}
</style>
