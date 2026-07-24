import logo from './logo.svg';
import './App.css';
import React, { useEffect } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { increment, decrement, setName } from './store/counterSlice'
import { fetchUserList } from './store/userSlice'


function App() {
  // 按命名空间取状态
  const { count, username } = useSelector(state => state.counter)
  const dispatch = useDispatch()
  const {list, loading, error} = useSelector(state => state.user)

  useEffect(() => {
    dispatch(fetchUserList())
  }, [dispatch])

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
      <div style={{ padding: 30 }}>
        <h3>用户名：{username}</h3>
        <h3>计数：{count}</h3>

        <button onClick={() => dispatch(increment())}>加1</button>
        &nbsp;&nbsp;
        <button onClick={() => dispatch(decrement())}>减1</button>
        <br /><br />
        <button onClick={() => dispatch(setName('李四'))}>设置用户名</button>

        <hr />
        <h3>异步用户列表</h3>
        {loading && <p>加载中...</p>}
        {error && <p style={{color:'red'}}>{error}</p>}
        <ul>
          {list.map(item => (
            <li key={item.id}>{item.name}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default App;
