<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div>
        <h2>店长工作台</h2>
        <p class="welcome-sub">
          当前用户：<el-tag type="danger" size="small">{{ userStore.userName || '未登录' }}</el-tag>
          &nbsp;|&nbsp;
          {{ currentDate }}
        </p>
      </div>
      <el-button type="primary" @click="loadData">
        <el-icon><Refresh /></el-icon>
        刷新数据
      </el-button>
    </div>

    <!-- 数据概览卡片 -->
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
      <el-col :span="4">
        <div class="stat-card success">
          <div class="stat-label">本月损耗金额</div>
          <div class="stat-value">¥{{ stats.monthDamageAmount || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- 待审批报损 -->
      <el-col :span="14">
        <div class="card">
          <div class="card-header">
            <h3>待审批报损</h3>
            <el-button type="primary" link @click="goTo('/damage')">查看全部</el-button>
          </div>
          <el-table :data="pendingDamages" stripe>
            <el-table-column prop="reportNo" label="报损单号" width="160" />
            <el-table-column prop="linenTypeName" label="布草类型" width="120" />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column prop="reporterName" label="上报人" width="100" />
            <el-table-column prop="reason" label="报损原因" show-overflow-tooltip />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="openApproveDialog(row)">审批</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!pendingDamages || pendingDamages.length === 0" description="暂无待审批报损单" />
        </div>
      </el-col>

      <!-- 快捷入口 -->
      <el-col :span="10">
        <div class="card">
          <div class="card-header">
            <h3>快捷入口</h3>
          </div>
          <div class="quick-entries">
            <div class="quick-item" @click="goTo('/track')">
              <el-icon :size="24" color="#409EFF"><Histogram /></el-icon>
              <span>布草轨迹</span>
            </div>
            <div class="quick-item" @click="goTo('/cost')">
              <el-icon :size="24" color="#909399"><Management /></el-icon>
              <span>成本分摊</span>
            </div>
            <div class="quick-item" @click="goTo('/damage')">
              <el-icon :size="24" color="#F56C6C"><Files /></el-icon>
              <span>报损管理</span>
            </div>
            <div class="quick-item" @click="goTo('/stocks')">
              <el-icon :size="24" color="#E6A23C"><Box /></el-icon>
              <span>库存管理</span>
            </div>
            <div class="quick-item" @click="goTo('/linen-types')">
              <el-icon :size="24" color="#67C23A"><Goods /></el-icon>
              <span>布草类型</span>
            </div>
            <div class="quick-item" @click="goTo('/rooms')">
              <el-icon :size="24" color="#409EFF"><House /></el-icon>
              <span>房间管理</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- 客赔管理 -->
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <h3>客赔管理</h3>
          </div>
          <el-table :data="guestCompensations" stripe>
            <el-table-column prop="reportNo" label="报损单号" width="160" />
            <el-table-column prop="linenTypeName" label="布草类型" width="120" />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column label="赔偿金额" width="120">
              <template #default="{ row }">
                <span style="color: #f56c6c; font-weight: 600;">¥{{ row.compensationAmount || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="收取状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.compensationCollected ? 'success' : 'warning'" size="small">
                  {{ row.compensationCollected ? '已收取' : '未收取' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button
                  type="success"
                  size="small"
                  link
                  @click="toggleCollected(row)"
                  v-if="!row.compensationCollected"
                >
                  标记已收
                </el-button>
                <span v-else style="color: #909399;">-</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!guestCompensations || guestCompensations.length === 0" description="暂无客人赔偿记录" />
        </div>
      </el-col>

      <!-- 安全库存设置 -->
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <h3>安全库存设置</h3>
          </div>
          <el-table :data="stocks" stripe>
            <el-table-column prop="linenTypeName" label="布草类型" width="140" />
            <el-table-column label="当前库存" width="120">
              <template #default="{ row }">
                <span
                  :style="{
                    color: row.inWarehouseQuantity < row.safetyStock ? '#f56c6c' : '#303133',
                    fontWeight: 600
                  }"
                >
                  {{ row.inWarehouseQuantity || 0 }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="安全库存" width="180">
              <template #default="{ row }">
                <el-input-number v-model="row.safetyStock" :min="0" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.inWarehouseQuantity < row.safetyStock" type="danger" size="small">库存不足</el-tag>
                <el-tag v-else type="success" size="small">正常</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 16px; text-align: right;">
            <el-button type="primary" @click="saveSafetyStock">保存设置</el-button>
          </div>
          <el-empty v-if="!stocks || stocks.length === 0" description="暂无库存数据" />
        </div>
      </el-col>
    </el-row>

    <!-- 审批对话框 -->
    <el-dialog v-model="approveVisible" title="审批报损" width="500px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="报损单号">
          <span>{{ approveForm.reportNo }}</span>
        </el-form-item>
        <el-form-item label="布草类型">
          <span>{{ approveForm.linenTypeName }}</span>
        </el-form-item>
        <el-form-item label="数量">
          <span>{{ approveForm.quantity }}</span>
        </el-form-item>
        <el-form-item label="报损原因">
          <span>{{ approveForm.reason }}</span>
        </el-form-item>
        <el-form-item label="责任归属" required>
          <el-select v-model="approveForm.responsibility" placeholder="请选择责任归属" style="width: 100%">
            <el-option label="客人赔偿" value="客人赔偿" />
            <el-option label="洗涤厂赔偿" value="洗涤厂赔偿" />
            <el-option label="正常损耗" value="正常损耗" />
          </el-select>
        </el-form-item>
        <el-form-item label="赔偿金额" v-if="approveForm.responsibility && approveForm.responsibility !== '正常损耗'">
          <el-input-number v-model="approveForm.compensationAmount" :min="0" :precision="2" :step="10" />
          <span style="margin-left: 8px; color: #909399;">元</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="approveForm.approvalRemark" type="textarea" :rows="3" placeholder="审批备注（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="danger" @click="handleApprove('reject')">驳回</el-button>
        <el-button type="primary" @click="handleApprove('approve')">通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh,
  Histogram,
  Management,
  Files,
  Box,
  Goods,
  House
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'
import {
  listDamageReports,
  listDamageByStatus,
  createDamageReport,
  approveDamageReport,
  listLinenStocks,
  updateSafetyStock,
  listLinenBags,
  listCheckoutRecords,
  listCostAllocation,
  listAllTracks
} from '@/api'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({})
const pendingDamages = ref([])
const guestCompensations = ref([])
const stocks = ref([])
const approveVisible = ref(false)

const approveForm = ref({
  id: null,
  reportNo: '',
  linenTypeName: '',
  quantity: 0,
  reason: '',
  responsibility: '',
  compensationAmount: 0,
  approvalRemark: ''
})

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日 dddd'))

// 跳转页面
function goTo(path) {
  router.push(path)
}

// 打开审批对话框
function openApproveDialog(row) {
  approveForm.value = {
    id: row.id,
    reportNo: row.reportNo,
    linenTypeName: row.linenTypeName,
    quantity: row.quantity,
    reason: row.reason,
    responsibility: '',
    compensationAmount: 0,
    approvalRemark: ''
  }
  approveVisible.value = true
}

// 处理审批
async function handleApprove(action) {
  if (action === 'approve' && !approveForm.value.responsibility) {
    ElMessage.warning('请选择责任归属')
    return
  }
  try {
    await ElMessageBox.confirm(
      action === 'approve' ? '确认通过此报损单？' : '确认驳回此报损单？',
      '审批确认',
      { type: 'warning' }
    )
    await approveDamageReport(approveForm.value.id, {
      status: action === 'approve' ? '已审批' : '已驳回',
      responsibility: approveForm.value.responsibility,
      compensationAmount: approveForm.value.compensationAmount,
      approvalRemark: approveForm.value.approvalRemark
    })
    ElMessage.success(action === 'approve' ? '审批通过' : '已驳回')
    approveVisible.value = false
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

// 切换收取状态
async function toggleCollected(row) {
  try {
    await ElMessageBox.confirm('确认标记为已收取？', '确认', { type: 'warning' })
    await approveDamageReport(row.id, {
      compensationCollected: true
    })
    ElMessage.success('已标记为收取')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

// 保存安全库存
async function saveSafetyStock() {
  try {
    const items = stocks.value.map((s) => ({
      id: s.id,
      safetyStock: s.safetyStock
    }))
    await updateSafetyStock(items)
    ElMessage.success('安全库存已更新')
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  }
}

// 加载所有数据
async function loadData() {
  try {
    const [bags, stockList, damages, checkoutRecords, costList] = await Promise.all([
      listLinenBags({}).catch(() => []),
      listLinenStocks().catch(() => []),
      listDamageReports().catch(() => []),
      listCheckoutRecords().catch(() => []),
      listCostAllocation(dayjs().startOf('month').format('YYYY-MM-DD'), dayjs().endOf('month').format('YYYY-MM-DD')).catch(() => [])
    ])

    const bagList = Array.isArray(bags) ? bags : []
    const stockArr = Array.isArray(stockList) ? stockList : []
    const damageList = Array.isArray(damages) ? damages : []
    const checkoutList = Array.isArray(checkoutRecords) ? checkoutRecords : []
    const costs = Array.isArray(costList) ? costList : []

    stats.value = {
      todayCheckout: checkoutList.filter((c) => {
        return dayjs(c.createTime).isSame(dayjs(), 'day')
      }).length,
      pendingPickup: bagList.filter((b) => b.status === '待取件').length,
      washing: bagList.filter((b) => ['洗涤中', '待洗涤'].includes(b.status)).length,
      stockWarning: stockArr.filter((s) => s.inWarehouseQuantity < s.safetyStock).length,
      pendingDamage: damageList.filter((d) => d.status === '待审批').length,
      monthDamageAmount: costs.reduce((sum, c) => sum + (c.damageCost || 0), 0).toFixed(2)
    }

    pendingDamages.value = damageList.filter((d) => d.status === '待审批')

    guestCompensations.value = damageList.filter(
      (d) => d.status === '已审批' && d.responsibility === '客人赔偿'
    )

    stocks.value = stockArr
  } catch (e) {
    console.error('加载数据失败', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.welcome-sub {
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

.quick-entries {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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
