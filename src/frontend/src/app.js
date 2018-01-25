import './main.css'
import background from './background.jpg';
import 'bootstrap';
import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { createStore, applyMiddleware } from 'redux'
import thunk from 'redux-thunk'
import {fromJS} from 'immutable'
import { Router, Route, IndexRoute, IndexRedirect, Link, browserHistory } from 'react-router'
import { login, logout, isLoggedIn, getAccessToken, isAdmin, handleAuthentication, refresh, setSession, hasScope } from './AuthService';

import reducer from './reducers'
import containers from './containers'
import {baseline} from './actions'
import { restGet, normalizeComplexResponse } from './client'

/* Sets the user default home screen to the ladder screen if they're only
   subscribed to one ladder
*/
const indexRoute = (store) => {
	return "/home"
}

const initializeUI = (store) => {
	// Render a <Router> with some <Route>s.
	// It does all the fancy routing stuff for us.
	console.log("rendering UI")
	console.log(containers)

	render((
	   <Provider store={store}>
		 <Router history={browserHistory}>
			<Route path="/" component={containers.appContainer}>
			  <IndexRedirect to={indexRoute(store)} />
			  <Route path="onboard" component={containers.OnboardContainer} />
			  <Route path="home" component={containers.HomeContainer} />
			  <Route path="termDepositList" component={containers.TermDepositListContainer} />
			  <Route path="termDeposit/:id" component={containers.TermDepositContainer} />
			  <Route path="customerList" component={containers.CustomerListContainer} />
			  <Route path="customer/:id" component={containers.CustomerContainer} />
			  <Route path="addTermDeposit/:id" component={containers.AddTermDepositContainer} />
			</Route>
		  </Router>
		</Provider>
	), document.getElementById('react'))
}

let ETag = 0

handleAuthentication((err) => {
	let store = createStore(reducer, normalizeComplexResponse({customers: [], termDeposits: [], currentCustomer: {}}), applyMiddleware(thunk))

	if (err) {
		console.log("User is not logged in")
		initializeUI(store)
	}
	else {

		if (isLoggedIn()) {
			store.dispatch(baseline()).then(() => 	{
				initializeUI(store)
		//		setInterval(() => { store.dispatch(baseline())}, 60000)
			})
		} else {
			console.log("User is not logged in")
			initializeUI(store)
		}
	}
})
