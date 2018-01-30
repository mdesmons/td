import { fromJS, merge } from 'immutable';

const initialStatus = fromJS({
	logged: false,
	admin: false
})

const connectionStatus = (state = initialStatus, action) => {
  	console.log("entering error reducer")
	switch (action.type) {
    case 'LOGOUT':
		return state.set('logged', false).set('admin', false)
    case 'LOGIN':
		console.log(action.data.toJS())
		return action.data.get('connectionStatus')
     default:
        return state
  }
}

export default connectionStatus
