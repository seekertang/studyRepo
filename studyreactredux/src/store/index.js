// src/store/index.js
import { createStore } from "redux";
import reducer from "./reducer";

// 创建唯一仓库
const store = createStore(
  reducer,
  // 开启浏览器Redux调试工具
  window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__()
);

export default store;