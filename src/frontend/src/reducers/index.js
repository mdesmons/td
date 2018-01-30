import { combineReducers } from 'redux-immutable'
import customers from './customers'
import termDeposits from './termDeposits'
import error from './error'
import interestRate from './interestRate'
import transfers from './transfers'
import connectionStatus from './connectionStatus'

const reducers = combineReducers({
  customers,
  termDeposits,
  connectionStatus,
  interestRate,
  transfers,
  error
})


export default reducers
