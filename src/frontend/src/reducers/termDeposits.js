import { fromJS, merge } from 'immutable';
const initialTermDeposits = fromJS({})


const termDeposits = (state = initialTermDeposits, action) => {
 	console.log("entering termDeposits reducer")

	switch (action.type) {
     case 'BASELINE':
		return action.data.get('termDeposits') || state
    case 'ADD_TERM_DEPOSIT':
		return state.merge(action.data.get('termDeposits'))
	case 'SET_CURRENT_CUSTOMER':
		return action.data.get("termDeposits") || state
	case 'LOGOUT':
		return state.clear()
    default:
        return state
  }
}

export default termDeposits
