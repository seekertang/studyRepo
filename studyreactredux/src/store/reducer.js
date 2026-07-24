// src/store/reducer.js
// 初始状态
const initialState = {
  count: 0,
  username: "访客"
};

// reducer 纯函数
function reducer(state = initialState, action) {
  switch (action.type) {
    case "INCREMENT":
      return { ...state, count: state.count + 1 };
    case "DECREMENT":
      return { ...state, count: state.count - 1 };
    case "SET_NAME":
      return { ...state, username: action.payload };
    default:
      return state;
  }
}

export default reducer;