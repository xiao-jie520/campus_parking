<template>
  <div class="login-page">
    <!-- 动态背景 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <div class="login-card glass-card">
      <div class="login-header">
        <div class="logo-icon">
          <el-icon size="36" color="#fff"><Van /></el-icon>
        </div>
        <h1>校园车辆管理系统</h1>
        <p>Campus Vehicle Management System</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            placeholder="请输入密码"
            type="password"
            show-password
            size="large"
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>© 2026 校园车辆管理系统</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '../api/vehicle.js'
import { setToken } from '../utils/auth.js'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form.username, form.password)
    setToken(res.data.token)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
}

.bg-circle-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, #667eea 0%, transparent 70%);
  top: -200px;
  right: -100px;
  animation: float1 8s ease-in-out infinite;
}

.bg-circle-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #764ba2 0%, transparent 70%);
  bottom: -100px;
  left: -50px;
  animation: float2 10s ease-in-out infinite;
}

.bg-circle-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, #f093fb 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: float3 12s ease-in-out infinite;
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-40px, 30px); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, -40px); }
}

@keyframes float3 {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.2); }
}

.login-card {
  width: 420px;
  padding: 48px 40px 32px;
  animation: fadeInUp 0.6s ease-out both;
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
  animation: pulse 3s ease-in-out infinite;
}

.login-header h1 {
  font-size: 24px;
  color: #1a1a2e;
  font-weight: 700;
  margin-bottom: 6px;
  letter-spacing: 1px;
}

.login-header p {
  font-size: 12px;
  color: #909399;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border-radius: 10px !important;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.35) !important;
  transition: all 0.3s ease !important;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.5) !important;
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: #c0c4cc;
}
</style>
