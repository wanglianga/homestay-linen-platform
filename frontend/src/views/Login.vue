<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>酒店布草管理系统</h1>
        <p>Hotel Linen Management Platform</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
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
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="form.role" placeholder="快速选择角色（测试用）" size="large" clearable>
            <el-option label="前台" value="reception" />
            <el-option label="保洁" value="cleaner" />
            <el-option label="司机" value="driver" />
            <el-option label="洗涤厂" value="factory" />
            <el-option label="库管" value="warehouse" />
            <el-option label="店长" value="manager" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="test-accounts">
        <h4>测试账号（密码均为 123456）</h4>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="前台">reception</el-descriptions-item>
          <el-descriptions-item label="保洁">cleaner</el-descriptions-item>
          <el-descriptions-item label="司机">driver</el-descriptions-item>
          <el-descriptions-item label="洗涤厂">factory</el-descriptions-item>
          <el-descriptions-item label="库管">warehouse</el-descriptions-item>
          <el-descriptions-item label="店长">manager</el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '123456',
  role: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

watch(
  () => form.role,
  (val) => {
    if (val) {
      form.username = val
      form.password = '123456'
    }
  }
)

async function handleLogin() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const data = await login({ username: form.username, password: form.password })
        userStore.setUser(data)
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (e) {
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 480px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  h1 {
    font-size: 24px;
    color: #303133;
    margin-bottom: 8px;
  }

  p {
    color: #909399;
    font-size: 14px;
  }
}

.login-form {
  .login-btn {
    width: 100%;
  }
}

.test-accounts {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;

  h4 {
    color: #606266;
    font-size: 14px;
    margin-bottom: 12px;
  }

  :deep(.el-descriptions__label) {
    width: 60px;
    font-weight: normal;
  }
}
</style>
