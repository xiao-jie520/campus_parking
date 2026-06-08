<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <el-icon size="22" color="#fff"><Van /></el-icon>
        </div>
        <div class="logo-info">
          <span class="logo-text">停车管理系统</span>
          <span class="logo-badge">Campus VMS</span>
        </div>
      </div>

      <nav class="menu">
        <router-link
          v-for="(item, index) in menuItems"
          :key="item.path"
          :to="item.path"
          class="menu-item"
          :class="{ active: $route.path === item.path }"
          :style="{ animationDelay: index * 0.05 + 's' }"
        >
          <div class="menu-item-indicator" />
          <el-icon size="18"><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="user-card" @click="$router.push('/profile')">
          <div class="user-avatar">
            {{ (username || 'U').charAt(0).toUpperCase() }}
          </div>
          <div class="user-meta">
            <span class="user-name">{{ username }}</span>
            <el-tag size="small" :type="roleTagType" effect="dark" class="role-tag">
              {{ roleLabel }}
            </el-tag>
          </div>
        </div>
        <el-tooltip content="退出登录" placement="right">
          <el-button class="logout-btn" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </aside>

    <main class="main-content">
      <div class="top-bar">
        <div class="breadcrumb">
          <span class="breadcrumb-title">{{ currentPageTitle }}</span>
          <span class="breadcrumb-sub">{{ currentPageSub }}</span>
        </div>
        <div class="top-bar-right">
          <span class="datetime">{{ currentTime }}</span>
        </div>
      </div>
      <div class="page-container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { removeToken, getUserRole, getUsername } from '../utils/auth.js'

const route = useRoute()
const router = useRouter()

const username = computed(() => getUsername() || '用户')
const userRole = computed(() => getUserRole())

const roleLabelMap = { ADMIN: '管理员', GUARD: '门卫', TEACHER: '教师', STUDENT: '学生' }
const roleTagTypeMap = { ADMIN: 'danger', GUARD: 'warning', TEACHER: '', STUDENT: 'info' }

const roleLabel = computed(() => roleLabelMap[userRole.value] || '')
const roleTagType = computed(() => roleTagTypeMap[userRole.value] || '')

const pageTitleMap = {
  '/dashboard': ['系统概览', '查看今日数据和车位使用情况'],
  '/vehicles': ['车辆管理', '管理校园注册车辆信息'],
  '/access': ['出入登记', '登记车辆入场与出场'],
  '/access-history': ['出入记录', '查看历史出入记录'],
  '/parking': ['停车引导', '管理停车区域和车位'],
  '/users': ['用户管理', '管理系统用户和权限'],
  '/profile': ['个人信息', '修改个人资料和密码'],
}

const currentPageTitle = computed(() => pageTitleMap[route.path]?.[0] || '页面')
const currentPageSub = computed(() => pageTitleMap[route.path]?.[1] || '')

const menuItems = computed(() => {
  const items = [
    { path: '/dashboard', label: '系统概览', icon: 'DataAnalysis' },
    { path: '/vehicles', label: '车辆管理', icon: 'Van' },
    { path: '/access', label: '出入登记', icon: 'Promotion' },
    { path: '/access-history', label: '出入记录', icon: 'Document' },
    { path: '/parking', label: '停车引导', icon: 'Location' },
  ]
  if (userRole.value === 'ADMIN') {
    items.push({ path: '/users', label: '用户管理', icon: 'User' })
  }
  return items
})

const currentTime = ref('')
let timer = null

function updateTime() {
  const now = new Date()
  const h = String(now.getHours()).padStart(2, '0')
  const m = String(now.getMinutes()).padStart(2, '0')
  const s = String(now.getSeconds()).padStart(2, '0')
  const month = now.getMonth() + 1
  const day = now.getDate()
  const weekDay = ['日', '一', '二', '三', '四', '五', '六'][now.getDay()]
  currentTime.value = `${month}月${day}日 周${weekDay} ${h}:${m}:${s}`
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  clearInterval(timer)
})

function handleLogout() {
  removeToken()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.15);
}

/* Logo */
.logo {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-info {
  display: flex;
  flex-direction: column;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: #ffffff;
  white-space: nowrap;
  letter-spacing: 0.5px;
}

.logo-badge {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-top: 2px;
}

/* 菜单 */
.menu {
  flex: 1;
  padding: 12px 0;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 24px;
  color: rgba(255, 255, 255, 0.55);
  text-decoration: none;
  font-size: 14px;
  transition: all 0.25s ease;
  position: relative;
  margin: 2px 8px;
  border-radius: 8px;
  animation: slideInLeft 0.3s ease-out both;
}

.menu-item-indicator {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: #667eea;
  border-radius: 0 3px 3px 0;
  transition: height 0.25s ease;
}

.menu-item:hover {
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.06);
}

.menu-item:hover .menu-item-indicator {
  height: 20px;
}

.menu-item.active {
  color: #ffffff;
  background: rgba(102, 126, 234, 0.2);
  font-weight: 500;
}

.menu-item.active .menu-item-indicator {
  height: 28px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  box-shadow: 0 0 12px rgba(102, 126, 234, 0.5);
}

/* 底部用户信息 */
.sidebar-footer {
  padding: 16px 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s ease;
  background: rgba(255, 255, 255, 0.04);
}

.user-card:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.user-name {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.role-tag {
  --el-tag-font-size: 10px !important;
  padding: 0 6px !important;
  height: 18px !important;
  line-height: 18px !important;
  width: fit-content;
}

.logout-btn {
  width: 36px !important;
  height: 36px !important;
  padding: 0 !important;
  background: rgba(255, 255, 255, 0.06) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.5) !important;
  border-radius: 10px !important;
  flex-shrink: 0;
  transition: all 0.25s ease !important;
}

.logout-btn:hover {
  background: rgba(245, 108, 108, 0.2) !important;
  border-color: rgba(245, 108, 108, 0.3) !important;
  color: #f56c6c !important;
}

/* 主内容区 */
.main-content {
  flex: 1;
  margin-left: 240px;
  min-height: 100vh;
  background: #f0f2f5;
  display: flex;
  flex-direction: column;
}

.top-bar {
  background: #fff;
  padding: 16px 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 50;
}

.breadcrumb {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.breadcrumb-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.breadcrumb-sub {
  font-size: 13px;
  color: var(--text-secondary);
}

.top-bar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.datetime {
  font-size: 13px;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.5px;
}

.page-container {
  flex: 1;
  padding: 24px;
}
</style>
