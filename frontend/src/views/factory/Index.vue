<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2>洗涤厂工作台</h2>
        <p class="welcome-sub">
          当前操作人：
          <el-tag type="info" size="small">{{ userStore.userName || '未登录' }}</el-tag>
          &nbsp;|&nbsp;
          {{ currentDate }}
        </p>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <div class="stat-card primary">
          <div class="stat-label">待验收袋数</div>
          <div class="stat-value">{{ pendingCheckList.length }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card warning">
          <div class="stat-label">待洗涤批次</div>
          <div class="stat-value">{{ pendingBatches.length }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-label">洗涤中批次</div>
          <div class="stat-value">{{ washingBatches.length }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card success">
          <div class="stat-label">已完成批次</div>
          <div class="stat-value">{{ finishedBatches.length }}</div>
        </div>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="待验收布草袋" name="check">
        <div class="table-toolbar">
          <el-button type="primary" @click="loadPendingCheck">刷新列表</el-button>
        </div>
        <el-table :data="pendingCheckList" stripe border>
          <el-table-column prop="bagCode" label="袋码" width="180" />
          <el-table-column prop="roomNo" label="房号" width="100" />
          <el-table-column prop="linenTypeName" label="布草类型" width="120" />
          <el-table-column prop="quantity" label="应收数量" width="100" />
          <el-table-column prop="pickupWeight" label="取件重量(克)" width="120" />
          <el-table-column prop="driverName" label="司机" width="100" />
          <el-table-column prop="pickupTime" label="取件时间" width="170">
            <template #default="{ row }">{{ formatTime(row.pickupTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openCheckDialog(row)">验收</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="pendingCheckList.length === 0" description="暂无待验收布草袋" />
      </el-tab-pane>

      <el-tab-pane label="洗涤批次管理" name="batch">
        <div class="filter-bar">
          <el-button type="primary" :icon="Plus" @click="openCreateBatchDialog">创建洗涤批次</el-button>
          <el-button @click="loadAllBatches">刷新</el-button>
        </div>

        <el-row :gutter="16">
          <el-col :span="12">
            <div class="card">
              <div class="card-header">
                <h3>待洗涤批次</h3>
              </div>
              <el-table :data="pendingBatches" stripe border size="small">
                <el-table-column prop="batchNo" label="批次号" width="180" />
                <el-table-column prop="bagCount" label="布草袋数" width="100" />
                <el-table-column prop="createTime" label="创建时间" width="170">
                  <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                  <template #default="{ row }">
                    <el-button type="primary" link size="small" @click="openAddBagsDialog(row)">添加布草袋</el-button>
                    <el-button type="success" link size="small" @click="handleStartWash(row)">开始洗涤</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="pendingBatches.length === 0" description="暂无待洗涤批次" />
            </div>
          </el-col>

          <el-col :span="12">
            <div class="card">
              <div class="card-header">
                <h3>洗涤中批次</h3>
              </div>
              <el-table :data="washingBatches" stripe border size="small">
                <el-table-column prop="batchNo" label="批次号" width="180" />
                <el-table-column prop="bagCount" label="布草袋数" width="100" />
                <el-table-column prop="startTime" label="开始时间" width="170">
                  <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                  <template #default="{ row }">
                    <el-button type="warning" link size="small" @click="openFinishDialog(row)">完成洗涤</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="washingBatches.length === 0" description="暂无洗涤中批次" />
            </div>
          </el-col>
        </el-row>

        <div class="card">
          <div class="card-header">
            <h3>已完成批次</h3>
          </div>
          <el-table :data="finishedBatches" stripe border>
            <el-table-column prop="batchNo" label="批次号" width="180" />
            <el-table-column prop="bagCount" label="布草袋数" width="100" />
            <el-table-column prop="returnQuantity" label="返还数量" width="100" />
            <el-table-column prop="lossQuantity" label="损耗数量" width="100" />
            <el-table-column prop="operatorName" label="操作人" width="100" />
            <el-table-column prop="finishTime" label="完成时间" width="170">
              <template #default="{ row }">{{ formatTime(row.finishTime) }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          </el-table>
          <el-empty v-if="finishedBatches.length === 0" description="暂无已完成批次" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="checkDialogVisible" title="验收布草袋" width="460px">
      <el-form :model="checkForm" label-width="100px">
        <el-form-item label="袋码">
          <span>{{ checkForm.bagCode }}</span>
        </el-form-item>
        <el-form-item label="房号">
          <span>{{ checkForm.roomNo }}</span>
        </el-form-item>
        <el-form-item label="布草类型">
          <span>{{ checkForm.linenTypeName }}</span>
        </el-form-item>
        <el-form-item label="应收数量">
          <span>{{ checkForm.quantity }}</span>
        </el-form-item>
        <el-form-item label="实收数量" required>
          <el-input-number v-model="checkForm.receivedQuantity" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="checkForm.remark" type="textarea" :rows="3" placeholder="短少说明等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCheck">确认验收</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="createBatchDialogVisible" title="创建洗涤批次" width="460px">
      <el-form :model="createBatchForm" :rules="createBatchRules" ref="createBatchFormRef" label-width="100px">
        <el-form-item label="批次号" prop="batchNo">
          <el-input v-model="createBatchForm.batchNo" placeholder="自动生成或手动输入" />
        </el-form-item>
        <el-form-item label="操作人">
          <span>{{ userStore.userName }}</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createBatchForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createBatchDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreateBatch">确认创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addBagsDialogVisible" title="添加布草袋到批次" width="720px">
      <p style="margin-bottom: 12px; color: #606266">
        当前批次：<b style="color: #409EFF">{{ currentBatch?.batchNo }}</b>
        &nbsp;|&nbsp;
        从状态为"洗涤中/待洗涤"的布草袋中选择添加
      </p>
      <el-table
        :data="availableBags"
        stripe
        border
        @selection-change="handleBagSelectionChange"
        max-height="360"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="bagCode" label="袋码" width="180" />
        <el-table-column prop="roomNo" label="房号" width="100" />
        <el-table-column prop="linenTypeName" label="布草类型" width="120" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="addBagsDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitting"
          :disabled="selectedBagIds.length === 0"
          @click="submitAddBags"
        >
          确认添加 ({{ selectedBagIds.length }})
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="finishDialogVisible" title="完成洗涤" width="480px">
      <el-form :model="finishForm" label-width="100px">
        <el-form-item label="批次号">
          <span>{{ finishForm.batchNo }}</span>
        </el-form-item>
        <el-form-item label="布草袋数">
          <span>{{ finishForm.bagCount }}</span>
        </el-form-item>
        <el-form-item label="返还数量" required>
          <el-input-number v-model="finishForm.returnQuantity" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="损耗数量" required>
          <el-input-number v-model="finishForm.lossQuantity" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="finishForm.remark" type="textarea" :rows="3" placeholder="损耗说明等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="finishDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitFinish">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import {
  listLinenBags,
  listLinenBagsByStatus,
  factoryCheckLinenBag,
  returnFromFactory,
  listWashBatches,
  listWashBatchesByStatus,
  createWashBatch,
  addBagsToBatch,
  startWashBatch,
  finishWashBatch,
  getWashBatch
} from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const activeTab = ref('check')
const submitting = ref(false)

const pendingCheckList = ref([])
const pendingBatches = ref([])
const washingBatches = ref([])
const finishedBatches = ref([])
const availableBags = ref([])

const checkDialogVisible = ref(false)
const checkForm = reactive({
  id: null,
  bagCode: '',
  roomNo: '',
  linenTypeName: '',
  quantity: 0,
  receivedQuantity: 0,
  remark: ''
})

const createBatchDialogVisible = ref(false)
const createBatchFormRef = ref(null)
const createBatchForm = reactive({
  batchNo: '',
  remark: ''
})
const createBatchRules = {
  batchNo: [{ required: true, message: '请输入批次号', trigger: 'blur' }]
}

const addBagsDialogVisible = ref(false)
const currentBatch = ref(null)
const selectedBagIds = ref([])

const finishDialogVisible = ref(false)
const finishForm = reactive({
  id: null,
  batchNo: '',
  bagCount: 0,
  returnQuantity: 0,
  lossQuantity: 0,
  remark: ''
})

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日 dddd'))

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '-'
}

function generateBatchNo() {
  return 'WB' + dayjs().format('YYYYMMDDHHmmss')
}

async function loadPendingCheck() {
  try {
    const data = await listLinenBagsByStatus('配送中')
    pendingCheckList.value = Array.isArray(data) ? data : []
  } catch (e) {
    pendingCheckList.value = []
  }
}

async function loadAllBatches() {
  try {
    const [pending, washing, finished] = await Promise.all([
      listWashBatchesByStatus('待洗涤').catch(() => []),
      listWashBatchesByStatus('洗涤中').catch(() => []),
      listWashBatchesByStatus('已完成').catch(() => [])
    ])
    pendingBatches.value = Array.isArray(pending) ? pending : []
    washingBatches.value = Array.isArray(washing) ? washing : []
    finishedBatches.value = Array.isArray(finished) ? finished : []
  } catch (e) {
    pendingBatches.value = []
    washingBatches.value = []
    finishedBatches.value = []
  }
}

async function loadAvailableBags() {
  try {
    const data = await listLinenBags({})
    const all = Array.isArray(data) ? data : []
    availableBags.value = all.filter(
      (b) => b.status === '洗涤中' || b.status === '待洗涤'
    )
  } catch (e) {
    availableBags.value = []
  }
}

function openCheckDialog(row) {
  Object.assign(checkForm, {
    id: row.id,
    bagCode: row.bagCode,
    roomNo: row.roomNo,
    linenTypeName: row.linenTypeName,
    quantity: row.quantity,
    receivedQuantity: row.quantity,
    remark: ''
  })
  checkDialogVisible.value = true
}

async function submitCheck() {
  if (checkForm.receivedQuantity == null || checkForm.receivedQuantity < 0) {
    ElMessage.warning('请填写实收数量')
    return
  }
  submitting.value = true
  try {
    await factoryCheckLinenBag(checkForm.id, {
      receivedQuantity: checkForm.receivedQuantity,
      remark: checkForm.remark,
      operatorId: userStore.userInfo?.id
    })
    ElMessage.success('验收成功')
    checkDialogVisible.value = false
    loadPendingCheck()
  } catch (e) {
    ElMessage.error(e?.message || '验收失败')
  } finally {
    submitting.value = false
  }
}

function openCreateBatchDialog() {
  createBatchForm.batchNo = generateBatchNo()
  createBatchForm.remark = ''
  createBatchDialogVisible.value = true
}

async function submitCreateBatch() {
  if (!createBatchForm.batchNo) {
    ElMessage.warning('请输入批次号')
    return
  }
  submitting.value = true
  try {
    await createWashBatch({
      batchNo: createBatchForm.batchNo,
      operatorId: userStore.userInfo?.id,
      remark: createBatchForm.remark
    })
    ElMessage.success('批次创建成功')
    createBatchDialogVisible.value = false
    loadAllBatches()
  } catch (e) {
    ElMessage.error(e?.message || '创建失败')
  } finally {
    submitting.value = false
  }
}

async function openAddBagsDialog(row) {
  currentBatch.value = row
  selectedBagIds.value = []
  addBagsDialogVisible.value = true
  await loadAvailableBags()
}

function handleBagSelectionChange(rows) {
  selectedBagIds.value = rows.map((r) => r.id)
}

async function submitAddBags() {
  if (selectedBagIds.value.length === 0) {
    ElMessage.warning('请选择要添加的布草袋')
    return
  }
  submitting.value = true
  try {
    await addBagsToBatch(currentBatch.value.id, selectedBagIds.value)
    ElMessage.success(`成功添加 ${selectedBagIds.value.length} 个布草袋`)
    addBagsDialogVisible.value = false
    loadAllBatches()
  } catch (e) {
    ElMessage.error(e?.message || '添加失败')
  } finally {
    submitting.value = false
  }
}

async function handleStartWash(row) {
  submitting.value = true
  try {
    await startWashBatch(row.id)
    ElMessage.success('已开始洗涤')
    loadAllBatches()
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

function openFinishDialog(row) {
  Object.assign(finishForm, {
    id: row.id,
    batchNo: row.batchNo,
    bagCount: row.bagCount || 0,
    returnQuantity: row.bagCount || 0,
    lossQuantity: 0,
    remark: ''
  })
  finishDialogVisible.value = true
}

async function submitFinish() {
  if (finishForm.returnQuantity == null || finishForm.returnQuantity < 0) {
    ElMessage.warning('请填写返还数量')
    return
  }
  if (finishForm.lossQuantity == null || finishForm.lossQuantity < 0) {
    ElMessage.warning('请填写损耗数量')
    return
  }
  submitting.value = true
  try {
    await finishWashBatch(finishForm.id, {
      returnQuantity: finishForm.returnQuantity,
      lossQuantity: finishForm.lossQuantity,
      remark: finishForm.remark,
      operatorId: userStore.userInfo?.id
    })
    ElMessage.success('洗涤完成')
    finishDialogVisible.value = false
    loadAllBatches()
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

function loadAll() {
  loadPendingCheck()
  loadAllBatches()
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

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
