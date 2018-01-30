import { fromJS, merge } from 'immutable';
const initialTermDeposits = fromJS({})


const termDeposits = (state = initialTermDeposits, action) => {
 	console.log("entering termDeposits reducer")

	switch (action.type) {
     case 'BASELINE':
		return action.data.get('termDeposits') || state
    case 'ADD_TERM_DEPOSIT':
		return state.merge(action.data.get('termDeposits'))
   case 'CLOSE_TERM_DEPOSIT':
   		// if the TD was immediately closed, remove it.
   		// if it's a Period Notice, keep showing it
   		if (action.data.get('termDeposits').first().get('reasonForClose') != 2) {
			return state.delete(action.data.get('termDeposits').keySeq().first())
		} else {
			return state
		}
	case 'SET_CURRENT_CUSTOMER':
		return action.data.get("termDeposits") || state
	case 'LOGOUT':
		return state.clear()
    default:
        return state
  }
}

export default termDeposits
