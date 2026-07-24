import logo from './logo.svg';
import './App.css';
import React from 'react';
import { useSelector, useDispatch } from 'react-redux';

function App() {
  const count = useSelector(state => state.count);
  const username = useSelector(state => state.username);
  const dispatch = useDispatch();

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

      <div style={{ padding: '20px' }}>
        <h3>用户名：{username}</h3>
        <h3>计数：{count}</h3>

        <button onClick={() => dispatch({ type: 'INCREMENT' })}>加1</button>
        &nbsp;&nbsp;
        <button onClick={() => dispatch({ type: 'DECREMENT' })}>减1</button>
        <br/><br/>
        <button onClick={() => dispatch({ type: 'SET_NAME', payload: '张三' })}>
          设置用户名
        </button>
      </div>
    </div>
  );
}

export default App;
