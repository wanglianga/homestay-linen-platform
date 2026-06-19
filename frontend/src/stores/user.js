import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref('')

  const isLoggedIn = computed(() => !!userInfo.value)
  const userRole = computed(() => userInfo.value?.role || '')
  const userName = computed(() => userInfo.value?.realName || '')

  function setUser(data) {
    userInfo.value = data
    token.value = data?.token || ''
    localStorage.setItem('linen_user', JSON.stringify(data))
  }

  function logout() {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('linen_user')
  }

  function loadFromStorage() {
    const stored = localStorage.getItem('linen_user')
    if (stored) {
      try {
        userInfo.value = JSON.parse(stored)
      } catch (e) {
        localStorage.removeItem('linen_user')
      }
    }
  }

  return {
    userInfo,
    token,
    isLoggedIn,
    userRole,
    userName,
    setUser,
    logout,
    loadFromStorage
  }
})
