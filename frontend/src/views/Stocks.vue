<template>
  <div class="page-container">
    <div class="page-header">
      <h2>库存管理</h2>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="openRestockDialog">仓库补货</el-button>
      </div>
    </div>

    <div class="card">
      <el-table :data="stocks" stripe border :row-class-name="rowClassName">
        <el-table-column prop="linenTypeName" label="布草类型" width="160" />
        <el-table-column prop="totalQuantity" label="总数量" width="100" />
        <el-table-column prop="inWarehouseQuantity" label="仓库在库" width="110">
          <template #default="{ row }">
            <span :class="{ 'warning-text': row.inWarehouseQuantity < row.safetyStock }">
              {{ row.inWarehouseQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="inUseQuantity" label="使用中" width="100" />
        <el-table-column prop="washingQuantity" label="洗涤中" width="100" />
        <el-table-column prop="deliveringQuantity" label="配送中" width="100" />
        <el-table-column prop="damagedQuantity" label="已损耗" width="100" />
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="openSafetyStockDialog(row)">调整安全库存</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!stocks || stocks.length === 0" description="暂无库存数据" />
    </div>

    <el-dialog v-model="safetyStockVisible" title="调整安全库存" width="420px">
      <el-form :model="safetyStockForm" :rules="safetyStockRules" ref="safetyStockFormRef" label-width="100px">
        <el-form-item label="布草类型">
          <el-input v-model="safetyStockForm.linenTypeName" disabled />
        </el-form-item>
        <el-form-item label="当前安全库存">
          <el-input :model-value="safetyStockForm.oldSafetyStock" disabled />
        </el-form-item>
        <el-form-item label="新安全库存" prop="safetyStock">
          <el-input-number v-model="safetyStockForm.safetyStock" :min="0" :step="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="safetyStockVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitSafetyStock">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="restockVisible" title="仓库补货（采购入库）" width="480px">
      <el-form :model="restockForm" :rules="restockRules" ref="restockFormRef" label-width="110px">
        <el-form-item label="布草类型" prop="linenTypeId">
          <el-select v-model="restockForm.linenTypeId" placeholder="请选择布草类型" style="width: 100%" filterable>
            <el-option v-for="item in linenTypes" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="补货数量" prop="quantity">
          <el-input-number v-model="restockForm.quantity" :min="1" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="restockForm.remark" type="textarea" :rows="2" placeholder="请输入备注（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="restockVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitRestock">确定入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Edit } from '@element-plus/icons-vue'
import { listLinenStocks, updateSafetyStock, restockLinen, listLinenTypes } from '@/api'

const stocks = ref([])
const linenTypes = ref([])
const safetyStockVisible = ref(false)
const restockVisible = ref(false)
const submitting = ref(false)
const safetyStockFormRef = ref(null)
const restockFormRef = ref(null)

const safetyStockForm = reactive({
  id: null,
  linenTypeId: null,
  linenTypeName: '',
  oldSafetyStock: 0,
  safetyStock: 0
})

const safetyStockRules = {
  safetyStock: [{ required: true, message: '请输入安全库存', trigger: 'blur' }]
}

const restockForm = reactive({
  linenTypeId: null,
  quantity: 1,
  remark: ''
})

const restockRules = {
  linenTypeId: [{ required: true, message: '请选择布草类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入补货数量', trigger: 'blur' }]
}

function rowClassName({ row }) {
  if (row.inWarehouseQuantity < row.safetyStock) {
    return 'danger-row'
  }
  return ''
}

async function loadStocks() {
  try {
    const data = await listLinenStocks()
    stocks.value = Array.isArray(data) ? data : []
  } catch (e) {
    stocks.value = []
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

function openSafetyStockDialog(row) {
  safetyStockForm.id = row.id
  safetyStockForm.linenTypeId = row.linenTypeId
  safetyStockForm.linenTypeName = row.linenTypeName
  safetyStockForm.oldSafetyStock = row.safetyStock
  safetyStockForm.safetyStock = row.safetyStock
  safetyStockVisible.value = true
}

async function submitSafetyStock() {
  if (!safetyStockFormRef.value) return
  await safetyStockFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await updateSafetyStock({
        id: safetyStockForm.id,
        linenTypeId: safetyStockForm.linenTypeId,
        safetyStock: safetyStockForm.safetyStock
      })
      ElMessage.success('安全库存调整成功')
      safetyStockVisible.value = false
      loadStocks()
    } catch (e) {
      ElMessage.error('调整失败')
    } finally {
      submitting.value = false
    }
  })
}

function openRestockDialog() {
  restockForm.linenTypeId = null
  restockForm.quantity = 1
  restockForm.remark = ''
  restockVisible.value = true
}

async function submitRestock() {
  if (!restockFormRef.value) return
  await restockFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await restockLinen({
        linenTypeId: restockForm.linenTypeId,
        quantity: restockForm.quantity,
        remark: restockForm.remark
      })
      ElMessage.success('补货入库成功')
      restockVisible.value = false
      loadStocks()
    } catch (e) {
      ElMessage.error('补货失败')
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadStocks()
  loadLinenTypes()
})
</script>

<style scoped lang="scss">
.header-actions {
  display: flex;
  gap: 10px;
}

:deep(.danger-row) {
  --el-table-tr-bg-color: #fef0f0;

  td {
    background-color: #fef0f0 !important;
  }
}

.warning-text {
  color: #f56c6c;
  font-weight: 600;
}
</style>
