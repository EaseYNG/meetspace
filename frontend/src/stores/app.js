import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useAppStore = defineStore('app', () => {
  const locale = ref(localStorage.getItem('locale') || 'zh')
  const isDark = ref(localStorage.getItem('isDark') === 'true')

  watch(locale, (val) => localStorage.setItem('locale', val))
  watch(isDark, (val) => localStorage.setItem('isDark', val))

  function toggleTheme() {
    isDark.value = !isDark.value
  }

  function setLocale(lang) {
    locale.value = lang
  }

  return { locale, isDark, toggleTheme, setLocale }
})
