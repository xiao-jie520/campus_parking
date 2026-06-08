<template>
  <div class="page">
    <div class="page-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input
            v-model="searchPlate"
            placeholder="输入车牌号搜索"
            clearable
            style="width: 260px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          添加车辆
        </el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading" class="styled-table">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="plateNumber" label="车牌号" width="140">
          <template #default="{ row }">
            <span class="plate-badge">{{ row.plateNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="车主姓名" width="110" />
        <el-table-column prop="ownerPhone" label="联系电话" width="140" />
        <el-table-column prop="vehicleType" label="车辆类型" width="110">
          <template #default="{ row }">
            <el-tag
              size="small"
              :type="row.vehicleType === 'INTERNAL' ? 'success' : 'info'"
              effect="light"
              round
            >
              {{ row.vehicleType === 'INTERNAL' ? '内部车辆' : '临时车辆' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <div class="status-indicator" :class="row.status === 1 ? 'status-ok' : 'status-blocked'">
              <span class="status-dot" />
              {{ row.status === 1 ? '正常' : '黑名单' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :total="page.total"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialog.visible"
      :title="dialog.isEdit ? '编辑车辆' : '添加车辆'"
      width="500px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="90px">
        <el-form-item label="车牌号" prop="plateNumber">
          <el-input v-model="dialog.form.plateNumber" placeholder="如：京A12345" />
        </el-form-item>
        <el-form-item label="车主姓名" prop="ownerName">
          <el-input v-model="dialog.form.ownerName" placeholder="请输入车主姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="ownerPhone">
          <el-input v-model="dialog.form.ownerPhone" placeholder="请输入11位手机号" />
        </el-form-item>
        <el-form-item label="车辆类型" prop="vehicleType">
          <el-select v-model="dialog.form.vehicleType" placeholder="请选择车辆类型" style="width: 100%">
            <el-option label="内部车辆" value="INTERNAL" />
            <el-option label="临时车辆" value="TEMPORARY" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialog.isEdit" label="状态" prop="status">
          <el-switch
            v-model="dialog.form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="正常"
            inactive-text="黑名单"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="dialog.submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVehicleList, addVehicle, updateVehicle, deleteVehicle } from '../api/vehicle.js'

const loading = ref(false)
const tableData = ref([])
const formRef = ref(null)
const searchPlate = ref('')

const page = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const dialog = reactive({
  visible: false,
  isEdit: false,
  submitting: false,
  form: {
    id: null,
    plateNumber: '',
    ownerName: '',
    ownerPhone: '',
    vehicleType: '',
    status: 1,
  },
})

const rules = {
  plateNumber: [
    { required: true, message: '请输入车牌号', trigger: 'blur' },
    { pattern: /^[一-龥][A-Z][A-Za-z0-9]{5}$/, message: '车牌号格式不正确，如：京A12345', trigger: 'blur' },
  ],
  ownerName: [
    { required: true, message: '请输入车主姓名', trigger: 'blur' },
  ],
  ownerPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' },
  ],
  vehicleType: [
    { required: true, message: '请选择车辆类型', trigger: 'change' },
  ],
}

onMounted(() => {
  fetchList()
})

async function fetchList() {
  loading.value = true
  try {
    const params = {
      currentPage: page.current,
      pageSize: page.size,
    }
    if (searchPlate.value.trim()) {
      params.plateNumber = searchPlate.value.trim()
    }
    const res = await getVehicleList(params)
    tableData.value = res.data.records
    page.total = res.data.total
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.current = 1
  fetchList()
}

function openAddDialog() {
  dialog.isEdit = false
  dialog.visible = true
}

function openEditDialog(row) {
  dialog.isEdit = true
  dialog.form = {
    id: row.id,
    plateNumber: row.plateNumber,
    ownerName: row.ownerName,
    ownerPhone: row.ownerPhone,
    vehicleType: row.vehicleType,
    status: row.status,
  }
  dialog.visible = true
}

function resetForm() {
  dialog.form = {
    id: null,
    plateNumber: '',
    ownerName: '',
    ownerPhone: '',
    vehicleType: '',
    status: 1,
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  dialog.submitting = true
  try {
    if (dialog.isEdit) {
      await updateVehicle(dialog.form)
      ElMessage.success('修改成功')
    } else {
      await addVehicle(dialog.form)
      ElMessage.success('添加成功')
    }
    dialog.visible = false
    fetchList()
  } catch {
    // handled by interceptor
  } finally {
    dialog.submitting = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除车辆「${row.plateNumber}」吗？`, '确认删除', {
    type: 'warning',
  })
    .then(async () => {
      await deleteVehicle(row.id)
      ElMessage.success('删除成功')
      fetchList()
    })
    .catch(() => {})
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar-left {
  display: flex;
  gap: 10px;
}

.plate-badge {
  display: inline-block;
  padding: 3px 10px;
  background: linear-gradient(135deg, #ecf5ff, #d9ecff);
  color: #409eff;
  font-weight: 700;
  font-family: 'SF Mono', 'Fira Code', monospace;
  font-size: 13px;
  border-radius: 6px;
  letter-spacing: 0.5px;
}

.status-indicator {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-ok {
  color: #67c23a;
}

.status-ok .status-dot {
  background: #67c23a;
  box-shadow: 0 0 6px rgba(103, 194, 58, 0.4);
}

.status-blocked {
  color: #f56c6c;
}

.status-blocked .status-dot {
  background: #f56c6c;
  box-shadow: 0 0 6px rgba(245, 108, 108, 0.4);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
