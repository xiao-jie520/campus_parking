<template>
  <div class="page">
    <div class="access-container">
      <!-- 左侧：登记表单 -->
      <div class="page-card form-card">
        <div class="form-header">
          <div class="form-header-icon">
            <el-icon size="20" color="#409eff"><Edit /></el-icon>
          </div>
          <h3>车辆出入登记</h3>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" @submit.prevent>
          <el-form-item label="车牌号" prop="plateNumber">
            <el-input
              v-model="form.plateNumber"
              placeholder="请输入车牌号"
              size="large"
              @blur="handlePlateBlur"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="出入类型" prop="accessType">
            <div class="type-switcher">
              <div
                class="type-option"
                :class="{ active: form.accessType === 'IN', typeIn: form.accessType === 'IN' }"
                @click="form.accessType = 'IN'"
              >
                <el-icon size="20"><Right /></el-icon>
                <span>入场</span>
              </div>
              <div
                class="type-option"
                :class="{ active: form.accessType === 'OUT', typeOut: form.accessType === 'OUT' }"
                @click="form.accessType = 'OUT'"
              >
                <el-icon size="20"><Back /></el-icon>
                <span>出场</span>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="备注">
            <el-input v-model="form.remark" placeholder="可选备注信息" />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="submitting"
              class="submit-btn"
              @click="handleSubmit"
            >
              <el-icon size="18"><CircleCheck /></el-icon>
              确认登记
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 最近记录 -->
        <transition name="slide-fade">
          <div v-if="lastRecord" class="last-record">
            <h4>
              <el-icon><Clock /></el-icon> 最近记录
            </h4>
            <div class="record-grid">
              <div class="record-item">
                <span class="record-label">车牌号</span>
                <span class="record-value plate-text">{{ lastRecord.plateNumber }}</span>
              </div>
              <div class="record-item">
                <span class="record-label">类型</span>
                <el-tag size="small" :type="lastRecord.accessType === 'IN' ? 'success' : 'warning'" effect="light" round>
                  {{ lastRecord.accessType === 'IN' ? '入场' : '出场' }}
                </el-tag>
              </div>
              <div class="record-item" v-if="lastRecord.spotNumber">
                <span class="record-label">车位</span>
                <span class="record-value spot-text">{{ lastRecord.spotNumber }}</span>
              </div>
              <div class="record-item" v-if="lastRecord.areaName">
                <span class="record-label">区域</span>
                <span class="record-value">{{ lastRecord.areaName }}</span>
              </div>
              <div class="record-item">
                <span class="record-label">时间</span>
                <span class="record-value">{{ lastRecord.accessTime }}</span>
              </div>
            </div>
          </div>
        </transition>
      </div>

      <!-- 右侧：引导信息面板 -->
      <div class="page-card guide-panel">
        <div class="guide-header">
          <div class="guide-header-icon">
            <el-icon size="20" color="#67c23a"><Location /></el-icon>
          </div>
          <h3>停车引导</h3>
        </div>

        <div v-if="guideList.length === 0" class="guide-empty">
          <div class="guide-empty-icon">
            <el-icon size="40" color="#dcdfe6"><Promotion /></el-icon>
          </div>
          <p>暂无引导信息</p>
          <span>完成入场登记后将在此显示车位分配</span>
        </div>

        <transition-group v-else name="guide-list" tag="div" class="guide-list">
          <div
            v-for="item in guideList"
            :key="item.time"
            class="guide-item"
            :class="item.type === 'IN' ? 'guide-in' : 'guide-out'"
          >
            <div class="guide-item-header">
              <el-tag size="small" :type="item.type === 'IN' ? 'success' : 'warning'" effect="dark" round>
                {{ item.type === 'IN' ? '入场' : '出场' }}
              </el-tag>
              <span class="guide-plate">{{ item.plateNumber }}</span>
              <span class="guide-time">{{ item.timeStr }}</span>
            </div>
            <template v-if="item.type === 'IN' && item.spotNumber">
              <div class="guide-spot">
                <div class="guide-spot-num">
                  <el-icon size="22"><Location /></el-icon>
                  <span>{{ item.spotNumber }}</span>
                </div>
                <div class="guide-area">{{ item.areaName }}</div>
              </div>
            </template>
            <template v-else-if="item.type === 'OUT'">
              <div class="guide-release">
                <el-icon><CircleCheck /></el-icon>
                <span>车位已释放</span>
              </div>
            </template>
          </div>
        </transition-group>
      </div>
    </div>

    <!-- 在场车辆列表 -->
    <div class="page-card parked-card">
      <div class="parked-header">
        <div class="parked-title">
          <div class="parked-title-icon">
            <el-icon size="18" color="#409eff"><Van /></el-icon>
          </div>
          <span>当前在场车辆</span>
          <el-tag size="small" type="info" effect="plain" round class="count-tag">
            {{ parkedVehicles.length }} 辆
          </el-tag>
        </div>
        <el-button text type="primary" @click="fetchParkedVehicles">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>

      <el-table :data="parkedVehicles" stripe style="width: 100%" empty-text="暂无在场车辆">
        <el-table-column prop="plateNumber" label="车牌号" width="140">
          <template #default="{ row }">
            <span class="plate-badge">{{ row.plateNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="车主" width="120" />
        <el-table-column prop="vehicleType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.vehicleType === 'INTERNAL' ? 'success' : 'info'" effect="light" round>
              {{ row.vehicleType === 'INTERNAL' ? '内部' : '临时' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="areaName" label="停放区域" width="120" />
        <el-table-column prop="spotNumber" label="车位编号" width="120">
          <template #default="{ row }">
            <span class="spot-highlight" v-if="row.spotNumber">{{ row.spotNumber }}</span>
            <span v-else class="text-placeholder">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="warning" size="small" @click="quickOut(row.plateNumber)" round>
              <el-icon><Back /></el-icon> 出场
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { recordAccess, getLatestRecord } from '../api/access.js'
import { getParkedVehicles } from '../api/vehicle.js'

const GUIDE_STORAGE_KEY = 'access_guide_list'

const formRef = ref(null)
const submitting = ref(false)
const lastRecord = ref(null)
const guideList = ref(loadGuideList())
const parkedVehicles = ref([])

const form = reactive({
  plateNumber: '',
  accessType: 'IN',
  remark: '',
})

const rules = {
  plateNumber: [
    { required: true, message: '请输入车牌号', trigger: 'blur' },
  ],
  accessType: [
    { required: true, message: '请选择出入类型', trigger: 'change' },
  ],
}

onMounted(() => {
  fetchParkedVehicles()
})

function loadGuideList() {
  try {
    const raw = sessionStorage.getItem(GUIDE_STORAGE_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

function saveGuideList() {
  sessionStorage.setItem(GUIDE_STORAGE_KEY, JSON.stringify(guideList.value))
}

function formatTime(date) {
  const h = String(date.getHours()).padStart(2, '0')
  const m = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  return `${h}:${m}:${s}`
}

function addGuideItem(data, accessType, plateNumber) {
  const now = new Date()
  const item = {
    type: accessType,
    plateNumber,
    spotNumber: data?.spotNumber || '',
    areaName: data?.areaName || '',
    time: now.getTime(),
    timeStr: formatTime(now),
  }
  guideList.value.unshift(item)
  if (guideList.value.length > 2) {
    guideList.value.pop()
  }
  saveGuideList()
}

async function fetchParkedVehicles() {
  try {
    const res = await getParkedVehicles()
    parkedVehicles.value = res.data || []
  } catch {}
}

function quickOut(plateNumber) {
  form.plateNumber = plateNumber
  form.accessType = 'OUT'
  form.remark = ''
  handleSubmit()
}

async function handlePlateBlur() {
  if (!form.plateNumber.trim()) return
  try {
    const res = await getLatestRecord(form.plateNumber.trim())
    if (res.data.latestRecord) {
      lastRecord.value = res.data.latestRecord
    } else {
      lastRecord.value = null
    }
    if (res.data.vehicle) {
      form.accessType = res.data.vehicle.parkingStatus === 1 ? 'OUT' : 'IN'
    }
  } catch {
    lastRecord.value = null
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const res = await recordAccess({
      plateNumber: form.plateNumber.trim(),
      accessType: form.accessType,
      remark: form.remark,
    })
    ElMessage.success(res.message)
    addGuideItem(res.data, form.accessType, form.plateNumber.trim())
    handlePlateBlur()
    fetchParkedVehicles()
  } catch {
    // handled by interceptor
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.access-container {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
  align-items: start;
}

/* 表单卡片 */
.form-header,
.guide-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 24px;
}

.form-header-icon,
.guide-header-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-header-icon {
  background: linear-gradient(135deg, #ecf5ff, #d9ecff);
}

.guide-header-icon {
  background: linear-gradient(135deg, #f0f9eb, #e1f3d8);
}

.form-header h3,
.guide-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

/* 类型切换器 */
.type-switcher {
  display: flex;
  gap: 12px;
}

.type-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  border: 2px solid var(--border);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s ease;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-regular);
}

.type-option:hover {
  border-color: var(--primary-light);
  color: var(--primary);
}

.type-option.active {
  font-weight: 600;
}

.type-option.typeIn.active {
  border-color: #67c23a;
  background: rgba(103, 194, 58, 0.06);
  color: #67c23a;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.15);
}

.type-option.typeOut.active {
  border-color: #e6a23c;
  background: rgba(230, 162, 60, 0.06);
  color: #e6a23c;
  box-shadow: 0 2px 8px rgba(230, 162, 60, 0.15);
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 10px !important;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* 最近记录 */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

.last-record {
  margin-top: 24px;
  background: linear-gradient(135deg, #f8f9fb, #f0f2f5);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 18px 20px;
}

.last-record h4 {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.record-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.record-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.record-label {
  font-size: 12px;
  color: var(--text-placeholder);
}

.record-value {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.plate-text {
  color: #409eff;
  font-family: 'SF Mono', 'Fira Code', monospace;
  font-weight: 700;
}

.spot-text {
  color: #67c23a;
  font-weight: 700;
}

/* 引导面板 */
.guide-empty {
  text-align: center;
  padding: 40px 20px;
}

.guide-empty-icon {
  margin-bottom: 12px;
}

.guide-empty p {
  font-size: 14px;
  color: var(--text-placeholder);
  margin: 0;
}

.guide-empty span {
  font-size: 12px;
  color: var(--text-placeholder);
  opacity: 0.7;
}

.guide-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.guide-item {
  border-radius: 12px;
  padding: 16px;
  border: 1px solid;
  transition: all 0.3s ease;
}

.guide-in {
  background: linear-gradient(135deg, #f0f9eb, #e8f5e1);
  border-color: #c2e7b0;
}

.guide-out {
  background: linear-gradient(135deg, #fdf6ec, #fce8c8);
  border-color: #f5dab1;
}

.guide-item-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.guide-plate {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  font-family: 'SF Mono', 'Fira Code', monospace;
}

.guide-time {
  margin-left: auto;
  font-size: 12px;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}

.guide-spot {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.guide-spot-num {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 24px;
  font-weight: 800;
  color: #67c23a;
}

.guide-area {
  font-size: 13px;
  color: #606266;
  background: rgba(255, 255, 255, 0.8);
  padding: 4px 12px;
  border-radius: 8px;
}

.guide-release {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #e6a23c;
  font-weight: 500;
}

/* 引导列表动画 */
.guide-list-enter-active {
  transition: all 0.4s ease;
}

.guide-list-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

/* 在场车辆 */
.parked-card {
  margin-top: 20px;
}

.parked-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.parked-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.parked-title-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #ecf5ff, #d9ecff);
  display: flex;
  align-items: center;
  justify-content: center;
}

.count-tag {
  margin-left: 4px;
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
}

.spot-highlight {
  color: #67c23a;
  font-weight: 700;
  font-size: 14px;
}

.text-placeholder {
  color: var(--text-placeholder);
}

@media (max-width: 1000px) {
  .access-container {
    grid-template-columns: 1fr;
  }
}
</style>
