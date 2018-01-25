import { fromJS, merge } from 'immutable';
const initialTransfers = fromJS({})


const transfers = (state = initialTransfers, action) => {
 	console.log("entering transfers reducer")

	switch (action.type) {
    case 'ADD_TERM_DEPOSIT':
		return state.merge(action.data.get('transfers'))
	case 'SET_CURRENT_CUSTOMER':
		return action.data.get("transfers") || state
	case 'LOGOUT':
		return state.clear()
    default:
        return state
  }
}

export default transfers
