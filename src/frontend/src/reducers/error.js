import { fromJS } from 'immutable';
const initialValue = fromJS({})

const error = (state = initialValue, action) => {
  	console.log("entering error reducer")

	switch (action.type) {
		case 'SIGNAL_ERROR':
			// the returned value is a new match
			console.log(state.toJS())
			let newState = state.set('message', action.data.getIn(['error', 'message'])).set('active', true)
			console.log(newState.toJS())
			return newState
		case 'CLEAR_ERROR':
			// the returned value is a new match
			return state.set('active', false)
		default:
		return state;
	}
}

export default error
