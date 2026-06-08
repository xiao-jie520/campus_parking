<template>
  <div class="page">
    <div class="page-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input
            v-model="searchPlate"
            placeholder="输入车牌号搜索"
            clearable
            style="width: 220px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="searchType" placeholder="出入类型" clearable style="width: 130px" @change="handleSearch">
            <el-option label="入场" value="IN" />
            <el-option label="出场" value="OUT" />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <div class="record-count" v-if="page.total > 0">
          共 <strong>{{ page.total }}</strong> 条记录
        </div>
      </div>

      <el-table :data="tableData" stripe v-loading="loading" class="styled-table">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="plateNumber" label="车牌号" width="140">
          <template #default="{ row }">
            <span class="plate-badge">{{ row.plateNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column label="出入类型" width="100" align="center">
          <template #default="{ row }">
            <div class="type-badge" :class="row.accessType === 'IN' ? 'type-in' : 'type-out'">
              <el-icon size="12"><component :is="row.accessType === 'IN' ? 'Right' : 'Back'" /></el-icon>
              {{ row.accessType === 'IN' ? '入场' : '出场' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="areaName" label="区域" width="100">
          <template #default="{ row }">
            <span v-if="row.areaName" class="area-text">{{ row.areaName }}</span>
            <span v-else class="text-placeholder">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="spotNumber" label="车位" width="100">
          <template #default="{ row }">
            <span v-if="row.spotNumber" class="spot-text">{{ row.spotNumber }}</span>
            <span v-else class="text-placeholder">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="accessTime" label="出入时间" min-width="170">
          <template #default="{ row }">
            <span class="time-text">{{ row.accessTime }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="remark" label="备注" min-width="150">
          <template #default="{ row }">
            <span v-if="row.remark">{{ row.remark }}</span>
            <span v-else class="text-placeholder">-</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :total="page.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAccessRecordList } from '../api/access.js'

const loading = ref(false)
const tableData = ref([])
const searchPlate = ref('')
const searchType = ref('')

const page = reactive({ current: 1, size: 10, total: 0 })

onMounted(() => { fetchList() })

async function fetchList() {
  loading.value = true
  try {
    const params = { currentPage: page.current, pageSize: page.size }
    if (searchPlate.value.trim()) params.plateNumber = searchPlate.value.trim()
    if (searchType.value) params.accessType = searchType.value
    const res = await getAccessRecordList(params)
    tableData.value = res.data.records
    page.total = res.data.total
  } catch {} finally { loading.value = false }
}

function handleSearch() { page.current = 1; fetchList() }
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

.record-count {
  font-size: 13px;
  color: var(--text-secondary);
}

.record-count strong {
  color: var(--primary);
  font-size: 15px;
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

.type-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.type-in {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.type-out {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.area-text {
  color: var(--text-regular);
  font-weight: 500;
}

.spot-text {
  color: #67c23a;
  font-weight: 600;
  font-family: 'SF Mono', 'Fira Code', monospace;
  font-size: 13px;
}

.time-text {
  font-variant-numeric: tabular-nums;
  color: var(--text-regular);
}

.text-placeholder {
  color: var(--text-placeholder);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
