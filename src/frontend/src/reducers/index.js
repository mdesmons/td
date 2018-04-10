import { combineReducers } from 'redux-immutable'
import clientAccounts from './clientAccounts'
import connectionStatus from './connectionStatus'
import customers from './customers'
import error from './error'
import interestRate from './interestRate'
import quotes from './quotes'
import termDeposits from './termDeposits'
import transfers from './transfers'

const reducers = combineReducers({
  clientAccounts,
  connectionStatus,
  customers,
  error,
  interestRate,
  quotes,
  termDeposits,
  transfers
})


export default reducers
