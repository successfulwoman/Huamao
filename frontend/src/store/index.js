
import { reactive } from 'vue'

const store = reactive({
  user: null,
  admin: null,
  categories: [],
  searchKeyword: ''
})

export const setUser = (user) => {
  store.user = user
  localStorage.setItem('user', JSON.stringify(user))
}

export const getUser = () => {
  if (!store.user) {
    const stored = localStorage.getItem('user')
    if (stored) {
      store.user = JSON.parse(stored)
    }
  }
  return store.user
}

export const clearUser = () => {
  store.user = null
  localStorage.removeItem('user')
}

export const setAdmin = (admin) => {
  store.admin = admin
  localStorage.setItem('admin', JSON.stringify(admin))
}

export const getAdmin = () => {
  if (!store.admin) {
    const stored = localStorage.getItem('admin')
    if (stored) {
      store.admin = JSON.parse(stored)
    }
  }
  return store.admin
}

export const clearAdmin = () => {
  store.admin = null
  localStorage.removeItem('admin')
}

export const setCategories = (categories) => {
  store.categories = categories
}

export const getCategories = () => {
  return store.categories
}

export const setSearchKeyword = (keyword) => {
  store.searchKeyword = keyword
}

export const getSearchKeyword = () => {
  return store.searchKeyword
}

export const clearSearchKeyword = () => {
  store.searchKeyword = ''
}

export { store }

export default store
