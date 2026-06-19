<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2>欢迎回来，{{ userStore.userName || '用户' }}</h2>
        <p class="welcome-sub">
          当前角色：
          <el-tag :type="roleTagType" size="small">{{ roleName }}</el-tag>
          &nbsp;|&nbsp;
          {{ currentDate }}
        </p>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="4">
        <div class="stat-card primary">
          <div class="stat-label">今日退房数</div>
          <div class="stat-value">{{ stats.todayCheckout || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card warning">
          <div class="stat-label">待取件袋数</div>
          <div class="stat-value">{{ stats.pendingPickup || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card">
          <div class="stat-label">洗涤中数量</div>
          <div class="stat-value">{{ stats.washing || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card success">
          <div class="stat-label">待补货数量</div>
          <div class="stat-value">{{ stats.pendingRestock || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card danger">
          <div class="stat-label">库存预警数</div>
          <div class="stat-value">{{ stats.stockWarning || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card warning">
          <div class="stat-label">待审批报损</div>
          <div class="stat-value">{{ stats.pendingDamage || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="8">
        <div class="card">
          <div class="card-header">
            <h3>快捷入口</h3>
          </div>
          <div class="quick-entries">
            <template v-for="item in quickEntries" :key="item.path">
              <div class="quick-item" @click="goTo(item.path)">
                <el-icon :size="28" :color="item.color">
                  <component :is="item.icon" />
                </el-icon>
                <span>{{ item.title }}</span>
              </div>
            </template>
          </div>
        </div>
      </el-col>
      <el-col :span="16">
        <div class="card">
          <div class="card-header">
            <h3>最近布草轨迹</h3>
            <el-button type="primary" link @click="goTo('/track')">查看全部</el-button>
          </div>
          <el-table :data="recentTracks" stripe>
            <el-table-column prop="bagCode" label="袋码" width="160" />
            <el-table-column prop="roomNo" label="房间号" width="100" />
            <el-table-column prop="linenTypeName" label="布草类型" width="100" />
            <el-table-column prop="action" label="操作" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="actionTagType(row.action)">{{ row.action }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="actionDetail" label="详情" show-overflow-tooltip />
            <el-table-column prop="operatorName" label="操作人" width="100" />
            <el-table-column prop="actionTime" label="操作时间" width="170">
              <template #default="{ row }">{{ formatTime(row.actionTime) }}</template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!recentTracks || recentTracks.length === 0" description="暂无轨迹记录" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import {
  Reading,
  Avatar,
  Van,
  Goods,
  Box,
  UserFilled,
  House,
  Files,
  Management,
  Histogram
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { listLinenBags, listLinenStocks, listDamageByStatus } from '@/api'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({})
const recentTracks = ref([])

const roleMap = {
  reception: '前台',
  cleaner: '保洁',
  driver: '司机',
  factory: '洗涤厂',
  warehouse: '库管',
  manager: '店长'
}

const roleName = computed(() => roleMap[userStore.userRole] || '未知角色')
const roleTagType = computed(() => {
  const map = {
    reception: '',
    cleaner: 'success',
    driver: 'warning',
    factory: 'info',
    warehouse: '',
    manager: 'danger'
  }
  return map[userStore.userRole] || ''
})

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日 dddd'))

const allQuickEntries = [
  { path: '/reception', title: '前台工作台', icon: Reading, color: '#409EFF', roles: ['reception', 'manager'] },
  { path: '/cleaner', title: '保洁工作台', icon: Avatar, color: '#67C23A', roles: ['cleaner', 'manager'] },
  { path: '/driver', title: '司机工作台', icon: Van, color: '#E6A23C', roles: ['driver', 'manager'] },
  { path: '/factory', title: '洗涤厂工作台', icon: Goods, color: '#909399', roles: ['factory', 'manager'] },
  { path: '/warehouse', title: '库管工作台', icon: Box, color: '#409EFF', roles: ['warehouse', 'manager'] },
  { path: '/manager', title: '店长工作台', icon: UserFilled, color: '#F56C6C', roles: ['manager'] },
  { path: '/rooms', title: '房间管理', icon: House, color: '#409EFF', roles: ['reception', 'manager'] },
  { path: '/linen-types', title: '布草类型', icon: Goods, color: '#67C23A', roles: ['manager'] },
  { path: '/stocks', title: '库存管理', icon: Box, color: '#E6A23C', roles: ['warehouse', 'manager'] },
  { path: '/damage', title: '报损管理', icon: Files, color: '#F56C6C', roles: ['manager', 'warehouse', 'factory'] },
  { path: '/cost', title: '成本分摊', icon: Management, color: '#909399', roles: ['manager'] },
  { path: '/track', title: '布草轨迹', icon: Histogram, color: '#409EFF', roles: null }
]

const quickEntries = computed(() => {
  const role = userStore.userRole
  return allQuickEntries.filter((item) => !item.roles || item.roles.includes(role)).slice(0, 8)
})

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

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '-'
}

function goTo(path) {
  router.push(path)
}

async function loadData() {
  try {
    const [bags, stocks, damages] = await Promise.all([
      listLinenBags({}).catch(() => []),
      listLinenStocks().catch(() => []),
      listDamageByStatus('待审批').catch(() => [])
    ])
    stats.value = {
      pendingPickup: Array.isArray(bags) ? bags.filter((b) => b.status === '待取件').length : 0,
      washing: Array.isArray(bags) ? bags.filter((b) => ['洗涤中', '待洗涤'].includes(b.status)).length : 0,
      pendingRestock: Array.isArray(stocks) ? stocks.filter((s) => s.inWarehouseQuantity < s.safetyStock).length : 0,
      stockWarning: Array.isArray(stocks) ? stocks.filter((s) => s.inWarehouseQuantity < s.safetyStock).length : 0,
      pendingDamage: Array.isArray(damages) ? damages.length : 0,
      todayCheckout: 0
    }
  } catch (e) {
    stats.value = {
      pendingPickup: 0,
      washing: 0,
      pendingRestock: 0,
      stockWarning: 0,
      pendingDamage: 0,
      todayCheckout: 0
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.stats-row {
  margin-bottom: 20px;
}

.welcome-sub {
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

.quick-entries {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fafafa;
  gap: 8px;

  &:hover {
    background: #ecf5ff;
    transform: translateY(-2px);
  }

  span {
    font-size: 13px;
    color: #606266;
  }
}
</style>
