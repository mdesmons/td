import { connect } from 'react-redux'
import {login} from '../actions'
import Home from '../components/Home'
import { browserHistory } from 'react-router'

const mapStateToProps = state => {
	return {
	customers: state.get('customers').toList(),
	connectionStatus: state.get('connectionStatus')
	}
}

const mapDispatchToProps = dispatch => {
	return {
   		onLogin : userData => { dispatch(login(userData)).then(() => {	console.log("Login succeeded"); browserHistory.push('/') })}
	}
}

const HomeContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(Home)

export default HomeContainer
