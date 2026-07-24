// src/store/counterSlice.js
import { createSlice } from '@reduxjs/toolkit'

// 初始状态
const initialState = {
  count: 0,
  username: '访客'
}

// 创建切片
const counterSlice = createSlice({
  name: 'counter', // 命名空间
  initialState,
  // 同步 reducer，可直接改state，RTK内部做了不可变处理
  reducers: {
    increment: (state) => {
      state.count += 1
    },
    decrement: (state) => {
      state.count -= 1
    },
    setName: (state, action) => {
      state.username = action.payload
    }
  }
})

// 导出action方法
export const { increment, decrement, setName } = counterSlice.actions

// 导出reducer给仓库使用
export default counterSlice.reducer