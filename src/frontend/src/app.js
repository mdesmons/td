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
import { populateConnectionStatus } from './AuthService';

import reducer from './reducers'
import containers from './containers'
import {baseline, loginImpl} from './actions'
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
			  <Route path="termDepositList/:id" component={containers.TermDepositListContainer} />
			  <Route path="termDeposit/:customerId/:id" component={containers.TermDepositContainer} />
			  <Route path="customerList" component={containers.CustomerListContainer} />
			  <Route path="customer/:id" component={containers.CustomerContainer} />
			  <Route path="addTermDeposit/:id" component={containers.AddTermDepositContainer} />
			  <Route path="addBulkTermDeposit/:id" component={containers.AddBulkTermDepositContainer} />
			  <Route path="quotes/:id" component={containers.QuotesContainer} />
			</Route>
		  </Router>
		</Provider>
	), document.getElementById('react'))
}

let ETag = 0

let store = createStore(reducer, normalizeComplexResponse({customers: [], termDeposits: [], currentCustomer: {}}), applyMiddleware(thunk))

/* Read the JWT from local storage if present and update the connectionStatus model */
store.dispatch(loginImpl(normalizeComplexResponse(populateConnectionStatus())))

if (store.getState().getIn(['connectionStatus', 'logged'])) {
	store.dispatch(baseline()).then(() => 	{
		initializeUI(store)
//		setInterval(() => { store.dispatch(baseline())}, 60000)
	})
} else {
	console.log("User is not logged in")
	initializeUI(store)
}
