<template>
  <div class="page">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div
        v-for="(card, index) in statCards"
        :key="card.label"
        class="stat-card"
        :style="{ animationDelay: index * 0.1 + 's' }"
      >
        <div class="stat-card-bg" :style="{ background: card.gradient }" />
        <div class="stat-icon" :style="{ background: card.iconBg }">
          <el-icon size="26" :color="card.iconColor"><component :is="card.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
        <div class="stat-trend" :class="card.trendClass">
          <el-icon size="14"><component :is="card.trendIcon" /></el-icon>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="chart-row">
      <!-- 各区域车位使用情况 -->
      <div class="chart-card page-card" v-loading="loadingArea">
        <div class="chart-header">
          <h3>各区域车位使用率</h3>
        </div>
        <div ref="areaChartRef" class="chart-body" />
      </div>

      <!-- 今日出入统计 -->
      <div class="chart-card page-card" v-loading="loading">
        <div class="chart-header">
          <h3>今日概况</h3>
        </div>
        <div ref="overviewChartRef" class="chart-body" />
      </div>
    </div>

    <!-- 区域详细进度 -->
    <div class="page-card area-detail" v-loading="loadingArea" style="margin-top: 20px">
      <div class="chart-header">
        <h3>区域车位详情</h3>
      </div>
      <div class="area-bars" v-if="areas.length > 0">
        <div
          v-for="(area, index) in areas"
          :key="area.id"
          class="area-bar-item"
          :style="{ animationDelay: index * 0.08 + 's' }"
        >
          <div class="area-bar-label">
            <div class="area-bar-left">
              <span class="area-dot" :style="{ background: getAreaColor(area) }" />
              <span class="area-bar-name">{{ area.areaName }}</span>
            </div>
            <span class="area-bar-count">
              <strong>{{ area.totalSpaces - area.availableSpaces }}</strong> / {{ area.totalSpaces }}
              <span class="area-bar-pct">
                ({{ area.totalSpaces > 0 ? Math.round((area.totalSpaces - area.availableSpaces) / area.totalSpaces * 100) : 0 }}%)
              </span>
            </span>
          </div>
          <el-progress
            :percentage="area.totalSpaces > 0 ? Math.round((area.totalSpaces - area.availableSpaces) / area.totalSpaces * 100) : 0"
            :color="getProgressColor(area)"
            :stroke-width="12"
            :show-text="false"
          />
        </div>
      </div>
      <div v-else class="empty-tip">暂无停车场数据</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { getTodayStatistics, getAreaStatistics } from '../api/statistics.js'

const loading = ref(false)
const loadingArea = ref(false)
const stats = ref({ todayIn: 0, todayOut: 0, currentParked: 0, totalVehicles: 0 })
const areas = ref([])

const areaChartRef = ref(null)
const overviewChartRef = ref(null)
let areaChart = null
let overviewChart = null

const statCards = computed(() => [
  {
    label: '注册车辆',
    value: stats.value.totalVehicles,
    icon: 'Van',
    gradient: 'linear-gradient(135deg, rgba(64,158,255,0.08) 0%, rgba(64,158,255,0) 100%)',
    iconBg: 'linear-gradient(135deg, #ecf5ff, #d9ecff)',
    iconColor: '#409eff',
    trendClass: 'trend-up',
    trendIcon: 'Top',
  },
  {
    label: '今日入场',
    value: stats.value.todayIn,
    icon: 'Right',
    gradient: 'linear-gradient(135deg, rgba(103,194,58,0.08) 0%, rgba(103,194,58,0) 100%)',
    iconBg: 'linear-gradient(135deg, #f0f9eb, #e1f3d8)',
    iconColor: '#67c23a',
    trendClass: 'trend-up',
    trendIcon: 'Top',
  },
  {
    label: '今日出场',
    value: stats.value.todayOut,
    icon: 'Back',
    gradient: 'linear-gradient(135deg, rgba(230,162,60,0.08) 0%, rgba(230,162,60,0) 100%)',
    iconBg: 'linear-gradient(135deg, #fdf6ec, #faecd8)',
    iconColor: '#e6a23c',
    trendClass: 'trend-down',
    trendIcon: 'Bottom',
  },
  {
    label: '当前在场',
    value: stats.value.currentParked,
    icon: 'Location',
    gradient: 'linear-gradient(135deg, rgba(245,108,108,0.08) 0%, rgba(245,108,108,0) 100%)',
    iconBg: 'linear-gradient(135deg, #fef0f0, #fde2e2)',
    iconColor: '#f56c6c',
    trendClass: 'trend-neutral',
    trendIcon: 'Minus',
  },
])

onMounted(() => {
  fetchStats()
  fetchAreas()
  window.addEventListener('resize', handleResize)
})

function handleResize() {
  areaChart?.resize()
  overviewChart?.resize()
}

async function fetchStats() {
  loading.value = true
  try {
    const res = await getTodayStatistics()
    stats.value = res.data
  } catch {} finally {
    loading.value = false
    await nextTick()
    initOverviewChart()
  }
}

async function fetchAreas() {
  loadingArea.value = true
  try {
    const res = await getAreaStatistics()
    areas.value = res.data
  } catch {} finally {
    loadingArea.value = false
    await nextTick()
    initAreaChart()
  }
}

function initAreaChart() {
  if (!areaChartRef.value || areas.value.length === 0) return
  if (areaChart) areaChart.dispose()

  areaChart = echarts.init(areaChartRef.value)
  const names = areas.value.map(a => a.areaName)
  const used = areas.value.map(a => a.totalSpaces - a.availableSpaces)
  const free = areas.value.map(a => a.availableSpaces)

  areaChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#eee',
      textStyle: { color: '#333', fontSize: 13 },
    },
    legend: {
      data: ['已占用', '空闲'],
      top: 0,
      right: 0,
      textStyle: { color: '#909399', fontSize: 12 },
    },
    grid: { left: 16, right: 16, bottom: 8, top: 40, containLabel: true },
    xAxis: {
      type: 'category',
      data: names,
      axisLine: { lineStyle: { color: '#e4e7ed' } },
      axisLabel: { color: '#606266', fontSize: 12 },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#909399', fontSize: 12 },
    },
    series: [
      {
        name: '已占用',
        type: 'bar',
        stack: 'total',
        data: used,
        barWidth: 36,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#f56c6c' },
            { offset: 1, color: '#fab6b6' },
          ]),
          borderRadius: [0, 0, 0, 0],
        },
      },
      {
        name: '空闲',
        type: 'bar',
        stack: 'total',
        data: free,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67c23a' },
            { offset: 1, color: '#b3e19d' },
          ]),
          borderRadius: [4, 4, 0, 0],
        },
      },
    ],
    animationDuration: 1000,
    animationEasing: 'cubicOut',
  })
}

function initOverviewChart() {
  if (!overviewChartRef.value) return
  if (overviewChart) overviewChart.dispose()

  overviewChart = echarts.init(overviewChartRef.value)

  overviewChart.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#eee',
      textStyle: { color: '#333', fontSize: 13 },
    },
    legend: {
      orient: 'vertical',
      right: 20,
      top: 'center',
      textStyle: { color: '#606266', fontSize: 13 },
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 3,
        },
        label: {
          show: true,
          position: 'center',
          formatter: () => `{val|${stats.value.currentParked}}\n{label|当前在场}`,
          rich: {
            val: { fontSize: 28, fontWeight: 700, color: '#303133', lineHeight: 36 },
            label: { fontSize: 13, color: '#909399', lineHeight: 22 },
          },
        },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold' },
        },
        labelLine: { show: false },
        data: [
          {
            value: stats.value.todayIn,
            name: '今日入场',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#67c23a' },
                { offset: 1, color: '#95d475' },
              ]),
            },
          },
          {
            value: stats.value.todayOut,
            name: '今日出场',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#e6a23c' },
                { offset: 1, color: '#f3d19e' },
              ]),
            },
          },
          {
            value: Math.max(stats.value.totalVehicles - stats.value.todayIn - stats.value.todayOut, 0),
            name: '其他',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#dcdfe6' },
                { offset: 1, color: '#ebeef5' },
              ]),
            },
          },
        ],
      },
    ],
    animationDuration: 1200,
    animationEasing: 'cubicOut',
  })
}

function getProgressColor(area) {
  if (area.totalSpaces === 0) return '#c0c4cc'
  const rate = (area.totalSpaces - area.availableSpaces) / area.totalSpaces
  if (rate >= 0.9) return '#f56c6c'
  if (rate >= 0.7) return '#e6a23c'
  return '#67c23a'
}

function getAreaColor(area) {
  if (area.totalSpaces === 0) return '#c0c4cc'
  const rate = (area.totalSpaces - area.availableSpaces) / area.totalSpaces
  if (rate >= 0.9) return '#f56c6c'
  if (rate >= 0.7) return '#e6a23c'
  return '#67c23a'
}
</script>

<style scoped>
/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  position: relative;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 22px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  animation: fadeInUp 0.5s ease-out both;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}

.stat-card-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.stat-icon {
  width: 54px;
  height: 54px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
}

.stat-info {
  flex: 1;
  min-width: 0;
  position: relative;
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-primary);
  font-variant-numeric: tabular-nums;
  animation: countUp 0.6s ease-out both;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.stat-trend {
  position: relative;
  padding: 4px 6px;
  border-radius: 6px;
  font-size: 12px;
}

.trend-up {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.trend-down {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.trend-neutral {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
}

/* 图表区域 */
.chart-row {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 20px;
  margin-top: 20px;
}

.chart-card {
  padding: 24px;
}

.chart-header {
  margin-bottom: 16px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.chart-body {
  width: 100%;
  height: 300px;
}

/* 区域详情 */
.area-detail {
  padding: 24px;
}

.area-bars {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.area-bar-item {
  animation: fadeInUp 0.4s ease-out both;
}

.area-bar-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.area-bar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.area-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.area-bar-name {
  color: var(--text-primary);
  font-weight: 600;
}

.area-bar-count {
  color: var(--text-secondary);
  font-size: 13px;
}

.area-bar-count strong {
  color: var(--text-primary);
  font-size: 15px;
}

.area-bar-pct {
  color: var(--text-placeholder);
  font-size: 12px;
  margin-left: 4px;
}

.empty-tip {
  color: var(--text-placeholder);
  font-size: 14px;
  text-align: center;
  padding: 40px 0;
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .chart-row {
    grid-template-columns: 1fr;
  }
}
</style>
