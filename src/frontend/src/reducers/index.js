import { combineReducers } from 'redux-immutable'
import customers from './customers'
import currentCustomer from './currentCustomer'
import termDeposits from './termDeposits'
import error from './error'
import interestRate from './interestRate'
import transfers from './transfers'
import connectionStatus from './connectionStatus'

const reducers = combineReducers({
  customers,
  currentCustomer,
  termDeposits,
  connectionStatus,
  interestRate,
  transfers,
  error
})


export default reducers
