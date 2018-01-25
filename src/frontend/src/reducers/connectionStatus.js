import { fromJS, merge } from 'immutable';

const initialStatus = fromJS({
	logged: false,
	admin: false
})

const connectionStatus = (state = initialStatus, action) => {
	switch (action.type) {
      case 'BASELINE':
   		return action.data.get('connectionStatus')
    case 'LOGOUT':
		return state.set('logged', false).set('admin', false)
    case 'LOGIN':
		return state.set('logged', true)
     default:
        return state
  }
}

export default connectionStatus
