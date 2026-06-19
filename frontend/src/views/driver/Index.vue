<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2>司机工作台</h2>
        <p class="welcome-sub">
          当前司机：
          <el-tag type="warning" size="small">{{ userStore.userName || '未登录' }}</el-tag>
          &nbsp;|&nbsp;
          {{ currentDate }}
        </p>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="8">
        <div class="stat-card warning">
          <div class="stat-label">待取件袋数</div>
          <div class="stat-value">{{ pendingPickupList.length }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card primary">
          <div class="stat-label">配送中袋数</div>
          <div class="stat-value">{{ deliveringList.length }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card success">
          <div class="stat-label">已送达袋数</div>
          <div class="stat-value">{{ deliveredList.length }}</div>
        </div>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="待取件" name="pending">
        <div class="filter-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索袋码/房号"
            clearable
            style="width: 240px"
            :prefix-icon="Search"
          />
          <el-button type="primary" @click="loadPendingPickup">刷新列表</el-button>
          <el-button
            type="success"
            :disabled="selectedBags.length === 0"
            @click="openBatchWeightDialog"
          >
            批量取件称重 ({{ selectedBags.length }})
          </el-button>
        </div>

        <el-table
          :data="filteredPendingList"
          stripe
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="bagCode" label="袋码" width="180" />
          <el-table-column prop="roomNo" label="房号" width="100" />
          <el-table-column prop="linenTypeName" label="布草类型" width="120" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="cleanerName" label="保洁员" width="100" />
          <el-table-column prop="baggingTime" label="装袋时间" width="170">
            <template #default="{ row }">{{ formatTime(row.baggingTime) }}</template>
          </el-table-column>
          <el-table-column prop="hasStain" label="是否有污渍" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.hasStain" type="danger" size="small">有污渍</el-tag>
              <el-tag v-else type="success" size="small">正常</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openSingleWeightDialog(row)">取件称重</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="filteredPendingList.length === 0" description="暂无待取件布草袋" />
      </el-tab-pane>

      <el-tab-pane label="配送中" name="delivering">
        <div class="table-toolbar">
          <el-button type="primary" @click="loadDelivering">刷新列表</el-button>
        </div>
        <el-table :data="deliveringList" stripe border>
          <el-table-column prop="bagCode" label="袋码" width="180" />
          <el-table-column prop="roomNo" label="房号" width="100" />
          <el-table-column prop="linenTypeName" label="布草类型" width="120" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="pickupWeight" label="取件重量(克)" width="120" />
          <el-table-column prop="pickupTime" label="取件时间" width="170">
            <template #default="{ row }">{{ formatTime(row.pickupTime) }}</template>
          </el-table-column>
          <el-table-column prop="driverName" label="司机" width="100" />
        </el-table>
        <el-empty v-if="deliveringList.length === 0" description="暂无配送中布草袋" />
      </el-tab-pane>

      <el-tab-pane label="已送达" name="delivered">
        <div class="table-toolbar">
          <el-button type="primary" @click="loadDelivered">刷新列表</el-button>
        </div>
        <el-table :data="deliveredList" stripe border>
          <el-table-column prop="bagCode" label="袋码" width="180" />
          <el-table-column prop="roomNo" label="房号" width="100" />
          <el-table-column prop="linenTypeName" label="布草类型" width="120" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="pickupWeight" label="取件重量(克)" width="120" />
          <el-table-column prop="factoryCheckTime" label="送达时间" width="170">
            <template #default="{ row }">{{ formatTime(row.factoryCheckTime) }}</template>
          </el-table-column>
          <el-table-column prop="driverName" label="司机" width="100" />
        </el-table>
        <el-empty v-if="deliveredList.length === 0" description="暂无已送达记录" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="singleWeightDialogVisible" title="取件称重" width="420px">
      <el-form :model="singleWeightForm" label-width="100px">
        <el-form-item label="袋码">
          <span>{{ singleWeightForm.bagCode }}</span>
        </el-form-item>
        <el-form-item label="房号">
          <span>{{ singleWeightForm.roomNo }}</span>
        </el-form-item>
        <el-form-item label="布草类型">
          <span>{{ singleWeightForm.linenTypeName }}</span>
        </el-form-item>
        <el-form-item label="数量">
          <span>{{ singleWeightForm.quantity }}</span>
        </el-form-item>
        <el-form-item label="取件重量(克)" required>
          <el-input-number v-model="singleWeightForm.pickupWeight" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="singleWeightDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitSinglePickup">确认取件</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchWeightDialogVisible" title="批量取件称重" width="460px">
      <p style="margin-bottom: 16px; color: #606266">
        已选择 <b style="color: #409EFF">{{ selectedBags.length }}</b> 个布草袋
      </p>
      <el-radio-group v-model="batchWeightMode" style="margin-bottom: 16px">
        <el-radio value="uniform">统一填写重量</el-radio>
        <el-radio value="individual">逐个填写重量</el-radio>
      </el-radio-group>

      <div v-if="batchWeightMode === 'uniform'" style="margin-bottom: 16px">
        <el-form label-width="120px">
          <el-form-item label="统一重量(克)" required>
            <el-input-number v-model="uniformWeight" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </el-form>
      </div>

      <div v-else>
        <el-table :data="selectedBags" size="small" border max-height="280">
          <el-table-column prop="bagCode" label="袋码" width="160" />
          <el-table-column prop="roomNo" label="房号" width="80" />
          <el-table-column label="重量(克)" width="160">
            <template #default="{ row }">
              <el-input-number
                v-model="row._pickupWeight"
                :min="0"
                :precision="2"
                size="small"
                style="width: 100%"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="batchWeightDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitBatchPickup">确认取件</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import {
  listLinenBags,
  listLinenBagsByStatus,
  pickupLinenBag,
  getLinenBagByCode
} from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const activeTab = ref('pending')
const searchKeyword = ref('')
const pendingPickupList = ref([])
const deliveringList = ref([])
const deliveredList = ref([])
const selectedBags = ref([])
const submitting = ref(false)

const singleWeightDialogVisible = ref(false)
const singleWeightForm = reactive({
  id: null,
  bagCode: '',
  roomNo: '',
  linenTypeName: '',
  quantity: 0,
  pickupWeight: 0
})

const batchWeightDialogVisible = ref(false)
const batchWeightMode = ref('uniform')
const uniformWeight = ref(0)

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日 dddd'))

const filteredPendingList = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  if (!kw) return pendingPickupList.value
  return pendingPickupList.value.filter(
    (b) =>
      (b.bagCode && b.bagCode.toLowerCase().includes(kw)) ||
      (b.roomNo && b.roomNo.toLowerCase().includes(kw))
  )
})

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '-'
}

function handleSelectionChange(rows) {
  selectedBags.value = rows.map((r) => ({ ...r, _pickupWeight: r._pickupWeight || 0 }))
}

async function loadPendingPickup() {
  try {
    const data = await listLinenBagsByStatus('待取件')
    pendingPickupList.value = Array.isArray(data) ? data : []
  } catch (e) {
    pendingPickupList.value = []
  }
}

async function loadDelivering() {
  try {
    const data = await listLinenBagsByStatus('配送中')
    deliveringList.value = Array.isArray(data) ? data : []
  } catch (e) {
    deliveringList.value = []
  }
}

async function loadDelivered() {
  try {
    const data = await listLinenBags({})
    const all = Array.isArray(data) ? data : []
    deliveredList.value = all.filter((b) => b.status === '洗涤中' || b.status === '待洗涤' || b.status === '已完成' || b.status === '待回库')
  } catch (e) {
    deliveredList.value = []
  }
}

function openSingleWeightDialog(row) {
  Object.assign(singleWeightForm, {
    id: row.id,
    bagCode: row.bagCode,
    roomNo: row.roomNo,
    linenTypeName: row.linenTypeName,
    quantity: row.quantity,
    pickupWeight: 0
  })
  singleWeightDialogVisible.value = true
}

async function submitSinglePickup() {
  if (!singleWeightForm.pickupWeight || singleWeightForm.pickupWeight <= 0) {
    ElMessage.warning('请填写取件重量')
    return
  }
  submitting.value = true
  try {
    await pickupLinenBag(singleWeightForm.id, {
      pickupWeight: singleWeightForm.pickupWeight,
      driverId: userStore.userInfo?.id
    })
    ElMessage.success('取件成功')
    singleWeightDialogVisible.value = false
    loadPendingPickup()
    loadDelivering()
  } catch (e) {
    ElMessage.error(e?.message || '取件失败')
  } finally {
    submitting.value = false
  }
}

function openBatchWeightDialog() {
  batchWeightMode.value = 'uniform'
  uniformWeight.value = 0
  selectedBags.value = selectedBags.value.map((r) => ({ ...r, _pickupWeight: 0 }))
  batchWeightDialogVisible.value = true
}

async function submitBatchPickup() {
  if (selectedBags.value.length === 0) {
    ElMessage.warning('未选择布草袋')
    return
  }

  if (batchWeightMode.value === 'uniform') {
    if (!uniformWeight.value || uniformWeight.value <= 0) {
      ElMessage.warning('请填写统一重量')
      return
    }
  } else {
    const invalid = selectedBags.value.some((b) => !b._pickupWeight || b._pickupWeight <= 0)
    if (invalid) {
      ElMessage.warning('请为每个布草袋填写有效重量')
      return
    }
  }

  submitting.value = true
  try {
    const tasks = selectedBags.value.map((bag) => {
      const weight = batchWeightMode.value === 'uniform' ? uniformWeight.value : bag._pickupWeight
      return pickupLinenBag(bag.id, {
        pickupWeight: weight,
        driverId: userStore.userInfo?.id
      })
    })
    await Promise.all(tasks)
    ElMessage.success(`成功取件 ${selectedBags.value.length} 个布草袋`)
    batchWeightDialogVisible.value = false
    selectedBags.value = []
    loadPendingPickup()
    loadDelivering()
  } catch (e) {
    ElMessage.error(e?.message || '批量取件失败')
  } finally {
    submitting.value = false
  }
}

function loadAll() {
  loadPendingPickup()
  loadDelivering()
  loadDelivered()
}

onMounted(() => {
  loadAll()
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

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
