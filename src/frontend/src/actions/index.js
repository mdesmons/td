import {Map} from 'immutable'
import { restGet, restPut, restPost, normalizeComplexResponse } from '../client'
import { logout, isLoggedIn, getAccessToken, setAccessToken, isAdmin, handleAuthentication, refresh, setSession, hasScope} from '../AuthService';
import {fromJS} from 'immutable'

export const signalError = (data) => {
  return {
    type: 'SIGNAL_ERROR',
    data
  }
}

export const clearError = () => {
  return {
    type: 'CLEAR_ERROR'
  }
}

export const logoutAction = () => {
  return {
    type: 'LOGOUT',
  }
}

export const onboardImpl = (data) => {
  return {
    type: 'ONBOARD',
    data
  }
}


export const onboard = (request) => {
	return function (dispatch) {
		return restPost('/api/v1/customer/', request)
			.done((data) => { dispatch(onboardImpl(normalizeComplexResponse(data)))})
			.fail((data) => { dispatch(signalError(normalizeComplexResponse(data.responseJSON)))})
	}
}


const loginImpl = () => {
  return {
    type: 'LOGIN'
  }
}

export const login = (data) => {
  	return function (dispatch) {
  		return restPost('/login', data, true)
  			.done((data, txt, jqXHR) =>  {
  				var accessToken = jqXHR.getResponseHeader("Authorization").replace("Bearer ", "")
  				setSession(accessToken)
  			 	dispatch(loginImpl())})
  			.fail((data) =>  { dispatch(signalError(normalizeComplexResponse(data.responseJSON))) })
  	}
}

const selectCustomerImpl = (data) => {
  return {
    type: 'SET_CURRENT_CUSTOMER',
    data
  }
}

export const selectCustomer = (id) => {
	return function (dispatch) {
		return restGet('/api/v1/customer/' + id + '/')
			.done((data) => { dispatch(selectCustomerImpl(normalizeComplexResponse(data)))})
			.fail((data) => { dispatch(signalError(normalizeComplexResponse(data.responseJSON)))})
	}
}

export const calculateInterestImpl = (data) => {
	return {
		type: 'CALCULATE_INTEREST',
		data
	}
}

export const calculateInterest = (id, data) => {
	return function (dispatch) {
		return restPut('/api/v1/customer/' + id + '/rate/', data)
			.done((data) => { dispatch(calculateInterestImpl(normalizeComplexResponse(data)))})
			.fail((data) => { dispatch(signalError(normalizeComplexResponse(data.responseJSON)))})
	}
}


const addTermDepositImpl = (data) => {
  return {
    type: 'ADD_TERM_DEPOSIT',
    data
  }
}

export const addTermDeposit = (id, data) => {
 	return function (dispatch) {
  		return restPost('/api/v1/customer/' + id + '/td/', data)
			.done((resp) => { dispatch(addTermDepositImpl(normalizeComplexResponse(resp)))})
			.fail((resp) => { dispatch(signalError(normalizeComplexResponse(resp.responseJSON)))})
  	}
}

const unsuspendPlayerImpl = (data) => {
  return {
    type: 'UNSUSPEND_PLAYER',
    data
  }
}

export const unsuspendPlayer = (request) => {
	return function (dispatch) {
		return restPut('/api/player/unsuspend/' + request.playerId, request)
			.done((data) => { dispatch(unsuspendPlayerImpl(normalizeComplexResponse(data)))})
			.fail((data) => { dispatch(signalError(normalizeComplexResponse(data.responseJSON)))})
	}
}


const setRankImpl = (data) => {
  return {
    type: 'SET_RANK',
    data
  }
}

export const setRank = (request) => {
	return function (dispatch) {
		return restPut('/api/admin/rank/' + request.ladderId, request)
			.done((data) => { dispatch(setRankImpl(normalizeComplexResponse(data)))})
			.fail((data) => { dispatch(signalError(normalizeComplexResponse(data.responseJSON)))})
	}
}

function baselineImpl(data)  {
	return {
		type: 'BASELINE',
		data
	}
}

let ETag = '0'

/* If this is a desk user, get the list of onboarded customers.
   If this is a regular user, get their TDs
*/
export const baseline = () => {
	let url = hasScope("desk") ? "/api/v1/customer/":"/api/player/"
	return function (dispatch) {
		return restGet(url, {"If-None-Match": ETag})
		.done((response, textStatus, request) => {
			if (request.status == 200) {
				ETag = 	request.getResponseHeader('etag')
				let data = normalizeComplexResponse(response).set('connectionStatus', fromJS({logged: isLoggedIn(), desk: true}))
				dispatch(baselineImpl(data))
			}
		})
		.fail((response) => {
			let data = normalizeComplexResponse(response).set('connectionStatus', fromJS({logged: false, admin: false}))
			dispatch(signalError(data))
		})
	}
}
