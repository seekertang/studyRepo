// src/store/index.js
import { configureStore } from '@reduxjs/toolkit'
import counterReducer from './counterSlice'
import userReducer from './userSlice'

// 自动合并reducer、内置中间件、自带调试工具
export const store = configureStore({
  reducer: {
    // 键名：组件取值的命名空间
    counter: counterReducer,
    user: userReducer
  }
})