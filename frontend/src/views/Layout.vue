<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <img src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="logo" />
        <span v-show="!isCollapse">布草管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#001529"
        text-color="#b7bec4"
        active-text-color="#fff"
        class="layout-menu"
      >
        <template v-for="item in menuList" :key="item.path">
          <el-menu-item :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <component :is="isCollapse ? Expand : Fold" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="user-avatar">
                {{ userStore.userName ? userStore.userName.charAt(0) : 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.userName || '用户' }}</span>
              <el-tag :type="roleTagType" size="small" effect="light" class="role-tag">
                {{ roleName }}
              </el-tag>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Expand,
  Fold,
  ArrowDown,
  User,
  SwitchButton,
  DataBoard,
  Reading,
  House,
  Box,
  Goods,
  Files,
  Histogram,
  Management,
  Avatar,
  Van,
  OfficeBuilding,
  Warehouse,
  UserFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const roleMap = {
  reception: '前台',
  cleaner: '保洁',
  driver: '司机',
  factory: '洗涤厂',
  warehouse: '库管',
  manager: '店长'
}

const roleName = computed(() => roleMap[userStore.userRole] || '未知角色')
const roleTagType = computed(() => {
  const map = {
    reception: '',
    cleaner: 'success',
    driver: 'warning',
    factory: 'info',
    warehouse: '',
    manager: 'danger'
  }
  return map[userStore.userRole] || ''
})

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '')

const allMenus = [
  { path: '/dashboard', title: '工作台', icon: DataBoard, roles: null },
  { path: '/reception', title: '前台工作台', icon: Reading, roles: ['reception', 'manager'] },
  { path: '/cleaner', title: '保洁工作台', icon: Avatar, roles: ['cleaner', 'manager'] },
  { path: '/driver', title: '司机工作台', icon: Van, roles: ['driver', 'manager'] },
  { path: '/factory', title: '洗涤厂工作台', icon: Goods, roles: ['factory', 'manager'] },
  { path: '/warehouse', title: '库管工作台', icon: Box, roles: ['warehouse', 'manager'] },
  { path: '/manager', title: '店长工作台', icon: UserFilled, roles: ['manager'] },
  { path: '/track', title: '布草轨迹', icon: Histogram, roles: null },
  { path: '/rooms', title: '房间管理', icon: House, roles: ['reception', 'manager'] },
  { path: '/linen-types', title: '布草类型', icon: Goods, roles: ['manager'] },
  { path: '/stocks', title: '库存管理', icon: Box, roles: ['warehouse', 'manager'] },
  { path: '/damage', title: '报损管理', icon: Files, roles: ['manager', 'warehouse', 'factory'] },
  { path: '/cost', title: '成本分摊', icon: Management, roles: ['manager'] }
]

const menuList = computed(() => {
  const role = userStore.userRole
  return allMenus.filter((item) => !item.roles || item.roles.includes(role))
})

function handleCommand(cmd) {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      })
      .catch(() => {})
  } else if (cmd === 'profile') {
    ElMessage.info('个人信息功能开发中')
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #001529;
  transition: width 0.2s;
  overflow: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    background-color: #002140;
    white-space: nowrap;

    img {
      width: 32px;
      height: 32px;
      margin-right: 8px;
    }
  }

  .layout-menu {
    border-right: none;
  }
}

.layout-header {
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      color: #606266;

      &:hover {
        color: #409eff;
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      gap: 8px;

      .user-avatar {
        background-color: #409eff;
      }

      .user-name {
        color: #303133;
      }

      .role-tag {
        margin-left: 4px;
      }
    }
  }
}

.layout-main {
  background-color: #f5f7fa;
  padding: 0;
}
</style>
