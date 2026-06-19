<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div>
        <h2>库管工作台</h2>
        <p class="welcome-sub">
          当前库管：<el-tag type="primary" size="small">{{ userStore.userName || '未登录' }}</el-tag>
          &nbsp;|&nbsp;
          {{ currentDate }}
        </p>
      </div>
      <el-button type="primary" @click="loadData">
        <el-icon><Refresh /></el-icon>
        刷新数据
      </el-button>
    </div>

    <!-- 库存概览卡片 -->
    <div class="card">
      <div class="card-header">
        <h3>库存概览</h3>
        <el-button type="primary" link @click="openSafetyStockDialog">设置安全库存</el-button>
      </div>
      <el-row :gutter="16">
        <el-col :span="6" v-for="stock in stocks" :key="stock.id">
          <div class="stock-card" :class="{ warning: stock.inWarehouseQuantity < stock.safetyStock }">
            <div class="stock-title">{{ stock.linenTypeName }}</div>
            <div class="stock-main">
              <span class="stock-num" :class="{ danger: stock.inWarehouseQuantity < stock.safetyStock }">
                {{ stock.inWarehouseQuantity || 0 }}
              </span>
              <span class="stock-unit">在库</span>
            </div>
            <div class="stock-details">
              <div class="stock-item">
                <span class="label">使用中</span>
                <span class="value">{{ stock.inUseQuantity || 0 }}</span>
              </div>
              <div class="stock-item">
                <span class="label">洗涤中</span>
                <span class="value">{{ stock.washingQuantity || 0 }}</span>
              </div>
              <div class="stock-item">
                <span class="label">配送中</span>
                <span class="value">{{ stock.deliveringQuantity || 0 }}</span>
              </div>
              <div class="stock-item">
                <span class="label">损耗</span>
                <span class="value">{{ stock.damagedQuantity || 0 }}</span>
              </div>
              <div class="stock-item">
                <span class="label">安全库存</span>
                <span class="value" :class="{ danger: stock.inWarehouseQuantity < stock.safetyStock }">
                  {{ stock.safetyStock || 0 }}
                </span>
              </div>
            </div>
            <div v-if="stock.inWarehouseQuantity < stock.safetyStock" class="stock-warning">
              ⚠️ 库存不足预警
            </div>
          </div>
        </el-col>
      </el-row>
      <el-empty v-if="!stocks || stocks.length === 0" description="暂无库存数据" />
    </div>

    <el-row :gutter="16">
      <!-- 待回库验收 -->
      <el-col :span="14">
        <div class="card">
          <div class="card-header">
            <h3>待回库验收</h3>
            <el-tag type="warning" size="small">待验收：{{ pendingReturnBags.length }}</el-tag>
          </div>
          <el-table :data="pendingReturnBags" stripe>
            <el-table-column prop="bagCode" label="袋码" width="160" />
            <el-table-column prop="linenTypeName" label="布草类型" width="120" />
            <el-table-column prop="roomNo" label="来源房间" width="100" />
            <el-table-column prop="expectedQuantity" label="应收数量" width="100" align="center" />
            <el-table-column label="实收数量" width="140">
              <template #default="{ row }">
                <el-input-number v-model="row.receivedQuantity" :min="0" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="备注" width="200">
              <template #default="{ row }">
                <el-input v-model="row.remark" placeholder="备注（选填）" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleCheckBag(row)">验收</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!pendingReturnBags || pendingReturnBags.length === 0" description="暂无待回库布草袋" />
        </div>
      </el-col>

      <!-- 房间补货 -->
      <el-col :span="10">
        <div class="card">
          <div class="card-header">
            <h3>房间补货</h3>
          </div>
          <el-form :model="restockForm" label-width="80px" label-position="right">
            <el-form-item label="房间">
              <el-select v-model="restockForm.roomId" placeholder="请选择房间" style="width: 100%">
                <el-option v-for="room in rooms" :key="room.id" :label="room.roomNo" :value="room.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="布草类型">
              <el-select v-model="restockForm.linenTypeId" placeholder="请选择布草类型" style="width: 100%" @change="onLinenTypeChange">
                <el-option v-for="lt in linenTypes" :key="lt.id" :label="lt.name" :value="lt.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="库存可用">
              <span style="color: #67C23A; font-weight: 600;">{{ availableStock }}</span> 件
            </el-form-item>
            <el-form-item label="补货数量">
              <el-input-number v-model="restockForm.quantity" :min="1" :max="availableStock || 100" size="small" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="restockForm.remark" type="textarea" :rows="2" placeholder="备注（选填）" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleRestock" :disabled="!canRestock">确认补货</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>

    <!-- 补货记录列表 -->
    <div class="card">
      <div class="card-header">
        <h3>最近补货记录</h3>
      </div>
      <el-table :data="restockRecords" stripe>
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="linenTypeName" label="布草类型" width="120" />
        <el-table-column prop="quantity" label="数量" width="100" align="center" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!restockRecords || restockRecords.length === 0" description="暂无补货记录" />
    </div>

    <!-- 安全库存设置对话框 -->
    <el-dialog v-model="safetyStockVisible" title="设置安全库存" width="500px">
      <el-form label-width="100px">
        <el-form-item v-for="stock in stocks" :key="stock.id" :label="stock.linenTypeName">
          <el-input-number v-model="stock.safetyStock" :min="0" size="small" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="safetyStockVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSafetyStock">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'
import {
  listLinenBags,
  listLinenBagsByStatus,
  warehouseCheckLinenBag,
  listLinenStocks,
  updateSafetyStock,
  restockLinen,
  listRoomRestocks,
  restockRoom,
  listRooms,
  listLinenTypes
} from '@/api'

const userStore = useUserStore()

const pendingReturnBags = ref([])
const stocks = ref([])
const restockRecords = ref([])
const rooms = ref([])
const linenTypes = ref([])
const safetyStockVisible = ref(false)

const restockForm = ref({
  roomId: null,
  linenTypeId: null,
  quantity: 1,
  remark: ''
})

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日 dddd'))

// 当前选中布草类型的可用库存
const availableStock = computed(() => {
  if (!restockForm.value.linenTypeId) return 0
  const stock = stocks.value.find((s) => s.linenTypeId === restockForm.value.linenTypeId)
  return stock?.inWarehouseQuantity || 0
})

// 是否可以提交补货
const canRestock = computed(() => {
  return (
    restockForm.value.roomId &&
    restockForm.value.linenTypeId &&
    restockForm.value.quantity > 0 &&
    restockForm.value.quantity <= availableStock.value
  )
})

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '-'
}

// 布草类型变化时重置数量
function onLinenTypeChange() {
  restockForm.value.quantity = 1
}

// 验收布草袋回库
async function handleCheckBag(row) {
  if (!row.receivedQuantity && row.receivedQuantity !== 0) {
    ElMessage.warning('请填写实收数量')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认验收袋码【${row.bagCode}】，实收数量：${row.receivedQuantity}，是否继续？`,
      '验收确认',
      { type: 'warning' }
    )
    await warehouseCheckLinenBag(row.id, {
      receivedQuantity: row.receivedQuantity,
      remark: row.remark
    })
    ElMessage.success('验收成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '验收失败')
    }
  }
}

// 提交房间补货
async function handleRestock() {
  if (!canRestock.value) return
  try {
    await ElMessageBox.confirm(
      `确认向房间补货，数量：${restockForm.value.quantity}，是否继续？`,
      '补货确认',
      { type: 'warning' }
    )
    await restockRoom(restockForm.value)
    ElMessage.success('补货成功')
    restockForm.value = {
      roomId: null,
      linenTypeId: null,
      quantity: 1,
      remark: ''
    }
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '补货失败')
    }
  }
}

// 打开安全库存设置
function openSafetyStockDialog() {
  safetyStockVisible.value = true
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
    safetyStockVisible.value = false
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  }
}

// 加载所有数据
async function loadData() {
  try {
    const [bags, stockList, restocks, roomList, linenTypeList] = await Promise.all([
      listLinenBagsByStatus('待回库').catch(() => []),
      listLinenStocks().catch(() => []),
      listRoomRestocks().catch(() => []),
      listRooms().catch(() => []),
      listLinenTypes().catch(() => [])
    ])
    pendingReturnBags.value = (Array.isArray(bags) ? bags : []).map((b) => ({
      ...b,
      receivedQuantity: b.expectedQuantity || 0,
      remark: ''
    }))
    stocks.value = Array.isArray(stockList) ? stockList : []
    restockRecords.value = (Array.isArray(restocks) ? restocks : []).slice(0, 10)
    rooms.value = Array.isArray(roomList) ? roomList : []
    linenTypes.value = Array.isArray(linenTypeList) ? linenTypeList : []
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

.stock-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  border: 1px solid #ebeef5;
  margin-bottom: 16px;
  transition: all 0.2s;

  &.warning {
    border-color: #f56c6c;
    background: #fef0f0;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
  }

  .stock-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }

  .stock-main {
    display: flex;
    align-items: baseline;
    gap: 6px;
    margin-bottom: 12px;

    .stock-num {
      font-size: 32px;
      font-weight: 700;
      color: #409eff;

      &.danger {
        color: #f56c6c;
      }
    }

    .stock-unit {
      font-size: 13px;
      color: #909399;
    }
  }

  .stock-details {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
    border-top: 1px solid #ebeef5;
    padding-top: 12px;

    .stock-item {
      display: flex;
      justify-content: space-between;
      font-size: 13px;

      .label {
        color: #909399;
      }

      .value {
        color: #303133;
        font-weight: 500;

        &.danger {
          color: #f56c6c;
        }
      }
    }
  }

  .stock-warning {
    margin-top: 10px;
    color: #f56c6c;
    font-size: 12px;
    text-align: center;
  }
}
</style>
