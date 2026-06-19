<template>
  <div class="page-container">
    <div class="page-header">
      <h2>报损管理</h2>
      <el-button type="primary" :icon="Plus" @click="openCreateDialog">创建报损单</el-button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-select v-model="filter.status" placeholder="按状态筛选" clearable style="width: 160px">
          <el-option label="待审批" value="待审批" />
          <el-option label="已审批" value="已审批" />
        </el-select>
        <el-select v-model="filter.liabilityType" placeholder="按责任类型筛选" clearable style="width: 160px">
          <el-option label="客责" value="客责" />
          <el-option label="厂责" value="厂责" />
          <el-option label="损耗" value="损耗" />
        </el-select>
        <el-button type="primary" @click="loadReports">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>

      <el-table :data="filteredReports" stripe border>
        <el-table-column prop="reportNo" label="报损单号" width="160" />
        <el-table-column label="关联袋码/房号" width="140">
          <template #default="{ row }">
            {{ row.bagCode || row.roomNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="linenTypeName" label="布草类型" width="120" />
        <el-table-column prop="quantity" label="报损数量" width="100" />
        <el-table-column prop="unitPrice" label="单价(元)" width="100">
          <template #default="{ row }">¥{{ row.unitPrice?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column label="总金额(元)" width="110">
          <template #default="{ row }">¥{{ (row.quantity * row.unitPrice).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="liabilityType" label="责任类型" width="100">
          <template #default="{ row }">
            <el-tag :type="liabilityTagType(row.liabilityType)" size="small">{{ row.liabilityType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '待审批' ? 'warning' : 'success'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="createTime" label="时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === '待审批' && isManager"
              type="success"
              link
              :icon="Check"
              @click="openApproveDialog(row)"
            >审批</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!filteredReports || filteredReports.length === 0" description="暂无报损记录" />
    </div>

    <el-dialog v-model="createVisible" title="创建报损单" width="520px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="110px">
        <el-form-item label="布草类型" prop="linenTypeId">
          <el-select v-model="createForm.linenTypeId" placeholder="请选择布草类型" style="width: 100%" filterable @change="onLinenTypeChange">
            <el-option v-for="item in linenTypes" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="createForm.quantity" :min="1" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单价(元)">
          <el-input :model-value="createForm.unitPrice" disabled />
        </el-form-item>
        <el-form-item label="责任类型" prop="liabilityType">
          <el-select v-model="createForm.liabilityType" placeholder="请选择责任类型" style="width: 100%">
            <el-option label="客责" value="客责" />
            <el-option label="厂责" value="厂责" />
            <el-option label="损耗" value="损耗" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联袋码">
          <el-select v-model="createForm.bagCode" placeholder="请选择袋码（选填）" style="width: 100%" clearable filterable>
            <el-option v-for="bag in linenBags" :key="bag.id" :label="bag.bagCode" :value="bag.bagCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联房号">
          <el-input v-model="createForm.roomNo" placeholder="请输入房号（选填）" />
        </el-form-item>
        <el-form-item label="客人姓名">
          <el-input v-model="createForm.guestName" placeholder="请输入客人姓名（选填）" />
        </el-form-item>
        <el-form-item label="照片">
          <el-upload
            v-model:file-list="createForm.photoList"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="3"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="approveVisible" title="审批报损单" width="420px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="报损单号">{{ approveData.reportNo }}</el-descriptions-item>
        <el-descriptions-item label="布草类型">{{ approveData.linenTypeName }}</el-descriptions-item>
        <el-descriptions-item label="报损数量">{{ approveData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="责任类型">{{ approveData.liabilityType }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ (approveData.quantity * approveData.unitPrice).toFixed(2) }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="approveForm" :rules="approveRules" ref="approveFormRef" label-width="90px" style="margin-top: 20px">
        <el-form-item label="审批意见" prop="approveRemark">
          <el-input v-model="approveForm.approveRemark" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitApprove">通过审批</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Check } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import {
  listDamageReports,
  listDamageByStatus,
  createDamageReport,
  approveDamageReport,
  listLinenTypes,
  listLinenBags
} from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isManager = computed(() => userStore.userRole === 'manager')

const reports = ref([])
const linenTypes = ref([])
const linenBags = ref([])
const createVisible = ref(false)
const approveVisible = ref(false)
const submitting = ref(false)
const createFormRef = ref(null)
const approveFormRef = ref(null)

const filter = reactive({
  status: '',
  liabilityType: ''
})

const createForm = reactive({
  linenTypeId: null,
  quantity: 1,
  unitPrice: 0,
  liabilityType: '',
  bagCode: '',
  roomNo: '',
  guestName: '',
  photoList: [],
  remark: ''
})

const createRules = {
  linenTypeId: [{ required: true, message: '请选择布草类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  liabilityType: [{ required: true, message: '请选择责任类型', trigger: 'change' }]
}

const approveData = reactive({
  id: null,
  reportNo: '',
  linenTypeName: '',
  quantity: 0,
  unitPrice: 0,
  liabilityType: ''
})

const approveForm = reactive({
  approveRemark: ''
})

const approveRules = {
  approveRemark: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

const filteredReports = computed(() => {
  return reports.value.filter((r) => {
    if (filter.status && r.status !== filter.status) return false
    if (filter.liabilityType && r.liabilityType !== filter.liabilityType) return false
    return true
  })
})

function liabilityTagType(type) {
  const map = {
    客责: 'danger',
    厂责: 'warning',
    损耗: 'info'
  }
  return map[type] || ''
}

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '-'
}

async function loadReports() {
  try {
    let data
    if (filter.status) {
      data = await listDamageByStatus(filter.status)
    } else {
      data = await listDamageReports()
    }
    reports.value = Array.isArray(data) ? data : []
  } catch (e) {
    reports.value = []
  }
}

async function loadLinenTypesData() {
  try {
    const data = await listLinenTypes()
    linenTypes.value = Array.isArray(data) ? data : []
  } catch (e) {
    linenTypes.value = []
  }
}

async function loadLinenBagsData() {
  try {
    const data = await listLinenBags({})
    linenBags.value = Array.isArray(data) ? data : []
  } catch (e) {
    linenBags.value = []
  }
}

function resetFilter() {
  filter.status = ''
  filter.liabilityType = ''
}

function onLinenTypeChange(id) {
  const item = linenTypes.value.find((t) => t.id === id)
  if (item) {
    createForm.unitPrice = item.unitPrice || 0
  }
}

function openCreateDialog() {
  createForm.linenTypeId = null
  createForm.quantity = 1
  createForm.unitPrice = 0
  createForm.liabilityType = ''
  createForm.bagCode = ''
  createForm.roomNo = ''
  createForm.guestName = ''
  createForm.photoList = []
  createForm.remark = ''
  createVisible.value = true
}

async function submitCreate() {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await createDamageReport({
        linenTypeId: createForm.linenTypeId,
        quantity: createForm.quantity,
        liabilityType: createForm.liabilityType,
        bagCode: createForm.bagCode || undefined,
        roomNo: createForm.roomNo || undefined,
        guestName: createForm.guestName || undefined,
        remark: createForm.remark || undefined
      })
      ElMessage.success('报损单创建成功')
      createVisible.value = false
      loadReports()
    } catch (e) {
      ElMessage.error('创建失败')
    } finally {
      submitting.value = false
    }
  })
}

function openApproveDialog(row) {
  approveData.id = row.id
  approveData.reportNo = row.reportNo
  approveData.linenTypeName = row.linenTypeName
  approveData.quantity = row.quantity
  approveData.unitPrice = row.unitPrice
  approveData.liabilityType = row.liabilityType
  approveForm.approveRemark = ''
  approveVisible.value = true
}

async function submitApprove() {
  if (!approveFormRef.value) return
  await approveFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await approveDamageReport(approveData.id, {
        approveRemark: approveForm.approveRemark
      })
      ElMessage.success('审批通过')
      approveVisible.value = false
      loadReports()
    } catch (e) {
      ElMessage.error('审批失败')
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadReports()
  loadLinenTypesData()
  loadLinenBagsData()
})
</script>

<style scoped lang="scss">
</style>
