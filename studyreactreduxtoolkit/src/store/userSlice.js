// src/store/userSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

// 1. 定义异步Action：请求用户列表
export const fetchUserList = createAsyncThunk(
  'user/fetchUserList',
  async (_, { rejectWithValue }) => {
    try {
      // 模拟公共接口
      const res = await fetch('https://jsonplaceholder.typicode.com/users')
      const data = await res.json()
      return data
    } catch (err) {
      return rejectWithValue('请求失败')
    }
  }
)

// 初始状态
const initialState = {
  list: [],
  loading: false,
  error: ''
}

// 创建切片
const userSlice = createSlice({
  name: 'user',
  initialState,
  // 同步reducer
  reducers: {},
  // 处理异步请求三种状态：pending/fulfilled/rejected
  extraReducers: (builder) => {
    builder
      .addCase(fetchUserList.pending, (state) => {
        state.loading = true
        state.error = ''
      })
      .addCase(fetchUserList.fulfilled, (state, action) => {
        state.loading = false
        state.list = action.payload
      })
      .addCase(fetchUserList.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload
      })
  }
})

export default userSlice.reducer