<template>
  <div class="page-container">
    <div class="page-header">
      <h2>成本分摊</h2>
      <div class="header-actions">
        <el-button type="primary" :icon="DocumentAdd" @click="openGenerateDialog">生成日报表</el-button>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="5">
        <div class="stat-card">
          <div class="stat-label">总洗涤成本</div>
          <div class="stat-value">¥{{ summary.totalWashCost?.toFixed(2) || '0.00' }}</div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="stat-card danger">
          <div class="stat-label">总损耗金额</div>
          <div class="stat-value">¥{{ summary.totalDamageAmount?.toFixed(2) || '0.00' }}</div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="stat-card success">
          <div class="stat-label">总客赔收入</div>
          <div class="stat-value">¥{{ summary.totalGuestCompensation?.toFixed(2) || '0.00' }}</div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="stat-card warning">
          <div class="stat-label">总厂赔收入</div>
          <div class="stat-value">¥{{ summary.totalFactoryCompensation?.toFixed(2) || '0.00' }}</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card primary">
          <div class="stat-label">净成本</div>
          <div class="stat-value">¥{{ summary.netCost?.toFixed(2) || '0.00' }}</div>
        </div>
      </el-col>
    </el-row>

    <div class="card">
      <div class="filter-bar">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 280px"
        />
        <el-button type="primary" :icon="Search" @click="loadCosts">查询</el-button>
        <el-button :icon="Refresh" @click="resetFilter">重置</el-button>
      </div>

      <el-table :data="costList" stripe border>
        <el-table-column prop="costDate" label="日期" width="120" />
        <el-table-column prop="linenTypeName" label="布草类型" width="140" />
        <el-table-column prop="washQuantity" label="洗涤数量" width="100" />
        <el-table-column prop="washUnitCost" label="洗涤成本单价" width="130">
          <template #default="{ row }">¥{{ row.washUnitCost?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="washTotalCost" label="洗涤成本总额" width="130">
          <template #default="{ row }">¥{{ row.washTotalCost?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="damageQuantity" label="损耗数量" width="100" />
        <el-table-column prop="damageAmount" label="损耗金额" width="120">
          <template #default="{ row }">¥{{ row.damageAmount?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="guestCompensation" label="客赔收入" width="120">
          <template #default="{ row }">¥{{ row.guestCompensation?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="factoryCompensation" label="厂赔收入" width="120">
          <template #default="{ row }">¥{{ row.factoryCompensation?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="netCost" label="净成本" width="120">
          <template #default="{ row }">
            <span :class="{ 'profit-text': row.netCost < 0 }">¥{{ row.netCost?.toFixed(2) || '0.00' }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!costList || costList.length === 0" description="暂无成本记录" />
    </div>

    <el-dialog v-model="generateVisible" title="生成日报表" width="400px">
      <el-form :model="generateForm" :rules="generateRules" ref="generateFormRef" label-width="90px">
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="generateForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitGenerate">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, DocumentAdd } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { listCostAllocation, generateDailyCost, listAllCosts } from '@/api'

const dateRange = ref([])
const costList = ref([])
const generateVisible = ref(false)
const submitting = ref(false)
const generateFormRef = ref(null)

const generateForm = reactive({
  date: dayjs().format('YYYY-MM-DD')
})

const generateRules = {
  date: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const summary = computed(() => {
  const data = costList.value || []
  const totalWashCost = data.reduce((sum, item) => sum + (item.washTotalCost || 0), 0)
  const totalDamageAmount = data.reduce((sum, item) => sum + (item.damageAmount || 0), 0)
  const totalGuestCompensation = data.reduce((sum, item) => sum + (item.guestCompensation || 0), 0)
  const totalFactoryCompensation = data.reduce((sum, item) => sum + (item.factoryCompensation || 0), 0)
  const netCost = totalWashCost + totalDamageAmount - totalGuestCompensation - totalFactoryCompensation
  return {
    totalWashCost,
    totalDamageAmount,
    totalGuestCompensation,
    totalFactoryCompensation,
    netCost
  }
})

async function loadCosts() {
  try {
    let data
    if (dateRange.value && dateRange.value.length === 2) {
      data = await listCostAllocation(dateRange.value[0], dateRange.value[1])
    } else {
      data = await listAllCosts()
    }
    costList.value = Array.isArray(data) ? data : []
  } catch (e) {
    costList.value = []
  }
}

function resetFilter() {
  dateRange.value = []
  loadCosts()
}

function openGenerateDialog() {
  generateForm.date = dayjs().format('YYYY-MM-DD')
  generateVisible.value = true
}

async function submitGenerate() {
  if (!generateFormRef.value) return
  await generateFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await generateDailyCost(generateForm.date)
      ElMessage.success('成本报表生成成功')
      generateVisible.value = false
      loadCosts()
    } catch (e) {
      ElMessage.error('生成失败')
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadCosts()
})
</script>

<style scoped lang="scss">
.header-actions {
  display: flex;
  gap: 10px;
}

.stats-row {
  margin-bottom: 20px;
}

.profit-text {
  color: #67c23a;
  font-weight: 600;
}
</style>
