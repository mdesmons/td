import { fromJS } from 'immutable';
const initialClientAccounts = fromJS({})

const clientAccounts = (state = initialClientAccounts, action) => {
  	console.log("entering clientAccounts reducer")

	switch (action.type) {
		case 'SET_CURRENT_CUSTOMER':
			return action.data.get('clientAccounts') || initialClientAccounts
		case 'LOGOUT':
			return state.clear()
		default:
			return state;
	}
}

export default clientAccounts
