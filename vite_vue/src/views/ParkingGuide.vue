<template>
  <div class="page">
    <div class="page-header">
      <div class="page-header-left">
        <h2>停车区域</h2>
        <span class="page-header-sub">共 {{ areas.length }} 个区域</span>
      </div>
      <el-button v-if="isAdmin" type="primary" @click="openAddArea" round>
        <el-icon><Plus /></el-icon> 新增区域
      </el-button>
    </div>

    <!-- 区域卡片列表 -->
    <div class="area-cards" v-loading="loadingAreas">
      <div
        v-for="(area, index) in areas"
        :key="area.id"
        class="area-card"
        :class="[areaStatusClass(area), { selected: selectedArea?.id === area.id }]"
        @click="selectArea(area)"
        :style="{ animationDelay: index * 0.08 + 's' }"
      >
        <div class="area-card-header">
          <span class="area-name">{{ area.areaName }}</span>
          <div v-if="isAdmin" class="area-actions" @click.stop>
            <el-button type="primary" size="small" link @click="openEditArea(area)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDeleteArea(area)">删除</el-button>
          </div>
        </div>

        <div class="area-ring-container">
          <svg viewBox="0 0 80 80" class="area-ring">
            <circle cx="40" cy="40" r="34" fill="none" stroke="#f0f0f0" stroke-width="6" />
            <circle
              cx="40" cy="40" r="34"
              fill="none"
              :stroke="getRingColor(area)"
              stroke-width="6"
              stroke-linecap="round"
              :stroke-dasharray="getRingDash(area)"
              stroke-dashoffset="0"
              transform="rotate(-90 40 40)"
              class="ring-progress"
            />
          </svg>
          <div class="ring-center">
            <span class="ring-number">{{ area.availableSpaces }}</span>
            <span class="ring-label">空闲</span>
          </div>
        </div>

        <div class="area-card-footer">
          <div class="area-stat">
            <span class="area-stat-val">{{ area.totalSpaces }}</span>
            <span class="area-stat-lbl">总车位</span>
          </div>
          <div class="area-stat">
            <span class="area-stat-val used">{{ area.totalSpaces - area.availableSpaces }}</span>
            <span class="area-stat-lbl">已占用</span>
          </div>
        </div>

        <div v-if="area.location" class="area-location">
          <el-icon size="12"><Location /></el-icon>
          {{ area.location }}
        </div>
      </div>

      <div v-if="areas.length === 0 && !loadingAreas" class="empty-tip">
        <el-icon size="48" color="#dcdfe6"><Location /></el-icon>
        <p>暂无停车场区域</p>
      </div>
    </div>

    <!-- 选中区域的车位列表 -->
    <transition name="slide-fade">
      <div v-if="selectedArea" class="page-card spot-section" style="margin-top: 20px">
        <div class="spot-header">
          <div class="spot-header-left">
            <h3>{{ selectedArea.areaName }}</h3>
            <div class="spot-legend">
              <span class="legend-item"><span class="legend-dot free" />空闲</span>
              <span class="legend-item"><span class="legend-dot occupied" />占用</span>
              <span class="legend-item"><span class="legend-dot broken" />故障</span>
            </div>
          </div>
          <el-button v-if="isAdmin" type="primary" size="small" @click="openAddSpot" round>
            <el-icon><Plus /></el-icon> 新增车位
          </el-button>
        </div>

        <div class="spot-grid" v-loading="loadingSpots">
          <div
            v-for="spot in spots"
            :key="spot.id"
            class="spot-item"
            :class="spotStatusClass(spot)"
          >
            <div class="spot-number">{{ spot.spotNumber }}</div>
            <div class="spot-status">{{ spotStatusLabel(spot.status) }}</div>
            <div v-if="spot.plateNumber" class="spot-plate">{{ spot.plateNumber }}</div>
            <div v-if="isAdmin && spot.status !== 1" class="spot-actions" @click.stop>
              <el-button type="danger" size="small" link @click="handleDeleteSpot(spot)">删除</el-button>
            </div>
          </div>
          <div v-if="spots.length === 0 && !loadingSpots" class="empty-tip small">
            该区域暂无车位
          </div>
        </div>
      </div>
    </transition>

    <!-- 新增/编辑区域弹窗 -->
    <el-dialog v-model="areaDialog.visible" :title="areaDialog.isEdit ? '编辑区域' : '新增区域'" width="450px" @closed="resetAreaForm">
      <el-form ref="areaFormRef" :model="areaDialog.form" :rules="areaRules" label-width="80px">
        <el-form-item label="区域名称" prop="areaName">
          <el-input v-model="areaDialog.form.areaName" placeholder="如：A区" />
        </el-form-item>
        <el-form-item label="位置描述">
          <el-input v-model="areaDialog.form.location" placeholder="如：教学楼东侧" />
        </el-form-item>
        <el-form-item v-if="areaDialog.isEdit" label="状态">
          <el-switch v-model="areaDialog.form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="areaDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="areaDialog.submitting" @click="handleAreaSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增车位弹窗 -->
    <el-dialog v-model="spotDialog.visible" title="新增车位" width="400px" @closed="resetSpotForm">
      <el-form ref="spotFormRef" :model="spotDialog.form" :rules="spotRules" label-width="80px">
        <el-form-item label="车位编号" prop="spotNumber">
          <el-input v-model="spotDialog.form.spotNumber" :placeholder="'如：' + (selectedArea?.areaName || 'A') + '-001'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="spotDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="spotDialog.submitting" @click="handleSpotSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAreaList, addArea, updateArea, deleteArea, getSpotList, addSpot, deleteSpot } from '../api/parking.js'
import { getUserRole } from '../utils/auth.js'

const isAdmin = computed(() => getUserRole() === 'ADMIN')

const loadingAreas = ref(false)
const loadingSpots = ref(false)
const areas = ref([])
const spots = ref([])
const selectedArea = ref(null)

const areaFormRef = ref(null)
const areaDialog = reactive({ visible: false, isEdit: false, submitting: false, form: { id: null, areaName: '', location: '', status: 1 } })
const areaRules = { areaName: [{ required: true, message: '请输入区域名称', trigger: 'blur' }] }

const spotFormRef = ref(null)
const spotDialog = reactive({ visible: false, submitting: false, form: { spotNumber: '' } })
const spotRules = { spotNumber: [{ required: true, message: '请输入车位编号', trigger: 'blur' }] }

const CIRCUMFERENCE = 2 * Math.PI * 34

onMounted(() => { fetchAreas() })

async function fetchAreas() {
  loadingAreas.value = true
  try {
    const res = await getAreaList()
    areas.value = res.data
    if (selectedArea.value) {
      const found = areas.value.find(a => a.id === selectedArea.value.id)
      if (found) {
        selectedArea.value = found
        fetchSpots()
      } else {
        selectedArea.value = null
      }
    }
  } catch {} finally { loadingAreas.value = false }
}

async function fetchSpots() {
  if (!selectedArea.value) return
  loadingSpots.value = true
  try {
    const res = await getSpotList(selectedArea.value.id)
    spots.value = res.data
  } catch {} finally { loadingSpots.value = false }
}

function selectArea(area) {
  selectedArea.value = area
  fetchSpots()
}

function areaStatusClass(area) {
  if (area.availableSpaces === 0) return 'full'
  if (area.availableSpaces <= Math.ceil(area.totalSpaces * 0.2)) return 'tight'
  return 'ok'
}

function spotStatusClass(spot) {
  if (spot.status === 0) return 'spot-free'
  if (spot.status === 1) return 'spot-occupied'
  return 'spot-broken'
}

function spotStatusLabel(status) {
  return { 0: '空闲', 1: '占用', 2: '故障' }[status] || '未知'
}

function getRingColor(area) {
  if (area.totalSpaces === 0) return '#c0c4cc'
  const rate = (area.totalSpaces - area.availableSpaces) / area.totalSpaces
  if (rate >= 0.9) return '#f56c6c'
  if (rate >= 0.7) return '#e6a23c'
  return '#67c23a'
}

function getRingDash(area) {
  if (area.totalSpaces === 0) return `0 ${CIRCUMFERENCE}`
  const rate = (area.totalSpaces - area.availableSpaces) / area.totalSpaces
  const filled = rate * CIRCUMFERENCE
  return `${filled} ${CIRCUMFERENCE - filled}`
}

function openAddArea() { areaDialog.isEdit = false; areaDialog.visible = true }
function openEditArea(area) {
  areaDialog.isEdit = true
  areaDialog.form = { id: area.id, areaName: area.areaName, location: area.location, status: area.status }
  areaDialog.visible = true
}
function resetAreaForm() { areaDialog.form = { id: null, areaName: '', location: '', status: 1 } }

async function handleAreaSubmit() {
  const valid = await areaFormRef.value.validate().catch(() => false)
  if (!valid) return
  areaDialog.submitting = true
  try {
    if (areaDialog.isEdit) {
      await updateArea(areaDialog.form)
      ElMessage.success('修改成功')
    } else {
      await addArea({ ...areaDialog.form, totalSpaces: 10 })
      ElMessage.success('添加成功')
    }
    areaDialog.visible = false
    fetchAreas()
  } catch {} finally { areaDialog.submitting = false }
}

function handleDeleteArea(area) {
  ElMessageBox.confirm(`确定要删除「${area.areaName}」吗？该区域下的车位也会被删除。`, '确认删除', { type: 'warning' })
    .then(async () => {
      await deleteArea(area.id)
      ElMessage.success('删除成功')
      if (selectedArea.value?.id === area.id) selectedArea.value = null
      fetchAreas()
    }).catch(() => {})
}

function openAddSpot() { spotDialog.visible = true }
function resetSpotForm() { spotDialog.form = { spotNumber: '' } }

async function handleSpotSubmit() {
  const valid = await spotFormRef.value.validate().catch(() => false)
  if (!valid) return
  spotDialog.submitting = true
  try {
    await addSpot({ areaId: selectedArea.value.id, spotNumber: spotDialog.form.spotNumber })
    ElMessage.success('添加成功')
    spotDialog.visible = false
    fetchSpots()
    fetchAreas()
  } catch {} finally { spotDialog.submitting = false }
}

function handleDeleteSpot(spot) {
  ElMessageBox.confirm(`确定要删除车位「${spot.spotNumber}」吗？`, '确认删除', { type: 'warning' })
    .then(async () => {
      await deleteSpot(spot.id)
      ElMessage.success('删除成功')
      fetchSpots()
      fetchAreas()
    }).catch(() => {})
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-header h2 {
  font-size: 18px;
  color: var(--text-primary);
  font-weight: 700;
  margin: 0;
}

.page-header-sub {
  font-size: 13px;
  color: var(--text-secondary);
}

/* 区域卡片 */
.area-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.area-card {
  background: #fff;
  border: 2px solid var(--border);
  border-radius: var(--radius-md);
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeInUp 0.4s ease-out both;
  position: relative;
}

.area-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.area-card.selected {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15);
}

.area-card.ok { border-top: 3px solid #67c23a; }
.area-card.tight { border-top: 3px solid #e6a23c; }
.area-card.full { border-top: 3px solid #f56c6c; }

.area-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.area-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
}

.area-actions {
  display: flex;
  gap: 4px;
}

/* 环形进度 */
.area-ring-container {
  position: relative;
  width: 90px;
  height: 90px;
  margin: 0 auto 16px;
}

.area-ring {
  width: 100%;
  height: 100%;
}

.ring-progress {
  transition: stroke-dasharray 0.8s ease-out;
}

.ring-center {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.ring-number {
  font-size: 22px;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1;
}

.ring-label {
  font-size: 11px;
  color: var(--text-secondary);
  margin-top: 2px;
}

/* 底部统计 */
.area-card-footer {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.area-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.area-stat-val {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}

.area-stat-val.used {
  color: #f56c6c;
}

.area-stat-lbl {
  font-size: 11px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.area-location {
  margin-top: 10px;
  font-size: 11px;
  color: var(--text-placeholder);
  display: flex;
  align-items: center;
  gap: 4px;
  justify-content: center;
}

/* 车位区域 */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.spot-section {
  padding: 24px;
}

.spot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.spot-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.spot-header h3 {
  font-size: 16px;
  color: var(--text-primary);
  font-weight: 600;
  margin: 0;
}

.spot-legend {
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: var(--text-secondary);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.legend-dot.free { background: #67c23a; }
.legend-dot.occupied { background: #f56c6c; }
.legend-dot.broken { background: #c0c4cc; }

/* 车位网格 */
.spot-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.spot-item {
  width: 110px;
  height: 86px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  position: relative;
  transition: all 0.25s ease;
  cursor: default;
}

.spot-item:hover {
  transform: scale(1.05);
}

.spot-free {
  background: linear-gradient(135deg, #f0f9eb, #e8f5e1);
  border: 1.5px solid #b3e19d;
}

.spot-occupied {
  background: linear-gradient(135deg, #fef0f0, #fde2e2);
  border: 1.5px solid #fab6b6;
}

.spot-broken {
  background: linear-gradient(135deg, #f5f7fa, #ebeef5);
  border: 1.5px solid #d3d4d6;
}

.spot-number {
  font-weight: 700;
  font-size: 14px;
  color: var(--text-primary);
}

.spot-free .spot-number { color: #67c23a; }
.spot-occupied .spot-number { color: #f56c6c; }

.spot-status {
  font-size: 11px;
  color: var(--text-secondary);
  margin-top: 3px;
}

.spot-plate {
  font-size: 11px;
  color: var(--text-regular);
  font-family: 'SF Mono', 'Fira Code', monospace;
  margin-top: 2px;
}

.spot-actions {
  position: absolute;
  bottom: 4px;
}

.empty-tip {
  color: var(--text-placeholder);
  font-size: 14px;
  text-align: center;
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.empty-tip.small {
  padding: 20px;
  font-size: 13px;
}
</style>
