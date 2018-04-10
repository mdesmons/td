import { fromJS, merge } from 'immutable';
const initialQuotes = fromJS({})


const quotes = (state = initialQuotes, action) => {
 	console.log("entering quotes reducer for action " + action.type)

	switch (action.type) {
     case 'BASELINE':
     case 'SET_CURRENT_CUSTOMER':
		return action.data.get('quotes') || state
    case 'ADD_QUOTE':
		return state.merge(action.data.get('quotes'))
   case 'CLOSE_QUOTE':
   		// when a quote is closed, its status changes to Closed
   		var keysToDelete = action.data.get('quotes').keySeq()
   		keysToDelete.forEach(function(k, v) { state = state.delete(k)} )
   		return state
	case 'LOGOUT':
		return state.clear()
    default:
        return state
  }
}

export default quotes
