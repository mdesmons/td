import { fromJS, merge } from 'immutable';
const initialTransfers = fromJS({})


const transfers = (state = initialTransfers, action) => {
 	console.log("entering transfers reducer")

	switch (action.type) {
    case 'ADD_TERM_DEPOSIT':
		return state.merge(action.data.get('transfers'))
	case 'SET_CURRENT_CUSTOMER':
		return action.data.get("transfers") || state
   case 'CLOSE_TERM_DEPOSIT':
		// when a TD is closed, its transactions are marked as Canceled
		return state.merge(action.data.get('transfers'))
	case 'LOGOUT':
		return state.clear()
    default:
        return state
  }
}

export default transfers
