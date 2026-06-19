<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2>保洁工作台</h2>
        <p class="header-sub">
          当前保洁员：<el-tag type="success" size="small">{{ userStore.userName || '未登录' }}</el-tag>
          &nbsp;|&nbsp;
          {{ currentDate }}
        </p>
      </div>
    </div>

    <div class="card">
      <div class="card-header">
        <h3>待清洁任务</h3>
        <el-tag type="warning" size="small">共 {{ pendingRooms.length }} 间</el-tag>
      </div>
      <div class="room-grid">
        <div
          v-for="room in pendingRooms"
          :key="room.id"
          class="room-card"
          @click="selectRoom(room)"
        >
          <div class="room-no">{{ room.roomNo }}</div>
          <div class="room-type">{{ room.roomType }}</div>
          <el-tag size="small" type="info" effect="dark">待清洁</el-tag>
        </div>
      </div>
      <el-empty v-if="!pendingRooms || pendingRooms.length === 0" description="暂无待清洁任务" />
    </div>

    <el-row :gutter="16">
      <el-col :span="14">
        <div class="card">
          <div class="card-header">
            <h3>退房点数</h3>
            <span v-if="selectedRoom" class="selected-room">
              当前房间：<el-tag type="primary">{{ selectedRoom.roomNo }} - {{ selectedRoom.roomType }}</el-tag>
            </span>
          </div>
          <el-alert
            v-if="!selectedRoom"
            title="请先在上方选择一个待清洁的房间"
            type="warning"
            :closable="false"
            style="margin-bottom: 16px"
          />
          <template v-if="selectedRoom">
            <div class="linen-list">
              <div
                v-for="(item, index) in linenItems"
                :key="index"
                class="linen-item"
              >
                <div class="linen-header">
                  <span class="linen-name">{{ item.linenTypeName }}</span>
                  <el-checkbox v-model="item.hasStain">有污渍</el-checkbox>
                </div>
                <div class="linen-form">
                  <el-form-item label="数量" style="margin-bottom: 0; flex: 0 0 160px">
                    <el-input-number v-model="item.quantity" :min="0" :max="999" />
                  </el-form-item>
                  <el-form-item label="污渍备注" style="margin-bottom: 0; flex: 1" v-if="item.hasStain">
                    <el-input v-model="item.stainRemark" placeholder="请填写污渍备注" />
                  </el-form-item>
                  <el-form-item label="污渍照片" style="margin-bottom: 0; flex: 1" v-if="item.hasStain">
                    <el-input v-model="item.stainPhoto" placeholder="请输入照片URL（模拟）" />
                  </el-form-item>
                </div>
              </div>
            </div>
            <div class="submit-bar">
              <el-button type="primary" :loading="submitting" :disabled="!canSubmit" @click="handleSubmitBag">
                装袋提交
              </el-button>
              <el-button @click="resetLinenItems">重置</el-button>
            </div>
          </template>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="card">
          <div class="card-header">
            <h3>已完成装袋记录</h3>
          </div>
          <el-table :data="myLinenBags" stripe>
            <el-table-column prop="bagCode" label="袋码" width="160" show-overflow-tooltip />
            <el-table-column prop="roomNo" label="房号" width="80" />
            <el-table-column prop="linenTypeName" label="布草类型" width="100" />
            <el-table-column prop="quantity" label="数量" width="70" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="bagStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="160">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!myLinenBags || myLinenBags.length === 0" description="暂无装袋记录" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import {
  listRooms,
  updateRoom,
  listLinenBags,
  createLinenBag,
  createCheckout,
  listCheckoutRecords,
  listLinenTypes,
  listLinenBagsByCheckout
} from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const rooms = ref([])
const linenTypes = ref([])
const checkoutRecords = ref([])
const myLinenBags = ref([])
const selectedRoom = ref(null)
const submitting = ref(false)

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日 dddd'))

const pendingRooms = computed(() => {
  return (rooms.value || []).filter((r) => r.status === '待清洁')
})

const linenItems = ref([])

const canSubmit = computed(() => {
  if (!selectedRoom.value) return false
  return linenItems.value.some((item) => item.quantity > 0)
})

function bagStatusType(status) {
  const map = {
    待取件: 'warning',
    取件中: 'primary',
    洗涤中: 'info',
    洗涤完成: 'success',
    已返店: '',
    已入库: 'success'
  }
  return map[status] || ''
}

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '-'
}

function generateBagCode() {
  const now = dayjs()
  const rand = Math.random().toString(36).substring(2, 8).toUpperCase()
  return `B${now.format('YYYYMMDDHHmmss')}${rand}`
}

function initLinenItems() {
  const defaultTypes = ['床单', '被套', '浴巾', '浴袍', '地巾']
  const typeList = linenTypes.value && linenTypes.value.length > 0 ? linenTypes.value : defaultTypes.map((name) => ({ name }))
  linenItems.value = typeList.map((t) => ({
    linenTypeId: t.id,
    linenTypeName: t.name || t.linenTypeName,
    quantity: 0,
    hasStain: false,
    stainRemark: '',
    stainPhoto: ''
  }))
}

function selectRoom(room) {
  selectedRoom.value = room
  initLinenItems()
}

function resetLinenItems() {
  initLinenItems()
}

async function loadRooms() {
  try {
    const data = await listRooms()
    rooms.value = Array.isArray(data) ? data : []
  } catch (e) {
    rooms.value = []
  }
}

async function loadLinenTypes() {
  try {
    const data = await listLinenTypes()
    linenTypes.value = Array.isArray(data) ? data : []
  } catch (e) {
    linenTypes.value = []
  }
}

async function loadMyLinenBags() {
  try {
    const data = await listLinenBags({})
    const allBags = Array.isArray(data) ? data : []
    const userId = userStore.userInfo?.id
    myLinenBags.value = userId ? allBags.filter((b) => b.cleanerId === userId) : allBags
  } catch (e) {
    myLinenBags.value = []
  }
}

async function handleSubmitBag() {
  if (!selectedRoom.value) {
    ElMessage.warning('请先选择房间')
    return
  }
  if (!canSubmit.value) {
    ElMessage.warning('请至少填写一种布草的数量')
    return
  }
  submitting.value = true
  try {
    const userId = userStore.userInfo?.id
    const userName = userStore.userName

    let checkoutRecord = null
    try {
      checkoutRecord = await createCheckout({
        roomId: selectedRoom.value.id,
        roomNo: selectedRoom.value.roomNo,
        guestName: '',
        cleanerId: userId,
        cleanerName: userName,
        remark: '保洁装袋'
      })
    } catch (e) {
      checkoutRecord = { id: Date.now() }
    }

    const validItems = linenItems.value.filter((item) => item.quantity > 0)
    for (const item of validItems) {
      const bagCode = generateBagCode()
      await createLinenBag({
        bagCode,
        checkoutId: checkoutRecord?.id,
        roomId: selectedRoom.value.id,
        roomNo: selectedRoom.value.roomNo,
        linenTypeId: item.linenTypeId,
        linenTypeName: item.linenTypeName,
        quantity: item.quantity,
        cleanerId: userId,
        cleanerName: userName,
        hasStain: item.hasStain,
        stainRemark: item.stainRemark,
        stainPhoto: item.stainPhoto,
        status: '待取件'
      })
    }

    await updateRoom({
      id: selectedRoom.value.id,
      status: '空闲'
    })

    ElMessage.success('装袋提交成功')
    selectedRoom.value = null
    linenItems.value = []
    await loadRooms()
    await loadMyLinenBags()
  } catch (e) {
    ElMessage.error('装袋提交失败：' + (e.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadRooms()
  loadLinenTypes()
  loadMyLinenBags()
})
</script>

<style scoped lang="scss">
.header-sub {
  color: #909399;
  margin-top: 6px;
  font-size: 14px;
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

.selected-room {
  font-size: 14px;
  color: #606266;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
  margin-bottom: 12px;
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
  background: #f4f4f5;
  border: 2px solid #d3d4d6;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-color: #409eff;
    background: #ecf5ff;
  }

  .room-no {
    font-size: 20px;
    font-weight: 600;
    color: #909399;
  }

  .room-type {
    font-size: 12px;
    color: #606266;
  }
}

.linen-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.linen-item {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px 16px;
  background: #fafafa;
}

.linen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .linen-name {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }
}

.linen-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.submit-bar {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
