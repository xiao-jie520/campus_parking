<template>
  <div class="page">
    <div class="page-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input
            v-model="searchUsername"
            placeholder="输入用户名搜索"
            clearable
            style="width: 220px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="searchRole" placeholder="角色筛选" clearable style="width: 140px" @change="handleSearch">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="门卫" value="GUARD" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <el-button type="primary" @click="openAddDialog" round>
          <el-icon><Plus /></el-icon>
          添加用户
        </el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading" class="styled-table">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="用户" min-width="180">
          <template #default="{ row }">
            <div class="user-cell">
              <div class="user-cell-avatar" :style="{ background: getAvatarColor(row.role) }">
                {{ (row.realName || row.username || '?').charAt(0).toUpperCase() }}
              </div>
              <div class="user-cell-info">
                <span class="user-cell-name">{{ row.realName || row.username }}</span>
                <span class="user-cell-id">@{{ row.username }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="roleTagType(row.role)" effect="light" round>
              {{ roleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140">
          <template #default="{ row }">
            <span class="phone-text">{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <div class="status-indicator" :class="row.status === 1 ? 'status-ok' : 'status-disabled'">
              <span class="status-dot" />
              {{ row.status === 1 ? '正常' : '禁用' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
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
      :title="dialog.isEdit ? '编辑用户' : '添加用户'"
      width="500px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="dialog.form.username" :disabled="dialog.isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!dialog.isEdit" label="密码" prop="password">
          <el-input v-model="dialog.form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="dialog.form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="dialog.form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="门卫" value="GUARD" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="dialog.form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="dialog.isEdit" label="状态" prop="status">
          <el-switch
            v-model="dialog.form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="正常"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="dialog.submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser } from '../api/user.js'

const loading = ref(false)
const tableData = ref([])
const formRef = ref(null)
const searchUsername = ref('')
const searchRole = ref('')

const page = reactive({ current: 1, size: 10, total: 0 })

const dialog = reactive({
  visible: false,
  isEdit: false,
  submitting: false,
  form: { id: null, username: '', password: '', realName: '', role: '', phone: '', status: 1 },
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' },
  ],
}

const roleMap = { ADMIN: '管理员', GUARD: '门卫', TEACHER: '教师', STUDENT: '学生' }
const roleTagMap = { ADMIN: 'danger', GUARD: 'warning', TEACHER: '', STUDENT: 'info' }
const avatarColorMap = {
  ADMIN: 'linear-gradient(135deg, #f56c6c, #e8453c)',
  GUARD: 'linear-gradient(135deg, #e6a23c, #d48816)',
  TEACHER: 'linear-gradient(135deg, #409eff, #337ecc)',
  STUDENT: 'linear-gradient(135deg, #67c23a, #4da32c)',
}

function roleLabel(role) { return roleMap[role] || role }
function roleTagType(role) { return roleTagMap[role] || '' }
function getAvatarColor(role) { return avatarColorMap[role] || 'linear-gradient(135deg, #909399, #73767a)' }

onMounted(() => { fetchList() })

async function fetchList() {
  loading.value = true
  try {
    const params = { currentPage: page.current, pageSize: page.size }
    if (searchUsername.value.trim()) params.username = searchUsername.value.trim()
    if (searchRole.value) params.role = searchRole.value
    const res = await getUserList(params)
    tableData.value = res.data.records
    page.total = res.data.total
  } catch {} finally { loading.value = false }
}

function handleSearch() { page.current = 1; fetchList() }

function openAddDialog() { dialog.isEdit = false; dialog.visible = true }

function openEditDialog(row) {
  dialog.isEdit = true
  dialog.form = { id: row.id, username: row.username, realName: row.realName, role: row.role, phone: row.phone, status: row.status }
  dialog.visible = true
}

function resetForm() {
  dialog.form = { id: null, username: '', password: '', realName: '', role: '', phone: '', status: 1 }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.submitting = true
  try {
    if (dialog.isEdit) {
      await updateUser(dialog.form)
      ElMessage.success('修改成功')
    } else {
      await addUser(dialog.form)
      ElMessage.success('添加成功')
    }
    dialog.visible = false
    fetchList()
  } catch {} finally { dialog.submitting = false }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除用户「${row.username}」吗？`, '确认删除', { type: 'warning' })
    .then(async () => {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      fetchList()
    }).catch(() => {})
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

/* 用户信息单元格 */
.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-cell-avatar {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 13px;
  flex-shrink: 0;
}

.user-cell-info {
  display: flex;
  flex-direction: column;
}

.user-cell-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.user-cell-id {
  font-size: 12px;
  color: var(--text-secondary);
}

.phone-text {
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.5px;
}

/* 状态指示器 */
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

.status-disabled {
  color: #f56c6c;
}

.status-disabled .status-dot {
  background: #f56c6c;
  box-shadow: 0 0 6px rgba(245, 108, 108, 0.4);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
