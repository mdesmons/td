import { fromJS } from 'immutable';
const initialCustomers = fromJS({})

const customers = (state = initialCustomers, action) => {
  	console.log("entering customers reducer")

	switch (action.type) {
		case 'BASELINE':
			return action.data.get('customers') || state
		case 'LOGOUT':
			return state.clear()
		case 'ONBOARD':
			return state.merge(action.data.get('customers'))
		default:
			return state;
	}
}

export default customers
