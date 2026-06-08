<template>
  <div class="page">
    <!-- 个人信息卡片 -->
    <div class="profile-card page-card" v-loading="loading">
      <div class="profile-header">
        <div class="profile-avatar" :style="{ background: avatarGradient }">
          {{ avatarLetter }}
        </div>
        <div class="profile-summary">
          <h3>{{ form.username }}</h3>
          <el-tag :type="roleTagType" effect="light" round size="small">{{ roleLabel }}</el-tag>
        </div>
      </div>

      <el-divider />

      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" style="max-width: 480px">
        <el-form-item label="用户名">
          <el-input :value="form.username" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave" round>
            <el-icon><Check /></el-icon>
            保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 修改密码卡片 -->
    <div class="page-card pwd-card">
      <div class="pwd-header">
        <div class="pwd-header-icon">
          <el-icon size="20" color="#e6a23c"><Lock /></el-icon>
        </div>
        <div>
          <h3>修改密码</h3>
          <p>建议定期更换密码以确保账户安全</p>
        </div>
      </div>

      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px" style="max-width: 480px; margin-top: 20px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" :loading="changingPwd" @click="handleChangePassword" round>
            <el-icon><Key /></el-icon>
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser, updateCurrentUser, updatePassword } from '../api/user.js'
import { getUserRole } from '../utils/auth.js'

const loading = ref(false)
const saving = ref(false)
const changingPwd = ref(false)
const formRef = ref(null)
const pwdFormRef = ref(null)

const form = reactive({ username: '', realName: '', phone: '' })

const roleLabelMap = { ADMIN: '管理员', GUARD: '门卫', TEACHER: '教师', STUDENT: '学生' }
const roleTagTypeMap = { ADMIN: 'danger', GUARD: 'warning', TEACHER: '', STUDENT: 'info' }
const avatarColorMap = {
  ADMIN: 'linear-gradient(135deg, #f56c6c, #c45656)',
  GUARD: 'linear-gradient(135deg, #e6a23c, #b8832e)',
  TEACHER: 'linear-gradient(135deg, #409eff, #2d7cd4)',
  STUDENT: 'linear-gradient(135deg, #67c23a, #4a9e28)',
}

const userRole = getUserRole()
const roleLabel = roleLabelMap[userRole] || ''
const roleTagType = roleTagTypeMap[userRole] || ''
const avatarGradient = avatarColorMap[userRole] || 'linear-gradient(135deg, #909399, #6b6e72)'
const avatarLetter = computed(() => (form.realName || form.username || '?').charAt(0).toUpperCase())

const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' },
  ],
}

const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getCurrentUser()
    form.username = res.data.username
    form.realName = res.data.realName
    form.phone = res.data.phone
  } catch {} finally {
    loading.value = false
  }
})

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await updateCurrentUser({ realName: form.realName, phone: form.phone })
    ElMessage.success('保存成功')
  } catch {} finally {
    saving.value = false
  }
}

async function handleChangePassword() {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  changingPwd.value = true
  try {
    await updatePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch {} finally {
    changingPwd.value = false
  }
}
</script>

<style scoped>
.page {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  align-items: start;
}

/* 个人信息 */
.profile-card {
  padding: 28px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 18px;
}

.profile-avatar {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 26px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.profile-summary {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.profile-summary h3 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

/* 密码修改 */
.pwd-card {
  padding: 28px;
}

.pwd-header {
  display: flex;
  align-items: center;
  gap: 14px;
}

.pwd-header-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #fdf6ec, #faecd8);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.pwd-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.pwd-header p {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 4px 0 0;
}

@media (max-width: 900px) {
  .page {
    grid-template-columns: 1fr;
  }
}
</style>
