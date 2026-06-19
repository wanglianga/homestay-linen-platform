<template>
  <div class="page-container">
    <div class="page-header">
      <h2>房间管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增房间</el-button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-select v-model="filter.floor" placeholder="按楼层筛选" clearable style="width: 160px">
          <el-option v-for="f in floorList" :key="f" :label="f + '楼'" :value="f" />
        </el-select>
        <el-select v-model="filter.status" placeholder="按状态筛选" clearable style="width: 160px">
          <el-option label="空闲" value="空闲" />
          <el-option label="占用" value="占用" />
          <el-option label="清扫中" value="清扫中" />
          <el-option label="待清洁" value="待清洁" />
        </el-select>
        <el-button type="primary" @click="loadRooms">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>

      <el-table :data="filteredRooms" stripe border>
        <el-table-column prop="roomNo" label="房号" width="120" />
        <el-table-column prop="floor" label="楼层" width="100">
          <template #default="{ row }">{{ row.floor }}楼</template>
        </el-table-column>
        <el-table-column prop="roomType" label="类型" width="140" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑房间' : '新增房间'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="房号" prop="roomNo">
          <el-input v-model="form.roomNo" placeholder="请输入房号" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="1" :max="99" style="width: 100%" />
        </el-form-item>
        <el-form-item label="类型" prop="roomType">
          <el-select v-model="form.roomType" placeholder="请选择房间类型" style="width: 100%">
            <el-option label="大床房" value="大床房" />
            <el-option label="双床房" value="双床房" />
            <el-option label="豪华大床房" value="豪华大床房" />
            <el-option label="豪华双床房" value="豪华双床房" />
            <el-option label="套房" value="套房" />
            <el-option label="总统套房" value="总统套房" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="空闲" value="空闲" />
            <el-option label="占用" value="占用" />
            <el-option label="清扫中" value="清扫中" />
            <el-option label="待清洁" value="待清洁" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { listRooms, createRoom, updateRoom, deleteRoom } from '@/api'

const rooms = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const filter = reactive({
  floor: null,
  status: ''
})

const form = reactive({
  id: null,
  roomNo: '',
  floor: 1,
  roomType: '',
  status: '空闲',
  remark: ''
})

const rules = {
  roomNo: [{ required: true, message: '请输入房号', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择房间类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const floorList = computed(() => {
  const floors = [...new Set(rooms.value.map((r) => r.floor))]
  return floors.sort((a, b) => a - b)
})

const filteredRooms = computed(() => {
  return rooms.value.filter((r) => {
    if (filter.floor != null && r.floor !== filter.floor) return false
    if (filter.status && r.status !== filter.status) return false
    return true
  })
})

function statusTagType(status) {
  const map = {
    空闲: 'success',
    占用: 'danger',
    清扫中: 'warning',
    待清洁: 'info'
  }
  return map[status] || ''
}

async function loadRooms() {
  try {
    const data = await listRooms()
    rooms.value = Array.isArray(data) ? data : []
  } catch (e) {
    rooms.value = []
  }
}

function resetFilter() {
  filter.floor = null
  filter.status = ''
}

function resetForm() {
  form.id = null
  form.roomNo = ''
  form.floor = 1
  form.roomType = ''
  form.status = '空闲'
  form.remark = ''
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
  ElMessageBox.confirm(`确定删除房间 ${row.roomNo} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteRoom(row.id)
        ElMessage.success('删除成功')
        loadRooms()
      } catch (e) {}
    })
    .catch(() => {})
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await updateRoom(form)
          ElMessage.success('编辑成功')
        } else {
          await createRoom(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadRooms()
      } catch (e) {
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  loadRooms()
})
</script>

<style scoped lang="scss">
</style>