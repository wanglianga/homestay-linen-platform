<template>
  <div class="page-container">
    <div class="page-header">
      <h2>布草轨迹查询</h2>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-input v-model="searchBagCode" placeholder="请输入袋码" clearable style="width: 200px" @keyup.enter="handleSearchBag" />
        <el-input v-model="searchRoomNo" placeholder="请输入房号" clearable style="width: 200px" @keyup.enter="handleSearchRoom" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </div>

    <div v-if="timelineTracks.length > 0" class="card">
      <div class="card-header">
        <h3>轨迹时间轴</h3>
      </div>
      <el-timeline>
        <el-timeline-item
          v-for="(track, index) in timelineTracks"
          :key="track.id || index"
          :timestamp="formatTime(track.actionTime)"
          :type="timelineItemType(track.action)"
          :hollow="index === 0"
        >
          <div class="timeline-content">
            <div class="timeline-header">
              <el-tag :type="actionTagType(track.action)" size="small">{{ track.action }}</el-tag>
              <span class="timeline-operator">{{ track.operatorName || '系统' }}</span>
            </div>
            <div class="timeline-detail">
              <span v-if="track.bagCode" class="detail-item">袋码：{{ track.bagCode }}</span>
              <span v-if="track.roomNo" class="detail-item">房号：{{ track.roomNo }}</span>
              <span v-if="track.linenTypeName" class="detail-item">类型：{{ track.linenTypeName }}</span>
              <span v-if="track.quantity" class="detail-item">数量：{{ track.quantity }}</span>
              <span v-if="track.locationFrom || track.locationTo" class="detail-item">
                位置：{{ track.locationFrom || '-' }} → {{ track.locationTo || '-' }}
              </span>
            </div>
            <div v-if="track.remark || track.actionDetail" class="timeline-remark">
              {{ track.remark || track.actionDetail }}
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>

    <div class="card">
      <div class="card-header">
        <h3>最近轨迹列表</h3>
      </div>
      <el-table :data="recentTracks" stripe border>
        <el-table-column prop="bagCode" label="袋码" width="160" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="linenTypeName" label="布草类型" width="120" />
        <el-table-column prop="action" label="操作" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="actionTagType(row.action)">{{ row.action }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="位置变化" width="220">
          <template #default="{ row }">
            {{ row.locationFrom || '-' }} → {{ row.locationTo || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="actionDetail" label="详情备注" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="actionTime" label="操作时间" width="170">
          <template #default="{ row }">{{ formatTime(row.actionTime) }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!recentTracks || recentTracks.length === 0" description="暂无轨迹记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { listTracksByBagCode, listTracksByRoom, listAllTracks } from '@/api'

const searchBagCode = ref('')
const searchRoomNo = ref('')
const timelineTracks = ref([])
const recentTracks = ref([])

function actionTagType(action) {
  const map = {
    装袋: 'warning',
    取件: 'primary',
    洗涤中: 'info',
    洗涤完成: 'success',
    返店: '',
    入库: 'success',
    报损: 'danger'
  }
  return map[action] || ''
}

function timelineItemType(action) {
  const map = {
    装袋: 'warning',
    取件: 'primary',
    洗涤中: 'info',
    洗涤完成: 'success',
    返店: '',
    入库: 'success',
    报损: 'danger'
  }
  return map[action] || 'primary'
}

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm:ss') : '-'
}

async function loadAllTracks() {
  try {
    const data = await listAllTracks()
    recentTracks.value = Array.isArray(data) ? data.slice(0, 50) : []
  } catch (e) {
    recentTracks.value = []
  }
}

async function handleSearchBag() {
  if (!searchBagCode.value.trim()) {
    ElMessage.warning('请输入袋码')
    return
  }
  try {
    const data = await listTracksByBagCode(searchBagCode.value.trim())
    timelineTracks.value = Array.isArray(data) ? data : []
    if (timelineTracks.value.length === 0) {
      ElMessage.info('未找到该袋码的轨迹记录')
    }
  } catch (e) {
    timelineTracks.value = []
    ElMessage.error('查询失败')
  }
}

async function handleSearchRoom() {
  if (!searchRoomNo.value.trim()) {
    ElMessage.warning('请输入房号')
    return
  }
  try {
    const data = await listTracksByRoom(searchRoomNo.value.trim())
    timelineTracks.value = Array.isArray(data) ? data : []
    if (timelineTracks.value.length === 0) {
      ElMessage.info('未找到该房号的轨迹记录')
    }
  } catch (e) {
    timelineTracks.value = []
    ElMessage.error('查询失败')
  }
}

function handleSearch() {
  if (searchBagCode.value.trim()) {
    handleSearchBag()
  } else if (searchRoomNo.value.trim()) {
    handleSearchRoom()
  } else {
    ElMessage.warning('请输入袋码或房号')
  }
}

function handleReset() {
  searchBagCode.value = ''
  searchRoomNo.value = ''
  timelineTracks.value = []
  loadAllTracks()
}

onMounted(() => {
  loadAllTracks()
})
</script>

<style scoped lang="scss">
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

.timeline-content {
  .timeline-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;

    .timeline-operator {
      font-size: 13px;
      color: #909399;
    }
  }

  .timeline-detail {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-bottom: 6px;

    .detail-item {
      font-size: 13px;
      color: #606266;
    }
  }

  .timeline-remark {
    font-size: 13px;
    color: #909399;
    background: #f5f7fa;
    padding: 6px 10px;
    border-radius: 4px;
  }
}
</style>
