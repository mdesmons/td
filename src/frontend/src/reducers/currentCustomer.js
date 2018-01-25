import { fromJS, merge } from 'immutable';

const initialCustomer = fromJS({})

const currentCustomer = (state = initialCustomer, action) => {
 switch (action.type) {
    case 'SET_CURRENT_CUSTOMER':
    	return action.data.getIn("customers", "0") || state
	case 'LOGOUT':
			return state.clear()
    default:
        return state
  }
}

export default currentCustomer
