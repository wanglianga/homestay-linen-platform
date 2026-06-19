<template>
  <div class="page-container">
    <div class="page-header">
      <h2>布草类型管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增类型</el-button>
    </div>

    <div class="card">
      <el-table :data="linenTypes" stripe border>
        <el-table-column prop="name" label="类型名称" width="160" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="unitPrice" label="单价(元)" width="120">
          <template #default="{ row }">¥{{ row.unitPrice?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="washCost" label="洗涤成本(元)" width="140">
          <template #default="{ row }">¥{{ row.washCost?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="standardWeight" label="标准重量(g)" width="130" />
        <el-table-column prop="perRoomQuantity" label="每房标配数量" width="130" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!linenTypes || linenTypes.length === 0" description="暂无布草类型" />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑布草类型' : '新增布草类型'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="洗涤成本" prop="washCost">
          <el-input-number v-model="form.washCost" :min="0" :precision="2" :step="0.5" style="width: 100%" />
        </el-form-item>
        <el-form-item label="标准重量(g)" prop="standardWeight">
          <el-input-number v-model="form.standardWeight" :min="0" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="每房标配数量" prop="perRoomQuantity">
          <el-input-number v-model="form.perRoomQuantity" :min="0" :step="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { listLinenTypes, createLinenType, updateLinenType, deleteLinenType } from '@/api'

const linenTypes = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  description: '',
  unitPrice: 0,
  washCost: 0,
  standardWeight: 0,
  perRoomQuantity: 0
})

const rules = {
  name: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  washCost: [{ required: true, message: '请输入洗涤成本', trigger: 'blur' }],
  standardWeight: [{ required: true, message: '请输入标准重量', trigger: 'blur' }],
  perRoomQuantity: [{ required: true, message: '请输入每房标配数量', trigger: 'blur' }]
}

function resetForm() {
  form.id = null
  form.name = ''
  form.description = ''
  form.unitPrice = 0
  form.washCost = 0
  form.standardWeight = 0
  form.perRoomQuantity = 0
}

async function loadLinenTypes() {
  try {
    const data = await listLinenTypes()
    linenTypes.value = Array.isArray(data) ? data : []
  } catch (e) {
    linenTypes.value = []
  }
}

function handleAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除布草类型"${row.name}"吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteLinenType(row.id)
      ElMessage.success('删除成功')
      loadLinenTypes()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value) {
        await updateLinenType(form)
        ElMessage.success('编辑成功')
      } else {
        await createLinenType(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadLinenTypes()
    } catch (e) {
      ElMessage.error(isEdit.value ? '编辑失败' : '新增失败')
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadLinenTypes()
})
</script>

<style scoped lang="scss">
</style>
